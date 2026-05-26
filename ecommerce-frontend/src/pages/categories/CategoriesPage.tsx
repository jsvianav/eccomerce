import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { useCategories, useCreateCategory, useUpdateCategory, useDeleteCategory } from "@/hooks/useCategories"
import type { CategoryResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select"

type FormValues = { name: string }

export default function CategoriesPage() {
  const { data = [], isLoading } = useCategories()
  const createMut = useCreateCategory()
  const updateMut = useUpdateCategory()
  const deleteMut = useDeleteCategory()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<CategoryResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [parentVal, setParentVal] = useState("none")

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setParentVal("none")
    reset({ name: "" })
    setDialogOpen(true)
  }

  const openEdit = (item: CategoryResponse) => {
    setEditItem(item)
    setParentVal(item.parentId ? String(item.parentId) : "none")
    reset({ name: item.name })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = { name: values.name, parentId: parentVal === "none" ? null : Number(parentVal) }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const getParentName = (parentId: number | null) => {
    if (!parentId) return <span className="text-muted-foreground text-xs">—</span>
    const parent = data.find((c) => c.id === parentId)
    return parent?.name ?? `#${parentId}`
  }

  const columns: Column<CategoryResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "name", header: "Nombre" },
    { key: "parentId", header: "Categoría Padre", render: (item) => getParentName(item.parentId) },
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
        title="Categorías"
        description="Organiza los productos por categorías y subcategorías"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nueva Categoría
          </Button>
        }
      />

      <DataTable<CategoryResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay categorías registradas"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Categoría" : "Nueva Categoría"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1">
              <Label>Nombre *</Label>
              <Input {...register("name", { required: "Campo requerido" })} placeholder="Nombre de la categoría" />
              {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
            </div>
            <div className="space-y-1">
              <Label>Categoría Padre (opcional)</Label>
              <Select value={parentVal} onValueChange={(v) => setParentVal(v)}>
                <SelectTrigger>
                  <SelectValue placeholder="Sin categoría padre" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="none">Sin categoría padre</SelectItem>
                  {data.filter((c) => !editItem || c.id !== editItem.id).map((c) => (
                    <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>
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
        title="Eliminar Categoría"
        description="¿Estás seguro de que deseas eliminar esta categoría?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
