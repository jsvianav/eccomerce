import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, ArrowLeftRight } from "lucide-react"
import { useInventoryMovements, useCreateInventoryMovement, useUpdateInventoryMovement, useDeleteInventoryMovement } from "@/hooks/useInventoryMovements"
import type { InventoryMovementResponse, MovementType } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { StatusBadge } from "@/components/StatusBadge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select"

const movementTypes: MovementType[] = ["DEBIT", "CREDIT", "RESERVE", "RELEASE"]

const typeLabels: Record<MovementType, string> = {
  DEBIT: "Débito",
  CREDIT: "Crédito",
  RESERVE: "Reserva",
  RELEASE: "Liberado",
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

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<InventoryMovementResponse>[] = [
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
      key: "orderId",
      header: "Pedido",
      render: (item) => (
        <span className="text-[13px] text-muted-foreground">#{item.orderId}</span>
      ),
    },
    {
      key: "type",
      header: "Tipo",
      render: (item) => <StatusBadge status={item.type} />,
    },
    {
      key: "qty",
      header: "Cantidad",
      render: (item) => (
        <span className="tabular-nums font-semibold text-[13px] text-foreground">{item.qty}</span>
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
        title="Movimientos de Inventario"
        description="Registra y consulta los movimientos de stock"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo movimiento
          </Button>
        }
      />

      <DataTable<InventoryMovementResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay movimientos registrados. Cada entrada o salida de stock quedará registrada aquí."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo movimiento
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <ArrowLeftRight className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar movimiento" : "Nuevo movimiento"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label htmlFor="mov-product">Producto ID</Label>
                <Input
                  id="mov-product"
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
                <Label htmlFor="mov-order">Pedido ID</Label>
                <Input
                  id="mov-order"
                  type="number"
                  {...register("orderId", { required: "Campo requerido" })}
                  placeholder="1"
                  aria-invalid={!!errors.orderId}
                />
                {errors.orderId && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.orderId.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="mov-type">Tipo</Label>
                <Select value={typeVal} onValueChange={(v) => setTypeVal(v as MovementType)}>
                  <SelectTrigger id="mov-type">
                    <SelectValue displayMap={typeLabels} />
                  </SelectTrigger>
                  <SelectContent>
                    {movementTypes.map((t) => (
                      <SelectItem key={t} value={t}>{typeLabels[t]}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="mov-qty">Cantidad</Label>
                <Input
                  id="mov-qty"
                  type="number"
                  min="1"
                  {...register("qty", { required: "Campo requerido", min: { value: 1, message: "Mínimo 1" } })}
                  placeholder="1"
                  aria-invalid={!!errors.qty}
                />
                {errors.qty && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.qty.message}</p>
                )}
              </div>
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
                {editItem ? "Guardar cambios" : "Registrar movimiento"}
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
        title="Eliminar movimiento"
        description="¿Estás seguro de que deseas eliminar este movimiento de inventario?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
