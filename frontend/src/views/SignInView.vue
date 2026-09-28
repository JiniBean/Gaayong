<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { safeRedirect } from '../composables/useLastRoute'
import { ApiError } from '../api/client'

const STYLE_ID = 'sign-page-css'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const userNm = ref('')
const pwd = ref('')
const rememberMe = ref(false)
const formError = ref<string | null>(null)
const submitting = ref(false)

const banner = computed(() => {
  const q = route.query
  if (q.registered !== undefined) {
    return { type: 'success' as const, text: '회원가입이 완료되었습니다. 로그인해주세요.' }
  }
  if (q.logout !== undefined) {
    return { type: 'success' as const, text: '로그아웃 되었습니다.' }
  }
  if (q.expired !== undefined) {
    return { type: 'error' as const, text: '세션이 만료되었습니다. 다시 로그인해주세요.' }
  }
  if (q.invalid !== undefined) {
    return { type: 'error' as const, text: '유효하지 않은 세션입니다. 다시 로그인해주세요.' }
  }
  if (q.error !== undefined) {
    return { type: 'error' as const, text: '아이디 또는 비밀번호가 올바르지 않습니다.' }
  }
  return null
})

async function onSubmit() {
  formError.value = null
  submitting.value = true
  try {
    await auth.login({
      userNm: userNm.value,
      pwd: pwd.value,
      rememberMe: rememberMe.value,
    })
    const target = safeRedirect(route.query.redirect)
    await router.replace(target ?? { path: '/' })
  } catch (e) {
    formError.value =
      e instanceof ApiError ? e.message : '아이디 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  const link = document.createElement('link')
  link.id = STYLE_ID
  link.rel = 'stylesheet'
  link.href = '/css/sign.css'
  document.head.appendChild(link)
})

onUnmounted(() => {
  document.getElementById(STYLE_ID)?.remove()
})
</script>

<template>
  <main class="pt:10">
    <section class="mt:8 d:flex fl-dir:column ai:center gap:10">
      <h1 class="page-title">로그인</h1>

      <div v-if="banner?.type === 'error'" class="error-message">{{ banner.text }}</div>
      <div v-if="banner?.type === 'success'" class="success-message">{{ banner.text }}</div>
      <div v-if="formError" class="error-message">{{ formError }}</div>

      <form
        class="form mt:8 form:underline d:flex fl-dir:column gap:10 ml:4 mr:4 md:mr:0 md:ml:0"
        @submit.prevent="onSubmit"
      >
        <label>
          <span>아이디</span>
          <input v-model="userNm" type="text" placeholder="아이디를 입력하세요" required>
        </label>
        <label>
          <span>비밀번호</span>
          <input v-model="pwd" type="password" placeholder="비밀번호를 입력하세요" required>
        </label>

        <div class="remember-me-container d:flex ai:center gap:2 mt:2">
          <input id="remember-me" v-model="rememberMe" type="checkbox" class="mr:1">
          <label for="remember-me" style="font-size: 0.9rem;">자동 로그인</label>
        </div>

        <ul class="link">
          <li><a href="">아이디 찾기</a></li>
          <li><a href="">비밀번호 찾기</a></li>
          <li><RouterLink to="/signup">회원가입</RouterLink></li>
        </ul>
        <div>
          <button type="submit" class="btn btn-color:main-2 w:10p h:1" :disabled="submitting">
            로그인
          </button>
        </div>
      </form>
    </section>
  </main>
</template>
