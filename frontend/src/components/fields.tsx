import "./ui.css";

interface BaseProps {
  label: string;
  required?: boolean;
  error?: string;
  hint?: string;
}

function FieldWrap({
  label,
  required,
  error,
  hint,
  children,
}: BaseProps & { children: React.ReactNode }) {
  return (
    <label className={`field${error ? " field--error" : ""}`}>
      <span className="field__label">
        {label}
        {required && <span className="field__req">*</span>}
      </span>
      {children}
      {error ? (
        <span className="field__error">{error}</span>
      ) : (
        hint && <span className="field__hint">{hint}</span>
      )}
    </label>
  );
}

export function TextField({
  value,
  onChange,
  type = "text",
  placeholder,
  ...base
}: BaseProps & {
  value: string;
  onChange: (v: string) => void;
  type?: string;
  placeholder?: string;
}) {
  return (
    <FieldWrap {...base}>
      <input
        type={type}
        value={value}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
      />
    </FieldWrap>
  );
}

export function NumberField({
  value,
  onChange,
  min,
  step = "any",
  ...base
}: BaseProps & {
  value: number;
  onChange: (v: number) => void;
  min?: number;
  step?: string | number;
}) {
  return (
    <FieldWrap {...base}>
      <input
        type="number"
        value={Number.isNaN(value) ? "" : value}
        min={min}
        step={step}
        onChange={(e) => onChange(e.target.value === "" ? NaN : Number(e.target.value))}
      />
    </FieldWrap>
  );
}

export function DateField({
  value,
  onChange,
  ...base
}: BaseProps & { value: string; onChange: (v: string) => void }) {
  return (
    <FieldWrap {...base}>
      <input type="date" value={value} onChange={(e) => onChange(e.target.value)} />
    </FieldWrap>
  );
}

export interface Option {
  value: number | string;
  label: string;
}

export function SelectField({
  value,
  onChange,
  options,
  placeholder = "Select…",
  ...base
}: BaseProps & {
  value: number | string | null | undefined;
  onChange: (v: string) => void;
  options: Option[];
  placeholder?: string;
}) {
  return (
    <FieldWrap {...base}>
      <select value={value ?? ""} onChange={(e) => onChange(e.target.value)}>
        <option value="">{placeholder}</option>
        {options.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
    </FieldWrap>
  );
}

/** Checklist-style multi-select for id arrays (products on an order, etc.). */
export function MultiSelectField({
  values,
  onChange,
  options,
  emptyHint = "No options available.",
  ...base
}: BaseProps & {
  values: number[];
  onChange: (next: number[]) => void;
  options: Option[];
  emptyHint?: string;
}) {
  function toggle(id: number) {
    onChange(values.includes(id) ? values.filter((v) => v !== id) : [...values, id]);
  }
  return (
    <FieldWrap {...base}>
      <div className="checklist">
        {options.length === 0 ? (
          <p className="checklist__empty">{emptyHint}</p>
        ) : (
          options.map((o) => (
            <label key={o.value} className="checklist__item">
              <input
                type="checkbox"
                checked={values.includes(Number(o.value))}
                onChange={() => toggle(Number(o.value))}
              />
              {o.label}
            </label>
          ))
        )}
      </div>
    </FieldWrap>
  );
}

/** Checklist where each selected option carries a positive integer quantity (id → quantity). */
export function QuantityMapField({
  value,
  onChange,
  options,
  emptyHint = "No options available.",
  ...base
}: BaseProps & {
  value: Record<number, number>;
  onChange: (next: Record<number, number>) => void;
  options: Option[];
  emptyHint?: string;
}) {
  function toggle(id: number) {
    const next = { ...value };
    if (id in next) delete next[id];
    else next[id] = 1;
    onChange(next);
  }
  function setQty(id: number, qty: number) {
    onChange({ ...value, [id]: Number.isNaN(qty) ? 1 : Math.max(1, Math.floor(qty)) });
  }
  return (
    <FieldWrap {...base}>
      <div className="checklist">
        {options.length === 0 ? (
          <p className="checklist__empty">{emptyHint}</p>
        ) : (
          options.map((o) => {
            const id = Number(o.value);
            const selected = id in value;
            return (
              <div key={o.value} className="checklist__item checklist__item--qty">
                <label className="checklist__pick">
                  <input type="checkbox" checked={selected} onChange={() => toggle(id)} />
                  {o.label}
                </label>
                {selected && (
                  <input
                    type="number"
                    className="checklist__qty"
                    min={1}
                    step={1}
                    value={value[id]}
                    onChange={(e) =>
                      setQty(id, e.target.value === "" ? 1 : Number(e.target.value))
                    }
                  />
                )}
              </div>
            );
          })
        )}
      </div>
    </FieldWrap>
  );
}
