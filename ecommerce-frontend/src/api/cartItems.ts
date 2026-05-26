import axiosClient from "./axiosClient"
import type { CartItemResponse, CreateCartItemRequest, UpdateCartItemRequest, DeleteResponse } from "@/types"

export const getCartItems = () => axiosClient.get<CartItemResponse[]>("/cartItem").then(r => r.data)
export const getCartItemById = (id: number) => axiosClient.get<CartItemResponse>(`/cartItem/${id}`).then(r => r.data)
export const createCartItem = (data: CreateCartItemRequest) => axiosClient.post<CartItemResponse>("/cartItem", data).then(r => r.data)
export const updateCartItem = (id: number, data: UpdateCartItemRequest) => axiosClient.put<CartItemResponse>(`/cartItem/${id}`, data).then(r => r.data)
export const deleteCartItem = (id: number) => axiosClient.delete<DeleteResponse>(`/cartItem/${id}`).then(r => r.data)
