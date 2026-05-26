import axiosClient from "./axiosClient"
import type { DocumentTypeResponse, CreateDocumentTypeRequest, UpdateDocumentTypeRequest, DeleteResponse } from "@/types"

export const getDocumentTypes = () => axiosClient.get<DocumentTypeResponse[]>("/documentType").then(r => r.data)
export const getDocumentTypeById = (id: number) => axiosClient.get<DocumentTypeResponse>(`/documentType/${id}`).then(r => r.data)
export const createDocumentType = (data: CreateDocumentTypeRequest) => axiosClient.post<DocumentTypeResponse>("/documentType", data).then(r => r.data)
export const updateDocumentType = (id: number, data: UpdateDocumentTypeRequest) => axiosClient.put<DocumentTypeResponse>(`/documentType/${id}`, data).then(r => r.data)
export const deleteDocumentType = (id: number) => axiosClient.delete<DeleteResponse>(`/documentType/${id}`).then(r => r.data)
