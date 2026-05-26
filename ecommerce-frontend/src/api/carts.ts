import axiosClient from "./axiosClient"
import type { CartResponse, CreateCartRequest, UpdateCartRequest, DeleteResponse } from "@/types"

export const getCarts = () => axiosClient.get<CartResponse[]>("/cart").then(r => r.data)
export const getCartById = (id: number) => axiosClient.get<CartResponse>(`/cart/${id}`).then(r => r.data)
export const createCart = (data: CreateCartRequest) => axiosClient.post<CartResponse>("/cart", data).then(r => r.data)
export const updateCart = (id: number, data: UpdateCartRequest) => axiosClient.put<CartResponse>(`/cart/${id}`, data).then(r => r.data)
export const deleteCart = (id: number) => axiosClient.delete<DeleteResponse>(`/cart/${id}`).then(r => r.data)
