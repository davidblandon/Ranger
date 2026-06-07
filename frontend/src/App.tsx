import { Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./components/AppShell";
import DashboardPage from "./pages/DashboardPage";
import OrdersPage from "./pages/OrdersPage";
import ProductsPage from "./pages/ProductsPage";
import MaterialsPage from "./pages/MaterialsPage";
import BatchesPage from "./pages/BatchesPage";
import PartnersPage from "./pages/PartnersPage";
import { RhAuthProvider, RhGuard } from "./context/RhAuthContext";
import HrLoginPage from "./pages/HrLoginPage";
import EmployeesPage from "./pages/EmployeesPage";
import PayrollsPage from "./pages/PayrollsPage";
import ShiftsPage from "./pages/ShiftsPage";
import AdminsPage from "./pages/AdminsPage";

export default function App() {
  return (
    // RhAuthProvider at the app level so auth state persists across route changes.
    <RhAuthProvider>
      <AppShell>
        <Routes>
          {/* Production routes */}
          <Route path="/" element={<DashboardPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/materials" element={<MaterialsPage />} />
          <Route path="/batches" element={<BatchesPage />} />
          <Route path="/partners" element={<PartnersPage />} />

          {/* HR login — public, no auth guard */}
          <Route path="/hr/login" element={<HrLoginPage />} />

          {/* HR authenticated routes — RhGuard redirects to /hr/login if not logged in */}
          <Route path="/hr" element={<RhGuard />}>
            <Route index element={<Navigate to="/hr/employees" replace />} />
            <Route path="employees" element={<EmployeesPage />} />
            <Route path="payrolls"  element={<PayrollsPage />} />
            <Route path="shifts"    element={<ShiftsPage />} />
            <Route path="admins"    element={<AdminsPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AppShell>
    </RhAuthProvider>
  );
}
