import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { bootstrapTheme } from './composables/useTheme'
import { setUnauthorizedHandler } from './api/client'
import { useAuthStore } from './stores/auth'

bootstrapTheme()

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

setUnauthorizedHandler(() => {
  const auth = useAuthStore(pinia)
  auth.clear()
  if (router.currentRoute.value.path !== '/signin') {
    router.push({ path: '/signin', query: { expired: 'true' } })
  }
})

app.mount('#app')
