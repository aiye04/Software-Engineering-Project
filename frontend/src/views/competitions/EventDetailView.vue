<template>
  <section>
    <div class="page-header"><h1 class="page-title">{{ event?.title || '赛事详情' }}</h1><el-button @click="router.back()">返回</el-button></div>
    <el-card v-if="event" shadow="never">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态"><el-tag>{{ statusText[event.status || 'running'] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="赛制">{{ event.format || 'Jeopardy' }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ event.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ event.endTime }}</el-descriptions-item>
        <el-descriptions-item label="主办方">{{ event.organizer || '战队内部' }}</el-descriptions-item>
        <el-descriptions-item label="说明">{{ event.description }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card v-if="event" class="section-card" shadow="never" header="题目列表">
      <el-table :data="event.challenges">
        <el-table-column prop="title" label="题目" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="score" label="分值" />
        <el-table-column prop="solved" label="解出人数" />
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrMock } from '@/api/client'
import { mockEvents } from '@/api/mock'
import type { EventItem } from '@/api/types'

const props = defineProps<{ id: string }>()
const router = useRouter()
const event = ref<EventItem>()
const statusText: Record<NonNullable<EventItem['status']>, string> = { upcoming: '未开始', running: '进行中', ended: '已结束' }
onMounted(async () => {
  const events = await getOrMock('/competitions', mockEvents)
  event.value = events.find((item) => item.id === Number(props.id)) || events[0]
})
</script>

<style scoped>
.section-card { margin-top: 16px; }
</style>
