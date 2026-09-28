import { api } from './client'
import type { ExpenseListResponse, ExpensePayload } from '../types/expense'
import type { MessageResponse } from '../types/category'

function queryString(params: { y?: number; month?: number; c?: string | null }) {
  const q = new URLSearchParams()
  if (params.y != null) q.set('y', String(params.y))
  if (params.month != null) q.set('month', String(params.month))
  if (params.c) q.set('c', params.c)
  const s = q.toString()
  return s ? `?${s}` : ''
}

export function fetchExpenses(params: { y?: number; month?: number; c?: string | null } = {}) {
  return api<ExpenseListResponse>(`/api/expense${queryString(params)}`)
}

export function createExpense(body: ExpensePayload) {
  return api<MessageResponse>('/api/expense', { method: 'POST', body })
}

export function updateExpense(id: string, body: ExpensePayload) {
  return api<MessageResponse>(`/api/expense/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteExpense(id: string) {
  return api<MessageResponse>(`/api/expense/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
