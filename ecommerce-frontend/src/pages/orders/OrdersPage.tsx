import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
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

  const columns: Column<OrderResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "userId", header: "Usuario ID" },
    { key: "status", header: "Estado", render: (item) => <StatusBadge status={item.status} /> },
    {
      key: "totalAmount",
      header: "Total",
      render: (item) => <span className="font-semibold">{formatCurrency(item.totalAmount, item.currency)}</span>,
    },
    { key: "currency", header: "Moneda" },
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
        title="Pedidos"
        description="Gestiona los pedidos realizados en la tienda"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Pedido
          </Button>
        }
      />

      <DataTable<OrderResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay pedidos registrados"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Pedido" : "Nuevo Pedido"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <Label>Usuario ID *</Label>
                <Input type="number" {...register("userId", { required: "Campo requerido" })} placeholder="1" />
                {errors.userId && <p className="text-xs text-destructive">{errors.userId.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Estado *</Label>
                <Select value={statusVal} onValueChange={(v) => setStatusVal(v as OrderStatus)}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {orderStatuses.map((s) => (
                      <SelectItem key={s} value={s}>{s}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1">
                <Label>Total *</Label>
                <Input type="number" step="0.01" {...register("totalAmount", { required: "Campo requerido" })} placeholder="0" />
                {errors.totalAmount && <p className="text-xs text-destructive">{errors.totalAmount.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Moneda *</Label>
                <Input {...register("currency", { required: "Campo requerido" })} placeholder="COP" />
                {errors.currency && <p className="text-xs text-destructive">{errors.currency.message}</p>}
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
        title="Eliminar Pedido"
        description="¿Estás seguro de que deseas eliminar este pedido?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
