import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, FileText } from "lucide-react"
import { useDocumentTypes, useCreateDocumentType, useUpdateDocumentType, useDeleteDocumentType } from "@/hooks/useDocumentTypes"
import type { DocumentTypeResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"

type FormValues = { code: string; name: string }

export default function DocumentTypesPage() {
  const { data = [], isLoading } = useDocumentTypes()
  const createMut = useCreateDocumentType()
  const updateMut = useUpdateDocumentType()
  const deleteMut = useDeleteDocumentType()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<DocumentTypeResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    reset({ code: "", name: "" })
    setDialogOpen(true)
  }

  const openEdit = (item: DocumentTypeResponse) => {
    setEditItem(item)
    reset({ code: item.code, name: item.name })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: values }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(values, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<DocumentTypeResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "code",
      header: "Código",
      render: (item) => (
        <span className="font-mono text-[12px] font-semibold tracking-wide text-foreground bg-muted/60 rounded px-1.5 py-0.5">
          {item.code}
        </span>
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
        title="Tipos de Documento"
        description="Administra los tipos de documento de identidad"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo tipo
          </Button>
        }
      />

      <DataTable<DocumentTypeResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay tipos de documento configurados. Añade los primeros para poder registrar usuarios."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo tipo
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <FileText className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar tipo de documento" : "Nuevo tipo de documento"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
            <div>
              <Label htmlFor="dt-code">Código</Label>
              <Input
                id="dt-code"
                {...register("code", { required: "Campo requerido" })}
                placeholder="CC"
                className="font-mono uppercase"
                aria-invalid={!!errors.code}
              />
              {errors.code && (
                <p className="mt-1 text-[11px] text-destructive">{errors.code.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="dt-name">Nombre</Label>
              <Input
                id="dt-name"
                {...register("name", { required: "Campo requerido" })}
                placeholder="Cédula de Ciudadanía"
                aria-invalid={!!errors.name}
              />
              {errors.name && (
                <p className="mt-1 text-[11px] text-destructive">{errors.name.message}</p>
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
                {editItem ? "Guardar cambios" : "Crear tipo"}
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
        title="Eliminar tipo de documento"
        description="¿Estás seguro de que deseas eliminar este tipo de documento? Los usuarios que lo usen quedarán sin referencia."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
