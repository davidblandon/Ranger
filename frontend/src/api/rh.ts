import { http } from "./client";
import type {
  RhAuthRequest,
  RhAuthResponse,
  EmployeeRequest,
  EmployeeResponse,
  PayrollRequest,
  PayrollGenerateRequest,
  PayrollResponse,
  ShiftRequest,
  ShiftResponse,
  AdminRequest,
  AdminResponse,
} from "./types";

export const rhAuthApi = {
  login:  (body: RhAuthRequest) => http.post<RhAuthResponse>("/api/auth/login", body),
  logout: ()                    => http.post<void>("/api/auth/logout", {}),
};

export const employeesApi = {
  list:   ()                                    => http.get<EmployeeResponse[]>("/api/employees"),
  get:    (id: number)                          => http.get<EmployeeResponse>(`/api/employees/${id}`),
  create: (body: EmployeeRequest)               => http.post<EmployeeResponse>("/api/employees", body),
  update: (id: number, body: EmployeeRequest)   => http.put<EmployeeResponse>(`/api/employees/${id}`, body),
  remove: (id: number)                          => http.del(`/api/employees/${id}`),
};

export const payrollsApi = {
  list:     ()                                    => http.get<PayrollResponse[]>("/api/payrolls"),
  get:      (id: number)                          => http.get<PayrollResponse>(`/api/payrolls/${id}`),
  create:   (body: PayrollRequest)               => http.post<PayrollResponse>("/api/payrolls", body),
  update:   (id: number, body: PayrollRequest)   => http.put<PayrollResponse>(`/api/payrolls/${id}`, body),
  remove:   (id: number)                          => http.del(`/api/payrolls/${id}`),
  generate: (body: PayrollGenerateRequest)        => http.post<PayrollResponse>("/api/payrolls/generate", body),
  paystub:  (id: number)                          => downloadPaystub(id),
};

export const shiftsApi = {
  list:   ()                                  => http.get<ShiftResponse[]>("/api/shifts"),
  get:    (id: number)                        => http.get<ShiftResponse>(`/api/shifts/${id}`),
  create: (body: ShiftRequest)               => http.post<ShiftResponse>("/api/shifts", body),
  update: (id: number, body: ShiftRequest)   => http.put<ShiftResponse>(`/api/shifts/${id}`, body),
  remove: (id: number)                        => http.del(`/api/shifts/${id}`),
};

export const adminsApi = {
  list:   ()                                  => http.get<AdminResponse[]>("/api/admins"),
  get:    (id: number)                        => http.get<AdminResponse>(`/api/admins/${id}`),
  create: (body: AdminRequest)               => http.post<AdminResponse>("/api/admins", body),
  update: (id: number, body: AdminRequest)   => http.put<AdminResponse>(`/api/admins/${id}`, body),
  remove: (id: number)                        => http.del(`/api/admins/${id}`),
};

async function downloadPaystub(id: number): Promise<void> {
  const res = await fetch(`/api/payrolls/${id}/paystub`);
  if (!res.ok) throw new Error(`Failed to download paystub (${res.status})`);
  const blob = await res.blob();
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement("a");
  a.href     = url;
  a.download = `paystub-${id}.pdf`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}
