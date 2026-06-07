import type { Option } from "../components/fields";

/** Build an id -> name map from a list of records. */
export function nameMap<T extends { id: number }>(
  items: T[] | null | undefined,
  label: (item: T) => string
): Map<number, string> {
  const map = new Map<number, string>();
  (items ?? []).forEach((item) => map.set(item.id, label(item)));
  return map;
}

/** Build <Select>/<MultiSelect> options from a list of records. */
export function toOptions<T extends { id: number }>(
  items: T[] | null | undefined,
  label: (item: T) => string
): Option[] {
  return (items ?? []).map((item) => ({ value: item.id, label: label(item) }));
}

/** Resolve a name from a map, with a graceful fallback for missing ids. */
export const resolveName = (map: Map<number, string>, id: number | null | undefined): string =>
  id == null ? "—" : map.get(id) ?? `#${id}`;
