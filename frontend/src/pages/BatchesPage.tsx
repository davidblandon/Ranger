import { useState } from "react";
import { batchesApi } from "../api/batches";
import { materialsApi } from "../api/materials";
import type { BatchRequest, BatchResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { DateField, QuantityMapField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";
import { formatMoney, formatDate, todayIso } from "../lib/format";
import { nameMap, toOptions } from "../lib/lookup";

export default function BatchesPage() {
  const { data, loading, error, reload } = useResource(() => batchesApi.list());
  const materials = useResource(() => materialsApi.list());
  const [editing, setEditing] = useState<BatchResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<BatchResponse | null>(null);
  const action = useAsyncAction();

  const batches = data ?? [];
  const materialNames = nameMap(materials.data, (m) => m.name);

  const columns: Column<BatchResponse>[] = [
    { key: "id", header: "Batch", numeric: true, render: (b) => <span className="cell-strong">#{b.id}</span> },
    { key: "arrival", header: "Arrival Date", render: (b) => formatDate(b.arrivalDate) },
    {
      key: "materials",
      header: "Materials",
      render: (b) => {
        const count = Object.keys(b.materials).length;
        return count ? (
          <span className="tag tag--count">{count}</span>
        ) : (
          <span className="cell-muted">none</span>
        );
      },
    },
    { key: "cost", header: "Total Cost", numeric: true, render: (b) => formatMoney(b.cost) },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => batchesApi.remove(toDelete.id));
    if (ok !== undefined) {
      setToDelete(null);
      reload();
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Receiving"
        title="Batches"
        count={batches.length}
        actions={<Button onClick={() => setCreating(true)}>+ New Batch</Button>}
      />

      {(loading || materials.loading) && <Loading />}
      {error && <ErrorState message={error} onRetry={reload} />}
      {data && batches.length === 0 && (
        <EmptyState
          title="No batches yet"
          message="Record incoming material shipments. Receiving a batch raises each linked material's stock."
          action={<Button onClick={() => setCreating(true)}>+ New Batch</Button>}
        />
      )}
      {data && batches.length > 0 && (
        <DataTable
          columns={columns}
          rows={batches}
          rowKey={(b) => b.id}
          onRowClick={(b) => setEditing(b)}
          actions={(b) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(b)}>
                Edit
              </Button>
              <Button variant="subtle" small onClick={() => setToDelete(b)}>
                Delete
              </Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <BatchForm
          initial={editing}
          materialOptions={toOptions(materials.data, (m) => m.name)}
          materialNames={materialNames}
          onClose={() => {
            setCreating(false);
            setEditing(null);
          }}
          onSaved={() => {
            setCreating(false);
            setEditing(null);
            reload();
            materials.reload();
          }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete batch?"
        message={`Batch #${toDelete?.id} will be permanently removed.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function BatchForm({
  initial,
  materialOptions,
  materialNames,
  onClose,
  onSaved,
}: {
  initial: BatchResponse | null;
  materialOptions: { value: number | string; label: string }[];
  materialNames: Map<number, string>;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<BatchRequest>(
    initial
      ? { arrivalDate: initial.arrivalDate, materials: { ...initial.materials } }
      : { arrivalDate: todayIso(), materials: {} }
  );
  const action = useAsyncAction();
  const set = <K extends keyof BatchRequest>(k: K, v: BatchRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const result = await action.run(() =>
      initial ? batchesApi.update(initial.id, form) : batchesApi.create(form)
    );
    if (result) onSaved();
  }

  const fe = action.fieldErrors ?? {};

  return (
    <Drawer
      open
      title={initial ? `Edit Batch #${initial.id}` : "New Batch"}
      subtitle="Batch"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Receive batch"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      {!initial && (
        <p className="field__hint">
          Receiving a new batch increases each selected material's stock by its quantity.
        </p>
      )}
      <DateField
        label="Arrival Date"
        required
        value={form.arrivalDate}
        onChange={(v) => set("arrivalDate", v)}
        error={fe.arrivalDate}
      />
      <QuantityMapField
        label="Materials"
        value={form.materials}
        onChange={(materials) => set("materials", materials)}
        options={materialOptions}
        emptyHint="Create materials first."
        hint={
          Object.keys(form.materials).length
            ? Object.entries(form.materials)
                .map(([id, qty]) => `${materialNames.get(Number(id)) ?? `#${id}`} ×${qty}`)
                .join(", ")
            : "Tick a material and set how many units arrived."
        }
      />
    </Drawer>
  );
}
