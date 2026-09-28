<script setup lang="ts">
import { ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import ThemeToggle from '../components/ThemeToggle.vue'

const route = useRoute()
const drawerOpen = ref(false)

const navItems = [
  { to: '/', label: '대시보드' },
  { to: '/expense', label: '지출' },
  { to: '/pay', label: '지출현황' },
  { to: '/income', label: '수입' },
  { to: '/fixed', label: '고정지출' },
  { to: '/budget', label: '예산' },
  { to: '/account', label: '통장' },
  { to: '/card', label: '카드' },
  { to: '/category', label: '카테고리' },
]

watch(
  () => route.fullPath,
  () => {
    drawerOpen.value = false
  },
)
</script>

<template>
  <header class="h:2 md:h:3 w:10p">
    <div class="d:flex gap:11 jc:space-between ai:center w:10p p:8">
      <h1 class="fs:13">
        <RouterLink to="/signin">가용</RouterLink>
      </h1>
      <div class="d:none md:d:flex fl-dir:column gap:3 jc:space-between ai:end fl-grow:1 h:10p">
        <div class="d:flex ai:center gap:8">
          <h1 class="d:none">top</h1>
          <ul class="d:flex gap:5">
            <li><RouterLink to="/signin">로그인</RouterLink></li>
            <li><RouterLink to="/signup">회원가입</RouterLink></li>
          </ul>
          <ThemeToggle variant="pc" />
        </div>
        <nav class="w:7p">
          <h1 class="d:none">down</h1>
          <ul class="d:flex jc:space-between fs:10">
            <li v-for="item in navItems" :key="item.to">
              <RouterLink :to="item.to">{{ item.label }}</RouterLink>
            </li>
          </ul>
        </nav>
      </div>
      <div class="d:flex gap:6 md:d:none">
        <div class="d:flex gap:2">
          <RouterLink to="/signin" class="icon icon:signin icon-size:6">로그인</RouterLink>
        </div>
        <button
          type="button"
          class="drawer-btn icon icon:menu icon-size:7"
          @click="drawerOpen = true"
        >
          메뉴
        </button>
      </div>
    </div>
  </header>

  <aside class="drawer d:flex fl-dir:column gap:9 p:4" :class="{ active: drawerOpen }">
    <div class="d:flex jc:space-between">
      <h1 class="fs:13">가용</h1>
      <button type="button" class="drawer-close icon icon:x icon-color:base-2" @click="drawerOpen = false">
        닫기
      </button>
    </div>
    <ul class="d:flex fl-grow:1 fl-dir:column gap:6 fs:9">
      <li v-for="item in navItems" :key="item.to">
        <RouterLink :to="item.to">{{ item.label }}</RouterLink>
      </li>
    </ul>
    <ThemeToggle variant="drawer" />
  </aside>

  <RouterView />

  <footer class="w:10p p:8">
    ©가용. All rights reserved.
  </footer>
</template>

<style scoped>
button.drawer-btn,
button.drawer-close {
  background: none;
  border: none;
  cursor: pointer;
}
</style>
