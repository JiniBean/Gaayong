<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ApiError } from '../api/client'

const STYLE_ID = 'sign-page-css'

const auth = useAuthStore()
const router = useRouter()

const userNm = ref('')
const name = ref('')
const pwd = ref('')
const email = ref('')
const formError = ref<string | null>(null)
const submitting = ref(false)

async function onSubmit() {
  formError.value = null
  submitting.value = true
  try {
    await auth.signup({
      userNm: userNm.value,
      name: name.value,
      pwd: pwd.value,
      email: email.value,
    })
    await router.replace({ path: '/signin', query: { registered: 'true' } })
  } catch (e) {
    formError.value =
      e instanceof ApiError ? e.message : '회원가입에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!document.getElementById(STYLE_ID)) {
    const link = document.createElement('link')
    link.id = STYLE_ID
    link.rel = 'stylesheet'
    link.href = '/css/sign.css'
    document.head.appendChild(link)
  }
})

onUnmounted(() => {
  // keep style if navigating between sign pages; SignIn also manages it
})
</script>

<template>
  <main class="pt:10">
    <section class="mt:8 d:flex fl-dir:column ai:center gap:10">
      <h1 class="page-title">회원가입</h1>
      <div v-if="formError" class="error-message">{{ formError }}</div>

      <form
        class="form mt:8 form:underline d:flex fl-dir:column gap:10 w:9"
        @submit.prevent="onSubmit"
      >
        <label>
          <span>아이디</span>
          <input v-model="userNm" type="text" placeholder="사용할 아이디를 입력하세요" required>
        </label>
        <label>
          <span>이름</span>
          <input v-model="name" type="text" placeholder="이름을 입력하세요" required>
        </label>
        <label>
          <span>비밀번호</span>
          <input v-model="pwd" type="password" placeholder="비밀번호를 입력하세요" required>
        </label>
        <label>
          <span>이메일</span>
          <input v-model="email" type="email" placeholder="이메일을 입력하세요">
        </label>
        <div>
          <button type="submit" class="mt:8 btn btn-color:main-2 w:10p h:1" :disabled="submitting">
            가입하기
          </button>
        </div>
      </form>
    </section>
  </main>
</template>
