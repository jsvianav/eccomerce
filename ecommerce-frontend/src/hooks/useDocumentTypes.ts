import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getDocumentTypes, getDocumentTypeById, createDocumentType, updateDocumentType, deleteDocumentType } from "@/api/documentTypes"
import type { CreateDocumentTypeRequest, UpdateDocumentTypeRequest } from "@/types"
import { toast } from "sonner"

export const DOCUMENT_TYPES_KEY = ["documentTypes"]

export function useDocumentTypes() {
  return useQuery({ queryKey: DOCUMENT_TYPES_KEY, queryFn: getDocumentTypes })
}

export function useDocumentType(id: number) {
  return useQuery({ queryKey: [...DOCUMENT_TYPES_KEY, id], queryFn: () => getDocumentTypeById(id), enabled: !!id })
}

export function useCreateDocumentType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateDocumentTypeRequest) => createDocumentType(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: DOCUMENT_TYPES_KEY }); toast.success("Tipo de documento creado correctamente") },
  })
}

export function useUpdateDocumentType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateDocumentTypeRequest }) => updateDocumentType(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: DOCUMENT_TYPES_KEY }); toast.success("Tipo de documento actualizado correctamente") },
  })
}

export function useDeleteDocumentType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteDocumentType(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: DOCUMENT_TYPES_KEY }); toast.success("Tipo de documento eliminado correctamente") },
  })
}
