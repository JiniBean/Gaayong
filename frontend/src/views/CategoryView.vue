<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createCategory,
  deleteCategory,
  fetchCategories,
  updateCategory,
} from '../api/category'
import type { CategoryItem, CategoryPayload, CodeItem } from '../types/category'
import { ApiError } from '../api/client'

const route = useRoute()
const router = useRouter()

const codeList = ref<CodeItem[]>([])
const list = ref<CategoryItem[]>([])
const error = ref<string | null>(null)
const showAdd = ref(false)
const editingId = ref<string | null>(null)

const filter = computed(() => {
  const c = route.query.c
  return typeof c === 'string' && c.length > 0 ? c : null
})

const emptyForm = (): CategoryPayload => ({ name: '', des: '', ctgCd: codeList.value[0]?.detailCd ?? 'E' })
const addForm = ref<CategoryPayload>(emptyForm())
const editForms = ref<Record<string, CategoryPayload>>({})

function itemsForCode(detailCd: string) {
  return list.value.filter((item) => item.ctgCd === detailCd)
}

function visibleCodeGroups() {
  if (!filter.value) return codeList.value
  return codeList.value.filter((c) => c.detailCd === filter.value)
}

function isTabActive(detailCd: string | null) {
  return filter.value === detailCd
}

async function load() {
  error.value = null
  try {
    const data = await fetchCategories(filter.value ?? undefined)
    codeList.value = data.codeList
    list.value = data.list
    if (!addForm.value.ctgCd && data.codeList.length > 0) {
      addForm.value.ctgCd = data.codeList[0].detailCd
    }
    for (const item of data.list) {
      editForms.value[item.id] = {
        name: item.name,
        des: item.des,
        ctgCd: item.ctgCd,
      }
    }
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '목록을 불러오지 못했습니다.'
  }
}

function navigateFilter(detailCd: string | null) {
  if (detailCd) {
    router.push({ path: '/category', query: { c: detailCd } })
  } else {
    router.push({ path: '/category' })
  }
}

function toggleAdd() {
  showAdd.value = !showAdd.value
  if (showAdd.value) {
    addForm.value = emptyForm()
  }
}

function cancelAdd() {
  showAdd.value = false
}

async function submitAdd() {
  error.value = null
  try {
    await createCategory(addForm.value)
    showAdd.value = false
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '저장에 실패했습니다.'
  }
}

function startEdit(id: string) {
  editingId.value = id
}

function cancelEdit() {
  editingId.value = null
}

async function submitEdit(id: string) {
  error.value = null
  const form = editForms.value[id]
  if (!form) return
  try {
    await updateCategory(id, form)
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
    await deleteCategory(id)
    if (editingId.value === id) editingId.value = null
    await load()
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '삭제에 실패했습니다.'
  }
}

const STYLE_ID = 'category-page-css'

onMounted(() => {
  const link = document.createElement('link')
  link.id = STYLE_ID
  link.rel = 'stylesheet'
  link.href = '/css/category.css'
  document.head.appendChild(link)
  load()
})

onUnmounted(() => {
  document.getElementById(STYLE_ID)?.remove()
})

watch(() => route.query.c, () => {
  load()
})
</script>

<template>
  <main>
    <div class="d:flex jc:space-between mt:8">
      <h1 class="page-title">카테고리</h1>
      <button type="button" class="addBtn btn btn-size:1 btn-color:main-2 fs:7" @click="toggleAdd">
        카테고리 추가
      </button>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>

    <section v-if="showAdd" class="add card">
      <h1 class="title">카테고리 추가</h1>
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
          <span>유형</span>
          <select v-model="addForm.ctgCd" class="mt:1">
            <option v-for="c in codeList" :key="c.detailCd" :value="c.detailCd">
              {{ c.detailNm }}
            </option>
          </select>
        </label>
        <div class="d:flex gap:4 mt:4">
          <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="cancelAdd">취소</button>
          <button type="submit" class="btn fl-grow:1">저장</button>
        </div>
      </form>
    </section>

    <section class="d:flex fl-dir:column gap:6">
      <h1 class="d:none">카테고리 목록</h1>

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
          v-for="c in codeList"
          :key="c.detailCd"
          type="button"
          class="btn btn-size:1 btn:outline"
          :class="{ 'btn-color:main-2': isTabActive(c.detailCd) }"
          @click="navigateFilter(c.detailCd)"
        >
          {{ c.detailNm }}
        </button>
      </div>

      <div class="grid gap:2">
        <div v-for="c in visibleCodeGroups()" :key="c.detailCd" class="card">
          <div class="title">{{ c.detailNm }}</div>
          <div class="d:flex fl-dir:column gap:2">
            <div v-for="item in itemsForCode(c.detailCd)" :key="item.id" class="modDiv">
              <div v-if="editingId !== item.id" class="card p:4 fl-dir:row ai:center w:10p">
                <div class="fl-grow:1 d:flex fl-dir:column gap:2">
                  <div class="title fw:2">{{ item.name }}</div>
                  <div class="desc fs:5">{{ item.des }}</div>
                </div>
                <div class="d:flex jc:end gap:2">
                  <button type="button" class="modBtn btn btn-size:1" @click="startEdit(item.id)">수정</button>
                  <button
                    type="button"
                    class="delBtn btn btn-size:1 bg-color:accent-1"
                    @click="confirmDelete(item.id)"
                  >
                    삭제
                  </button>
                </div>
              </div>
              <section v-else class="mod card">
                <h1 class="title">카테고리 수정</h1>
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
                    <span>유형</span>
                    <select v-model="editForms[item.id].ctgCd" class="mt:1">
                      <option v-for="code in codeList" :key="code.detailCd" :value="code.detailCd">
                        {{ code.detailNm }}
                      </option>
                    </select>
                  </label>
                  <div class="d:flex gap:4 mt:4">
                    <button type="button" class="cnclBtn btn btn:outline fl-grow:1" @click="cancelEdit">취소</button>
                    <button type="submit" class="btn fl-grow:1">저장</button>
                  </div>
                </form>
              </section>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
