import axiosClient from "./axiosClient"
import type { InventoryMovementResponse, CreateInventoryMovementRequest, UpdateInventoryMovementRequest, DeleteResponse } from "@/types"

export const getInventoryMovements = () => axiosClient.get<InventoryMovementResponse[]>("/inventoryMovement").then(r => r.data)
export const getInventoryMovementById = (id: number) => axiosClient.get<InventoryMovementResponse>(`/inventoryMovement/${id}`).then(r => r.data)
export const createInventoryMovement = (data: CreateInventoryMovementRequest) => axiosClient.post<InventoryMovementResponse>("/inventoryMovement", data).then(r => r.data)
export const updateInventoryMovement = (id: number, data: UpdateInventoryMovementRequest) => axiosClient.put<InventoryMovementResponse>(`/inventoryMovement/${id}`, data).then(r => r.data)
export const deleteInventoryMovement = (id: number) => axiosClient.delete<DeleteResponse>(`/inventoryMovement/${id}`).then(r => r.data)
