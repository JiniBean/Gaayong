import { api } from './client'
import type { AccountListResponse, AccountPayload, MessageResponse } from '../types/account'

export function fetchAccounts() {
  return api<AccountListResponse>('/api/account')
}

export function createAccount(body: AccountPayload) {
  return api<MessageResponse>('/api/account', { method: 'POST', body })
}

export function updateAccount(id: string, body: AccountPayload) {
  return api<MessageResponse>(`/api/account/${encodeURIComponent(id)}`, { method: 'PUT', body })
}

export function deleteAccount(id: string) {
  return api<MessageResponse>(`/api/account/${encodeURIComponent(id)}`, { method: 'DELETE' })
}
