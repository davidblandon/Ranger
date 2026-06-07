import { http } from "./client";
import type { OrderRequest, OrderResponse } from "./types";

const base = "/api/orders";

export const ordersApi = {
  list: () => http.get<OrderResponse[]>(base),
  get: (id: number) => http.get<OrderResponse>(`${base}/${id}`),
  create: (body: OrderRequest) => http.post<OrderResponse>(base, body),
  update: (id: number, body: OrderRequest) => http.put<OrderResponse>(`${base}/${id}`, body),
  remove: (id: number) => http.del(`${base}/${id}`),
};
