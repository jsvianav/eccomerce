import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getInventory, getInventoryById, createInventory, updateInventory, deleteInventory } from "@/api/inventory"
import type { CreateInventoryRequest, UpdateInventoryRequest } from "@/types"
import { toast } from "sonner"

export const INVENTORY_KEY = ["inventory"]

export function useInventory() {
  return useQuery({ queryKey: INVENTORY_KEY, queryFn: getInventory })
}

export function useInventoryItem(id: number) {
  return useQuery({ queryKey: [...INVENTORY_KEY, id], queryFn: () => getInventoryById(id), enabled: !!id })
}

export function useCreateInventory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateInventoryRequest) => createInventory(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: INVENTORY_KEY }); toast.success("Inventario creado correctamente") },
  })
}

export function useUpdateInventory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateInventoryRequest }) => updateInventory(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: INVENTORY_KEY }); toast.success("Inventario actualizado correctamente") },
  })
}

export function useDeleteInventory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteInventory(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: INVENTORY_KEY }); toast.success("Inventario eliminado correctamente") },
  })
}
