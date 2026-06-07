import { http } from "./client";
import type { MaterialRequest, MaterialResponse } from "./types";

const base = "/api/materials";

export const materialsApi = {
  list: () => http.get<MaterialResponse[]>(base),
  get: (id: number) => http.get<MaterialResponse>(`${base}/${id}`),
  create: (body: MaterialRequest) => http.post<MaterialResponse>(base, body),
  update: (id: number, body: MaterialRequest) => http.put<MaterialResponse>(`${base}/${id}`, body),
  remove: (id: number) => http.del(`${base}/${id}`),
};
