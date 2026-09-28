<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createCard, deleteCard, fetchCards, updateCard } from '../api/card'
import { ApiError } from '../api/client'
import { usePageCss } from '../composables/usePageCss'
import type { CardItem, CardPayload } from '../types/card'
import { DAY_OPTIONS } from '../utils/domainHelpers'

usePageCss('/css/budget.css')

const list = ref<CardItem[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)

const emptyForm = (): CardPayload => ({ issuer: '', name: '', dd: '1' })
const addForm = ref<CardPayload>(emptyForm())
const editForms = ref<Record<string, CardPayload>>({})

async function load() {
  error.value = null
  try {
    const data = await fetchCards()
    list.value = data.list
    for (const item of data.list) {
      editForms.value[item.id] = {
        issuer: item.issuer,
        name: item.name,
        dd: String(item.dd),
      }
    }
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '목록을 불러오지 못했습니다.'
  }
}

function toggleAdd() {
  showAdd.value = !showAdd.value
  if (showAdd.value) addForm.value = emptyForm()
}

async function submitAdd() {
  error.value = null
  try {
    await createCard(addForm.value)
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
    await updateCard(id, form)
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
    await deleteCard(id)
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
      <h1 class="page-title">카드 관리</h1>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">카드추가</button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">카드 추가</h1>
      <form class="form form:underline grid" @submit.prevent="submitAdd">
        <label><span>카드사</span><input v-model="addForm.issuer" type="text" required></label>
        <label><span>카드명</span><input v-model="addForm.name" type="text"></label>
        <label>
          <span>결제일</span>
          <span class="d:flex fl-grow:1 ai:center">
            <span class="mr:2 desc fw:2">매달</span>
            <select v-model="addForm.dd">
              <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
            </select>
          </span>
        </label>
        <div class="d:flex gap:4 mt:4">
          <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="showAdd = false">취소</button>
          <button type="submit" class="btn fl-grow:1">저장</button>
        </div>
      </form>
    </section>

    <section class="d:flex fl-dir:column gap:2">
      <h1 class="d:none">카드 목록</h1>
      <div v-for="item in list" :key="item.id" class="modDiv">
        <div
          v-if="editingId !== item.id"
          class="list card p:4 fl-dir:row ai:center w:10p pos:relative cursor:pointer"
          @click="startEdit(item.id)"
        >
          <div class="fl-grow:1 d:flex fl-dir:column gap:4 ml:1">
            <div class="desc fs:5">{{ item.issuer }}</div>
            <div class="title fw:2 fl-grow:1">{{ item.name }}</div>
            <div class="desc fs:5">매달 {{ item.dd }}일 결제</div>
          </div>
          <button
            type="button"
            class="delBtn icon icon:x icon-size:1 as:self-start cursor:pointer pos:absolute top:1 right:1"
            @click.stop="confirmDelete(item.id)"
          />
        </div>
        <section v-else class="mod card">
          <h1 class="d:none">카드 수정</h1>
          <form class="form form:underline grid" @submit.prevent="submitEdit(item.id)">
            <label><span>카드사</span><input v-model="editForms[item.id].issuer" type="text" required></label>
            <label><span>카드명</span><input v-model="editForms[item.id].name" type="text"></label>
            <label>
              <span>결제일</span>
              <span class="d:flex fl-grow:1 ai:center">
                <span class="mr:2 desc fw:2">매달</span>
                <select v-model="editForms[item.id].dd">
                  <option v-for="d in DAY_OPTIONS" :key="d" :value="d">{{ d }}일</option>
                </select>
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
