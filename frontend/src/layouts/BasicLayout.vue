<template>
  <el-container class="app-shell">
    <el-aside width="236px" class="sidebar">
      <div class="brand">
        <span class="brand-icon">🏆</span>
        <span>CTF 训练平台</span>
      </div>
      <el-menu :default-active="activePath" router background-color="#111827" text-color="#cbd5e1" active-text-color="#ffffff">
        <el-menu-item index="/dashboard"><span class="menu-icon">📊</span><span>看板</span></el-menu-item>
        <el-menu-item index="/users"><span class="menu-icon">👥</span><span>用户管理</span></el-menu-item>
        <el-menu-item index="/competitions"><span class="menu-icon">🚩</span><span>赛事</span></el-menu-item>
        <el-menu-item index="/writeups"><span class="menu-icon">📝</span><span>WriteUp</span></el-menu-item>
        <el-menu-item index="/training"><span class="menu-icon">🎯</span><span>训练任务</span></el-menu-item>
        <el-menu-item index="/notifications"><span class="menu-icon">🔔</span><span>通知</span></el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div>
          <strong>{{ routeTitle }}</strong>
          <span class="muted topbar-subtitle">后端接口不可用时自动展示 mock 数据</span>
        </div>
        <el-dropdown>
          <span class="user-entry">
            <el-avatar :size="32">{{ initials }}</el-avatar>
            {{ authStore.user?.nickname || '未登录' }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>{{ authStore.user?.role || '-' }}</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activePath = computed(() => route.path)
const routeTitle = computed(() => String(route.meta.title || '工作台'))
const initials = computed(() => (authStore.user?.nickname || 'U').slice(0, 1))

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.sidebar {
  background: #111827;
  color: #fff;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 64px;
  padding: 0 20px;
  font-size: 18px;
  font-weight: 700;
}

.brand-icon,
.menu-icon {
  display: inline-flex;
  width: 24px;
  justify-content: center;
  margin-right: 8px;
}

.sidebar :deep(.el-menu) {
  border-right: 0;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.topbar-subtitle {
  margin-left: 12px;
  font-size: 13px;
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.content {
  padding: 24px;
  background: #f5f7fb;
}
</style>
