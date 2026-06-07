import { NavLink, useLocation, useNavigate } from "react-router-dom";
import "./AppShell.css";

const PROD_NAV = [
  { to: "/", label: "Dashboard", end: true, glyph: "◷" },
  { to: "/orders", label: "Orders", glyph: "▤" },
  { to: "/products", label: "Products", glyph: "◫" },
  { to: "/materials", label: "Materials", glyph: "▦" },
  { to: "/batches", label: "Batches", glyph: "▥" },
  { to: "/partners", label: "Partners", glyph: "◍" },
];

const HR_NAV = [
  { to: "/hr/employees", label: "Employees", glyph: "◉" },
  { to: "/hr/payrolls",  label: "Payrolls",  glyph: "◈" },
  { to: "/hr/shifts",    label: "Shifts",    glyph: "▦" },
  { to: "/hr/admins",    label: "Admins",    glyph: "◐" },
];

export default function AppShell({ children }: { children: React.ReactNode }) {
  const location = useLocation();
  const navigate = useNavigate();
  const isHr = location.pathname.startsWith("/hr");

  function switchTo(service: "production" | "hr") {
    if (service === "hr") navigate("/hr/employees");
    else navigate("/");
  }

  return (
    <div className="shell">
      <aside className="shell__sidebar">
        <div className="shell__brand">
          <span className="shell__brand-mark">R</span>
          <span className="shell__brand-text">
            <strong>RANGER</strong>
            <span className="shell__brand-sub">{isHr ? "Human Resources" : "Production Control"}</span>
          </span>
        </div>

        <div className="shell__switcher">
          <button
            className={`shell__switch-btn${!isHr ? " shell__switch-btn--active" : ""}`}
            onClick={() => switchTo("production")}
          >
            Production
          </button>
          <button
            className={`shell__switch-btn shell__switch-btn--hr${isHr ? " shell__switch-btn--hr-active" : ""}`}
            onClick={() => switchTo("hr")}
          >
            HR
          </button>
        </div>

        {!isHr ? (
          <nav className="shell__nav">
            <p className="eyebrow shell__nav-head">Production</p>
            {PROD_NAV.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  `shell__link${isActive ? " shell__link--active" : ""}`
                }
              >
                <span className="shell__link-glyph" aria-hidden>{item.glyph}</span>
                {item.label}
              </NavLink>
            ))}
          </nav>
        ) : (
          <nav className="shell__nav shell__nav--hr">
            <p className="eyebrow shell__nav-head shell__nav-head--hr">Human Resources</p>
            {HR_NAV.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `shell__link shell__link--hr${isActive ? " shell__link--hr-active" : ""}`
                }
              >
                <span className="shell__link-glyph" aria-hidden>{item.glyph}</span>
                {item.label}
              </NavLink>
            ))}
          </nav>
        )}

        <div className="shell__foot">
          <div className="shell__foot-row">
            <span className={`shell__dot${isHr ? " shell__dot--hr" : ""}`} />
            Connected to {isHr ? "rh-service" : "production-service"}
          </div>
        </div>
      </aside>

      <main className={`shell__main${isHr ? " hr-scope" : ""}`}>{children}</main>
    </div>
  );
}
