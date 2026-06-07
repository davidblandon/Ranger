import { http } from "./client";
import type { BatchRequest, BatchResponse } from "./types";

const base = "/api/batches";

export const batchesApi = {
  list: () => http.get<BatchResponse[]>(base),
  get: (id: number) => http.get<BatchResponse>(`${base}/${id}`),
  create: (body: BatchRequest) => http.post<BatchResponse>(base, body),
  update: (id: number, body: BatchRequest) => http.put<BatchResponse>(`${base}/${id}`, body),
  remove: (id: number) => http.del(`${base}/${id}`),
};
