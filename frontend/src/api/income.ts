import { api } from './client'
import type { IncomeListResponse, IncomePayload } from '../types/income'
import type { MessageResponse } from '../types/category'

function queryString(params: { y?: number; month?: number; c?: string | null }) {
  const q = new URLSearchParams()
  if (params.y != null) q.set('y', String(params.y))
  if (params.month != null) q.set('month', String(params.month))
  if (params.c) q.set('c', params.c)
  const s = q.toString()
  return s ? `?${s}` : ''
}

export function fetchIncomes(params: { y?: number; month?: number; c?: string | null } = {}) {
  return api<IncomeListResponse>(`/api/income${queryString(params)}`)
}

export function createIncome(body: IncomePayload) {
  return api<MessageResponse>('/api/income', { method: 'POST', body })
}

export function updateIncome(id: string, body: IncomePayload) {
  return api<MessageResponse>(`/api/income/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteIncome(id: string) {
  return api<MessageResponse>(`/api/income/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
