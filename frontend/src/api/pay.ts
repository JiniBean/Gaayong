import { api } from './client'
import type { PayListResponse, PayPayload } from '../types/pay'
import type { MessageResponse } from '../types/category'

function queryString(params: { y?: number; month?: number; c?: string | null }) {
  const q = new URLSearchParams()
  if (params.y != null) q.set('y', String(params.y))
  if (params.month != null) q.set('month', String(params.month))
  if (params.c) q.set('c', params.c)
  const s = q.toString()
  return s ? `?${s}` : ''
}

export function fetchPays(params: { y?: number; month?: number; c?: string | null } = {}) {
  return api<PayListResponse>(`/api/pay${queryString(params)}`)
}

export function createPay(body: PayPayload) {
  return api<MessageResponse>('/api/pay', { method: 'POST', body })
}

export function updatePay(id: string, body: PayPayload) {
  return api<MessageResponse>(`/api/pay/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deletePay(id: string) {
  return api<MessageResponse>(`/api/pay/${encodeURIComponent(id)}`, { method: 'DELETE' })
}

export function togglePayPaid(id: string, item: PayPayload, currentlyPaid: boolean) {
  const body: PayPayload = { ...item }
  if (!currentlyPaid) {
    body.isPaid = 'on'
  }
  return updatePay(id, body)
}
