import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getCarts, getCartById, createCart, updateCart, deleteCart } from "@/api/carts"
import type { CreateCartRequest, UpdateCartRequest } from "@/types"
import { toast } from "sonner"

export const CARTS_KEY = ["carts"]

export function useCarts() {
  return useQuery({ queryKey: CARTS_KEY, queryFn: getCarts })
}

export function useCart(id: number) {
  return useQuery({ queryKey: [...CARTS_KEY, id], queryFn: () => getCartById(id), enabled: !!id })
}

export function useCreateCart() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateCartRequest) => createCart(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CARTS_KEY }); toast.success("Carrito creado correctamente") },
  })
}

export function useUpdateCart() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCartRequest }) => updateCart(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CARTS_KEY }); toast.success("Carrito actualizado correctamente") },
  })
}

export function useDeleteCart() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteCart(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CARTS_KEY }); toast.success("Carrito eliminado correctamente") },
  })
}
