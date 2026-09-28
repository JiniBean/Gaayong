<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createBudget, deleteBudget, fetchBudgets, updateBudget } from '../api/budget'
import { ApiError } from '../api/client'
import { usePageCss } from '../composables/usePageCss'
import type { CategoryItem } from '../types/category'
import type { BudgetItem, BudgetPayload } from '../types/budget'
import { DAY_OPTIONS } from '../utils/domainHelpers'
import { formatAmount, formatAmountWithUnit, stripAmount } from '../utils/formatAmount'

usePageCss('/css/budget.css')

interface BudgetFormState {
  name: string
  des: string
  amt: string
  dd: string
  ctgId: string
}

const total = ref(0)
const list = ref<BudgetItem[]>([])
const categoryList = ref<CategoryItem[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)

function emptyForm(): BudgetFormState {
  return {
    name: '',
    des: '',
    amt: '',
    dd: '1',
    ctgId: categoryList.value[0]?.id ?? '',
  }
}

const addForm = ref<BudgetFormState>(emptyForm())
const editForms = ref<Record<string, BudgetFormState>>({})

function itemFromRow(item: BudgetItem): BudgetFormState {
  return {
    name: item.name,
    des: item.des,
    amt: formatAmount(item.amt),
    dd: String(item.dd),
    ctgId: item.ctgId,
  }
}

function onAmtFocus(form: BudgetFormState) {
  form.amt = stripAmount(form.amt)
}

function onAmtBlur(form: BudgetFormState) {
  if (form.amt) form.amt = formatAmount(form.amt)
}

function onAmtInput(form: BudgetFormState) {
  form.amt = form.amt.replace(/[^0-9]/g, '')
}

function toPayload(form: BudgetFormState): BudgetPayload {
  return {
    name: form.name,
    des: form.des,
    amt: stripAmount(form.amt),
    dd: form.dd,
    ctgId: form.ctgId,
  }
}

async function load() {
  error.value = null
  try {
    const data = await fetchBudgets()
    total.value = data.total
    list.value = data.list
    categoryList.value = data.categoryList
    for (const item of data.list) {
      editForms.value[item.id] = itemFromRow(item)
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
    await createBudget(toPayload(addForm.value))
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
    await updateBudget(id, toPayload(form))
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
    await deleteBudget(id)
    if (editingId.value === id) editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '삭제에 실패했습니다.'
  }
}

onMounted(load)
</script>

<template>
  <main>
    <div class="d:flex jc:space-between mt:8">
      <h1 class="page-title">이번 달 예산</h1>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">
        예산추가
      </button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">예산 추가</h1>
      <form class="form form:underline grid" @submit.prevent="submitAdd">
        <label>
          <span>이름</span>
          <input v-model="addForm.name" type="text" required>
        </label>
        <label>
          <span>설명</span>
          <input v-model="addForm.des" type="text">
        </label>
        <label>
          <span>금액</span>
          <span class="d:flex ai:end gap:1">
            <input
              v-model="addForm.amt"
              type="text"
              @focus="onAmtFocus(addForm)"
              @blur="onAmtBlur(addForm)"
              @input="onAmtInput(addForm)"
            >
            <span class="desc">원</span>
          </span>
        </label>
        <div class="d:flex gap:8">
          <label class="fl-grow:1">
            <span>일자</span>
            <span class="d:flex fl-grow:1 ai:center">
              <span class="mr:2 desc fw:2">매달</span>
              <select v-model="addForm.dd">
                <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
              </select>
            </span>
          </label>
          <label class="fl-grow:1">
            <span>카테고리</span>
            <select v-model="addForm.ctgId">
              <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
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
      <h1 class="d:none">예산 현황</h1>
      <div class="card">
        <div class="title">총 예산</div>
        <div class="value">{{ formatAmountWithUnit(total) }}</div>
      </div>
    </section>

    <section class="d:flex fl-dir:column gap:2">
      <h1 class="d:none">예산 내역</h1>
      <div v-for="item in list" :key="item.id" class="modDiv">
        <div
          v-if="editingId !== item.id"
          class="list card p:4 fl-dir:row ai:center w:10p pos:relative cursor:pointer"
          @click="startEdit(item.id)"
        >
          <div class="fl-grow:1 d:flex fl-dir:column gap:4 ml:1">
            <div><span class="tag">{{ item.ctg }}</span></div>
            <div class="title fw:2 fl-grow:1">{{ item.name }}</div>
            <div class="desc fs:5">매달 {{ item.dd }}일</div>
          </div>
          <div class="title fw:2">{{ formatAmountWithUnit(item.amt) }}</div>
          <button
            type="button"
            class="delBtn icon icon:x icon-size:1 as:self-start cursor:pointer pos:absolute top:1 right:1"
            @click.stop="confirmDelete(item.id)"
          />
        </div>
        <section v-else class="mod card">
          <h1 class="d:none">예산 수정</h1>
          <form class="form form:underline grid" @submit.prevent="submitEdit(item.id)">
            <label>
              <span>이름</span>
              <input v-model="editForms[item.id].name" type="text" required>
            </label>
            <label>
              <span>설명</span>
              <input v-model="editForms[item.id].des" type="text">
            </label>
            <label>
              <span>금액</span>
              <span class="d:flex ai:end gap:1">
                <input
                  v-model="editForms[item.id].amt"
                  type="text"
                  @focus="onAmtFocus(editForms[item.id])"
                  @blur="onAmtBlur(editForms[item.id])"
                  @input="onAmtInput(editForms[item.id])"
                >
                <span class="desc">원</span>
              </span>
            </label>
            <div class="d:flex gap:8">
              <label class="fl-grow:1">
                <span>일자</span>
                <span class="d:flex fl-grow:1 ai:center">
                  <span class="mr:2 desc fw:2">매달</span>
                  <select v-model="editForms[item.id].dd">
                    <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
                  </select>
                </span>
              </label>
              <label class="fl-grow:1">
                <span>카테고리</span>
                <select v-model="editForms[item.id].ctgId">
                  <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
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
    </section>
  </main>
</template>
