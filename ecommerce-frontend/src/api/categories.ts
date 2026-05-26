import axiosClient from "./axiosClient"
import type { CategoryResponse, CreateCategoryRequest, UpdateCategoryRequest, DeleteResponse } from "@/types"

export const getCategories = () => axiosClient.get<CategoryResponse[]>("/category").then(r => r.data)
export const getCategoryById = (id: number) => axiosClient.get<CategoryResponse>(`/category/${id}`).then(r => r.data)
export const createCategory = (data: CreateCategoryRequest) => axiosClient.post<CategoryResponse>("/category", data).then(r => r.data)
export const updateCategory = (id: number, data: UpdateCategoryRequest) => axiosClient.put<CategoryResponse>(`/category/${id}`, data).then(r => r.data)
export const deleteCategory = (id: number) => axiosClient.delete<DeleteResponse>(`/category/${id}`).then(r => r.data)
