import { api } from './client'
import type { ThemeMode } from '../composables/useTheme'

export function patchTheme(theme: ThemeMode) {
  return api<void>('/api/user/theme', { method: 'PATCH', body: { theme } })
}
