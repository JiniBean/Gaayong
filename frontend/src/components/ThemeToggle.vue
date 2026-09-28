<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useAuthStore } from '../stores/auth'
import { applyTheme, persistGuestTheme, type ThemeMode } from '../composables/useTheme'
import { patchTheme } from '../api/user'

const props = withDefaults(
  defineProps<{
    variant?: 'pc' | 'drawer'
  }>(),
  { variant: 'drawer' },
)

const auth = useAuthStore()

function readBodyTheme(): ThemeMode {
  return document.body.classList.contains('dark') ? 'dark' : 'light'
}

const localMode = ref<ThemeMode>(
  auth.isAuthenticated ? auth.theme : readBodyTheme(),
)

watch(
  () => auth.theme,
  (t) => {
    if (auth.isAuthenticated) localMode.value = t
  },
)

watch(
  () => auth.isAuthenticated,
  (loggedIn) => {
    localMode.value = loggedIn ? auth.theme : readBodyTheme()
  },
)

const mode = computed(() => localMode.value)

async function setTheme(next: ThemeMode) {
  if (auth.isAuthenticated) {
    await patchTheme(next)
    if (auth.user) {
      auth.setUser({ ...auth.user, theme: next })
    }
    applyTheme(next)
    localMode.value = next
    return
  }
  persistGuestTheme(next)
  localMode.value = next
}

function togglePc() {
  setTheme(mode.value === 'light' ? 'dark' : 'light')
}
</script>

<template>
  <div
    v-if="variant === 'pc'"
    class="theme-pc d:none md:d:flex cursor:pointer pl:2"
    title="테마 변경"
    @click="togglePc"
  >
    <div class="light-mode fl-grow:1 icon icon:sun icon-color:accent-3">light</div>
    <div class="dark-mode fl-grow:1 icon icon:moon-fill icon-color:base-1">dark</div>
  </div>

  <div
    v-else
    class="theme d:flex as:end w:fit-content bd bd-radius:1 cursor:pointer"
    :class="mode === 'light' ? 'bd-color:base-2' : 'bd-color:base-10'"
  >
    <div
      class="light-mode icon icon:sun icon-size:6"
      :class="mode === 'light' ? 'icon-color:sub-1 bg-color:base-3' : 'icon-color:base-5'"
      @click="setTheme('light')"
    >
      light
    </div>
    <div
      class="dark-mode icon icon:moon icon-size:6"
      :class="mode === 'dark' ? 'icon-color:base-2 bg-color:base-10' : 'icon-color:base-4'"
      @click="setTheme('dark')"
    >
      dark
    </div>
  </div>
</template>
