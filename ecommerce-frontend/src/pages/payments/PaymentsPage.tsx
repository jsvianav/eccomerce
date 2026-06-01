import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, CreditCard } from "lucide-react"
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

const statusLabels: Record<PaymentStatus, string> = {
  SUCCEEDED: "Exitoso",
  FAILED: "Fallido",
}

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

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<PaymentResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
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
      key: "status",
      header: "Estado",
      render: (item) => <StatusBadge status={item.status} />,
    },
    {
      key: "providerRef",
      header: "Referencia",
      render: (item) => (
        <span className="font-mono text-[12px] text-foreground">{item.providerRef}</span>
      ),
    },
    {
      key: "idempotencyKey",
      header: "Clave",
      className: "max-w-[180px]",
      render: (item) => (
        <span
          className="font-mono text-[11px] text-muted-foreground truncate block max-w-[160px]"
          title={item.idempotencyKey}
        >
          {item.idempotencyKey}
        </span>
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
        title="Pagos"
        description="Consulta y gestiona los pagos procesados"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo pago
          </Button>
        }
      />

      <DataTable<PaymentResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay pagos registrados. Los pagos procesados aparecerán aquí."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo pago
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <CreditCard className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar pago" : "Nuevo pago"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label htmlFor="pay-order">Pedido ID</Label>
                <Input
                  id="pay-order"
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
                <Label htmlFor="pay-status">Estado</Label>
                <Select value={statusVal} onValueChange={(v) => setStatusVal(v as PaymentStatus)}>
                  <SelectTrigger id="pay-status">
                    <SelectValue displayMap={statusLabels} />
                  </SelectTrigger>
                  <SelectContent>
                    {paymentStatuses.map((s) => (
                      <SelectItem key={s} value={s}>{statusLabels[s]}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div>
              <Label htmlFor="pay-ref">Referencia del proveedor</Label>
              <Input
                id="pay-ref"
                {...register("providerRef", { required: "Campo requerido" })}
                placeholder="REF-001"
                aria-invalid={!!errors.providerRef}
              />
              {errors.providerRef && (
                <p className="mt-1 text-[11px] text-destructive">{errors.providerRef.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="pay-key">Clave de idempotencia</Label>
              <Input
                id="pay-key"
                {...register("idempotencyKey", { required: "Campo requerido" })}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                className="font-mono text-[12px]"
                aria-invalid={!!errors.idempotencyKey}
              />
              {errors.idempotencyKey && (
                <p className="mt-1 text-[11px] text-destructive">{errors.idempotencyKey.message}</p>
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
                {editItem ? "Guardar cambios" : "Registrar pago"}
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
        title="Eliminar pago"
        description="¿Estás seguro de que deseas eliminar este pago? Esta acción no se puede deshacer."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
