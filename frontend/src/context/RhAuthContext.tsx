import { createContext, useContext, useState, useCallback } from "react";
import { Outlet, Navigate, useLocation } from "react-router-dom";
import { rhAuthApi } from "../api/rh";
import type { RhAuthResponse } from "../api/types";
import { ApiRequestError } from "../api/client";

const LS_KEY = "rh_user";

function loadStored(): RhAuthResponse | null {
  try {
    const raw = localStorage.getItem(LS_KEY);
    return raw ? (JSON.parse(raw) as RhAuthResponse) : null;
  } catch {
    return null;
  }
}

interface RhAuthCtx {
  user: RhAuthResponse | null;
  login:  (username: string, password: string) => Promise<string | null>;
  logout: () => Promise<void>;
}

const RhAuth = createContext<RhAuthCtx | null>(null);

export function useRhAuth() {
  const ctx = useContext(RhAuth);
  if (!ctx) throw new Error("useRhAuth must be used inside RhAuthProvider");
  return ctx;
}

/** Wraps the whole app (or the HR section) with auth state. */
export function RhAuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<RhAuthResponse | null>(loadStored);

  const login = useCallback(async (username: string, password: string): Promise<string | null> => {
    try {
      const res = await rhAuthApi.login({ username, password });
      setUser(res);
      localStorage.setItem(LS_KEY, JSON.stringify(res));
      return null;
    } catch (err) {
      if (err instanceof ApiRequestError) return err.message;
      return "Login failed";
    }
  }, []);

  const logout = useCallback(async () => {
    try { await rhAuthApi.logout(); } catch { /* ignore */ }
    setUser(null);
    localStorage.removeItem(LS_KEY);
  }, []);

  return <RhAuth.Provider value={{ user, login, logout }}>{children}</RhAuth.Provider>;
}

/** Layout route that redirects to /hr/login when unauthenticated. */
export function RhGuard() {
  const { user } = useRhAuth();
  const location  = useLocation();
  if (!user) {
    return <Navigate to="/hr/login" state={{ from: location }} replace />;
  }
  return <Outlet />;
}
