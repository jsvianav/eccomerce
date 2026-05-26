import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getOrders, getOrderById, createOrder, updateOrder, deleteOrder } from "@/api/orders"
import type { CreateOrderRequest, UpdateOrderRequest } from "@/types"
import { toast } from "sonner"

export const ORDERS_KEY = ["orders"]

export function useOrders() {
  return useQuery({ queryKey: ORDERS_KEY, queryFn: getOrders })
}

export function useOrder(id: number) {
  return useQuery({ queryKey: [...ORDERS_KEY, id], queryFn: () => getOrderById(id), enabled: !!id })
}

export function useCreateOrder() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateOrderRequest) => createOrder(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ORDERS_KEY }); toast.success("Pedido creado correctamente") },
  })
}

export function useUpdateOrder() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateOrderRequest }) => updateOrder(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ORDERS_KEY }); toast.success("Pedido actualizado correctamente") },
  })
}

export function useDeleteOrder() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteOrder(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ORDERS_KEY }); toast.success("Pedido eliminado correctamente") },
  })
}
