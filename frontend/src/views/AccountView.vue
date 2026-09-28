<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createAccount, deleteAccount, fetchAccounts, updateAccount } from '../api/account'
import { ApiError } from '../api/client'
import { usePageCss } from '../composables/usePageCss'
import type { AccountItem, AccountPayload } from '../types/account'
import { formatAmount, formatAmountWithUnit, stripAmount } from '../utils/formatAmount'

usePageCss('/css/budget.css')

const total = ref(0)
const list = ref<AccountItem[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)

const emptyForm = (): AccountPayload => ({ bank: '', name: '', accNum: '', amt: '' })
const addForm = ref<AccountPayload>(emptyForm())
const editForms = ref<Record<string, AccountPayload>>({})

function onAmtFocus(form: AccountPayload) {
  form.amt = stripAmount(form.amt)
}

function onAmtBlur(form: AccountPayload) {
  if (form.amt) form.amt = formatAmount(form.amt)
}

function onAmtInput(form: AccountPayload) {
  form.amt = form.amt.replace(/[^0-9]/g, '')
}

function amtForSubmit(amt: string) {
  return stripAmount(amt)
}

async function load() {
  error.value = null
  try {
    const data = await fetchAccounts()
    total.value = data.total
    list.value = data.list
    for (const item of data.list) {
      editForms.value[item.id] = {
        bank: item.bank,
        name: item.name,
        accNum: item.accNum,
        amt: formatAmount(item.amt),
      }
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
    await createAccount({ ...addForm.value, amt: amtForSubmit(addForm.value.amt) })
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
    await updateAccount(id, { ...form, amt: amtForSubmit(form.amt) })
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
    await deleteAccount(id)
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
      <h1 class="page-title">통장 관리</h1>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">통장추가</button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">통장 추가</h1>
      <form class="form form:underline grid" @submit.prevent="submitAdd">
        <label><span>은행</span><input v-model="addForm.bank" type="text" required></label>
        <label><span>통장명</span><input v-model="addForm.name" type="text"></label>
        <label><span>계좌번호</span><input v-model="addForm.accNum" type="text"></label>
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
        <div class="d:flex gap:4 mt:4">
          <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="showAdd = false">취소</button>
          <button type="submit" class="btn fl-grow:1">저장</button>
        </div>
      </form>
    </section>

    <section class="grid">
      <h1 class="d:none">통장 현황</h1>
      <div class="card">
        <div class="title">총 잔액</div>
        <div class="value">{{ formatAmountWithUnit(total) }}</div>
      </div>
    </section>

    <section class="d:flex fl-dir:column gap:8">
      <h1 class="d:none">통장 잔고 내역</h1>
      <div v-for="item in list" :key="item.id" class="modDiv d:flex fl-dir:column gap:2">
        <div
          v-if="editingId !== item.id"
          class="list card p:4 fl-dir:row ai:center w:10p pos:relative cursor:pointer"
          @click="startEdit(item.id)"
        >
          <div class="fl-grow:1 d:flex fl-dir:column gap:4">
            <div class="desc fs:5">{{ item.bank }}</div>
            <div class="title fw:2 fl-grow:1">{{ item.name }}</div>
            <div class="desc fs:5">{{ item.accNum }}</div>
          </div>
          <div class="title fw:2">{{ formatAmountWithUnit(item.amt) }}</div>
          <button
            type="button"
            class="delBtn icon icon:x icon-size:1 as:self-start cursor:pointer pos:absolute top:1 right:1"
            @click.stop="confirmDelete(item.id)"
          />
        </div>
        <section v-else class="mod card">
          <h1 class="d:none">통장 수정</h1>
          <form class="form form:underline grid" @submit.prevent="submitEdit(item.id)">
            <label><span>은행</span><input v-model="editForms[item.id].bank" type="text" required></label>
            <label><span>통장명</span><input v-model="editForms[item.id].name" type="text"></label>
            <label><span>계좌번호</span><input v-model="editForms[item.id].accNum" type="text"></label>
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
