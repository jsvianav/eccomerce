import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, ClipboardList } from "lucide-react"
import { useOrders, useCreateOrder, useUpdateOrder, useDeleteOrder } from "@/hooks/useOrders"
import type { OrderResponse, OrderStatus } from "@/types"
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
import { formatCurrency } from "@/lib/utils"

const orderStatuses: OrderStatus[] = ["CREATED", "PAID", "CANCELLED"]

const statusLabels: Record<OrderStatus, string> = {
  CREATED: "Creado",
  PAID: "Pagado",
  CANCELLED: "Cancelado",
}

type FormValues = { userId: string; totalAmount: string; currency: string }

export default function OrdersPage() {
  const { data = [], isLoading } = useOrders()
  const createMut = useCreateOrder()
  const updateMut = useUpdateOrder()
  const deleteMut = useDeleteOrder()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<OrderResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [statusVal, setStatusVal] = useState<OrderStatus>("CREATED")

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setStatusVal("CREATED")
    reset({ userId: "", totalAmount: "0", currency: "COP" })
    setDialogOpen(true)
  }

  const openEdit = (item: OrderResponse) => {
    setEditItem(item)
    setStatusVal(item.status)
    reset({ userId: String(item.userId), totalAmount: String(item.totalAmount), currency: item.currency })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = {
      userId: Number(values.userId),
      status: statusVal,
      totalAmount: parseFloat(values.totalAmount),
      currency: values.currency,
    }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<OrderResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "userId",
      header: "Usuario",
      render: (item) => (
        <span className="text-[13px] text-muted-foreground">#{item.userId}</span>
      ),
    },
    {
      key: "status",
      header: "Estado",
      render: (item) => <StatusBadge status={item.status} />,
    },
    {
      key: "totalAmount",
      header: "Total",
      render: (item) => (
        <div>
          <p className="tabular-nums font-semibold text-[13px] text-foreground">
            {formatCurrency(item.totalAmount, item.currency)}
          </p>
          <p className="text-[11px] text-muted-foreground">{item.currency}</p>
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
        title="Pedidos"
        description="Gestiona los pedidos realizados en la tienda"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo pedido
          </Button>
        }
      />

      <DataTable<OrderResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="Aún no hay pedidos. Cuando los clientes realicen compras, aparecerán aquí."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo pedido
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <ClipboardList className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar pedido" : "Nuevo pedido"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label htmlFor="order-user">Usuario ID</Label>
                <Input
                  id="order-user"
                  type="number"
                  {...register("userId", { required: "Campo requerido" })}
                  placeholder="1"
                  aria-invalid={!!errors.userId}
                />
                {errors.userId && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.userId.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="order-status">Estado</Label>
                <Select value={statusVal} onValueChange={(v) => setStatusVal(v as OrderStatus)}>
                  <SelectTrigger id="order-status">
                    <SelectValue displayMap={statusLabels} />
                  </SelectTrigger>
                  <SelectContent>
                    {orderStatuses.map((s) => (
                      <SelectItem key={s} value={s}>{statusLabels[s]}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="order-amount">Total</Label>
                <Input
                  id="order-amount"
                  type="number"
                  step="0.01"
                  {...register("totalAmount", { required: "Campo requerido" })}
                  placeholder="0.00"
                  aria-invalid={!!errors.totalAmount}
                />
                {errors.totalAmount && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.totalAmount.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="order-currency">Moneda</Label>
                <Input
                  id="order-currency"
                  {...register("currency", { required: "Campo requerido" })}
                  placeholder="COP"
                  aria-invalid={!!errors.currency}
                />
                {errors.currency && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.currency.message}</p>
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
                {editItem ? "Guardar cambios" : "Crear pedido"}
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
        title="Eliminar pedido"
        description="¿Estás seguro de que deseas eliminar este pedido? Esta acción no se puede deshacer."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
