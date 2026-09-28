<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchHome } from '../api/home'
import { ApiError } from '../api/client'
import { usePageCss } from '../composables/usePageCss'
import type { HomeDashboard } from '../types/home'
import { formatAmountWithUnit } from '../utils/formatAmount'

usePageCss('/css/home.css')

const data = ref<HomeDashboard | null>(null)
const error = ref<string | null>(null)

const todayLabel = computed(() => {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}.${m}.${day}`
})

const diffAmt = computed(() => {
  if (!data.value) return 0
  return data.value.availAmt - data.value.expAvailAmt
})

async function load() {
  error.value = null
  try {
    data.value = await fetchHome()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '대시보드를 불러오지 못했습니다.'
  }
}

onMounted(load)
</script>

<template>
  <main>
    <div v-if="error" class="error-message mt:8">{{ error }}</div>

    <section v-if="data" class="grid mt:8">
      <h1>{{ todayLabel }}</h1>

      <RouterLink to="/expense">
        <div class="card">
          <div class="title">가용금액</div>
          <div class="value">{{ formatAmountWithUnit(data.availAmt) }}</div>
          <div class="desc">잔고 - (지출 예정 비용 + 카드대금)</div>
        </div>
      </RouterLink>

      <RouterLink to="/fixed">
        <div class="card">
          <div class="title">미결제 고정지출</div>
          <div class="value">{{ formatAmountWithUnit(data.unpaidFixed) }}</div>
          <div class="desc">지출 예정인 고정 비용</div>
        </div>
      </RouterLink>

      <RouterLink to="/card">
        <div class="card">
          <div class="title">미결제 카드대금</div>
          <div class="value">{{ formatAmountWithUnit(data.cardPmt) }}</div>
          <div class="desc">지난 달 + 이번 달 카드 대금</div>
          <div v-for="(c, idx) in data.cardPmtList" :key="idx">
            <div class="desc d:flex jc:space-between bd-bottom bd-color:base-3 p:2">
              <span>{{ c.name }}</span>
              <span>{{ formatAmountWithUnit(c.total) }}</span>
            </div>
          </div>
        </div>
      </RouterLink>

      <RouterLink to="/account">
        <div class="card">
          <div class="title">통장 잔고</div>
          <div class="value">{{ formatAmountWithUnit(data.cash) }}</div>
          <div class="desc">모든 통장 잔고 총합</div>
        </div>
      </RouterLink>
    </section>

    <section v-if="data" class="grid">
      <h1>수입/지출 정보</h1>
      <RouterLink to="/income">
        <div class="card">
          <div class="title">총 수입</div>
          <div class="value">{{ formatAmountWithUnit(data.incomeTotal) }}</div>
          <div class="desc">예산 + 그 외 수입</div>
        </div>
      </RouterLink>
      <RouterLink to="/expense">
        <div class="card">
          <div class="title">총 지출</div>
          <div class="value">{{ formatAmountWithUnit(data.expenseTotal) }}</div>
          <div class="desc">고정 비용 + 이번 달 변동 지출</div>
        </div>
      </RouterLink>
    </section>

    <section v-if="data" class="grid">
      <h1>예산 정보</h1>
      <RouterLink to="/budget">
        <div class="card">
          <div class="title">예산</div>
          <div class="value">{{ formatAmountWithUnit(data.budgetTotal) }}</div>
          <div class="desc">예산 총합</div>
        </div>
      </RouterLink>
      <div class="card">
        <div class="title">예상 가용금액</div>
        <div class="value">{{ formatAmountWithUnit(data.expAvailAmt) }}</div>
        <div class="desc">예산 - 고정지출</div>
      </div>
      <div class="card">
        <div class="title">차이</div>
        <div class="value">{{ formatAmountWithUnit(diffAmt) }}</div>
        <div class="desc">가용금액 - 예상 가용금액</div>
      </div>
    </section>
  </main>
</template>
