import axiosClient from "./axiosClient"
import type { ProductResponse, CreateProductRequest, UpdateProductRequest, DeleteResponse } from "@/types"

export const getProducts = () => axiosClient.get<ProductResponse[]>("/product").then(r => r.data)
export const getProductById = (id: number) => axiosClient.get<ProductResponse>(`/product/${id}`).then(r => r.data)
export const createProduct = (data: CreateProductRequest) => axiosClient.post<ProductResponse>("/product", data).then(r => r.data)
export const updateProduct = (id: number, data: UpdateProductRequest) => axiosClient.put<ProductResponse>(`/product/${id}`, data).then(r => r.data)
export const deleteProduct = (id: number) => axiosClient.delete<DeleteResponse>(`/product/${id}`).then(r => r.data)
