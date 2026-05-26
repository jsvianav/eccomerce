import axiosClient from "./axiosClient"
import type { OrderResponse, CreateOrderRequest, UpdateOrderRequest, DeleteResponse } from "@/types"

export const getOrders = () => axiosClient.get<OrderResponse[]>("/order").then(r => r.data)
export const getOrderById = (id: number) => axiosClient.get<OrderResponse>(`/order/${id}`).then(r => r.data)
export const createOrder = (data: CreateOrderRequest) => axiosClient.post<OrderResponse>("/order", data).then(r => r.data)
export const updateOrder = (id: number, data: UpdateOrderRequest) => axiosClient.put<OrderResponse>(`/order/${id}`, data).then(r => r.data)
export const deleteOrder = (id: number) => axiosClient.delete<DeleteResponse>(`/order/${id}`).then(r => r.data)
