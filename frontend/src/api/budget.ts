import { api } from './client'
import type { BudgetListResponse, BudgetPayload } from '../types/budget'
import type { MessageResponse } from '../types/category'

export function fetchBudgets() {
  return api<BudgetListResponse>('/api/budget')
}

export function createBudget(body: BudgetPayload) {
  return api<MessageResponse>('/api/budget', { method: 'POST', body })
}

export function updateBudget(id: string, body: BudgetPayload) {
  return api<MessageResponse>(`/api/budget/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteBudget(id: string) {
  return api<MessageResponse>(`/api/budget/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
