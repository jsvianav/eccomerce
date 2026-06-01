import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, Warehouse } from "lucide-react"
import { useInventory, useCreateInventory, useUpdateInventory, useDeleteInventory } from "@/hooks/useInventory"
import type { InventoryResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"

type FormValues = { productId: string; stock: string }

export default function InventoryPage() {
  const { data = [], isLoading } = useInventory()
  const createMut = useCreateInventory()
  const updateMut = useUpdateInventory()
  const deleteMut = useDeleteInventory()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<InventoryResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    reset({ productId: "", stock: "0" })
    setDialogOpen(true)
  }

  const openEdit = (item: InventoryResponse) => {
    setEditItem(item)
    reset({ productId: String(item.productId), stock: String(item.stock) })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = { productId: Number(values.productId), stock: Number(values.stock) }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<InventoryResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "productId",
      header: "Producto",
      render: (item) => (
        <span className="text-[13px] text-muted-foreground">#{item.productId}</span>
      ),
    },
    {
      key: "stock",
      header: "Stock",
      render: (item) => (
        <div className="flex items-center gap-2">
          <span className="tabular-nums font-semibold text-[13px] text-foreground">
            {item.stock}
          </span>
          {item.stock === 0 && (
            <Badge variant="destructive">Sin stock</Badge>
          )}
          {item.stock > 0 && item.stock < 10 && (
            <Badge variant="warning">Stock bajo</Badge>
          )}
          {item.stock >= 10 && (
            <Badge variant="success">Disponible</Badge>
          )}
        </div>
      ),
    },
    {
      key: "actions",
      header: "",
      className: "w-20 text-right",
      render: (item) => (
        <div className="flex items-center justify-end gap-1">
          <Button
            variant="ghost"
            size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-foreground"
            onClick={() => openEdit(item)}
            title="Editar"
          >
            <Pencil className="h-3.5 w-3.5" strokeWidth={2} />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-destructive"
            onClick={() => setDeleteId(item.id)}
            title="Eliminar"
          >
            <Trash2 className="h-3.5 w-3.5" strokeWidth={2} />
          </Button>
        </div>
      ),
    },
  ]

  return (
    <div>
      <PageHeader
        title="Inventario"
        description="Controla el stock disponible por producto"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo registro
          </Button>
        }
      />

      <DataTable<InventoryResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay registros de inventario. Añade el stock inicial de tus productos para empezar."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo registro
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <Warehouse className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar inventario" : "Nuevo registro de inventario"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div>
              <Label htmlFor="inv-product">Producto ID</Label>
              <Input
                id="inv-product"
                type="number"
                {...register("productId", { required: "Campo requerido" })}
                placeholder="1"
                aria-invalid={!!errors.productId}
              />
              {errors.productId && (
                <p className="mt-1 text-[11px] text-destructive">{errors.productId.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="inv-stock">Unidades en stock</Label>
              <Input
                id="inv-stock"
                type="number"
                min="0"
                {...register("stock", { required: "Campo requerido", min: { value: 0, message: "El stock no puede ser negativo" } })}
                placeholder="0"
                aria-invalid={!!errors.stock}
              />
              {errors.stock && (
                <p className="mt-1 text-[11px] text-destructive">{errors.stock.message}</p>
              )}
            </div>

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => setDialogOpen(false)}
                disabled={isMutating}
              >
                Cancelar
              </Button>
              <Button type="submit" size="sm" loading={isMutating}>
                {editItem ? "Guardar cambios" : "Crear registro"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* ── Delete confirm ── */}
      <ConfirmDialog
        open={deleteId !== null}
        onOpenChange={(v) => !v && setDeleteId(null)}
        onConfirm={() => {
          if (deleteId !== null) deleteMut.mutate(deleteId, { onSuccess: () => setDeleteId(null) })
        }}
        title="Eliminar registro de inventario"
        description="¿Estás seguro de que deseas eliminar este registro? El stock del producto quedará sin referencia."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
