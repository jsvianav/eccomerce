import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { useInventory, useCreateInventory, useUpdateInventory, useDeleteInventory } from "@/hooks/useInventory"
import type { InventoryResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { cn } from "@/lib/utils"

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

  const columns: Column<InventoryResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "productId", header: "Producto ID" },
    {
      key: "stock",
      header: "Stock",
      render: (item) => (
        <span className={cn("font-semibold", item.stock < 10 ? "text-destructive" : "text-green-600 dark:text-green-400")}>
          {item.stock}
          {item.stock < 10 && <span className="ml-1 text-xs font-normal">(bajo)</span>}
        </span>
      ),
    },
    {
      key: "actions",
      header: "Acciones",
      className: "text-right",
      render: (item) => (
        <div className="flex items-center justify-end gap-2">
          <Button variant="ghost" size="icon" onClick={() => openEdit(item)}>
            <Pencil className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => setDeleteId(item.id)}>
            <Trash2 className="h-4 w-4 text-destructive" />
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
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Registro
          </Button>
        }
      />

      <DataTable<InventoryResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay registros de inventario"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Inventario" : "Nuevo Registro de Inventario"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1">
              <Label>Producto ID *</Label>
              <Input type="number" {...register("productId", { required: "Campo requerido" })} placeholder="1" />
              {errors.productId && <p className="text-xs text-destructive">{errors.productId.message}</p>}
            </div>
            <div className="space-y-1">
              <Label>Stock *</Label>
              <Input type="number" {...register("stock", { required: "Campo requerido" })} placeholder="0" />
              {errors.stock && <p className="text-xs text-destructive">{errors.stock.message}</p>}
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setDialogOpen(false)}>Cancelar</Button>
              <Button type="submit" loading={createMut.isPending || updateMut.isPending}>
                {editItem ? "Guardar cambios" : "Crear"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={deleteId !== null}
        onOpenChange={(v) => !v && setDeleteId(null)}
        onConfirm={() => { if (deleteId !== null) deleteMut.mutate(deleteId, { onSuccess: () => setDeleteId(null) }) }}
        title="Eliminar Registro"
        description="¿Estás seguro de que deseas eliminar este registro de inventario?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
