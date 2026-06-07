import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useRhAuth } from "../context/RhAuthContext";
import "./HrLoginPage.css";

export default function HrLoginPage() {
  const { login } = useRhAuth();
  const navigate  = useNavigate();
  const location  = useLocation();
  const from      = (location.state as { from?: { pathname: string } })?.from?.pathname ?? "/hr/employees";

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error,    setError]    = useState<string | null>(null);
  const [busy,     setBusy]     = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setBusy(true);
    setError(null);
    const err = await login(username, password);
    if (err) {
      setError(err);
      setBusy(false);
    } else {
      navigate(from, { replace: true });
    }
  }

  return (
    <div className="hr-login">
      <div className="hr-login__card">
        <div className="hr-login__brand">
          <span className="hr-login__mark">HR</span>
          <div>
            <p className="hr-login__title">Human Resources</p>
            <p className="hr-login__sub">Ranger — Staff & Payroll Portal</p>
          </div>
        </div>

        <form className="hr-login__form" onSubmit={submit} noValidate>
          {error && <div className="hr-login__error">{error}</div>}

          <label className="field">
            <span className="field__label">Username</span>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="admin"
              autoComplete="username"
              autoFocus
              required
            />
          </label>

          <label className="field">
            <span className="field__label">Password</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </label>

          <button className="hr-login__btn" type="submit" disabled={busy}>
            {busy ? "Signing in…" : "Sign in"}
          </button>
        </form>

        <p className="hr-login__hint">
          Default admin: <code>admin</code> / <code>supersecurepassword</code>
        </p>
      </div>
    </div>
  );
}
