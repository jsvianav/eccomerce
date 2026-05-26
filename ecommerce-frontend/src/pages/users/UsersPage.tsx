import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from "@/hooks/useUsers"
import { useDocumentTypes } from "@/hooks/useDocumentTypes"
import type { UserResponse } from "@/types"
import { PageHeader } from "@/components/PageHeader"
import { DataTable } from "@/components/DataTable"
import type { Column } from "@/components/DataTable"
import { ConfirmDialog } from "@/components/ConfirmDialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select"

type FormValues = {
  fullName: string
  email: string
  phone: string
  documentNumber: string
  documentTypeId: string
  birthDate: string
  country: string
  address: string
}

export default function UsersPage() {
  const { data = [], isLoading } = useUsers()
  const { data: docTypes = [] } = useDocumentTypes()
  const createMut = useCreateUser()
  const updateMut = useUpdateUser()
  const deleteMut = useDeleteUser()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editItem, setEditItem] = useState<UserResponse | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [docTypeVal, setDocTypeVal] = useState("")

  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<FormValues>()

  const openCreate = () => {
    setEditItem(null)
    setDocTypeVal("")
    reset({ fullName: "", email: "", phone: "", documentNumber: "", documentTypeId: "", birthDate: "", country: "", address: "" })
    setDialogOpen(true)
  }

  const openEdit = (item: UserResponse) => {
    setEditItem(item)
    const dtId = String(item.documentTypeId)
    setDocTypeVal(dtId)
    reset({
      fullName: item.fullName,
      email: item.email,
      phone: "",
      documentNumber: item.documentNumber,
      documentTypeId: dtId,
      birthDate: "",
      country: "",
      address: "",
    })
    setDialogOpen(true)
  }

  const onSubmit = (values: FormValues) => {
    const payload = {
      fullName: values.fullName,
      email: values.email,
      phone: values.phone,
      documentNumber: values.documentNumber,
      documentTypeId: Number(values.documentTypeId),
      birthDate: values.birthDate,
      country: values.country,
      address: values.address,
    }
    if (editItem) {
      updateMut.mutate({ id: editItem.id, data: payload }, { onSuccess: () => setDialogOpen(false) })
    } else {
      createMut.mutate(payload, { onSuccess: () => setDialogOpen(false) })
    }
  }

  const columns: Column<UserResponse>[] = [
    { key: "id", header: "ID", className: "w-16" },
    { key: "fullName", header: "Nombre Completo" },
    { key: "email", header: "Email" },
    { key: "documentTypeName", header: "Tipo Doc." },
    { key: "documentNumber", header: "N° Documento" },
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
        title="Usuarios"
        description="Gestiona los usuarios registrados en la plataforma"
        action={
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo Usuario
          </Button>
        }
      />

      <DataTable<UserResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="No hay usuarios registrados"
      />

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editItem ? "Editar Usuario" : "Nuevo Usuario"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1 col-span-2">
                <Label>Nombre completo *</Label>
                <Input {...register("fullName", { required: "Campo requerido" })} placeholder="Juan Pérez" />
                {errors.fullName && <p className="text-xs text-destructive">{errors.fullName.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Email *</Label>
                <Input type="email" {...register("email", { required: "Campo requerido" })} placeholder="juan@email.com" />
                {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Teléfono *</Label>
                <Input {...register("phone", { required: "Campo requerido" })} placeholder="+57 300 000 0000" />
                {errors.phone && <p className="text-xs text-destructive">{errors.phone.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Tipo de Documento *</Label>
                <Select
                  value={docTypeVal}
                  onValueChange={(v) => { setDocTypeVal(v); setValue("documentTypeId", v) }}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccionar" />
                  </SelectTrigger>
                  <SelectContent>
                    {docTypes.map((dt) => (
                      <SelectItem key={dt.id} value={String(dt.id)}>{dt.name}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1">
                <Label>N° Documento *</Label>
                <Input {...register("documentNumber", { required: "Campo requerido" })} placeholder="1234567890" />
                {errors.documentNumber && <p className="text-xs text-destructive">{errors.documentNumber.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>Fecha de Nacimiento *</Label>
                <Input type="date" {...register("birthDate", { required: "Campo requerido" })} />
                {errors.birthDate && <p className="text-xs text-destructive">{errors.birthDate.message}</p>}
              </div>
              <div className="space-y-1">
                <Label>País *</Label>
                <Input {...register("country", { required: "Campo requerido" })} placeholder="Colombia" />
                {errors.country && <p className="text-xs text-destructive">{errors.country.message}</p>}
              </div>
              <div className="space-y-1 col-span-2">
                <Label>Dirección *</Label>
                <Input {...register("address", { required: "Campo requerido" })} placeholder="Calle 123 #45-67" />
                {errors.address && <p className="text-xs text-destructive">{errors.address.message}</p>}
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
        title="Eliminar Usuario"
        description="¿Estás seguro de que deseas eliminar este usuario?"
        loading={deleteMut.isPending}
      />
    </div>
  )
}
