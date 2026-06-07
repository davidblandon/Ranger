import type { OrderState } from "../api/types";
import "./ui.css";

const LABELS: Record<OrderState, string> = {
  PENDING: "Pending",
  IN_PRODUCTION: "In production",
  COMPLETED: "Completed",
  CANCELLED: "Cancelled",
};

export default function StatusBadge({ state }: { state: OrderState }) {
  return (
    <span className={`badge badge--${state.toLowerCase()}`}>
      <span className="badge__dot" />
      {LABELS[state]}
    </span>
  );
}
