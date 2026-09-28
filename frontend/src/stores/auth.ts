import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchMe, login as loginApi, logout as logoutApi, signup as signupApi } from '../api/auth'
import type { AuthUser, LoginRequest, SignupRequest } from '../types/auth'
import { applyTheme, clearGuestTheme, getStoredTheme, type ThemeMode } from '../composables/useTheme'
import { ApiError } from '../api/client'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)
  const initialized = ref(false)
  let initPromise: Promise<void> | null = null

  const isAuthenticated = computed(() => user.value !== null)
  const displayName = computed(() => user.value?.name ?? '')
  const theme = computed<ThemeMode>(() => {
    const t = user.value?.theme
    return t === 'dark' ? 'dark' : 'light'
  })

  async function init() {
    if (initialized.value) return
    if (!initPromise) {
      initPromise = (async () => {
        try {
          user.value = await fetchMe()
          clearGuestTheme()
          applyTheme(user.value.theme === 'dark' ? 'dark' : 'light')
        } catch (e) {
          user.value = null
          applyTheme(getStoredTheme() ?? 'light')
          if (!(e instanceof ApiError && e.status === 401)) {
            // non-auth failure still marks initialized
          }
        } finally {
          initialized.value = true
        }
      })()
    }
    await initPromise
  }

  async function login(body: LoginRequest) {
    const result = await loginApi(body)
    user.value = result
    clearGuestTheme()
    applyTheme(result.theme === 'dark' ? 'dark' : 'light')
    return result
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      user.value = null
    }
  }

  async function signup(body: SignupRequest) {
    return signupApi(body)
  }

  function setUser(next: AuthUser) {
    user.value = next
  }

  function clear() {
    user.value = null
  }

  return {
    user,
    initialized,
    isAuthenticated,
    displayName,
    theme,
    init,
    login,
    logout,
    signup,
    setUser,
    clear,
  }
})
