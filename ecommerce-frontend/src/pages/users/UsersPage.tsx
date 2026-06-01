import { useState } from "react"
import { useForm } from "react-hook-form"
import { Plus, Pencil, Trash2, Users } from "lucide-react"
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

  // displayMap for document type select
  const docTypeMap = Object.fromEntries(docTypes.map((dt) => [String(dt.id), dt.name]))

  const isMutating = createMut.isPending || updateMut.isPending

  const columns: Column<UserResponse>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (item) => (
        <span className="tabular-nums text-[12px] text-muted-foreground">#{item.id}</span>
      ),
    },
    {
      key: "fullName",
      header: "Nombre",
      render: (item) => (
        <div>
          <p className="font-medium text-[13px] text-foreground">{item.fullName}</p>
          <p className="text-[11px] text-muted-foreground">{item.email}</p>
        </div>
      ),
    },
    {
      key: "documentTypeName",
      header: "Documento",
      render: (item) => (
        <div>
          <p className="text-[12px] text-muted-foreground">{item.documentTypeName}</p>
          <p className="tabular-nums font-medium text-[12px] text-foreground">{item.documentNumber}</p>
        </div>
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
        title="Usuarios"
        description="Gestiona los usuarios registrados en la plataforma"
        action={
          <Button onClick={openCreate} size="sm">
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo usuario
          </Button>
        }
      />

      <DataTable<UserResponse>
        data={data}
        columns={columns}
        loading={isLoading}
        emptyMessage="Aún no hay usuarios registrados. Añade el primero para comenzar."
        emptyAction={
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-3.5 w-3.5" strokeWidth={2.5} />
            Nuevo usuario
          </Button>
        }
      />

      {/* ── Create / Edit dialog ── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <div className="flex items-center gap-2.5">
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-primary/10 ring-1 ring-primary/20">
                <Users className="h-4 w-4 text-primary" strokeWidth={2} />
              </div>
              <DialogTitle>
                {editItem ? "Editar usuario" : "Nuevo usuario"}
              </DialogTitle>
            </div>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3" noValidate>
            {/* Full name — spans full width */}
            <div>
              <Label htmlFor="user-name">Nombre completo</Label>
              <Input
                id="user-name"
                {...register("fullName", { required: "Campo requerido" })}
                placeholder="Juan Pérez"
                aria-invalid={!!errors.fullName}
              />
              {errors.fullName && (
                <p className="mt-1 text-[11px] text-destructive">{errors.fullName.message}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label htmlFor="user-email">Email</Label>
                <Input
                  id="user-email"
                  type="email"
                  {...register("email", { required: "Campo requerido" })}
                  placeholder="juan@email.com"
                  aria-invalid={!!errors.email}
                />
                {errors.email && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.email.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="user-phone">Teléfono</Label>
                <Input
                  id="user-phone"
                  {...register("phone", { required: "Campo requerido" })}
                  placeholder="+57 300 000 0000"
                  aria-invalid={!!errors.phone}
                />
                {errors.phone && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.phone.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="user-doctype">Tipo de documento</Label>
                <Select
                  value={docTypeVal}
                  onValueChange={(v) => { setDocTypeVal(v); setValue("documentTypeId", v) }}
                >
                  <SelectTrigger id="user-doctype">
                    <SelectValue placeholder="Seleccionar" displayMap={docTypeMap} />
                  </SelectTrigger>
                  <SelectContent>
                    {docTypes.length === 0 ? (
                      <div className="px-2 py-3 text-[12px] text-muted-foreground text-center">
                        No hay tipos disponibles
                      </div>
                    ) : (
                      docTypes.map((dt) => (
                        <SelectItem key={dt.id} value={String(dt.id)}>{dt.name}</SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="user-docnum">N° de documento</Label>
                <Input
                  id="user-docnum"
                  {...register("documentNumber", { required: "Campo requerido" })}
                  placeholder="1234567890"
                  aria-invalid={!!errors.documentNumber}
                />
                {errors.documentNumber && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.documentNumber.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="user-birth">Fecha de nacimiento</Label>
                <Input
                  id="user-birth"
                  type="date"
                  {...register("birthDate", { required: "Campo requerido" })}
                  aria-invalid={!!errors.birthDate}
                />
                {errors.birthDate && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.birthDate.message}</p>
                )}
              </div>
              <div>
                <Label htmlFor="user-country">País</Label>
                <Input
                  id="user-country"
                  {...register("country", { required: "Campo requerido" })}
                  placeholder="Colombia"
                  aria-invalid={!!errors.country}
                />
                {errors.country && (
                  <p className="mt-1 text-[11px] text-destructive">{errors.country.message}</p>
                )}
              </div>
            </div>

            {/* Address — spans full width */}
            <div>
              <Label htmlFor="user-address">Dirección</Label>
              <Input
                id="user-address"
                {...register("address", { required: "Campo requerido" })}
                placeholder="Calle 123 #45-67"
                aria-invalid={!!errors.address}
              />
              {errors.address && (
                <p className="mt-1 text-[11px] text-destructive">{errors.address.message}</p>
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
                {editItem ? "Guardar cambios" : "Crear usuario"}
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
        title="Eliminar usuario"
        description="¿Estás seguro de que deseas eliminar este usuario? Se perderán todos sus datos y pedidos asociados."
        loading={deleteMut.isPending}
      />
    </div>
  )
}
