import { useState } from "react";
import { payrollsApi, employeesApi } from "../api/rh";
import { useRhAuth } from "../context/RhAuthContext";
import type { PayrollRequest, PayrollGenerateRequest, PayrollResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { NumberField, SelectField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";

const MONTHS = [
  "January","February","March","April","May","June",
  "July","August","September","October","November","December",
].map((m, i) => ({ value: String(i + 1).padStart(2, "0"), label: m }));

const currentYear = new Date().getFullYear();
const YEARS = Array.from({ length: 5 }, (_, i) => {
  const y = String(currentYear - 2 + i);
  return { value: y, label: y };
});

const EMPTY_PAYROLL: PayrollRequest = {
  month: "", year: String(currentYear), paid: false, amount: 0, employeeId: 0,
};

function PaidBadge({ paid }: { paid: boolean }) {
  return (
    <span className={`badge badge--${paid ? "paid" : "unpaid"}`}>
      {paid ? "Paid" : "Unpaid"}
    </span>
  );
}

export default function PayrollsPage() {
  const { logout } = useRhAuth();
  const { data, loading, error, reload } = useResource(() => payrollsApi.list());
  const { data: employees } = useResource(() => employeesApi.list());
  const [editing,    setEditing]    = useState<PayrollResponse | null>(null);
  const [creating,   setCreating]   = useState(false);
  const [generating, setGenerating] = useState(false);
  const [toDelete,   setToDelete]   = useState<PayrollResponse | null>(null);
  const action     = useAsyncAction();
  const dlAction   = useAsyncAction();

  const payrolls      = data ?? [];
  const employeeMap   = Object.fromEntries((employees ?? []).map((e) => [e.id, e.name]));
  const employeeOpts  = (employees ?? []).map((e) => ({ value: e.id, label: e.name }));

  const columns: Column<PayrollResponse>[] = [
    { key: "id",      header: "ID",       numeric: true, render: (p) => `#${p.id}`, width: "60px" },
    { key: "employee",header: "Employee", render: (p) => <span className="cell-strong">{employeeMap[p.employeeId] ?? `#${p.employeeId}`}</span> },
    { key: "period",  header: "Period",   render: (p) => `${p.month}/${p.year}` },
    { key: "amount",  header: "Amount",   numeric: true, render: (p) => `$${p.amount.toLocaleString()}` },
    { key: "paid",    header: "Status",   render: (p) => <PaidBadge paid={p.paid} /> },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => payrollsApi.remove(toDelete.id));
    if (ok !== undefined) { setToDelete(null); reload(); }
  }

  async function handleDownload(p: PayrollResponse) {
    await dlAction.run(() => payrollsApi.paystub(p.id));
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Human Resources"
        title="Payrolls"
        count={payrolls.length}
        actions={
          <>
            <Button variant="ghost" small onClick={logout}>Sign out</Button>
            <Button variant="ghost" onClick={() => setGenerating(true)}>Generate</Button>
            <Button onClick={() => setCreating(true)}>+ New Payroll</Button>
          </>
        }
      />

      {loading && <Loading />}
      {error   && <ErrorState message={error} onRetry={reload} />}
      {data && payrolls.length === 0 && (
        <EmptyState
          title="No payrolls yet"
          message="Create or auto-generate payroll records for your employees."
          action={<Button onClick={() => setCreating(true)}>+ New Payroll</Button>}
        />
      )}
      {data && payrolls.length > 0 && (
        <DataTable
          columns={columns}
          rows={payrolls}
          rowKey={(p) => p.id}
          onRowClick={(p) => setEditing(p)}
          actions={(p) => (
            <>
              <Button variant="subtle" small onClick={() => handleDownload(p)}>PDF</Button>
              <Button variant="subtle" small onClick={() => setEditing(p)}>Edit</Button>
              <Button variant="subtle" small onClick={() => setToDelete(p)}>Delete</Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <PayrollForm
          initial={editing}
          employeeOpts={employeeOpts}
          onClose={() => { setCreating(false); setEditing(null); }}
          onSaved={() => { setCreating(false); setEditing(null); reload(); }}
        />
      )}

      {generating && (
        <GenerateForm
          employeeOpts={employeeOpts}
          onClose={() => setGenerating(false)}
          onSaved={() => { setGenerating(false); reload(); }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete payroll?"
        message={`Payroll #${toDelete?.id} for ${employeeMap[toDelete?.employeeId ?? 0] ?? "this employee"} will be permanently removed.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function PayrollForm({
  initial,
  employeeOpts,
  onClose,
  onSaved,
}: {
  initial: PayrollResponse | null;
  employeeOpts: { value: number; label: string }[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<PayrollRequest>(
    initial
      ? { month: initial.month, year: initial.year, paid: initial.paid, amount: initial.amount, employeeId: initial.employeeId }
      : EMPTY_PAYROLL
  );
  const action = useAsyncAction();
  const set = <K extends keyof PayrollRequest>(k: K, v: PayrollRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));
  const fe = action.fieldErrors ?? {};

  async function submit() {
    const result = await action.run(() =>
      initial ? payrollsApi.update(initial.id, form) : payrollsApi.create(form)
    );
    if (result) onSaved();
  }

  return (
    <Drawer
      open
      title={initial ? "Edit Payroll" : "New Payroll"}
      subtitle="Payroll"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>Cancel</Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create payroll"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <SelectField
        label="Employee" required
        value={form.employeeId || ""}
        onChange={(v) => set("employeeId", Number(v))}
        options={employeeOpts}
        error={fe.employeeId}
      />
      <div className="form-row">
        <SelectField label="Month" required value={form.month} onChange={(v) => set("month", v)} options={MONTHS} error={fe.month} />
        <SelectField label="Year"  required value={form.year}  onChange={(v) => set("year", v)}  options={YEARS}  error={fe.year} />
      </div>
      <NumberField label="Amount ($)" required min={0} value={form.amount} onChange={(v) => set("amount", v)} error={fe.amount} />
      <label className="field">
        <span className="field__label">Status</span>
        <select value={form.paid ? "1" : "0"} onChange={(e) => set("paid", e.target.value === "1")}>
          <option value="0">Unpaid</option>
          <option value="1">Paid</option>
        </select>
      </label>
    </Drawer>
  );
}

function GenerateForm({
  employeeOpts,
  onClose,
  onSaved,
}: {
  employeeOpts: { value: number; label: string }[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<PayrollGenerateRequest>({
    employeeId: 0, month: "", year: String(currentYear),
  });
  const action = useAsyncAction();
  const set = <K extends keyof PayrollGenerateRequest>(k: K, v: PayrollGenerateRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const result = await action.run(() => payrollsApi.generate(form));
    if (result) onSaved();
  }

  return (
    <Drawer
      open
      title="Generate Payroll"
      subtitle="Auto-generate"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>Cancel</Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Generating…" : "Generate"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <SelectField
        label="Employee" required
        value={form.employeeId || ""}
        onChange={(v) => set("employeeId", Number(v))}
        options={employeeOpts}
      />
      <div className="form-row">
        <SelectField label="Month" required value={form.month} onChange={(v) => set("month", v)} options={MONTHS} />
        <SelectField label="Year"  required value={form.year}  onChange={(v) => set("year", v)}  options={YEARS} />
      </div>
    </Drawer>
  );
}
