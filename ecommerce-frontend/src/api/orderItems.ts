import axiosClient from "./axiosClient"
import type { OrderItemResponse, CreateOrderItemRequest, UpdateOrderItemRequest, DeleteResponse } from "@/types"

export const getOrderItems = () => axiosClient.get<OrderItemResponse[]>("/orderItem").then(r => r.data)
export const getOrderItemById = (id: number) => axiosClient.get<OrderItemResponse>(`/orderItem/${id}`).then(r => r.data)
export const createOrderItem = (data: CreateOrderItemRequest) => axiosClient.post<OrderItemResponse>("/orderItem", data).then(r => r.data)
export const updateOrderItem = (id: number, data: UpdateOrderItemRequest) => axiosClient.put<OrderItemResponse>(`/orderItem/${id}`, data).then(r => r.data)
export const deleteOrderItem = (id: number) => axiosClient.delete<DeleteResponse>(`/orderItem/${id}`).then(r => r.data)
