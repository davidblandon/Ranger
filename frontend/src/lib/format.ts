// Display formatters. Kept tiny and dependency-free.

const money = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
});

export const formatMoney = (value: number): string => money.format(value ?? 0);

export const formatNumber = (value: number): string =>
  new Intl.NumberFormat("en-US").format(value ?? 0);

/** yyyy-MM-dd -> "Jun 6, 2026"; passes through empty/invalid input. */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" });
}

/** Today as yyyy-MM-dd for date input defaults. */
export const todayIso = (): string => new Date().toISOString().slice(0, 10);
