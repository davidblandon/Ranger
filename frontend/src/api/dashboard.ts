import { http } from "./client";
import type { DashboardResponse } from "./types";

export const dashboardApi = {
  get: () => http.get<DashboardResponse>("/api/dashboard"),
};
