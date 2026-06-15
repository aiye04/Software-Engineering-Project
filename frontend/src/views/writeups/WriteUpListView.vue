<template>
  <section>
    <div class="page-header"><h1 class="page-title">WriteUp 列表</h1><el-button type="primary" @click="router.push('/writeups/new')">新建 WriteUp</el-button></div>
    <el-card shadow="never">
      <el-table :data="writeups" stripe>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="eventTitle" label="赛事" />
        <el-table-column prop="challengeTitle" label="题目" />
        <el-table-column prop="author" label="作者" />
        <el-table-column label="标签"><template #default="{ row }"><el-tag v-for="tag in row.tags" :key="tag" class="tag">{{ tag }}</el-tag></template></el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" />
        <el-table-column label="操作" width="180"><template #default="{ row }"><el-button link type="primary" @click="router.push(`/writeups/${row.id}`)">查看</el-button><el-button link @click="router.push(`/writeups/${row.id}/edit`)">编辑</el-button></template></el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrMock } from '@/api/client'
import { mockWriteUps } from '@/api/mock'
import type { WriteUp } from '@/api/types'

const router = useRouter()
const writeups = ref<WriteUp[]>([])
onMounted(async () => { writeups.value = await getOrMock('/writeups', mockWriteUps) })
</script>

<style scoped>
.tag { margin-right: 4px; }
</style>
