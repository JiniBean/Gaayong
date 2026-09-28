import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export function useMonthNav() {
  const route = useRoute()
  const router = useRouter()
  const now = new Date()

  const year = computed(() => {
    const y = route.query.y
    if (typeof y === 'string' && y.length > 0) return parseInt(y, 10)
    return now.getFullYear()
  })

  const month = computed(() => {
    const m = route.query.month
    if (typeof m === 'string' && m.length > 0) return parseInt(m, 10)
    return now.getMonth() + 1
  })

  const categoryFilter = computed(() => {
    const c = route.query.c
    return typeof c === 'string' && c.length > 0 ? c : null
  })

  const monthLabel = computed(() => `${month.value}월`)

  function buildQuery(overrides: { y?: number; month?: number; c?: string | null } = {}) {
    const q: Record<string, string> = {
      y: String(overrides.y ?? year.value),
      month: String(overrides.month ?? month.value),
    }
    const c = overrides.c !== undefined ? overrides.c : categoryFilter.value
    if (c) q.c = c
    return q
  }

  function goPrev() {
    const m = month.value
    const y = year.value
    const prevMonth = m === 1 ? 12 : m - 1
    const prevYear = m === 1 ? y - 1 : y
    router.push({ query: buildQuery({ y: prevYear, month: prevMonth }) })
  }

  function goNext() {
    const m = month.value
    const y = year.value
    const nextMonth = m === 12 ? 1 : m + 1
    const nextYear = m === 12 ? y + 1 : y
    router.push({ query: buildQuery({ y: nextYear, month: nextMonth }) })
  }

  function setCategory(c: string | null) {
    router.push({ query: buildQuery({ c }) })
  }

  return {
    year,
    month,
    categoryFilter,
    monthLabel,
    buildQuery,
    goPrev,
    goNext,
    setCategory,
  }
}
