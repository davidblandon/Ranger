// TypeScript mirror of the production-service DTOs (the API contract).
// Computed fields (price/benefice/cost) are read-only — present on responses,
// never sent on requests.

export type OrderState = "PENDING" | "IN_PRODUCTION" | "COMPLETED" | "CANCELLED";

export const ORDER_STATES: OrderState[] = [
  "PENDING",
  "IN_PRODUCTION",
  "COMPLETED",
  "CANCELLED",
];

// — Product —
export interface ProductRequest {
  name: string;
  type?: string | null;
  size?: string | null;
  color?: string | null;
  materialIds: number[];
  price: number;
  cost: number;
  stock: number;
}
export interface ProductResponse {
  id: number;
  name: string;
  type: string | null;
  size: string | null;
  color: string | null;
  materialIds: number[];
  price: number;
  cost: number;
  stock: number;
}

// — Material —
export interface MaterialRequest {
  name: string;
  type?: string | null;
  partnerId?: number | null;
  cost: number;
  stock: number;
}
export interface MaterialResponse {
  id: number;
  name: string;
  type: string | null;
  partnerId: number | null;
  cost: number;
  stock: number;
}

// — Order —
// `products` maps a product id to the ordered quantity.
export interface OrderRequest {
  date?: string | null; // ISO yyyy-MM-dd
  partnerId: number;
  products: Record<number, number>;
  state?: OrderState | null;
}
export interface OrderResponse {
  id: number;
  date: string | null;
  partnerId: number;
  products: Record<number, number>;
  price: number;
  state: OrderState;
  benefice: number;
}

// — Batch —
// `materials` maps a material id to the quantity received in the batch.
export interface BatchRequest {
  arrivalDate: string; // ISO yyyy-MM-dd
  materials: Record<number, number>;
}
export interface BatchResponse {
  id: number;
  arrivalDate: string;
  materials: Record<number, number>;
  cost: number;
}

// — Partner —
export interface PartnerRequest {
  name: string;
  rol: string;
  bankNumber?: string | null;
  telephone?: string | null;
}
export interface PartnerResponse {
  id: number;
  name: string;
  rol: string;
  bankNumber: string | null;
  telephone: string | null;
}

// — Dashboard —
export interface DashboardResponse {
  totalProducts: number;
  totalMaterials: number;
  totalOrders: number;
  totalBatches: number;
  totalPartners: number;
  ordersByState: Partial<Record<OrderState, number>>;
  completedRevenue: number;
  completedBenefice: number;
  outOfStockProducts: number;
}

// — Error envelope (GlobalExceptionHandler.ApiError) —
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string> | null;
}
