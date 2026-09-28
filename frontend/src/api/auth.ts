import { api } from './client'
import type { AuthUser, LoginRequest, MessageResponse, SignupRequest } from '../types/auth'

export function fetchMe() {
  return api<AuthUser>('/api/auth/me')
}

export function login(body: LoginRequest) {
  return api<AuthUser>('/api/auth/login', { method: 'POST', body })
}

export function signup(body: SignupRequest) {
  return api<MessageResponse>('/api/auth/signup', { method: 'POST', body })
}

export function logout() {
  return api<MessageResponse>('/api/auth/logout', { method: 'POST' })
}
