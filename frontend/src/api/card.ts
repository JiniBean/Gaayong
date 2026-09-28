import { api } from './client'
import type { CardListResponse, CardPayload } from '../types/card'
import type { MessageResponse } from '../types/category'

export function fetchCards() {
  return api<CardListResponse>('/api/card')
}

export function createCard(body: CardPayload) {
  return api<MessageResponse>('/api/card', { method: 'POST', body })
}

export function updateCard(id: string, body: CardPayload) {
  return api<MessageResponse>(`/api/card/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteCard(id: string) {
  return api<MessageResponse>(`/api/card/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
