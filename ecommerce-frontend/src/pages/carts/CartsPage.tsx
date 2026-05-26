import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { useCarts, useCreateCart, useUpdateCart, useDeleteCart } from "@/hooks/useCarts"
import type { CartResponse, CartStatus } from "@/types"
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

const cartStatuses: CartStatus[] = ["ACTIVE", "CHECKED_OUT", "ABANDONED"]

type FormValues = { userId: string }

export default function CartsPage() {
  const { data = [], isLoading } = useCarts()
  const createMut = useCreateCart()
  const updateMut = useUpdateCart()
  const deleteMut = useDeleteCart()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<CartResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [statusVal, setStatusVal] = useState<CartStatus>("ACTIVE")

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setStatusVal("ACTIVE")
    reset({ userId: "" })
    setDialogOpen(true)
  }

  const openEdit = (item: CartResponse) => {
    setEditItem(item)
    setStatusVal(item.status)
    reset({ userId: String(item.userId) })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = { userId: Number(values.userId), status: statusVal }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const columns: Column<CartResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "userId", header: "Usuario ID" },
    { key: "status", header: "Estado", render: (item) => <StatusBadge status={item.status} /> },
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
        title="Carritos"
        description="Gestiona los carritos de compra de los usuarios"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Carrito
          </Button>
        }
      />

      <DataTable<CartResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay carritos registrados"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Carrito" : "Nuevo Carrito"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1">
              <Label>Usuario ID *</Label>
              <Input type="number" {...register("userId", { required: "Campo requerido" })} placeholder="1" />
              {errors.userId && <p className="text-xs text-destructive">{errors.userId.message}</p>}
            </div>
            <div className="space-y-1">
              <Label>Estado *</Label>
              <Select value={statusVal} onValueChange={(v) => setStatusVal(v as CartStatus)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {cartStatuses.map((s) => (
                    <SelectItem key={s} value={s}>{s}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
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
        title="Eliminar Carrito"
        description="¿Estás seguro de que deseas eliminar este carrito?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
