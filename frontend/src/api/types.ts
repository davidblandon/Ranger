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

// ─────────────────────────────────────────────
// RH Service DTOs
// ─────────────────────────────────────────────

// — RH Auth —
export interface RhAuthRequest { username: string; password: string; }
export interface RhAuthResponse { id: number; username: string; role: "ADMIN" | "EMPLOYEE"; }

// — Employee —
export interface EmployeeRequest {
  name: string;
  username: string;
  password: string;
  telephone: string;
  address: string;
  bankAccount: string;
  monthlyHours: number;
  salary: number;
  shiftId?: number | null;
}
export interface EmployeeResponse {
  id: number;
  name: string;
  username: string;
  role: string;
  telephone: string;
  address: string;
  bankAccount: string;
  monthlyHours: number;
  salary: number;
  shiftId: number | null;
  payrollIds: number[];
}

// — Payroll —
export interface PayrollRequest {
  month: string;
  year: string;
  paid: boolean;
  amount: number;
  employeeId: number;
}
export interface PayrollGenerateRequest {
  employeeId: number;
  month: string;
  year: string;
}
export interface PayrollResponse {
  id: number;
  month: string;
  year: string;
  paid: boolean;
  amount: number;
  employeeId: number;
}

// — Shift / TimeBlock —
export interface TimeBlockRequest { start: string; end: string; }
export interface TimeBlockResponse { start: string; end: string; }
export interface ShiftRequest {
  monday?: TimeBlockRequest[];
  tuesday?: TimeBlockRequest[];
  wednesday?: TimeBlockRequest[];
  thursday?: TimeBlockRequest[];
  friday?: TimeBlockRequest[];
  saturday?: TimeBlockRequest[];
  sunday?: TimeBlockRequest[];
}
export interface ShiftResponse {
  id: number;
  monday: TimeBlockResponse[];
  tuesday: TimeBlockResponse[];
  wednesday: TimeBlockResponse[];
  thursday: TimeBlockResponse[];
  friday: TimeBlockResponse[];
  saturday: TimeBlockResponse[];
  sunday: TimeBlockResponse[];
}

// — Admin —
export interface AdminRequest {
  name: string;
  username: string;
  password: string;
  telephone: string;
  address: string;
  bankAccount: string;
  permissions: string;
}
export interface AdminResponse {
  id: number;
  name: string;
  username: string;
  role: string;
  telephone: string;
  address: string;
  bankAccount: string;
  permissions: string;
}

// — Error envelope (GlobalExceptionHandler.ApiError) —
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string> | null;
}
