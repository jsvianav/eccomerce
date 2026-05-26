import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getPayments, getPaymentById, createPayment, updatePayment, deletePayment } from "@/api/payments"
import type { CreatePaymentRequest, UpdatePaymentRequest } from "@/types"
import { toast } from "sonner"

export const PAYMENTS_KEY = ["payments"]

export function usePayments() {
  return useQuery({ queryKey: PAYMENTS_KEY, queryFn: getPayments })
}

export function usePayment(id: number) {
  return useQuery({ queryKey: [...PAYMENTS_KEY, id], queryFn: () => getPaymentById(id), enabled: !!id })
}

export function useCreatePayment() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreatePaymentRequest) => createPayment(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PAYMENTS_KEY }); toast.success("Pago creado correctamente") },
  })
}

export function useUpdatePayment() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdatePaymentRequest }) => updatePayment(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PAYMENTS_KEY }); toast.success("Pago actualizado correctamente") },
  })
}

export function useDeletePayment() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deletePayment(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PAYMENTS_KEY }); toast.success("Pago eliminado correctamente") },
  })
}
