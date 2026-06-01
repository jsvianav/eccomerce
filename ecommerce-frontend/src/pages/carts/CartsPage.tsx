import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, ShoppingCart } from "lucide-react"
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

const statusLabels: Record<CartStatus, string> = {
  ACTIVE: "Activo",
  CHECKED_OUT: "Procesado",
  ABANDONED: "Abandonado",
}

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

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<CartResponse>[] = [
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
        title="Carritos"
        description="Gestiona los carritos de compra de los usuarios"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo carrito
          </Button>
        }
      />

      <DataTable<CartResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay carritos activos. Los nuevos carritos aparecerán aquí en cuanto los usuarios empiecen a comprar."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo carrito
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <ShoppingCart className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar carrito" : "Nuevo carrito"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div>
              <Label htmlFor="cart-user">Usuario ID</Label>
              <Input
                id="cart-user"
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
              <Label htmlFor="cart-status">Estado</Label>
              <Select value={statusVal} onValueChange={(v) => setStatusVal(v as CartStatus)}>
                <SelectTrigger id="cart-status">
                  <SelectValue displayMap={statusLabels} />
                </SelectTrigger>
                <SelectContent>
                  {cartStatuses.map((s) => (
                    <SelectItem key={s} value={s}>{statusLabels[s]}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
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
                {editItem ? "Guardar cambios" : "Crear carrito"}
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
        title="Eliminar carrito"
        description="¿Estás seguro de que deseas eliminar este carrito? Se perderán todos los ítems asociados."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
