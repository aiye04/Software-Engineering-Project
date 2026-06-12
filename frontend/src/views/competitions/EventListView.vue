<template>
  <section>
    <div class="page-header"><h1 class="page-title">赛事列表</h1><el-button type="primary">创建赛事</el-button></div>
    <el-row :gutter="16">
      <el-col v-for="event in events" :key="event.id" :xs="24" :md="12">
        <el-card class="event-card" shadow="hover">
          <template #header><div class="card-header"><strong>{{ event.title }}</strong><el-tag :type="statusType[event.status || 'running']">{{ statusText[event.status || 'running'] }}</el-tag></div></template>
          <p class="muted">{{ event.description }}</p>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="赛制">{{ event.format || 'Jeopardy' }}</el-descriptions-item>
            <el-descriptions-item label="时间">{{ event.startTime }} - {{ event.endTime }}</el-descriptions-item>
            <el-descriptions-item label="主办方">{{ event.organizer || '战队内部' }}</el-descriptions-item>
          </el-descriptions>
          <template #footer><el-button type="primary" @click="router.push(`/competitions/${event.id}`)">查看详情</el-button></template>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrMock } from '@/api/client'
import { mockEvents } from '@/api/mock'
import type { EventItem } from '@/api/types'

const router = useRouter()
const events = ref<EventItem[]>([])
const statusText: Record<NonNullable<EventItem['status']>, string> = { upcoming: '未开始', running: '进行中', ended: '已结束' }
const statusType: Record<NonNullable<EventItem['status']>, 'warning' | 'success' | 'info'> = { upcoming: 'warning', running: 'success', ended: 'info' }
onMounted(async () => { events.value = await getOrMock('/competitions', mockEvents) })
</script>

<style scoped>
.event-card { margin-bottom: 16px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>
