import { NavLink } from "react-router-dom";
import "./AppShell.css";

const NAV = [
  { to: "/", label: "Dashboard", end: true, glyph: "◷" },
  { to: "/orders", label: "Orders", glyph: "▤" },
  { to: "/products", label: "Products", glyph: "◫" },
  { to: "/materials", label: "Materials", glyph: "▦" },
  { to: "/batches", label: "Batches", glyph: "▥" },
  { to: "/partners", label: "Partners", glyph: "◍" },
];

export default function AppShell({ children }: { children: React.ReactNode }) {
  return (
    <div className="shell">
      <aside className="shell__sidebar">
        <div className="shell__brand">
          <span className="shell__brand-mark">R</span>
          <span className="shell__brand-text">
            <strong>RANGER</strong>
            <span className="shell__brand-sub">Production Control</span>
          </span>
        </div>

        <nav className="shell__nav">
          <p className="eyebrow shell__nav-head">Workspace</p>
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                `shell__link${isActive ? " shell__link--active" : ""}`
              }
            >
              <span className="shell__link-glyph" aria-hidden>
                {item.glyph}
              </span>
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="shell__foot">
          <span className="shell__dot" /> Connected to production-service
        </div>
      </aside>

      <main className="shell__main">{children}</main>
    </div>
  );
}
