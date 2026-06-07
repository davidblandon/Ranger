import { useState } from "react";
import { ApiRequestError } from "../api/client";

export interface ActionState {
  submitting: boolean;
  error: string | null;
  fieldErrors: Record<string, string> | null;
}

/**
 * Wraps a mutating call (create/update/delete), tracking submit state and
 * normalizing backend ApiError into a message + per-field errors for forms.
 */
export function useAsyncAction() {
  const [state, setState] = useState<ActionState>({
    submitting: false,
    error: null,
    fieldErrors: null,
  });

  async function run<T>(action: () => Promise<T>): Promise<T | undefined> {
    setState({ submitting: true, error: null, fieldErrors: null });
    try {
      const result = await action();
      setState({ submitting: false, error: null, fieldErrors: null });
      return result;
    } catch (err) {
      if (err instanceof ApiRequestError) {
        setState({ submitting: false, error: err.message, fieldErrors: err.fieldErrors ?? null });
      } else {
        setState({ submitting: false, error: "Something went wrong.", fieldErrors: null });
      }
      return undefined;
    }
  }

  return { ...state, run };
}
