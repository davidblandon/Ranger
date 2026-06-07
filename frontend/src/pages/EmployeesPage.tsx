import { useState } from "react";
import { employeesApi, shiftsApi } from "../api/rh";
import { useRhAuth } from "../context/RhAuthContext";
import type { EmployeeRequest, EmployeeResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { TextField, NumberField, SelectField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";

const EMPTY: EmployeeRequest = {
  name: "", username: "", password: "",
  telephone: "", address: "", bankAccount: "",
  monthlyHours: 0, salary: 0, shiftId: null,
};

export default function EmployeesPage() {
  const { logout } = useRhAuth();
  const { data, loading, error, reload } = useResource(() => employeesApi.list());
  const { data: shifts } = useResource(() => shiftsApi.list());
  const [editing,  setEditing]  = useState<EmployeeResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<EmployeeResponse | null>(null);
  const action = useAsyncAction();

  const employees   = data ?? [];
  const shiftOptions = (shifts ?? []).map((s) => ({
    value: s.id,
    label: `Shift #${s.id}`,
  }));

  const columns: Column<EmployeeResponse>[] = [
    { key: "name",     header: "Name",          render: (e) => <span className="cell-strong">{e.name}</span> },
    { key: "username", header: "Username",       render: (e) => <span className="mono">{e.username}</span> },
    { key: "role",     header: "Role",           render: (e) => e.role },
    { key: "telephone",header: "Telephone",      render: (e) => e.telephone },
    { key: "salary",   header: "Salary",         numeric: true, render: (e) => `$${e.salary.toLocaleString()}` },
    { key: "hours",    header: "Monthly Hrs",    numeric: true, render: (e) => e.monthlyHours },
    { key: "shift",    header: "Shift",          render: (e) => e.shiftId ? `#${e.shiftId}` : <span className="cell-muted">—</span> },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => employeesApi.remove(toDelete.id));
    if (ok !== undefined) { setToDelete(null); reload(); }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Human Resources"
        title="Employees"
        count={employees.length}
        actions={
          <>
            <Button variant="ghost" small onClick={logout}>Sign out</Button>
            <Button onClick={() => setCreating(true)}>+ New Employee</Button>
          </>
        }
      />

      {loading && <Loading />}
      {error   && <ErrorState message={error} onRetry={reload} />}
      {data && employees.length === 0 && (
        <EmptyState
          title="No employees yet"
          message="Add your first employee to get started."
          action={<Button onClick={() => setCreating(true)}>+ New Employee</Button>}
        />
      )}
      {data && employees.length > 0 && (
        <DataTable
          columns={columns}
          rows={employees}
          rowKey={(e) => e.id}
          onRowClick={(e) => setEditing(e)}
          actions={(e) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(e)}>Edit</Button>
              <Button variant="subtle" small onClick={() => setToDelete(e)}>Delete</Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <EmployeeForm
          initial={editing}
          shiftOptions={shiftOptions}
          onClose={() => { setCreating(false); setEditing(null); }}
          onSaved={() => { setCreating(false); setEditing(null); reload(); }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete employee?"
        message={`"${toDelete?.name}" will be permanently removed, along with all linked payrolls.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function EmployeeForm({
  initial,
  shiftOptions,
  onClose,
  onSaved,
}: {
  initial: EmployeeResponse | null;
  shiftOptions: { value: number; label: string }[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<EmployeeRequest>(
    initial
      ? {
          name: initial.name, username: initial.username, password: "",
          telephone: initial.telephone, address: initial.address,
          bankAccount: initial.bankAccount, monthlyHours: initial.monthlyHours,
          salary: initial.salary, shiftId: initial.shiftId,
        }
      : EMPTY
  );
  const action = useAsyncAction();
  const set = <K extends keyof EmployeeRequest>(k: K, v: EmployeeRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));
  const fe = action.fieldErrors ?? {};

  async function submit() {
    const result = await action.run(() =>
      initial ? employeesApi.update(initial.id, form) : employeesApi.create(form)
    );
    if (result) onSaved();
  }

  return (
    <Drawer
      open
      title={initial ? "Edit Employee" : "New Employee"}
      subtitle="Employee"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>Cancel</Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create employee"}
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
      <div className="form-row">
        <NumberField label="Monthly hours" required min={0} value={form.monthlyHours} onChange={(v) => set("monthlyHours", v)} error={fe.monthlyHours} />
        <NumberField label="Salary ($)" required min={0} value={form.salary} onChange={(v) => set("salary", v)} error={fe.salary} />
      </div>
      <SelectField
        label="Assigned shift"
        value={form.shiftId ?? ""}
        onChange={(v) => set("shiftId", v ? Number(v) : null)}
        options={shiftOptions}
        placeholder="No shift assigned"
      />
    </Drawer>
  );
}
