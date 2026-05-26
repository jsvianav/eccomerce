import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getUsers, getUserById, createUser, updateUser, deleteUser } from "@/api/users"
import type { CreateUserRequest, UpdateUserRequest } from "@/types"
import { toast } from "sonner"

export const USERS_KEY = ["users"]

export function useUsers() {
  return useQuery({ queryKey: USERS_KEY, queryFn: getUsers })
}

export function useUser(id: number) {
  return useQuery({ queryKey: [...USERS_KEY, id], queryFn: () => getUserById(id), enabled: !!id })
}

export function useCreateUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateUserRequest) => createUser(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: USERS_KEY }); toast.success("Usuario creado correctamente") },
  })
}

export function useUpdateUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateUserRequest }) => updateUser(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: USERS_KEY }); toast.success("Usuario actualizado correctamente") },
  })
}

export function useDeleteUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteUser(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: USERS_KEY }); toast.success("Usuario eliminado correctamente") },
  })
}
