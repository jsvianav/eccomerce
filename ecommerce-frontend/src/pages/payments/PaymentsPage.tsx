import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { usePayments, useCreatePayment, useUpdatePayment, useDeletePayment } from "@/hooks/usePayments"
import type { PaymentResponse, PaymentStatus } from "@/types"
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

const paymentStatuses: PaymentStatus[] = ["SUCCEEDED", "FAILED"]

type FormValues = { orderId: string; providerRef: string; idempotencyKey: string }

export default function PaymentsPage() {
  const { data = [], isLoading } = usePayments()
  const createMut = useCreatePayment()
  const updateMut = useUpdatePayment()
  const deleteMut = useDeletePayment()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<PaymentResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [statusVal, setStatusVal] = useState<PaymentStatus>("SUCCEEDED")

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setStatusVal("SUCCEEDED")
    reset({ orderId: "", providerRef: "", idempotencyKey: "" })
    setDialogOpen(true)
  }

  const openEdit = (item: PaymentResponse) => {
    setEditItem(item)
    setStatusVal(item.status)
    reset({ orderId: String(item.orderId), providerRef: item.providerRef, idempotencyKey: item.idempotencyKey })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = {
      orderId: Number(values.orderId),
      status: statusVal,
      providerRef: values.providerRef,
      idempotencyKey: values.idempotencyKey,
    }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const columns: Column<PaymentResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "orderId", header: "Pedido ID" },
    { key: "status", header: "Estado", render: (item) => <StatusBadge status={item.status} /> },
    { key: "providerRef", header: "Referencia Proveedor" },
    { key: "idempotencyKey", header: "Clave Idempotencia", className: "max-w-xs truncate" },
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
        title="Pagos"
        description="Consulta y gestiona los pagos procesados"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Pago
          </Button>
        }
      />

      <DataTable<PaymentResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay pagos registrados"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Pago" : "Nuevo Pago"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <Label>Pedido ID *</Label>
                <Input type="number" {...register("orderId", { required: "Campo requerido" })} placeholder="1" />
                {errors.orderId && <p className="text-xs text-destructive">{errors.orderId.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Estado *</Label>
                <Select value={statusVal} onValueChange={(v) => setStatusVal(v as PaymentStatus)}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {paymentStatuses.map((s) => (
                      <SelectItem key={s} value={s}>{s}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-1">
              <Label>Referencia Proveedor *</Label>
              <Input {...register("providerRef", { required: "Campo requerido" })} placeholder="REF-001" />
              {errors.providerRef && <p className="text-xs text-destructive">{errors.providerRef.message}</p>}
            </div>
            <div className="space-y-1">
              <Label>Clave de Idempotencia *</Label>
              <Input {...register("idempotencyKey", { required: "Campo requerido" })} placeholder="uuid-key" />
              {errors.idempotencyKey && <p className="text-xs text-destructive">{errors.idempotencyKey.message}</p>}
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
        title="Eliminar Pago"
        description="¿Estás seguro de que deseas eliminar este pago?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
