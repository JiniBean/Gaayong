<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  createPay,
  deletePay,
  fetchPays,
  togglePayPaid,
  updatePay,
} from '../api/pay'
import { ApiError } from '../api/client'
import { useMonthNav } from '../composables/useMonthNav'
import { usePageCss } from '../composables/usePageCss'
import type { CategoryItem } from '../types/category'
import type { PayItem, PayPayload } from '../types/pay'
import { formatListDate, isTruthyFlag, todayIsoDate } from '../utils/domainHelpers'
import { formatAmount, formatAmountWithUnit, stripAmount } from '../utils/formatAmount'

usePageCss('/css/budget.css')
usePageCss('/css/fixed.css')

const route = useRoute()
const { year, month, categoryFilter, monthLabel, goPrev, goNext, setCategory } = useMonthNav()

const total = ref(0)
const unpaid = ref(0)
const categoryTotal = ref(0)
const list = ref<PayItem[]>([])
const categoryList = ref<CategoryItem[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)
const ctgScroll = ref<HTMLElement | null>(null)

interface PayForm extends PayPayload {
  isPaidChecked: boolean
}

const emptyForm = (): PayForm => ({
  name: '',
  amt: '',
  dd: todayIsoDate(),
  ctgId: categoryList.value[0]?.id ?? '',
  isPaidChecked: false,
})
const addForm = ref<PayForm>(emptyForm())
const editForms = ref<Record<string, PayForm>>({})

function dayOfWeek(dd: string): string {
  const days = ['일', '월', '화', '수', '목', '금', '토']
  const date = new Date(dd)
  if (Number.isNaN(date.getTime())) return ''
  return days[date.getDay()] ?? ''
}

function onAmtFocus(form: { amt: string }) {
  form.amt = stripAmount(form.amt)
}

function onAmtBlur(form: { amt: string }) {
  if (form.amt) form.amt = formatAmount(form.amt)
}

function onAmtInput(form: { amt: string }) {
  form.amt = form.amt.replace(/[^0-9]/g, '')
}

function amtForSubmit(amt: string) {
  return stripAmount(amt)
}

function payContext(): { y: string; month: string; c?: string } {
  const ctx: { y: string; month: string; c?: string } = {
    y: String(year.value),
    month: String(month.value),
  }
  if (categoryFilter.value) ctx.c = categoryFilter.value
  return ctx
}

function itemToForm(item: PayItem): PayForm {
  return {
    name: item.name,
    amt: formatAmount(item.amt),
    dd: item.dd,
    ctgId: item.ctgId,
    isPaidChecked: isTruthyFlag(item.isPaid),
  }
}

function payloadFromForm(form: PayForm): PayPayload {
  const body: PayPayload = {
    name: form.name,
    amt: amtForSubmit(form.amt),
    dd: form.dd,
    ctgId: form.ctgId,
    ...payContext(),
  }
  if (form.isPaidChecked) {
    body.isPaid = 'on'
  }
  return body
}

function isTabActive(c: string | null) {
  return categoryFilter.value === c
}

function isPaid(item: PayItem): boolean {
  return isTruthyFlag(item.isPaid)
}

async function load() {
  error.value = null
  try {
    const data = await fetchPays({
      y: year.value,
      month: month.value,
      c: categoryFilter.value,
    })
    total.value = data.total
    unpaid.value = data.unpaid
    categoryTotal.value = data.categoryTotal
    list.value = data.list
    categoryList.value = data.categoryList
    for (const item of data.list) {
      editForms.value[item.id] = itemToForm(item)
    }
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '목록을 불러오지 못했습니다.'
  }
}

function toggleAdd() {
  showAdd.value = !showAdd.value
  if (showAdd.value) {
    addForm.value = emptyForm()
  }
}

async function submitAdd() {
  error.value = null
  try {
    await createPay(payloadFromForm(addForm.value))
    showAdd.value = false
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '저장에 실패했습니다.'
  }
}

function startEdit(id: string) {
  editingId.value = id
}

async function submitEdit(id: string) {
  error.value = null
  const form = editForms.value[id]
  if (!form) return
  try {
    await updatePay(id, payloadFromForm(form))
    editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '수정에 실패했습니다.'
  }
}

async function confirmDelete(id: string) {
  if (!confirm('삭제하시겠습니까?')) return
  error.value = null
  try {
    await deletePay(id)
    if (editingId.value === id) editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '삭제에 실패했습니다.'
  }
}

async function togglePaid(item: PayItem, e: Event) {
  e.stopPropagation()
  error.value = null
  const paid = isPaid(item)
  const body: PayPayload = {
    name: item.name,
    amt: String(item.amt),
    dd: item.dd,
    ctgId: item.ctgId,
    ...payContext(),
  }
  try {
    await togglePayPaid(item.id, body, paid)
    await load()
  } catch (err) {
    error.value = err instanceof ApiError ? err.message : '상태 변경에 실패했습니다.'
  }
}

function scrollCtg(dir: 'left' | 'right') {
  const el = ctgScroll.value
  if (!el) return
  el.scrollLeft += dir === 'left' ? -el.offsetWidth * 0.9 : el.offsetWidth * 0.9
}

onMounted(load)

watch(() => route.query, () => {
  load()
}, { deep: true })
</script>

<template>
  <main>
    <div class="d:flex jc:space-between mt:8">
      <div class="d:flex ai:center gap:6">
        <button type="button" class="prev" @click="goPrev">
          <div class="icon icon:caret-left icon-color:base-5">&lt;</div>
        </button>
        <h1 class="page-title">{{ monthLabel }} 지출</h1>
        <button type="button" class="next" @click="goNext">
          <div class="icon icon:caret-right icon-color:base-5">&gt;</div>
        </button>
      </div>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">
        지출추가
      </button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">지출 추가</h1>
      <form class="form form:underline grid" @submit.prevent="submitAdd">
        <label>
          <span>내역</span>
          <input v-model="addForm.name" type="text">
        </label>
        <label>
          <span>금액</span>
          <input
            v-model="addForm.amt"
            type="text"
            @focus="onAmtFocus(addForm)"
            @blur="onAmtBlur(addForm)"
            @input="onAmtInput(addForm)"
          >
        </label>
        <label>
          <span>일자</span>
          <input v-model="addForm.dd" type="date" style="cursor: pointer">
        </label>
        <label>
          <span>카테고리</span>
          <select v-model="addForm.ctgId">
            <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </label>
        <div class="d:flex gap:4 mt:4">
          <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="showAdd = false">취소</button>
          <button type="submit" class="btn fl-grow:1">저장</button>
        </div>
      </form>
    </section>

    <section class="grid">
      <h1 class="d:none">지출 현황</h1>
      <div class="card">
        <div class="title">총 지출</div>
        <div class="value">{{ formatAmountWithUnit(total) }}</div>
        <div class="d:flex jc:space-between bd-bottom bd-color:base-3 p:2">
          <span>미결제 지출</span>
          <span>{{ formatAmountWithUnit(unpaid) }}</span>
        </div>
      </div>
    </section>

    <section class="d:flex fl-dir:column gap:6">
      <h1 class="d:none">지출 내역</h1>
      <div class="d:flex ai:center">
        <button
          type="button"
          class="scroll-left icon icon:caret-left icon-color:base-5 md:d:none"
          @click="scrollCtg('left')"
        >
          &lt;
        </button>
        <div ref="ctgScroll" class="ctg-btn scroll:hidden fs:5 d:flex fl-dir:row gap:2">
          <button
            type="button"
            class="btn btn-size:1 btn:outline"
            :class="{ 'btn-color:main-2': isTabActive(null) }"
            @click="setCategory(null)"
          >
            전체
          </button>
          <button
            v-for="c in categoryList"
            :key="c.id"
            type="button"
            class="btn btn-size:1 btn:outline"
            :class="{ 'btn-color:main-2': isTabActive(c.id) }"
            @click="setCategory(c.id)"
          >
            {{ c.name }}
          </button>
        </div>
        <button
          type="button"
          class="scroll-right icon icon:caret-right icon-color:base-5 md:d:none"
          @click="scrollCtg('right')"
        >
          &gt;
        </button>
      </div>
      <div class="d:flex fl-dir:column gap:2">
        <div class="as:end fs:9 pr:4">총 {{ formatAmountWithUnit(categoryTotal) }}</div>
        <div v-for="item in list" :key="item.id" class="modDiv">
          <div
            v-if="editingId !== item.id"
            class="list card p:4 fl-dir:row ai:center w:10p pos:relative cursor:pointer"
            @click="startEdit(item.id)"
          >
            <div class="d:flex fl-dir:column ai:center gap:2" style="width: 12%; max-width: 90px">
              <div>{{ formatListDate(item.dd) }}</div>
              <div class="fs:4">{{ dayOfWeek(item.dd) }}</div>
            </div>
            <div class="fl-grow:1 d:flex fl-dir:column gap:4">
              <div><span class="tag">{{ item.ctg }}</span></div>
              <div class="title fw:2">{{ item.name }}</div>
            </div>
            <div class="title fw:2">{{ formatAmountWithUnit(item.amt) }}</div>
            <button
              type="button"
              class="delBtn icon icon:x icon-size:1 as:self-start cursor:pointer pos:absolute top:1 right:1"
              @click.stop="confirmDelete(item.id)"
            />
            <button
              type="button"
              class="pmtMod btn btn-size:1 fs:5"
              style="width: 65px"
              :class="{ 'btn-color:sub-1': !isPaid(item) }"
              @click="togglePaid(item, $event)"
            >
              {{ isPaid(item) ? '취소' : '받음' }}
            </button>
          </div>
          <section v-else class="mod card">
            <h1 class="title">지출 수정</h1>
            <form class="form form:underline grid" @submit.prevent="submitEdit(item.id)">
              <label>
                <span>내역</span>
                <input v-model="editForms[item.id].name" type="text">
              </label>
              <label>
                <span>금액</span>
                <input
                  v-model="editForms[item.id].amt"
                  type="text"
                  @focus="onAmtFocus(editForms[item.id])"
                  @blur="onAmtBlur(editForms[item.id])"
                  @input="onAmtInput(editForms[item.id])"
                >
              </label>
              <label>
                <span>일자</span>
                <input v-model="editForms[item.id].dd" type="date" style="cursor: pointer">
              </label>
              <label>
                <span>카테고리</span>
                <select v-model="editForms[item.id].ctgId">
                  <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
                </select>
              </label>
              <label>
                <input v-model="editForms[item.id].isPaidChecked" type="checkbox">받음 완료
              </label>
              <div class="d:flex gap:4 mt:4">
                <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="editingId = null">취소</button>
                <button type="submit" class="btn fl-grow:1">저장</button>
              </div>
            </form>
          </section>
        </div>
      </div>
    </section>
  </main>
</template>
