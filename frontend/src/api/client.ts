import type { ApiError } from "./types";

// Base URL is read from env; defaults to "" so requests hit the same origin
// ("/api/..."), which the Vite dev proxy and the nginx prod proxy forward to
// the backend. Never hard-code a backend host here.
const BASE = import.meta.env.VITE_API_BASE_URL ?? "";

/** Error thrown for any non-2xx response, carrying the parsed ApiError body. */
export class ApiRequestError extends Error {
  readonly status: number;
  readonly fieldErrors?: Record<string, string> | null;

  constructor(status: number, message: string, fieldErrors?: Record<string, string> | null) {
    super(message);
    this.name = "ApiRequestError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function parseError(res: Response): Promise<ApiRequestError> {
  try {
    const body = (await res.json()) as ApiError;
    return new ApiRequestError(
      res.status,
      body.message || body.error || res.statusText,
      body.fieldErrors
    );
  } catch {
    return new ApiRequestError(res.status, res.statusText || "Request failed");
  }
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${BASE}${path}`, {
      method,
      headers: body !== undefined ? { "Content-Type": "application/json" } : undefined,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiRequestError(0, "Network error — is the backend reachable?");
  }

  if (!res.ok) throw await parseError(res);

  // 204 No Content (DELETE) — nothing to parse.
  if (res.status === 204 || res.headers.get("content-length") === "0") {
    return undefined as T;
  }
  return (await res.json()) as T;
}

export const http = {
  get: <T>(path: string) => request<T>("GET", path),
  post: <T>(path: string, body: unknown) => request<T>("POST", path, body),
  put: <T>(path: string, body: unknown) => request<T>("PUT", path, body),
  del: (path: string) => request<void>("DELETE", path).then(() => true as const),
};
