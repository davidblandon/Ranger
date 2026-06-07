import { useState } from "react";
import { ordersApi } from "../api/orders";
import { partnersApi } from "../api/partners";
import { productsApi } from "../api/products";
import type { OrderRequest, OrderResponse, OrderState } from "../api/types";
import { ORDER_STATES } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import StatusBadge from "../components/StatusBadge";
import { SelectField, DateField, QuantityMapField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";
import { formatMoney, formatDate, todayIso } from "../lib/format";
import { nameMap, toOptions, resolveName } from "../lib/lookup";

const STATE_LABELS: Record<OrderState, string> = {
  PENDING: "Pending",
  IN_PRODUCTION: "In production",
  COMPLETED: "Completed",
  CANCELLED: "Cancelled",
};

export default function OrdersPage() {
  const { data, loading, error, reload } = useResource(() => ordersApi.list());
  const partners = useResource(() => partnersApi.list());
  const products = useResource(() => productsApi.list());
  const [editing, setEditing] = useState<OrderResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<OrderResponse | null>(null);
  const action = useAsyncAction();

  const orders = data ?? [];
  const partnerNames = nameMap(partners.data, (p) => p.name);

  const columns: Column<OrderResponse>[] = [
    { key: "id", header: "Order", numeric: true, render: (o) => <span className="cell-strong">#{o.id}</span> },
    { key: "date", header: "Date", render: (o) => formatDate(o.date) },
    { key: "partner", header: "Partner", render: (o) => resolveName(partnerNames, o.partnerId) },
    {
      key: "products",
      header: "Items",
      render: (o) => {
        const count = Object.keys(o.products).length;
        return count ? (
          <span className="tag tag--count">{count}</span>
        ) : (
          <span className="cell-muted">0</span>
        );
      },
    },
    { key: "state", header: "State", render: (o) => <StatusBadge state={o.state} /> },
    { key: "price", header: "Price", numeric: true, render: (o) => formatMoney(o.price) },
    {
      key: "benefice",
      header: "Benefice",
      numeric: true,
      render: (o) => (
        <span style={{ color: o.benefice >= 0 ? "var(--pine)" : "var(--clay)" }}>
          {formatMoney(o.benefice)}
        </span>
      ),
    },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => ordersApi.remove(toDelete.id));
    if (ok !== undefined) {
      setToDelete(null);
      reload();
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Sales"
        title="Orders"
        count={orders.length}
        actions={<Button onClick={() => setCreating(true)}>+ New Order</Button>}
      />

      {(loading || partners.loading || products.loading) && <Loading />}
      {error && <ErrorState message={error} onRetry={reload} />}
      {data && orders.length === 0 && (
        <EmptyState
          title="No orders yet"
          message="Create an order from a partner and the products they purchased."
          action={<Button onClick={() => setCreating(true)}>+ New Order</Button>}
        />
      )}
      {data && orders.length > 0 && (
        <DataTable
          columns={columns}
          rows={orders}
          rowKey={(o) => o.id}
          onRowClick={(o) => setEditing(o)}
          actions={(o) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(o)}>
                Edit
              </Button>
              <Button variant="subtle" small onClick={() => setToDelete(o)}>
                Delete
              </Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <OrderForm
          initial={editing}
          partnerOptions={toOptions(partners.data, (p) => p.name)}
          productOptions={toOptions(products.data, (p) => `${p.name} · ${formatMoney(p.price)}`)}
          onClose={() => {
            setCreating(false);
            setEditing(null);
          }}
          onSaved={() => {
            setCreating(false);
            setEditing(null);
            reload();
          }}
        />
      )}

      <ConfirmDialog
        open={!!toDelete}
        title="Delete order?"
        message={`Order #${toDelete?.id} will be permanently removed. Completed orders restore product stock on deletion.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function OrderForm({
  initial,
  partnerOptions,
  productOptions,
  onClose,
  onSaved,
}: {
  initial: OrderResponse | null;
  partnerOptions: { value: number | string; label: string }[];
  productOptions: { value: number | string; label: string }[];
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<OrderRequest>(
    initial
      ? {
          date: initial.date ?? todayIso(),
          partnerId: initial.partnerId,
          products: { ...initial.products },
          state: initial.state,
        }
      : { date: todayIso(), partnerId: 0, products: {}, state: "PENDING" }
  );
  const action = useAsyncAction();
  const set = <K extends keyof OrderRequest>(k: K, v: OrderRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const payload: OrderRequest = {
      date: form.date || null,
      partnerId: form.partnerId,
      products: form.products,
      state: form.state,
    };
    const result = await action.run(() =>
      initial ? ordersApi.update(initial.id, payload) : ordersApi.create(payload)
    );
    if (result) onSaved();
  }

  const fe = action.fieldErrors ?? {};

  return (
    <Drawer
      open
      title={initial ? `Edit Order #${initial.id}` : "New Order"}
      subtitle="Order"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create order"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}

      {initial && (
        <div className="readout">
          <div className="readout__cell">
            <p className="eyebrow">Price</p>
            <div className="readout__val">{formatMoney(initial.price)}</div>
          </div>
          <div className="readout__cell">
            <p className="eyebrow">Benefice</p>
            <div className="readout__val" style={{ color: "var(--pine)" }}>
              {formatMoney(initial.benefice)}
            </div>
          </div>
        </div>
      )}

      <DateField
        label="Date"
        value={form.date ?? ""}
        onChange={(v) => set("date", v)}
        error={fe.date}
      />
      <SelectField
        label="Partner"
        required
        value={form.partnerId || null}
        onChange={(v) => set("partnerId", v ? Number(v) : 0)}
        options={partnerOptions}
        placeholder="Select a partner"
        error={fe.partnerId}
      />
      <SelectField
        label="State"
        value={form.state ?? "PENDING"}
        onChange={(v) => set("state", v as OrderState)}
        options={ORDER_STATES.map((s) => ({ value: s, label: STATE_LABELS[s] }))}
        placeholder="Select state"
        error={fe.state}
        hint="Completing an order consumes product stock; insufficient stock is rejected."
      />
      <QuantityMapField
        label="Products"
        required
        value={form.products}
        onChange={(products) => set("products", products)}
        options={productOptions}
        emptyHint="Create products first."
        error={fe.products as string | undefined}
        hint="Price and benefice are computed from selected products × quantity."
      />
    </Drawer>
  );
}
