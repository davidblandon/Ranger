import { useState } from "react";
import { materialsApi } from "../api/materials";
import { partnersApi } from "../api/partners";
import type { MaterialRequest, MaterialResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { TextField, NumberField, SelectField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";
import { formatMoney } from "../lib/format";
import { nameMap, toOptions, resolveName } from "../lib/lookup";

const EMPTY: MaterialRequest = {
  name: "",
  type: "",
  partnerId: null,
  cost: 0,
  stock: 0,
};

export default function MaterialsPage() {
  const { data, loading, error, reload } = useResource(() => materialsApi.list());
  const partners = useResource(() => partnersApi.list());
  const [editing, setEditing] = useState<MaterialResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<MaterialResponse | null>(null);
  const action = useAsyncAction();

  const materials = data ?? [];
  const partnerNames = nameMap(partners.data, (p) => p.name);

  const columns: Column<MaterialResponse>[] = [
    { key: "name", header: "Material", render: (m) => <span className="cell-strong">{m.name}</span> },
    {
      key: "type",
      header: "Type",
      render: (m) => m.type || <span className="cell-muted">—</span>,
    },
    {
      key: "partner",
      header: "Supplier",
      render: (m) =>
        m.partnerId ? resolveName(partnerNames, m.partnerId) : <span className="cell-muted">—</span>,
    },
    { key: "cost", header: "Cost", numeric: true, render: (m) => formatMoney(m.cost) },
    { key: "stock", header: "Stock", numeric: true, render: (m) => m.stock },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => materialsApi.remove(toDelete.id));
    if (ok !== undefined) {
      setToDelete(null);
      reload();
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Inventory"
        title="Materials"
        count={materials.length}
        actions={<Button onClick={() => setCreating(true)}>+ New Material</Button>}
      />

      {(loading || partners.loading) && <Loading />}
      {error && <ErrorState message={error} onRetry={reload} />}
      {data && materials.length === 0 && (
        <EmptyState
          title="No materials yet"
          message="Track the raw materials used in production."
          action={<Button onClick={() => setCreating(true)}>+ New Material</Button>}
        />
      )}
      {data && materials.length > 0 && (
        <DataTable
          columns={columns}
          rows={materials}
          rowKey={(m) => m.id}
          onRowClick={(m) => setEditing(m)}
          actions={(m) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(m)}>
                Edit
              </Button>
              <Button variant="subtle" small onClick={() => setToDelete(m)}>
                Delete
              </Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <MaterialForm
          initial={editing}
          partnerOptions={toOptions(partners.data, (p) => p.name)}
          onClose={() => {
            setCreating(false);
            setEditing(null);
          }}
          onSaved={() => {
            setCreating(false);
            setEditing(null);
            reload();
          }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete material?"
        message={`“${toDelete?.name}” will be permanently removed. This may fail if it is referenced by a product.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function MaterialForm({
  initial,
  partnerOptions,
  onClose,
  onSaved,
}: {
  initial: MaterialResponse | null;
  partnerOptions: { value: number | string; label: string }[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<MaterialRequest>(
    initial
      ? {
          name: initial.name,
          type: initial.type ?? "",
          partnerId: initial.partnerId,
          cost: initial.cost,
          stock: initial.stock,
        }
      : EMPTY
  );
  const action = useAsyncAction();
  const set = <K extends keyof MaterialRequest>(k: K, v: MaterialRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const payload: MaterialRequest = {
      ...form,
      cost: Number.isNaN(form.cost) ? 0 : form.cost,
      stock: Number.isNaN(form.stock) ? 0 : form.stock,
    };
    const result = await action.run(() =>
      initial ? materialsApi.update(initial.id, payload) : materialsApi.create(payload)
    );
    if (result) onSaved();
  }

  const fe = action.fieldErrors ?? {};

  return (
    <Drawer
      open
      title={initial ? "Edit Material" : "New Material"}
      subtitle="Material"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create material"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <TextField
        label="Name"
        required
        value={form.name}
        onChange={(v) => set("name", v)}
        error={fe.name}
      />
      <TextField label="Type" value={form.type ?? ""} onChange={(v) => set("type", v)} />
      <SelectField
        label="Supplier (partner)"
        value={form.partnerId}
        onChange={(v) => set("partnerId", v ? Number(v) : null)}
        options={partnerOptions}
        placeholder="No supplier"
        error={fe.partnerId}
      />
      <div className="form-row">
        <NumberField
          label="Cost"
          min={0}
          value={form.cost}
          onChange={(v) => set("cost", v)}
          error={fe.cost}
        />
        <NumberField
          label="Stock"
          min={0}
          step={1}
          value={form.stock}
          onChange={(v) => set("stock", v)}
          error={fe.stock}
        />
      </div>
    </Drawer>
  );
}
