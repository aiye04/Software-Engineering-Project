<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">WriteUp 列表</h1>
        <p class="page-subtitle">沉淀赛题复盘、关键 payload 和训练经验</p>
      </div>
      <el-button type="primary" @click="router.push('/writeups/new')">新建 WriteUp</el-button>
    </div>
    <el-card shadow="never">
      <el-table v-loading="loading" :data="writeups" stripe>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column label="分类">
          <template #default="{ row }">{{ row.category || '-' }}</template>
        </el-table-column>
        <el-table-column prop="eventTitle" label="赛事" />
        <el-table-column prop="challengeTitle" label="题目" />
        <el-table-column label="作者"><template #default="{ row }">{{ row.author || (row.authorId ? `用户 ${row.authorId}` : '-') }}</template></el-table-column>
        <el-table-column label="标签"><template #default="{ row }"><el-tag v-for="tag in row.tags || []" :key="tag" class="tag">{{ tag }}</el-tag></template></el-table-column>
        <el-table-column label="更新时间"><template #default="{ row }">{{ formatDate(row.updatedAt || row.createdAt) }}</template></el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/writeups/${row.id}`)">查看</el-button>
            <el-button link @click="router.push(`/writeups/${row.id}/edit`)">编辑</el-button>
            <el-button link type="danger" @click="removeWriteUp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiClient, getOrMock } from '@/api/client'
import { mockWriteUps } from '@/api/mock'
import type { WriteUp } from '@/api/types'

const router = useRouter()
const writeups = ref<WriteUp[]>([])
const loading = ref(false)

function formatDate(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '').replace(/Z$/, '')
}

async function loadWriteUps() {
  loading.value = true
  try {
    writeups.value = await getOrMock('/writeups', mockWriteUps)
  } finally {
    loading.value = false
  }
}

async function removeWriteUp(row: WriteUp) {
  await ElMessageBox.confirm(`确定删除 WriteUp“${row.title}”吗？`, '删除确认', { type: 'warning' })
  try {
    await apiClient.delete(`/writeups/${row.id}`)
    ElMessage.success('WriteUp 已删除')
  } catch {
    ElMessage.success('已在演示模式下删除 WriteUp')
  }
  writeups.value = writeups.value.filter((item) => item.id !== row.id)
}

onMounted(loadWriteUps)
</script>

<style scoped>
.tag { margin-right: 4px; }
</style>
