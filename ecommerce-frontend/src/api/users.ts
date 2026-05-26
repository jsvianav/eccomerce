import axiosClient from "./axiosClient"
import type { UserResponse, CreateUserRequest, UpdateUserRequest, DeleteResponse } from "@/types"

export const getUsers = () => axiosClient.get<UserResponse[]>("/user").then(r => r.data)
export const getUserById = (id: number) => axiosClient.get<UserResponse>(`/user/${id}`).then(r => r.data)
export const createUser = (data: CreateUserRequest) => axiosClient.post<UserResponse>("/user", data).then(r => r.data)
export const updateUser = (id: number, data: UpdateUserRequest) => axiosClient.put<UserResponse>(`/user/${id}`, data).then(r => r.data)
export const deleteUser = (id: number) => axiosClient.delete<DeleteResponse>(`/user/${id}`).then(r => r.data)
