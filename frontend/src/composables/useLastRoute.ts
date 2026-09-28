import type { RouteLocationRaw } from 'vue-router'

const STORAGE_KEY = 'gaayong:lastRoute'

export const ALLOWED_PATHS = new Set([
  '/',
  '/expense',
  '/income',
  '/fixed',
  '/pay',
  '/budget',
  '/account',
  '/card',
  '/category',
])

const EXCLUDED_PATHS = new Set(['/signin', '/signup'])

function pathnameOf(path: string) {
  return path.split('?')[0] ?? path
}

export function isValidRoute(path: string) {
  if (!path || typeof path !== 'string') return false
  if (!path.startsWith('/') || path.startsWith('//')) return false
  if (/javascript:/i.test(path)) return false
  return ALLOWED_PATHS.has(pathnameOf(path))
}

export function isExcluded(path: string) {
  return EXCLUDED_PATHS.has(pathnameOf(path))
}

export function saveLastRoute(fullPath: string) {
  if (isExcluded(fullPath)) return
  if (!isValidRoute(fullPath)) return
  localStorage.setItem(STORAGE_KEY, fullPath)
}

export function forceHome() {
  localStorage.setItem(STORAGE_KEY, '/')
}

/** Returns a restore target if last route is valid and not home. */
export function peekRestore(): string | null {
  const target = localStorage.getItem(STORAGE_KEY)
  if (!target || !isValidRoute(target)) return null
  if (pathnameOf(target) === '/') return null
  return target
}

export function safeRedirect(raw: unknown): RouteLocationRaw | null {
  if (typeof raw !== 'string') return null
  if (!raw.startsWith('/') || raw.startsWith('//')) return null
  if (/javascript:/i.test(raw)) return null
  if (!ALLOWED_PATHS.has(pathnameOf(raw))) return null
  return raw
}
