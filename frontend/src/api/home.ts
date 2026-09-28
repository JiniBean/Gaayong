import { api } from './client'
import type { HomeDashboard } from '../types/home'

export function fetchHome() {
  return api<HomeDashboard>('/api/home')
}
