import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getCategories, getCategoryById, createCategory, updateCategory, deleteCategory } from "@/api/categories"
import type { CreateCategoryRequest, UpdateCategoryRequest } from "@/types"
import { toast } from "sonner"

export const CATEGORIES_KEY = ["categories"]

export function useCategories() {
  return useQuery({ queryKey: CATEGORIES_KEY, queryFn: getCategories })
}

export function useCategory(id: number) {
  return useQuery({ queryKey: [...CATEGORIES_KEY, id], queryFn: () => getCategoryById(id), enabled: !!id })
}

export function useCreateCategory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateCategoryRequest) => createCategory(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CATEGORIES_KEY }); toast.success("Categoría creada correctamente") },
  })
}

export function useUpdateCategory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCategoryRequest }) => updateCategory(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CATEGORIES_KEY }); toast.success("Categoría actualizada correctamente") },
  })
}

export function useDeleteCategory() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteCategory(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: CATEGORIES_KEY }); toast.success("Categoría eliminada correctamente") },
  })
}
