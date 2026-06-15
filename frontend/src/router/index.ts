import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BasicLayout from '@/layouts/BasicLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { public: true, title: '登录' },
  },
  {
    path: '/',
    component: BasicLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { title: '看板' } },
      { path: 'users', name: 'users', component: () => import('@/views/users/UserManagementView.vue'), meta: { title: '用户管理' } },
      { path: 'competitions', name: 'events', component: () => import('@/views/competitions/EventListView.vue'), meta: { title: '赛事列表' } },
      { path: 'competitions/:id', name: 'event-detail', component: () => import('@/views/competitions/EventDetailView.vue'), meta: { title: '赛事详情' }, props: true },
      { path: 'writeups', name: 'writeups', component: () => import('@/views/writeups/WriteUpListView.vue'), meta: { title: 'WriteUp 列表' } },
      { path: 'writeups/new', name: 'writeup-new', component: () => import('@/views/writeups/WriteUpEditorView.vue'), meta: { title: '新建 WriteUp' } },
      { path: 'writeups/:id', name: 'writeup-detail', component: () => import('@/views/writeups/WriteUpDetailView.vue'), meta: { title: 'WriteUp 详情' }, props: true },
      { path: 'writeups/:id/edit', name: 'writeup-edit', component: () => import('@/views/writeups/WriteUpEditorView.vue'), meta: { title: '编辑 WriteUp' }, props: true },
      { path: 'training', name: 'training', component: () => import('@/views/training/TrainingTasksView.vue'), meta: { title: '训练任务' } },
      { path: 'notifications', name: 'notifications', component: () => import('@/views/notifications/NotificationView.vue'), meta: { title: '通知' } },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  document.title = `${String(to.meta.title || '平台')} - CTF 赛事训练平台`
  if (!to.meta.public && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && authStore.isAuthenticated) {
    return { name: 'dashboard' }
  }
  return true
})

export default router
