<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { RouterLink } from 'vue-router'
import {
  createExpense,
  deleteExpense,
  fetchExpenses,
  updateExpense,
} from '../api/expense'
import { ApiError } from '../api/client'
import { useMonthNav } from '../composables/useMonthNav'
import { usePageCss } from '../composables/usePageCss'
import type { CategoryItem } from '../types/category'
import type { ExpenseItem, ExpensePayload, NamedRef } from '../types/expense'
import { formatListDate, todayIsoDate } from '../utils/domainHelpers'
import { formatAmount, formatAmountWithUnit, stripAmount } from '../utils/formatAmount'

usePageCss('/css/budget.css')

const route = useRoute()
const { year, month, categoryFilter, monthLabel, goPrev, goNext, setCategory } = useMonthNav()

interface ExpenseForm extends ExpensePayload {
  pmtType: 'a' | 'c'
}

const total = ref(0)
const varTotal = ref(0)
const fixedTotal = ref(0)
const categoryTotal = ref(0)
const list = ref<ExpenseItem[]>([])
const categoryList = ref<CategoryItem[]>([])
const accountList = ref<NamedRef[]>([])
const cardList = ref<NamedRef[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)
const ctgScroll = ref<HTMLElement | null>(null)

const emptyForm = (): ExpenseForm => ({
  name: '',
  amt: '',
  dd: todayIsoDate(),
  ctgId: categoryList.value[0]?.id ?? '',
  pmtType: 'a',
  acctId: accountList.value[0]?.id ?? '',
  cardId: cardList.value[0]?.id ?? '',
  type: 'VAR',
})
const addForm = ref<ExpenseForm>(emptyForm())
const editForms = ref<Record<string, ExpenseForm>>({})

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

function itemToForm(item: ExpenseItem): ExpenseForm {
  const pmtType: 'a' | 'c' = item.acctId ? 'a' : 'c'
  return {
    name: item.name,
    amt: formatAmount(item.amt),
    dd: item.dd,
    ctgId: item.ctgId,
    pmtType,
    acctId: item.acctId ?? accountList.value[0]?.id ?? '',
    cardId: item.cardId ?? cardList.value[0]?.id ?? '',
    type: 'VAR',
  }
}

function payloadFromForm(form: ExpenseForm): ExpensePayload {
  const base: ExpensePayload = {
    name: form.name,
    amt: amtForSubmit(form.amt),
    dd: form.dd,
    ctgId: form.ctgId,
    type: form.type ?? 'VAR',
  }
  if (form.pmtType === 'a') {
    return form.acctId ? { ...base, acctId: form.acctId } : base
  }
  return form.cardId ? { ...base, cardId: form.cardId } : base
}

function onPmtTypeChange(form: ExpenseForm) {
  if (form.pmtType === 'a') {
    form.cardId = ''
    if (!form.acctId && accountList.value[0]) {
      form.acctId = accountList.value[0].id
    }
  } else {
    form.acctId = ''
    if (!form.cardId && cardList.value[0]) {
      form.cardId = cardList.value[0].id
    }
  }
}

function isTabActive(c: string | null) {
  return categoryFilter.value === c
}

async function load() {
  error.value = null
  try {
    const data = await fetchExpenses({
      y: year.value,
      month: month.value,
      c: categoryFilter.value,
    })
    total.value = data.total
    varTotal.value = data.varTotal
    fixedTotal.value = data.fixedTotal
    categoryTotal.value = data.categoryTotal
    list.value = data.list
    categoryList.value = data.categoryList
    accountList.value = data.accountList
    cardList.value = data.cardList
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
    await createExpense(payloadFromForm(addForm.value))
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
    await updateExpense(id, payloadFromForm(form))
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
    await deleteExpense(id)
    if (editingId.value === id) editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '삭제에 실패했습니다.'
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
          <span>결제일</span>
          <input v-model="addForm.dd" type="date" style="cursor: pointer">
        </label>
        <div class="pmt d:flex gap:8 md:jc:space-between">
          <label class="fl-grow:1 mr:4 md:mr:8">
            <span>카테고리</span>
            <select v-model="addForm.ctgId">
              <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </label>
          <label class="fl-grow:1">
            <span>결제유형</span>
            <select v-model="addForm.pmtType" @change="onPmtTypeChange(addForm)">
              <option value="a">통장</option>
              <option value="c">카드</option>
            </select>
          </label>
          <label v-show="addForm.pmtType === 'a'" class="fl-grow:1">
            <span>통장</span>
            <select v-model="addForm.acctId">
              <option v-for="a in accountList" :key="a.id" :value="a.id">{{ a.name }}</option>
            </select>
          </label>
          <label v-show="addForm.pmtType === 'c'" class="fl-grow:1">
            <span>카드</span>
            <select v-model="addForm.cardId">
              <option v-for="c in cardList" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </label>
        </div>
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
          <span>변동지출</span>
          <span>{{ formatAmountWithUnit(varTotal) }}</span>
        </div>
        <RouterLink to="/fixed">
          <div class="d:flex jc:space-between bd-bottom bd-color:base-3 p:2">
            <span>고정지출</span>
            <span>{{ formatAmountWithUnit(fixedTotal) }}</span>
          </div>
        </RouterLink>
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
              <div class="desc fs:5">{{ item.method }}</div>
            </div>
            <div class="title fw:2">{{ formatAmountWithUnit(item.amt) }}</div>
            <button
              type="button"
              class="delBtn icon icon:x icon-size:1 as:self-start cursor:pointer pos:absolute top:1 right:1"
              @click.stop="confirmDelete(item.id)"
            />
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
                <span>결제일</span>
                <input v-model="editForms[item.id].dd" type="date" style="cursor: pointer">
              </label>
              <div class="pmt d:flex gap:8">
                <label>
                  <span>카테고리</span>
                  <select v-model="editForms[item.id].ctgId">
                    <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
                  </select>
                </label>
                <label>
                  <span>결제유형</span>
                  <select
                    v-model="editForms[item.id].pmtType"
                    @change="onPmtTypeChange(editForms[item.id])"
                  >
                    <option value="a">통장</option>
                    <option value="c">카드</option>
                  </select>
                </label>
                <label v-show="editForms[item.id].pmtType === 'a'">
                  <span>통장</span>
                  <select v-model="editForms[item.id].acctId">
                    <option v-for="a in accountList" :key="a.id" :value="a.id">{{ a.name }}</option>
                  </select>
                </label>
                <label v-show="editForms[item.id].pmtType === 'c'">
                  <span>카드</span>
                  <select v-model="editForms[item.id].cardId">
                    <option v-for="c in cardList" :key="c.id" :value="c.id">{{ c.name }}</option>
                  </select>
                </label>
              </div>
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
