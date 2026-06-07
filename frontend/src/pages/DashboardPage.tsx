import { dashboardApi } from "../api/dashboard";
import type { OrderState } from "../api/types";
import { ORDER_STATES } from "../api/types";
import PageHeader from "../components/PageHeader";
import StatCard from "../components/StatCard";
import { Loading, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { formatMoney, formatNumber } from "../lib/format";
import "../components/page.css";

const STATE_META: Record<OrderState, { label: string; color: string }> = {
  PENDING: { label: "Pending", color: "var(--ochre)" },
  IN_PRODUCTION: { label: "In production", color: "var(--slate)" },
  COMPLETED: { label: "Completed", color: "var(--pine)" },
  CANCELLED: { label: "Cancelled", color: "var(--clay)" },
};

export default function DashboardPage() {
  const { data, loading, error, reload } = useResource(() => dashboardApi.get());

  return (
    <div className="page">
      <PageHeader eyebrow="Overview" title="Production Dashboard" />

      {loading && <Loading label="Gathering production metrics…" />}
      {error && <ErrorState message={error} onRetry={reload} />}

      {data && (
        <>
          <div className="grid-stats">
            <StatCard label="Open Orders" value={formatNumber(data.totalOrders)} accent="var(--ember)" />
            <StatCard
              label="Completed Revenue"
              value={formatMoney(data.completedRevenue)}
              accent="var(--pine)"
              sub="From completed orders"
            />
            <StatCard
              label="Completed Benefice"
              value={formatMoney(data.completedBenefice)}
              accent="var(--pine)"
              sub="Net margin earned"
            />
            <StatCard
              label="Out of Stock"
              value={formatNumber(data.outOfStockProducts)}
              accent={data.outOfStockProducts > 0 ? "var(--clay)" : "var(--ink-3)"}
              sub="Products at zero stock"
            />
          </div>

          <section className="panel dash-lower">
            <div className="dash-lower__orders">
              <p className="dash-lower__heading">
                Orders by State
                <span className="dash-lower__sub">{formatNumber(data.totalOrders)} total</span>
              </p>
              <OrdersByState ordersByState={data.ordersByState} total={data.totalOrders} />
            </div>

            <div className="dash-lower__divider" />

            <div className="dash-lower__inventory">
              <p className="dash-lower__heading">Inventory</p>
              <div className="tally">
                {(
                  [
                    { label: "Products",  value: data.totalProducts  },
                    { label: "Materials", value: data.totalMaterials },
                    { label: "Batches",   value: data.totalBatches   },
                    { label: "Partners",  value: data.totalPartners  },
                  ] as const
                ).map(({ label, value }) => (
                  <div className="tally__item" key={label}>
                    <span className="tally__label">{label}</span>
                    <span className="tally__value">{formatNumber(value)}</span>
                  </div>
                ))}
              </div>
            </div>
          </section>
        </>
      )}
    </div>
  );
}

function OrdersByState({
  ordersByState,
  total,
}: {
  ordersByState: Partial<Record<OrderState, number>>;
  total: number;
}) {
  if (total === 0) {
    return <p className="state__msg">No orders recorded yet.</p>;
  }
  return (
    <div className="statebar">
      {ORDER_STATES.map((state) => {
        const count = ordersByState[state] ?? 0;
        const pct = total > 0 ? Math.round((count / total) * 100) : 0;
        const meta = STATE_META[state];
        return (
          <div className="statebar__row" key={state}>
            <span style={{ fontSize: "var(--text-sm)", color: "var(--ink-2)" }}>{meta.label}</span>
            <div className="statebar__track">
              <div
                className="statebar__fill"
                style={{ width: `${pct}%`, background: meta.color }}
              />
            </div>
            <span className="statebar__num">{count}</span>
          </div>
        );
      })}
    </div>
  );
}
