export type ThemeMode = 'light' | 'dark'

const THEME_KEY = 'theme'

export function getStoredTheme(): ThemeMode | null {
  const value = localStorage.getItem(THEME_KEY)
  return value === 'light' || value === 'dark' ? value : null
}

export function applyTheme(mode: ThemeMode) {
  const body = document.body
  body.classList.toggle('light', mode === 'light')
  body.classList.toggle('dark', mode === 'dark')
}

export function bootstrapTheme() {
  applyTheme(getStoredTheme() ?? 'light')
}

export function persistGuestTheme(mode: ThemeMode) {
  localStorage.setItem(THEME_KEY, mode)
  applyTheme(mode)
}

export function clearGuestTheme() {
  localStorage.removeItem(THEME_KEY)
}
