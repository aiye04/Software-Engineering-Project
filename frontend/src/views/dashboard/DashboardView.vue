<template>
  <section>
    <div class="page-header"><h1 class="page-title">运营看板</h1></div>
    <div class="card-grid">
      <el-card v-for="item in summary" :key="item.label" shadow="never">
        <div class="metric"><span>{{ item.label }}</span><strong>{{ item.value }}</strong></div>
      </el-card>
    </div>
    <el-row :gutter="16" class="dashboard-row">
      <el-col :span="14"><el-card shadow="never" header="分类能力雷达"><div ref="chartRef" class="chart" /></el-card></el-col>
      <el-col :span="10"><el-card shadow="never" header="近期动态"><el-timeline><el-timeline-item v-for="activity in (stats.recentActivities || [])" :key="activity.id" :timestamp="activity.time">{{ activity.action }}</el-timeline-item></el-timeline></el-card></el-col>
    </el-row>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getOrMock } from '@/api/client'
import type { DashboardStats } from '@/api/types'
import { mockDashboard } from '@/api/mock'

const stats = ref<DashboardStats>(mockDashboard)
const chartRef = ref<HTMLDivElement>()
const summary = computed(() => [
  { label: '注册用户', value: stats.value.users ?? stats.value.userCount ?? 0 },
  { label: '进行中赛事', value: stats.value.runningEvents ?? stats.value.competitionCount ?? 0 },
  { label: 'WriteUp 总数', value: stats.value.writeups ?? stats.value.writeUpCount ?? 0 },
  { label: '待完成任务', value: stats.value.pendingTasks ?? stats.value.trainingTaskCount ?? 0 },
])

onMounted(async () => {
  stats.value = await getOrMock('/dashboard', mockDashboard)
  if (chartRef.value) {
    echarts.init(chartRef.value).setOption({
      tooltip: {},
      radar: { indicator: (stats.value.categoryScores || mockDashboard.categoryScores || []).map((item) => ({ name: item.category, max: 100 })) },
      series: [{ type: 'radar', data: [{ value: (stats.value.categoryScores || mockDashboard.categoryScores || []).map((item) => item.score), name: '能力值' }] }],
    })
  }
})
</script>

<style scoped>
.metric { display: flex; flex-direction: column; gap: 12px; }
.metric strong { font-size: 32px; color: #2563eb; }
.dashboard-row { margin-top: 16px; }
.chart { height: 360px; }
</style>
