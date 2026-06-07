import "./DataTable.css";

export interface Column<T> {
  key: string;
  header: string;
  /** Render the cell. */
  render: (row: T) => React.ReactNode;
  align?: "left" | "right" | "center";
  /** Use mono/tabular figures (numbers, money, ids). */
  numeric?: boolean;
  width?: string;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  onRowClick?: (row: T) => void;
  actions?: (row: T) => React.ReactNode;
}

export default function DataTable<T>({
  columns,
  rows,
  rowKey,
  onRowClick,
  actions,
}: DataTableProps<T>) {
  return (
    <div className="table-wrap">
      <table className="table">
        <thead>
          <tr>
            {columns.map((c) => (
              <th
                key={c.key}
                style={{ textAlign: c.align ?? (c.numeric ? "right" : "left"), width: c.width }}
              >
                {c.header}
              </th>
            ))}
            {actions && <th className="table__actions-head" />}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr
              key={rowKey(row)}
              onClick={onRowClick ? () => onRowClick(row) : undefined}
              className={onRowClick ? "table__row--clickable" : undefined}
            >
              {columns.map((c) => (
                <td
                  key={c.key}
                  className={c.numeric ? "mono" : undefined}
                  style={{ textAlign: c.align ?? (c.numeric ? "right" : "left") }}
                >
                  {c.render(row)}
                </td>
              ))}
              {actions && (
                <td className="table__actions" onClick={(e) => e.stopPropagation()}>
                  <div className="table__actions-inner">{actions(row)}</div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
