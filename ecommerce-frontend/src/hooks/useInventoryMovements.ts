import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getInventoryMovements, getInventoryMovementById, createInventoryMovement, updateInventoryMovement, deleteInventoryMovement } from "@/api/inventoryMovements"
import type { CreateInventoryMovementRequest, UpdateInventoryMovementRequest } from "@/types"
import { toast } from "sonner"

export const MOVEMENTS_KEY = ["inventoryMovements"]

export function useInventoryMovements() {
  return useQuery({ queryKey: MOVEMENTS_KEY, queryFn: getInventoryMovements })
}

export function useInventoryMovement(id: number) {
  return useQuery({ queryKey: [...MOVEMENTS_KEY, id], queryFn: () => getInventoryMovementById(id), enabled: !!id })
}

export function useCreateInventoryMovement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateInventoryMovementRequest) => createInventoryMovement(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: MOVEMENTS_KEY }); toast.success("Movimiento creado correctamente") },
  })
}

export function useUpdateInventoryMovement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateInventoryMovementRequest }) => updateInventoryMovement(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: MOVEMENTS_KEY }); toast.success("Movimiento actualizado correctamente") },
  })
}

export function useDeleteInventoryMovement() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteInventoryMovement(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: MOVEMENTS_KEY }); toast.success("Movimiento eliminado correctamente") },
  })
}
