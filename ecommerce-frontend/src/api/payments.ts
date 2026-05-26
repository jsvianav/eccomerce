import axiosClient from "./axiosClient"
import type { PaymentResponse, CreatePaymentRequest, UpdatePaymentRequest, DeleteResponse } from "@/types"

export const getPayments = () => axiosClient.get<PaymentResponse[]>("/payment").then(r => r.data)
export const getPaymentById = (id: number) => axiosClient.get<PaymentResponse>(`/payment/${id}`).then(r => r.data)
export const createPayment = (data: CreatePaymentRequest) => axiosClient.post<PaymentResponse>("/payment", data).then(r => r.data)
export const updatePayment = (id: number, data: UpdatePaymentRequest) => axiosClient.put<PaymentResponse>(`/payment/${id}`, data).then(r => r.data)
export const deletePayment = (id: number) => axiosClient.delete<DeleteResponse>(`/payment/${id}`).then(r => r.data)
