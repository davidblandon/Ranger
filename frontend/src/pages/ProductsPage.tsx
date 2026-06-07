import { useState } from "react";
import { productsApi } from "../api/products";
import { materialsApi } from "../api/materials";
import type { ProductRequest, ProductResponse } from "../api/types";
import PageHeader from "../components/PageHeader";
import DataTable, { type Column } from "../components/DataTable";
import Drawer from "../components/Drawer";
import Button from "../components/Button";
import ConfirmDialog from "../components/ConfirmDialog";
import { TextField, NumberField, MultiSelectField } from "../components/fields";
import { Loading, EmptyState, ErrorState } from "../components/States";
import { useResource } from "../hooks/useResource";
import { useAsyncAction } from "../hooks/useAsyncAction";
import { formatMoney } from "../lib/format";
import { nameMap, toOptions } from "../lib/lookup";

const EMPTY: ProductRequest = {
  name: "",
  type: "",
  size: "",
  color: "",
  materialIds: [],
  price: 0,
  cost: 0,
  stock: 0,
};

export default function ProductsPage() {
  const { data, loading, error, reload } = useResource(() => productsApi.list());
  const materials = useResource(() => materialsApi.list());
  const [editing, setEditing] = useState<ProductResponse | null>(null);
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState<ProductResponse | null>(null);
  const action = useAsyncAction();

  const products = data ?? [];
  const materialNames = nameMap(materials.data, (m) => m.name);

  const columns: Column<ProductResponse>[] = [
    { key: "name", header: "Product", render: (p) => <span className="cell-strong">{p.name}</span> },
    {
      key: "attrs",
      header: "Attributes",
      render: (p) => {
        const tags = [p.type, p.size, p.color].filter(Boolean) as string[];
        return tags.length ? (
          <span className="cell-tags">
            {tags.map((t) => (
              <span className="tag" key={t}>
                {t}
              </span>
            ))}
          </span>
        ) : (
          <span className="cell-muted">—</span>
        );
      },
    },
    {
      key: "materials",
      header: "Materials",
      render: (p) =>
        p.materialIds.length ? (
          <span className="tag tag--count">{p.materialIds.length}</span>
        ) : (
          <span className="cell-muted">none</span>
        ),
    },
    { key: "price", header: "Price", numeric: true, render: (p) => formatMoney(p.price) },
    { key: "cost", header: "Cost", numeric: true, render: (p) => formatMoney(p.cost) },
    {
      key: "stock",
      header: "Stock",
      numeric: true,
      render: (p) =>
        p.stock === 0 ? (
          <span className="badge badge--cancelled">
            <span className="badge__dot" />0
          </span>
        ) : (
          p.stock
        ),
    },
  ];

  async function handleDelete() {
    if (!toDelete) return;
    const ok = await action.run(() => productsApi.remove(toDelete.id));
    if (ok !== undefined) {
      setToDelete(null);
      reload();
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Catalogue"
        title="Products"
        count={products.length}
        actions={<Button onClick={() => setCreating(true)}>+ New Product</Button>}
      />

      {(loading || materials.loading) && <Loading />}
      {error && <ErrorState message={error} onRetry={reload} />}
      {data && products.length === 0 && (
        <EmptyState
          title="No products yet"
          message="Define the goods you manufacture and sell."
          action={<Button onClick={() => setCreating(true)}>+ New Product</Button>}
        />
      )}
      {data && products.length > 0 && (
        <DataTable
          columns={columns}
          rows={products}
          rowKey={(p) => p.id}
          onRowClick={(p) => setEditing(p)}
          actions={(p) => (
            <>
              <Button variant="subtle" small onClick={() => setEditing(p)}>
                Edit
              </Button>
              <Button variant="subtle" small onClick={() => setToDelete(p)}>
                Delete
              </Button>
            </>
          )}
        />
      )}

      {(creating || editing) && (
        <ProductForm
          initial={editing}
          materialOptions={toOptions(materials.data, (m) => m.name)}
          materialNames={materialNames}
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
        title="Delete product?"
        message={`“${toDelete?.name}” will be permanently removed.`}
        busy={action.submitting}
        onConfirm={handleDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}

function ProductForm({
  initial,
  materialOptions,
  materialNames,
  onClose,
  onSaved,
}: {
  initial: ProductResponse | null;
  materialOptions: { value: number | string; label: string }[];
  materialNames: Map<number, string>;
  onClose: () => void;
  onSaved: () => void;
}) {
  const [form, setForm] = useState<ProductRequest>(
    initial
      ? {
          name: initial.name,
          type: initial.type ?? "",
          size: initial.size ?? "",
          color: initial.color ?? "",
          materialIds: initial.materialIds,
          price: initial.price,
          cost: initial.cost,
          stock: initial.stock,
        }
      : EMPTY
  );
  const action = useAsyncAction();
  const set = <K extends keyof ProductRequest>(k: K, v: ProductRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  async function submit() {
    const payload: ProductRequest = {
      ...form,
      price: Number.isNaN(form.price) ? 0 : form.price,
      cost: Number.isNaN(form.cost) ? 0 : form.cost,
      stock: Number.isNaN(form.stock) ? 0 : form.stock,
    };
    const result = await action.run(() =>
      initial ? productsApi.update(initial.id, payload) : productsApi.create(payload)
    );
    if (result) onSaved();
  }

  const fe = action.fieldErrors ?? {};
  const margin = (form.price || 0) - (form.cost || 0);

  return (
    <Drawer
      open
      title={initial ? "Edit Product" : "New Product"}
      subtitle="Product"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={action.submitting}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={action.submitting}>
            {action.submitting ? "Saving…" : initial ? "Save changes" : "Create product"}
          </Button>
        </>
      }
    >
      {action.error && <div className="form-error">{action.error}</div>}
      <TextField
        label="Name"
        required
        value={form.name}
        onChange={(v) => set("name", v)}
        error={fe.name}
      />
      <div className="form-row">
        <TextField label="Type" value={form.type ?? ""} onChange={(v) => set("type", v)} />
        <TextField label="Size" value={form.size ?? ""} onChange={(v) => set("size", v)} />
      </div>
      <TextField label="Color" value={form.color ?? ""} onChange={(v) => set("color", v)} />
      <div className="form-row">
        <NumberField
          label="Price"
          min={0}
          value={form.price}
          onChange={(v) => set("price", v)}
          error={fe.price}
        />
        <NumberField
          label="Cost"
          min={0}
          value={form.cost}
          onChange={(v) => set("cost", v)}
          error={fe.cost}
        />
      </div>
      <NumberField
        label="Stock"
        min={0}
        step={1}
        value={form.stock}
        onChange={(v) => set("stock", v)}
        error={fe.stock}
        hint={`Unit margin: ${formatMoney(margin)}`}
      />
      <MultiSelectField
        label="Materials"
        values={form.materialIds}
        onChange={(ids) => set("materialIds", ids)}
        options={materialOptions}
        emptyHint="Create materials first to compose products."
        hint={
          form.materialIds.length
            ? form.materialIds.map((id) => materialNames.get(id) ?? `#${id}`).join(", ")
            : undefined
        }
      />
    </Drawer>
  );
}
