import { http } from "./client";
import type { ProductRequest, ProductResponse } from "./types";

const base = "/api/products";

export const productsApi = {
  list: () => http.get<ProductResponse[]>(base),
  get: (id: number) => http.get<ProductResponse>(`${base}/${id}`),
  create: (body: ProductRequest) => http.post<ProductResponse>(base, body),
  update: (id: number, body: ProductRequest) => http.put<ProductResponse>(`${base}/${id}`, body),
  remove: (id: number) => http.del(`${base}/${id}`),
};
