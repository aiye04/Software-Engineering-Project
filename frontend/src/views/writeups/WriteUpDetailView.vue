<template>
  <section>
    <div class="page-header"><h1 class="page-title">{{ writeup?.title || 'WriteUp 详情' }}</h1><div><el-button @click="router.back()">返回</el-button><el-button type="primary" @click="router.push(`/writeups/${props.id}/edit`)">编辑</el-button></div></div>
    <el-card v-if="writeup" shadow="never">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="赛事">{{ writeup.eventTitle }}</el-descriptions-item>
        <el-descriptions-item label="题目">{{ writeup.challengeTitle }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ writeup.author }}</el-descriptions-item>
        <el-descriptions-item label="可见性">{{ visibilityText[writeup.visibility] }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ writeup.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      <pre class="content">{{ writeup.content }}</pre>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrMock } from '@/api/client'
import { mockWriteUps } from '@/api/mock'
import type { WriteUp } from '@/api/types'

const props = defineProps<{ id: string }>()
const router = useRouter()
const writeup = ref<WriteUp>()
const visibilityText: Record<WriteUp['visibility'], string> = { public: '公开', team: '团队可见', private: '仅自己' }
onMounted(async () => {
  const list = await getOrMock('/writeups', mockWriteUps)
  writeup.value = list.find((item) => item.id === Number(props.id)) || list[0]
})
</script>

<style scoped>
.content { margin-top: 20px; white-space: pre-wrap; line-height: 1.7; font-family: ui-monospace, SFMono-Regular, Menlo, monospace; }
</style>
