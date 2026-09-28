import { api } from './client'
import type { FixedListResponse, FixedPayload } from '../types/fixed'
import type { MessageResponse } from '../types/category'

function queryString(f?: string | null) {
  if (!f) return ''
  return `?f=${encodeURIComponent(f)}`
}

export function fetchFixed(f?: string | null) {
  return api<FixedListResponse>(`/api/fixed${queryString(f)}`)
}

export function createFixed(body: FixedPayload) {
  return api<MessageResponse>('/api/fixed', { method: 'POST', body: { ...body, type: 'FIX' } })
}

export function updateFixed(id: string, body: FixedPayload) {
  return api<MessageResponse>(`/api/fixed/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteFixed(id: string) {
  return api<MessageResponse>(`/api/fixed/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
