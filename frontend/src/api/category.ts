import { api } from './client'
import type { CategoryListResponse, CategoryPayload, MessageResponse } from '../types/category'

export function fetchCategories(c?: string) {
  const query = c ? `?c=${encodeURIComponent(c)}` : ''
  return api<CategoryListResponse>(`/api/category${query}`)
}

export function createCategory(body: CategoryPayload) {
  return api<MessageResponse>('/api/category', { method: 'POST', body })
}

export function updateCategory(id: string, body: CategoryPayload) {
  return api<MessageResponse>(`/api/category/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteCategory(id: string) {
  return api<MessageResponse>(`/api/category/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
