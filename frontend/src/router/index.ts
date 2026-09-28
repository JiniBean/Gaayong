import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  forceHome,
  isExcluded,
  peekRestore,
  safeRedirect,
  saveLastRoute,
} from '../composables/useLastRoute'
import AppLayout from '../layouts/AppLayout.vue'
import GuestShell from '../layouts/GuestShell.vue'
import HomeView from '../views/HomeView.vue'
import CategoryView from '../views/CategoryView.vue'
import AccountView from '../views/AccountView.vue'
import CardView from '../views/CardView.vue'
import ExpenseView from '../views/ExpenseView.vue'
import IncomeView from '../views/IncomeView.vue'
import PayView from '../views/PayView.vue'
import FixedView from '../views/FixedView.vue'
import BudgetView from '../views/BudgetView.vue'
import SignInView from '../views/SignInView.vue'
import SignUpView from '../views/SignUpView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/signin',
      component: GuestShell,
      meta: { guestOnly: true },
      children: [{ path: '', name: 'signin', component: SignInView }],
    },
    {
      path: '/signup',
      component: GuestShell,
      meta: { guestOnly: true },
      children: [{ path: '', name: 'signup', component: SignUpView }],
    },
    {
      path: '/',
      component: AppLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'home', component: HomeView },
        { path: 'category', name: 'category', component: CategoryView },
        { path: 'expense', name: 'expense', component: ExpenseView },
        { path: 'pay', name: 'pay', component: PayView },
        { path: 'income', name: 'income', component: IncomeView },
        { path: 'fixed', name: 'fixed', component: FixedView },
        { path: 'budget', name: 'budget', component: BudgetView },
        { path: 'account', name: 'account', component: AccountView },
        { path: 'card', name: 'card', component: CardView },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  await auth.init()

  if (to.matched.some((r) => r.meta.requiresAuth) && !auth.isAuthenticated) {
    return { path: '/signin', query: { redirect: to.fullPath } }
  }

  if (to.matched.some((r) => r.meta.guestOnly) && auth.isAuthenticated) {
    return safeRedirect(to.query.redirect) ?? { path: '/' }
  }

  if (auth.isAuthenticated && to.path === '/' && to.name === 'home') {
    const restored = peekRestore()
    if (restored) {
      return restored
    }
  }

  if (auth.isAuthenticated && !isExcluded(to.path)) {
    saveLastRoute(to.fullPath)
  }
})

export { forceHome }
export default router
