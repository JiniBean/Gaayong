<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createFixed, deleteFixed, fetchFixed, updateFixed } from '../api/fixed'
import { ApiError } from '../api/client'
import { usePageCss } from '../composables/usePageCss'
import type { CategoryItem } from '../types/category'
import type { NamedRef } from '../types/expense'
import type { FixedItem, FixedPayload } from '../types/fixed'
import { DAY_OPTIONS, isTruthyFlag } from '../utils/domainHelpers'
import { formatAmount, formatAmountWithUnit, stripAmount } from '../utils/formatAmount'

usePageCss('/css/fixed.css')

interface FixedFormState {
  name: string
  amt: string
  dd: string
  ctgId: string
  pmtType: 'a' | 'c'
  acctId: string
  cardId: string
  isPaid: boolean
  isAutoPay: boolean
}

const route = useRoute()
const router = useRouter()

const total = ref(0)
const unpaid = ref(0)
const list = ref<FixedItem[]>([])
const categoryList = ref<CategoryItem[]>([])
const accountList = ref<NamedRef[]>([])
const cardList = ref<NamedRef[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)

const filter = computed(() => {
  const f = route.query.f
  if (f === 'true' || f === 'false') return f
  return null
})

function emptyForm(): FixedFormState {
  return {
    name: '',
    amt: '',
    dd: '',
    ctgId: categoryList.value[0]?.id ?? '',
    pmtType: 'a',
    acctId: accountList.value[0]?.id ?? '',
    cardId: cardList.value[0]?.id ?? '',
    isPaid: false,
    isAutoPay: false,
  }
}

const addForm = ref<FixedFormState>(emptyForm())
const editForms = ref<Record<string, FixedFormState>>({})

function isTabActive(value: string | null) {
  return filter.value === value
}

function itemFromRow(item: FixedItem): FixedFormState {
  const useAcct = item.acctId != null && item.acctId !== ''
  return {
    name: item.name,
    amt: formatAmount(item.amt),
    dd: item.dd != null && item.dd !== '' ? String(item.dd) : '',
    ctgId: item.ctgId,
    pmtType: useAcct ? 'a' : 'c',
    acctId: item.acctId ? String(item.acctId) : accountList.value[0]?.id ?? '',
    cardId: item.cardId ? String(item.cardId) : cardList.value[0]?.id ?? '',
    isPaid: isTruthyFlag(item.isPaid),
    isAutoPay: isTruthyFlag(item.isAutoPay),
  }
}

function onAmtFocus(form: FixedFormState) {
  form.amt = stripAmount(form.amt)
}

function onAmtBlur(form: FixedFormState) {
  if (form.amt) form.amt = formatAmount(form.amt)
}

function onAmtInput(form: FixedFormState) {
  form.amt = form.amt.replace(/[^0-9]/g, '')
}

function autoPayLabel(form: FixedFormState) {
  return form.dd ? `매달 ${form.dd}일에 자동 결제` : '매달 결제일에 자동 결제'
}

function syncAutoPay(form: FixedFormState) {
  if (!form.dd) {
    form.isAutoPay = false
  }
}

function onPmtTypeChange(form: FixedFormState) {
  if (form.pmtType === 'a') {
    form.cardId = ''
    if (!form.acctId && accountList.value.length > 0) {
      form.acctId = accountList.value[0].id
    }
  } else {
    form.acctId = ''
    if (!form.cardId && cardList.value.length > 0) {
      form.cardId = cardList.value[0].id
    }
  }
}

function toPayload(form: FixedFormState): FixedPayload {
  const payload: FixedPayload = {
    name: form.name,
    amt: stripAmount(form.amt),
    ctgId: form.ctgId,
  }
  if (form.dd) payload.dd = form.dd
  if (form.pmtType === 'a' && form.acctId) payload.acctId = form.acctId
  if (form.pmtType === 'c' && form.cardId) payload.cardId = form.cardId
  if (form.isPaid) payload.isPaid = 'on'
  if (form.isAutoPay && form.dd) payload.isAutoPay = 'on'
  return payload
}

function ddLabel(item: FixedItem) {
  if (item.dd == null || item.dd === '') return '일자 지정 안함'
  const dd = String(item.dd)
  if (isTruthyFlag(item.isAutoPay)) return `매달 ${dd}일 · 자동`
  return `매달 ${dd}일`
}

async function load() {
  error.value = null
  try {
    const data = await fetchFixed(filter.value ?? undefined)
    total.value = data.total
    unpaid.value = data.unpaid
    list.value = data.list
    categoryList.value = data.categoryList
    accountList.value = data.accountList
    cardList.value = data.cardList
    for (const item of data.list) {
      editForms.value[item.id] = itemFromRow(item)
    }
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '목록을 불러오지 못했습니다.'
  }
}

function navigateFilter(f: string | null) {
  if (f) {
    router.push({ path: '/fixed', query: { f } })
  } else {
    router.push({ path: '/fixed' })
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
    await createFixed(toPayload(addForm.value))
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
    await updateFixed(id, toPayload(form))
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
    await deleteFixed(id)
    if (editingId.value === id) editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '삭제에 실패했습니다.'
  }
}

async function togglePaid(item: FixedItem) {
  error.value = null
  const paid = isTruthyFlag(item.isPaid)
  const payload: FixedPayload = {
    name: item.name,
    amt: stripAmount(String(item.amt)),
    ctgId: item.ctgId,
  }
  if (item.dd != null && item.dd !== '') payload.dd = String(item.dd)
  if (item.acctId) payload.acctId = String(item.acctId)
  if (item.cardId) payload.cardId = String(item.cardId)
  if (!paid) payload.isPaid = 'on'
  try {
    await updateFixed(item.id, payload)
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '결제 상태 변경에 실패했습니다.'
  }
}

onMounted(load)

watch(() => route.query.f, () => {
  load()
})
</script>

<template>
  <main>
    <div class="d:flex jc:space-between mt:8">
      <h1 class="page-title">고정 지출</h1>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">
        지출추가
      </button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">고정 지출 추가</h1>
      <form class="form form:underline" @submit.prevent="submitAdd">
        <label>
          <span>내역</span>
          <input v-model="addForm.name" type="text" required>
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
        <div class="pmt grid-fixed">
          <label>
            <span>일자</span>
            <span class="d:flex fl-grow:1 ai:center">
              <span class="mr:2 desc fw:2">매달</span>
              <select v-model="addForm.dd" class="fl-grow:1" @change="syncAutoPay(addForm)">
                <option value="">선택안함</option>
                <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
              </select>
            </span>
          </label>
          <label>
            <span>카테고리</span>
            <select v-model="addForm.ctgId">
              <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </label>
          <label>
            <span>결제유형</span>
            <select v-model="addForm.pmtType" @change="onPmtTypeChange(addForm)">
              <option value="a">통장</option>
              <option value="c">카드</option>
            </select>
          </label>
          <label v-show="addForm.pmtType === 'a'">
            <span>통장</span>
            <select v-model="addForm.acctId">
              <option v-for="a in accountList" :key="a.id" :value="a.id">{{ a.name }}</option>
            </select>
          </label>
          <label v-show="addForm.pmtType === 'c'">
            <span>카드</span>
            <select v-model="addForm.cardId">
              <option v-for="c in cardList" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </label>
          <label class="auto-pay-row">
            <input
              v-model="addForm.isAutoPay"
              type="checkbox"
              name="isAutoPay"
              :disabled="!addForm.dd"
            >
            <span class="auto-pay-label">{{ autoPayLabel(addForm) }}</span>
          </label>
        </div>
        <label>
          <input v-model="addForm.isPaid" type="checkbox" name="isPaid">
          이번 달 결제 완료된 내역이에요
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
        <div class="title">총 고정지출</div>
        <div class="value">{{ formatAmountWithUnit(total) }}</div>
        <div class="d:flex jc:space-between bd-bottom bd-color:base-3 p:2">
          <span>미결제 지출</span>
          <span>{{ formatAmountWithUnit(unpaid) }}</span>
        </div>
      </div>
    </section>

    <section class="d:flex fl-dir:column gap:8">
      <h1 class="d:none">지출 내역</h1>
      <div class="d:flex fl-dir:row gap:2 fs:5">
        <button
          type="button"
          class="btn btn-size:1 btn:outline"
          :class="{ 'btn-color:main-2': isTabActive(null) }"
          @click="navigateFilter(null)"
        >
          전체
        </button>
        <button
          type="button"
          class="btn btn-size:1 btn:outline"
          :class="{ 'btn-color:main-2': isTabActive('false') }"
          @click="navigateFilter('false')"
        >
          미결제
        </button>
        <button
          type="button"
          class="btn btn-size:1 btn:outline"
          :class="{ 'btn-color:main-2': isTabActive('true') }"
          @click="navigateFilter('true')"
        >
          결제완료
        </button>
      </div>

      <div class="d:flex fl-dir:column gap:2">
        <div v-for="item in list" :key="item.id" class="modDiv">
          <div
            v-if="editingId !== item.id"
            class="list card p:4 fl-dir:row ai:center w:10p pos:relative cursor:pointer"
            @click="startEdit(item.id)"
          >
            <div class="fl-grow:1 d:flex fl-dir:column gap:4">
              <div><span class="tag">{{ item.ctg }}</span></div>
              <div class="title fw:2">{{ item.name }}</div>
              <div class="desc fs:5">{{ ddLabel(item) }}</div>
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
              :class="{ 'btn-color:sub-1': !isTruthyFlag(item.isPaid) }"
              @click.stop="togglePaid(item)"
            >
              {{ isTruthyFlag(item.isPaid) ? '취소' : '결제' }}
            </button>
          </div>
          <section v-else class="mod card">
            <h1 class="title">고정 지출 수정</h1>
            <form class="form form:underline" @submit.prevent="submitEdit(item.id)">
              <label>
                <span>내역</span>
                <input v-model="editForms[item.id].name" type="text" required>
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
              <div class="pmt grid-fixed">
                <label>
                  <span>일자</span>
                  <span class="d:flex fl-grow:1 ai:center">
                    <span class="mr:2 desc fw:2">매달</span>
                    <select
                      v-model="editForms[item.id].dd"
                      class="fl-grow:1"
                      @change="syncAutoPay(editForms[item.id])"
                    >
                      <option value="">선택안함</option>
                      <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
                    </select>
                  </span>
                </label>
                <label>
                  <span>카테고리</span>
                  <select v-model="editForms[item.id].ctgId">
                    <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
                  </select>
                </label>
                <label>
                  <span>결제유형</span>
                  <select v-model="editForms[item.id].pmtType" @change="onPmtTypeChange(editForms[item.id])">
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
                <label class="auto-pay-row">
                  <input
                    v-model="editForms[item.id].isAutoPay"
                    type="checkbox"
                    name="isAutoPay"
                    :disabled="!editForms[item.id].dd"
                  >
                  <span class="auto-pay-label">{{ autoPayLabel(editForms[item.id]) }}</span>
                </label>
              </div>
              <label>
                <input v-model="editForms[item.id].isPaid" type="checkbox" name="isPaid">
                이번 달 결제 완료된 내역이에요
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
