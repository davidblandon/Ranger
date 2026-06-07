import { createPortal } from "react-dom";
import Button from "./Button";
import "./page.css";

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  message: string;
  confirmLabel?: string;
  busy?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel = "Delete",
  busy = false,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  if (!open) return null;
  return createPortal(
    <div className="confirm__overlay" onMouseDown={onCancel}>
      <div className="confirm" onMouseDown={(e) => e.stopPropagation()}>
        <h3 className="confirm__title">{title}</h3>
        <p className="confirm__msg">{message}</p>
        <div className="confirm__actions">
          <Button variant="ghost" onClick={onCancel} disabled={busy}>
            Cancel
          </Button>
          <Button variant="danger" onClick={onConfirm} disabled={busy}>
            {busy ? "Working…" : confirmLabel}
          </Button>
        </div>
      </div>
    </div>,
    document.body
  );
}
