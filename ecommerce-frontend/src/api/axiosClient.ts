import axios from "axios"
import type { AxiosError } from "axios"
import { toast } from "sonner"
import type { ApiError } from "@/types"

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
})

axiosClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    const message = error.response?.data?.message || error.message || "Error desconocido"
    toast.error(message)
    return Promise.reject(error)
  }
)

export default axiosClient
