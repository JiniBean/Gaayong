export function formatAmount(value: number | string | null | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '0'
  }
  const num = typeof value === 'number' ? value : Number(String(value).replace(/,/g, ''))
  if (Number.isNaN(num)) {
    return '0'
  }
  return num.toLocaleString()
}

export function formatAmountWithUnit(value: number | string | null | undefined): string {
  return `${formatAmount(value)}원`
}

export function stripAmount(value: string): string {
  return value.replace(/,/g, '')
}
