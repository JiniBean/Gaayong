export const DAY_OPTIONS = Array.from({ length: 31 }, (_, i) => String(i + 1))

export function todayIsoDate(): string {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

export function formatListDate(dd: string | null | undefined): string {
  if (!dd) return ''
  const date = new Date(dd)
  if (Number.isNaN(date.getTime())) return dd
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${m}/${day}`
}

export function isTruthyFlag(value: boolean | number | string | null | undefined): boolean {
  return value === true || value === 1 || value === '1' || value === 'true'
}
