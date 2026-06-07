import "./page.css";

interface PageHeaderProps {
  eyebrow: string;
  title: string;
  count?: number;
  actions?: React.ReactNode;
}

export default function PageHeader({ eyebrow, title, count, actions }: PageHeaderProps) {
  return (
    <header className="page-head">
      <div>
        <p className="eyebrow">{eyebrow}</p>
        <h1 className="page-head__title">
          {title}
          {count !== undefined && <span className="page-head__count mono">{count}</span>}
        </h1>
      </div>
      {actions && <div className="page-head__actions">{actions}</div>}
    </header>
  );
}
