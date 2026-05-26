import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, Package } from "lucide-react"
import {
  useProducts,
  useCreateProduct,
  useUpdateProduct,
  useDeleteProduct,
} from "@/hooks/useProducts"
import type { ProductResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { StatusBadge } from "@/components/StatusBadge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"
import { formatCurrency } from "@/lib/utils"

type FormValues = {
  name: string
  description: string
  price: string
  available: boolean
}

export default function ProductsPage() {
  const { data = [], isLoading } = useProducts()
  const createMut = useCreateProduct()
  const updateMut = useUpdateProduct()
  const deleteMut = useDeleteProduct()

  const [dialogOpen, setDialogOpen]   = useState(false)
  const [editItem, setEditItem]       = useState<ProductResponse | null>(null)
  const [deleteId, setDeleteId]       = useState<number | null>(null)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    reset({ name: "", description: "", price: "", available: true })
    setDialogOpen(true)
  }

  const openEdit = (item: ProductResponse) => {
    setEditItem(item)
    reset({
      name: item.name,
      description: item.description,
      price: String(item.price),
      available: item.available,
    })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = {
      name: values.name,
      description: values.description,
      price: parseFloat(values.price),
      available: values.available,
    }
    if (editItem) {
      updateMut.mutate(
        { id: editItem.id, data: payload },
        { onSuccess: () => setDialogOpen(false) }
      )
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const columns: Column<ProductResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14 text-muted-foreground",
      render: (item) => (
        <span className="tabular text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "name",
      header: "Nombre",
      render: (item) => (
        <span className="font-medium text-foreground">{item.name}</span>
      ),
    },
    {
      key: "description",
      header: "Descripción",
      className: "max-w-[200px]",
      render: (item) => (
        <span className="truncate block text-[12px] text-muted-foreground max-w-[200px]">
          {item.description}
        </span>
      ),
    },
    {
      key: "price",
      header: "Precio",
      render: (item) => (
        <span className="font-semibold tabular text-foreground">
          {formatCurrency(item.price)}
        </span>
      ),
    },
    {
      key: "available",
      header: "Estado",
      render: (item) => (
        <StatusBadge status={item.available ? "ACTIVE" : "CANCELLED"} />
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

  const isMutating = createMut.isPending || updateMut.isPending

  return (
    <div>
      <PageHeader
        title="Productos"
        description="Catálogo de productos de la tienda"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo producto
          </Button>
        }
      />

      <DataTable<ProductResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="Agrega el primer producto para comenzar a vender."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Crear producto
          </Button>
        }
      />

      {/* ─── Create / Edit dialog ─── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            {/* Icon + title (minimalist-ui §5: compact icon treatment) */}
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <Package className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar producto" : "Nuevo producto"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            {/* Name */}
            <div>
              <Label htmlFor="name">Nombre</Label>
              <Input
                id="name"
                placeholder="Ej. Camiseta de algodón"
                {...register("name", { required: "Campo requerido" })}
                aria-invalid={!!errors.name}
              />
              {errors.name && (
                <p className="mt-1 text-[11px] text-destructive">{errors.name.message}</p>
              )}
            </div>

            {/* Description */}
            <div>
              <Label htmlFor="description">Descripción</Label>
              <Input
                id="description"
                placeholder="Breve descripción del producto"
                {...register("description", { required: "Campo requerido" })}
                aria-invalid={!!errors.description}
              />
              {errors.description && (
                <p className="mt-1 text-[11px] text-destructive">{errors.description.message}</p>
              )}
            </div>

            {/* Price */}
            <div>
              <Label htmlFor="price">Precio (COP)</Label>
              <Input
                id="price"
                type="number"
                step="100"
                min="0"
                placeholder="0"
                className="tabular"
                {...register("price", {
                  required: "Campo requerido",
                  min: { value: 0, message: "Debe ser mayor o igual a 0" },
                })}
                aria-invalid={!!errors.price}
              />
              {errors.price && (
                <p className="mt-1 text-[11px] text-destructive">{errors.price.message}</p>
              )}
            </div>

            {/* Available toggle */}
            <div className="flex items-center justify-between rounded-lg border border-border bg-muted/30 px-3 py-2.5">
              <div>
                <p className="text-[13px] font-medium text-foreground">Disponible para venta</p>
                <p className="text-[11px] text-muted-foreground">
                  Visible y comprable en la tienda
                </p>
              </div>
              <label className="relative inline-flex cursor-pointer items-center">
                <input
                  type="checkbox"
                  className="peer sr-only"
                  {...register("available")}
                />
                <div className={`
                  h-5 w-9 rounded-full bg-muted ring-1 ring-border transition-colors duration-200
                  peer-checked:bg-primary peer-checked:ring-primary/50
                  after:absolute after:left-0.5 after:top-0.5 after:h-4 after:w-4
                  after:rounded-full after:bg-white after:shadow-sm
                  after:transition-transform after:duration-200
                  peer-checked:after:translate-x-4
                `} />
              </label>
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
                {editItem ? "Guardar cambios" : "Crear producto"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* ─── Delete confirm ─── */}
      <ConfirmDialog
        open={deleteId !== null}
        onOpenChange={(v) => !v && setDeleteId(null)}
        onConfirm={() => {
          if (deleteId !== null) {
            deleteMut.mutate(deleteId, { onSuccess: () => setDeleteId(null) })
          }
        }}
        title="Eliminar producto"
        description="Esta acción es permanente. El producto se eliminará junto con todos sus datos asociados."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
