import { useState } from "react";
import { shiftsApi } from "../api/rh";
import { useRhAuth } from "../context/RhAuthContext";
import type { ShiftRequest, ShiftResponse, TimeBlockRequest } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";
import "./ShiftsPage.css";

type Day = "monday" | "tuesday" | "wednesday" | "thursday" | "friday" | "saturday" | "sunday";
const DAYS: Day[] = ["monday","tuesday","wednesday","thursday","friday","saturday","sunday"];
const DAY_LABELS: Record<Day, string> = {
  monday: "Mon", tuesday: "Tue", wednesday: "Wed",
  thursday: "Thu", friday: "Fri", saturday: "Sat", sunday: "Sun",
};

function shiftSummary(s: ShiftResponse): string {
  const activeDays = DAYS.filter((d) => s[d].length > 0);
  if (activeDays.length === 0) return "No days scheduled";
  return `${activeDays.length} day${activeDays.length > 1 ? "s" : ""} · ${activeDays.map((d) => DAY_LABELS[d]).join(", ")}`;
}

function totalBlocks(s: ShiftResponse): number {
  return DAYS.reduce((acc, d) => acc + s[d].length, 0);
}

export default function ShiftsPage() {
  const { logout } = useRhAuth();
  const { data, loading, error, reload } = useResource(() => shiftsApi.list());
  const [editing,  setEditing]  = useState<ShiftResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<ShiftResponse | null>(null);
  const action = useAsyncAction();

  const shifts = data ?? [];

  const columns: Column<ShiftResponse>[] = [
    { key: "id",      header: "ID",       numeric: true, render: (s) => `#${s.id}`, width: "60px" },
    { key: "schedule",header: "Schedule", render: (s) => <span className="cell-strong">{shiftSummary(s)}</span> },
    { key: "blocks",  header: "Blocks",   numeric: true, render: (s) => totalBlocks(s) },
    {
      key: "days",
      header: "Days",
      render: (s) => (
        <div className="day-pips">
          {DAYS.map((d) => (
            <span
              key={d}
              className={`day-pip ${s[d].length > 0 ? "day-pip--active" : ""}`}
              title={d}
            >
              {DAY_LABELS[d][0]}
            </span>
          ))}
        </div>
      ),
    },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => shiftsApi.remove(toDelete.id));
    if (ok !== undefined) { setToDelete(null); reload(); }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Human Resources"
        title="Shifts"
        count={shifts.length}
        actions={
          <>
            <Button variant="ghost" small onClick={logout}>Sign out</Button>
            <Button onClick={() => setCreating(true)}>+ New Shift</Button>
          </>
        }
      />

      {loading && <Loading />}
      {error   && <ErrorState message={error} onRetry={reload} />}
      {data && shifts.length === 0 && (
        <EmptyState
          title="No shifts defined"
          message="Create a shift schedule and assign it to employees."
          action={<Button onClick={() => setCreating(true)}>+ New Shift</Button>}
        />
      )}
      {data && shifts.length > 0 && (
        <DataTable
          columns={columns}
          rows={shifts}
          rowKey={(s) => s.id}
          onRowClick={(s) => setEditing(s)}
          actions={(s) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(s)}>Edit</Button>
              <Button variant="subtle" small onClick={() => setToDelete(s)}>Delete</Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <ShiftForm
          initial={editing}
          onClose={() => { setCreating(false); setEditing(null); }}
          onSaved={() => { setCreating(false); setEditing(null); reload(); }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete shift?"
        message={`Shift #${toDelete?.id} will be permanently removed. Employees assigned to it will have no shift.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function ShiftForm({
  initial,
  onClose,
  onSaved,
}: {
  initial: ShiftResponse | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [schedule, setSchedule] = useState<Record<Day, TimeBlockRequest[]>>(() => {
    const base: Record<Day, TimeBlockRequest[]> = {
      monday: [], tuesday: [], wednesday: [], thursday: [],
      friday: [], saturday: [], sunday: [],
    };
    if (initial) {
      DAYS.forEach((d) => {
        base[d] = initial[d].map((b) => ({ start: b.start, end: b.end }));
      });
    }
    return base;
  });
  const action = useAsyncAction();

  function addBlock(day: Day) {
    setSchedule((s) => ({ ...s, [day]: [...s[day], { start: "09:00:00", end: "17:00:00" }] }));
  }
  function removeBlock(day: Day, idx: number) {
    setSchedule((s) => ({ ...s, [day]: s[day].filter((_, i) => i !== idx) }));
  }
  function updateBlock(day: Day, idx: number, field: "start" | "end", value: string) {
    setSchedule((s) => {
      const blocks = [...s[day]];
      blocks[idx] = { ...blocks[idx], [field]: value.length === 5 ? value + ":00" : value };
      return { ...s, [day]: blocks };
    });
  }

  async function submit() {
    const body: ShiftRequest = {};
    DAYS.forEach((d) => { if (schedule[d].length > 0) body[d] = schedule[d]; });
    const result = await action.run(() =>
      initial ? shiftsApi.update(initial.id, body) : shiftsApi.create(body)
    );
    if (result) onSaved();
  }

  return (
    <Drawer
      open
      title={initial ? "Edit Shift" : "New Shift"}
      subtitle="Weekly Schedule"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>Cancel</Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create shift"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <div className="shift-grid">
        {DAYS.map((day) => (
          <div key={day} className="shift-day">
            <div className="shift-day__head">
              <span className="shift-day__label">{day.charAt(0).toUpperCase() + day.slice(1)}</span>
              <button className="shift-day__add" type="button" onClick={() => addBlock(day)}>
                + Add
              </button>
            </div>
            {schedule[day].length === 0 ? (
              <p className="shift-day__empty">Off</p>
            ) : (
              schedule[day].map((block, idx) => (
                <div key={idx} className="shift-block">
                  <input
                    type="time"
                    className="shift-block__time"
                    value={block.start.slice(0, 5)}
                    onChange={(e) => updateBlock(day, idx, "start", e.target.value)}
                  />
                  <span className="shift-block__sep">–</span>
                  <input
                    type="time"
                    className="shift-block__time"
                    value={block.end.slice(0, 5)}
                    onChange={(e) => updateBlock(day, idx, "end", e.target.value)}
                  />
                  <button
                    className="shift-block__remove"
                    type="button"
                    onClick={() => removeBlock(day, idx)}
                    aria-label="Remove"
                  >
                    ✕
                  </button>
                </div>
              ))
            )}
          </div>
        ))}
      </div>
    </Drawer>
  );
}
