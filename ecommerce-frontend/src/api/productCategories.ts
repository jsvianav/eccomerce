import axiosClient from "./axiosClient"
import type { ProductCategoryResponse, CreateProductCategoryRequest, UpdateProductCategoryRequest, DeleteResponse } from "@/types"

export const getProductCategories = () => axiosClient.get<ProductCategoryResponse[]>("/productCategory").then(r => r.data)
export const getProductCategoryById = (id: number) => axiosClient.get<ProductCategoryResponse>(`/productCategory/${id}`).then(r => r.data)
export const createProductCategory = (data: CreateProductCategoryRequest) => axiosClient.post<ProductCategoryResponse>("/productCategory", data).then(r => r.data)
export const updateProductCategory = (id: number, data: UpdateProductCategoryRequest) => axiosClient.put<ProductCategoryResponse>(`/productCategory/${id}`, data).then(r => r.data)
export const deleteProductCategory = (id: number) => axiosClient.delete<DeleteResponse>(`/productCategory/${id}`).then(r => r.data)
