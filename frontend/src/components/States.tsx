import "./ui.css";

export function Loading({ label = "Loading…" }: { label?: string }) {
  return (
    <div className="state">
      <span className="spinner" />
      <p className="state__msg">{label}</p>
    </div>
  );
}

export function EmptyState({
  title = "Nothing here yet",
  message,
  action,
}: {
  title?: string;
  message?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="state">
      <span className="state__glyph" aria-hidden>
        ◌
      </span>
      <p className="state__title">{title}</p>
      {message && <p className="state__msg">{message}</p>}
      {action}
    </div>
  );
}

export function ErrorState({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="state state--error">
      <span className="state__glyph" aria-hidden>
        ⚠
      </span>
      <p className="state__title">Couldn’t load this</p>
      <p className="state__msg">{message}</p>
      {onRetry && (
        <button className="btn btn--ghost btn--sm" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  );
}
