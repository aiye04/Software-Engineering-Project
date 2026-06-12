<template>
  <section>
    <div class="page-header"><h1 class="page-title">通知中心</h1><el-button @click="markAllRead">全部标为已读</el-button></div>
    <el-card shadow="never">
      <el-empty v-if="notifications.length === 0" description="暂无通知" />
      <el-timeline v-else>
        <el-timeline-item v-for="item in notifications" :key="item.id" :timestamp="item.createdAt" :type="item.read ? 'info' : 'primary'">
          <el-card shadow="never" class="notice-card">
            <div class="notice-title"><strong>{{ item.title }}</strong><el-tag :type="item.read ? 'info' : 'danger'">{{ item.read ? '已读' : '未读' }}</el-tag></div>
            <p>{{ item.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getOrMock } from '@/api/client'
import { mockNotifications } from '@/api/mock'
import type { NotificationItem } from '@/api/types'

const notifications = ref<NotificationItem[]>([])
function markAllRead() { notifications.value = notifications.value.map((item) => ({ ...item, read: true })) }
onMounted(async () => { notifications.value = await getOrMock('/notifications', mockNotifications) })
</script>

<style scoped>
.notice-card { width: 100%; }
.notice-title { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
p { margin-bottom: 0; color: #4b5563; }
</style>
