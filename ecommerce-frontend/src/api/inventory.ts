import axiosClient from "./axiosClient"
import type { InventoryResponse, CreateInventoryRequest, UpdateInventoryRequest, DeleteResponse } from "@/types"

export const getInventory = () => axiosClient.get<InventoryResponse[]>("/inventory").then(r => r.data)
export const getInventoryById = (id: number) => axiosClient.get<InventoryResponse>(`/inventory/${id}`).then(r => r.data)
export const createInventory = (data: CreateInventoryRequest) => axiosClient.post<InventoryResponse>("/inventory", data).then(r => r.data)
export const updateInventory = (id: number, data: UpdateInventoryRequest) => axiosClient.put<InventoryResponse>(`/inventory/${id}`, data).then(r => r.data)
export const deleteInventory = (id: number) => axiosClient.delete<DeleteResponse>(`/inventory/${id}`).then(r => r.data)
