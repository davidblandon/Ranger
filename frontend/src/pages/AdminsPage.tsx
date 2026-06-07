import { useState } from "react";
import { adminsApi } from "../api/rh";
import { useRhAuth } from "../context/RhAuthContext";
import type { AdminRequest, AdminResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { TextField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";

const EMPTY: AdminRequest = {
  name: "", username: "", password: "",
  telephone: "", address: "", bankAccount: "", permissions: "",
};

export default function AdminsPage() {
  const { logout } = useRhAuth();
  const { data, loading, error, reload } = useResource(() => adminsApi.list());
  const [editing,  setEditing]  = useState<AdminResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<AdminResponse | null>(null);
  const action = useAsyncAction();

  const admins = data ?? [];

  const columns: Column<AdminResponse>[] = [
    { key: "name",        header: "Name",        render: (a) => <span className="cell-strong">{a.name}</span> },
    { key: "username",    header: "Username",     render: (a) => <span className="mono">{a.username}</span> },
    { key: "telephone",   header: "Telephone",    render: (a) => a.telephone },
    { key: "permissions", header: "Permissions",  render: (a) => a.permissions },
    { key: "bankAccount", header: "Bank Account", render: (a) => a.bankAccount || <span className="cell-muted">—</span> },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => adminsApi.remove(toDelete.id));
    if (ok !== undefined) { setToDelete(null); reload(); }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Human Resources"
        title="Administrators"
        count={admins.length}
        actions={
          <>
            <Button variant="ghost" small onClick={logout}>Sign out</Button>
            <Button onClick={() => setCreating(true)}>+ New Admin</Button>
          </>
        }
      />

      {loading && <Loading />}
      {error   && <ErrorState message={error} onRetry={reload} />}
      {data && admins.length === 0 && (
        <EmptyState
          title="No administrators"
          message="Add admin users who can manage the HR system."
          action={<Button onClick={() => setCreating(true)}>+ New Admin</Button>}
        />
      )}
      {data && admins.length > 0 && (
        <DataTable
          columns={columns}
          rows={admins}
          rowKey={(a) => a.id}
          onRowClick={(a) => setEditing(a)}
          actions={(a) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(a)}>Edit</Button>
              <Button variant="subtle" small onClick={() => setToDelete(a)}>Delete</Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <AdminForm
          initial={editing}
          onClose={() => { setCreating(false); setEditing(null); }}
          onSaved={() => { setCreating(false); setEditing(null); reload(); }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete administrator?"
        message={`"${toDelete?.name}" will be permanently removed from the admin roster.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function AdminForm({
  initial,
  onClose,
  onSaved,
}: {
  initial: AdminResponse | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<AdminRequest>(
    initial
      ? {
          name: initial.name, username: initial.username, password: "",
          telephone: initial.telephone, address: initial.address,
          bankAccount: initial.bankAccount, permissions: initial.permissions,
        }
      : EMPTY
  );
  const action = useAsyncAction();
  const set = <K extends keyof AdminRequest>(k: K, v: AdminRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));
  const fe = action.fieldErrors ?? {};

  async function submit() {
    const result = await action.run(() =>
      initial ? adminsApi.update(initial.id, form) : adminsApi.create(form)
    );
    if (result) onSaved();
  }

  return (
    <Drawer
      open
      title={initial ? "Edit Admin" : "New Admin"}
      subtitle="Administrator"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>Cancel</Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create admin"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <TextField label="Full name" required value={form.name} onChange={(v) => set("name", v)} error={fe.name} />
      <div className="form-row">
        <TextField label="Username" required value={form.username} onChange={(v) => set("username", v)} error={fe.username} />
        <TextField label="Password" required={!initial} type="password" value={form.password} onChange={(v) => set("password", v)} hint={initial ? "Leave blank to keep current" : undefined} error={fe.password} />
      </div>
      <div className="form-row">
        <TextField label="Telephone" required value={form.telephone} onChange={(v) => set("telephone", v)} error={fe.telephone} />
        <TextField label="Bank account" required value={form.bankAccount} onChange={(v) => set("bankAccount", v)} error={fe.bankAccount} />
      </div>
      <TextField label="Address" required value={form.address} onChange={(v) => set("address", v)} error={fe.address} />
      <TextField label="Permissions" required value={form.permissions} onChange={(v) => set("permissions", v)} hint="e.g. ROLE_ADMIN" error={fe.permissions} />
    </Drawer>
  );
}
