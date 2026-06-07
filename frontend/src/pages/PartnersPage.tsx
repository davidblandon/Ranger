import { useState } from "react";
import { partnersApi } from "../api/partners";
import type { PartnerRequest, PartnerResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { TextField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";

const EMPTY: PartnerRequest = { name: "", rol: "", bankNumber: "", telephone: "" };

export default function PartnersPage() {
  const { data, loading, error, reload } = useResource(() => partnersApi.list());
  const [editing, setEditing] = useState<PartnerResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<PartnerResponse | null>(null);
  const action = useAsyncAction();

  const partners = data ?? [];

  const columns: Column<PartnerResponse>[] = [
    { key: "name", header: "Name", render: (p) => <span className="cell-strong">{p.name}</span> },
    { key: "rol", header: "Role", render: (p) => p.rol },
    {
      key: "telephone",
      header: "Telephone",
      render: (p) => p.telephone || <span className="cell-muted">—</span>,
    },
    {
      key: "bankNumber",
      header: "Bank Number",
      numeric: true,
      render: (p) => p.bankNumber || <span className="cell-muted">—</span>,
    },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => partnersApi.remove(toDelete.id));
    if (ok !== undefined) {
      setToDelete(null);
      reload();
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Network"
        title="Partners"
        count={partners.length}
        actions={<Button onClick={() => setCreating(true)}>+ New Partner</Button>}
      />

      {loading && <Loading />}
      {error && <ErrorState message={error} onRetry={reload} />}
      {data && partners.length === 0 && (
        <EmptyState
          title="No partners yet"
          message="Add suppliers and clients you work with."
          action={<Button onClick={() => setCreating(true)}>+ New Partner</Button>}
        />
      )}
      {data && partners.length > 0 && (
        <DataTable
          columns={columns}
          rows={partners}
          rowKey={(p) => p.id}
          onRowClick={(p) => setEditing(p)}
          actions={(p) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(p)}>
                Edit
              </Button>
              <Button variant="subtle" small onClick={() => setToDelete(p)}>
                Delete
              </Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <PartnerForm
          initial={editing}
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
        title="Delete partner?"
        message={`“${toDelete?.name}” will be permanently removed. This may fail if it is referenced by materials or orders.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function PartnerForm({
  initial,
  onClose,
  onSaved,
}: {
  initial: PartnerResponse | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<PartnerRequest>(
    initial
      ? {
          name: initial.name,
          rol: initial.rol,
          bankNumber: initial.bankNumber ?? "",
          telephone: initial.telephone ?? "",
        }
      : EMPTY
  );
  const action = useAsyncAction();
  const set = <K extends keyof PartnerRequest>(k: K, v: PartnerRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const result = await action.run(() =>
      initial ? partnersApi.update(initial.id, form) : partnersApi.create(form)
    );
    if (result) onSaved();
  }

  const fe = action.fieldErrors ?? {};

  return (
    <Drawer
      open
      title={initial ? "Edit Partner" : "New Partner"}
      subtitle="Partner"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create partner"}
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
      <TextField
        label="Role"
        required
        value={form.rol}
        onChange={(v) => set("rol", v)}
        error={fe.rol}
        hint="e.g. Supplier, Client, Distributor"
      />
      <div className="form-row">
        <TextField
          label="Telephone"
          value={form.telephone ?? ""}
          onChange={(v) => set("telephone", v)}
        />
        <TextField
          label="Bank Number"
          value={form.bankNumber ?? ""}
          onChange={(v) => set("bankNumber", v)}
        />
      </div>
    </Drawer>
  );
}
