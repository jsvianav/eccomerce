import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, TrendingUp, TrendingDown, BookmarkCheck, BookmarkX } from "lucide-react"
import { useInventoryMovements, useCreateInventoryMovement, useUpdateInventoryMovement, useDeleteInventoryMovement } from "@/hooks/useInventoryMovements"
import type { InventoryMovementResponse, MovementType } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select"
import type { ReactNode } from "react"

const movementTypes: MovementType[] = ["DEBIT", "CREDIT", "RESERVE", "RELEASE"]

const typeIconMap: Record<MovementType, ReactNode> = {
  DEBIT: <TrendingDown className="h-4 w-4 text-red-500" />,
  CREDIT: <TrendingUp className="h-4 w-4 text-green-500" />,
  RESERVE: <BookmarkCheck className="h-4 w-4 text-blue-500" />,
  RELEASE: <BookmarkX className="h-4 w-4 text-gray-500" />,
}

type FormValues = { productId: string; orderId: string; qty: string }

export default function InventoryMovementsPage() {
  const { data = [], isLoading } = useInventoryMovements()
  const createMut = useCreateInventoryMovement()
  const updateMut = useUpdateInventoryMovement()
  const deleteMut = useDeleteInventoryMovement()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<InventoryMovementResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [typeVal, setTypeVal] = useState<MovementType>("DEBIT")

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setTypeVal("DEBIT")
    reset({ productId: "", orderId: "", qty: "1" })
    setDialogOpen(true)
  }

  const openEdit = (item: InventoryMovementResponse) => {
    setEditItem(item)
    setTypeVal(item.type)
    reset({ productId: String(item.productId), orderId: String(item.orderId), qty: String(item.qty) })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = {
      productId: Number(values.productId),
      orderId: Number(values.orderId),
      type: typeVal,
      qty: Number(values.qty),
    }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const columns: Column<InventoryMovementResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "productId", header: "Producto ID" },
    { key: "orderId", header: "Pedido ID" },
    {
      key: "type",
      header: "Tipo",
      render: (item) => (
        <div className="flex items-center gap-2">
          {typeIconMap[item.type]}
          <span>{item.type}</span>
        </div>
      ),
    },
    { key: "qty", header: "Cantidad" },
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
        title="Movimientos de Inventario"
        description="Registra y consulta los movimientos de stock"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Movimiento
          </Button>
        }
      />

      <DataTable<InventoryMovementResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay movimientos registrados"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Movimiento" : "Nuevo Movimiento"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <Label>Producto ID *</Label>
                <Input type="number" {...register("productId", { required: "Campo requerido" })} placeholder="1" />
                {errors.productId && <p className="text-xs text-destructive">{errors.productId.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Pedido ID *</Label>
                <Input type="number" {...register("orderId", { required: "Campo requerido" })} placeholder="1" />
                {errors.orderId && <p className="text-xs text-destructive">{errors.orderId.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Tipo *</Label>
                <Select value={typeVal} onValueChange={(v) => setTypeVal(v as MovementType)}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {movementTypes.map((t) => (
                      <SelectItem key={t} value={t}>{t}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1">
                <Label>Cantidad *</Label>
                <Input type="number" {...register("qty", { required: "Campo requerido" })} placeholder="1" />
                {errors.qty && <p className="text-xs text-destructive">{errors.qty.message}</p>}
              </div>
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
        title="Eliminar Movimiento"
        description="¿Estás seguro de que deseas eliminar este movimiento?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
