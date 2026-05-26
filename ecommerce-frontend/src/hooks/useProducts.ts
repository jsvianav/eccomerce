import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getProducts, getProductById, createProduct, updateProduct, deleteProduct } from "@/api/products"
import type { CreateProductRequest, UpdateProductRequest } from "@/types"
import { toast } from "sonner"

export const PRODUCTS_KEY = ["products"]

export function useProducts() {
  return useQuery({ queryKey: PRODUCTS_KEY, queryFn: getProducts })
}

export function useProduct(id: number) {
  return useQuery({ queryKey: [...PRODUCTS_KEY, id], queryFn: () => getProductById(id), enabled: !!id })
}

export function useCreateProduct() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateProductRequest) => createProduct(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PRODUCTS_KEY }); toast.success("Producto creado correctamente") },
  })
}

export function useUpdateProduct() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProductRequest }) => updateProduct(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PRODUCTS_KEY }); toast.success("Producto actualizado correctamente") },
  })
}

export function useDeleteProduct() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteProduct(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: PRODUCTS_KEY }); toast.success("Producto eliminado correctamente") },
  })
}
