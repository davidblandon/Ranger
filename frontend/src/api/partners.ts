import { http } from "./client";
import type { PartnerRequest, PartnerResponse } from "./types";

const base = "/api/partners";

export const partnersApi = {
  list: () => http.get<PartnerResponse[]>(base),
  get: (id: number) => http.get<PartnerResponse>(`${base}/${id}`),
  create: (body: PartnerRequest) => http.post<PartnerResponse>(base, body),
  update: (id: number, body: PartnerRequest) => http.put<PartnerResponse>(`${base}/${id}`, body),
  remove: (id: number) => http.del(`${base}/${id}`),
};
