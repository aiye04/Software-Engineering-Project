<template>
  <section>
    <div class="page-header"><h1 class="page-title">训练任务</h1><el-button type="primary">发布任务</el-button></div>
    <el-card shadow="never">
      <el-table :data="tasks" stripe>
        <el-table-column prop="title" label="任务" min-width="180" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="difficulty" label="难度"><template #default="{ row }"><el-tag :type="difficultyType[row.difficulty]">{{ row.difficulty }}</el-tag></template></el-table-column>
        <el-table-column prop="assignee" label="负责人" />
        <el-table-column prop="dueDate" label="截止日期" />
        <el-table-column label="进度" width="220"><template #default="{ row }"><el-progress :percentage="row.progress" /></template></el-table-column>
        <el-table-column prop="status" label="状态"><template #default="{ row }">{{ statusText[row.status] }}</template></el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getOrMock } from '@/api/client'
import { mockTrainingTasks } from '@/api/mock'
import type { TrainingTask } from '@/api/types'

const tasks = ref<TrainingTask[]>([])
const statusText: Record<TrainingTask['status'], string> = { todo: '未开始', doing: '进行中', done: '已完成' }
const difficultyType: Record<TrainingTask['difficulty'], 'success' | 'warning' | 'danger'> = { 入门: 'success', 进阶: 'warning', 困难: 'danger' }
onMounted(async () => { tasks.value = await getOrMock('/training/tasks', mockTrainingTasks) })
</script>
