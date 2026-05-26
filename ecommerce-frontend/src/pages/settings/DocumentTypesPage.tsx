import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
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

  const columns: Column<DocumentTypeResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "code", header: "Código" },
    { key: "name", header: "Nombre" },
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
        title="Tipos de Documento"
        description="Administra los tipos de documento de identidad"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Tipo
          </Button>
        }
      />

      <DataTable<DocumentTypeResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay tipos de documento registrados"
        emptyAction={<Button onClick={openCreate} size="sm"><Plus className="h-4 w-4" />Nuevo Tipo</Button>}
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Tipo de Documento" : "Nuevo Tipo de Documento"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1">
              <Label>Código *</Label>
              <Input {...register("code", { required: "Campo requerido" })} placeholder="CC" />
              {errors.code && <p className="text-xs text-destructive">{errors.code.message}</p>}
            </div>
            <div className="space-y-1">
              <Label>Nombre *</Label>
              <Input {...register("name", { required: "Campo requerido" })} placeholder="Cédula de Ciudadanía" />
              {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
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
        title="Eliminar Tipo de Documento"
        description="¿Estás seguro de que deseas eliminar este tipo de documento?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
