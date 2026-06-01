import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, Tag } from "lucide-react"
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

  // Build displayMap for parent select
  const parentMap = Object.fromEntries(
    data.map((c) => [String(c.id), c.name])
  )
  parentMap["none"] = "Sin categoría padre"

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<CategoryResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "name",
      header: "Nombre",
      render: (item) => (
        <p className="font-medium text-[13px] text-foreground">{item.name}</p>
      ),
    },
    {
      key: "parentId",
      header: "Categoría padre",
      render: (item) => {
        if (!item.parentId) return <span className="text-[12px] text-muted-foreground/50">—</span>
        const parent = data.find((c) => c.id === item.parentId)
        return (
          <span className="text-[12px] text-muted-foreground">
            {parent?.name ?? `#${item.parentId}`}
          </span>
        )
      },
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
        title="Categorías"
        description="Organiza los productos por categorías y subcategorías"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nueva categoría
          </Button>
        }
      />

      <DataTable<CategoryResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="Aún no hay categorías. Crea la primera para empezar a organizar tu catálogo."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nueva categoría
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <Tag className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar categoría" : "Nueva categoría"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div>
              <Label htmlFor="cat-name">Nombre</Label>
              <Input
                id="cat-name"
                {...register("name", { required: "Campo requerido" })}
                placeholder="Nombre de la categoría"
                aria-invalid={!!errors.name}
              />
              {errors.name && (
                <p className="mt-1 text-[11px] text-destructive">{errors.name.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="cat-parent">Categoría padre</Label>
              <Select value={parentVal} onValueChange={(v) => setParentVal(v)}>
                <SelectTrigger id="cat-parent">
                  <SelectValue displayMap={parentMap} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="none">Sin categoría padre</SelectItem>
                  {data
                    .filter((c) => !editItem || c.id !== editItem.id)
                    .map((c) => (
                      <SelectItem key={c.id} value={String(c.id)}>{c.name}</SelectItem>
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
                {editItem ? "Guardar cambios" : "Crear categoría"}
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
        title="Eliminar categoría"
        description="¿Estás seguro de que deseas eliminar esta categoría? Las subcategorías asociadas perderán su referencia padre."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
