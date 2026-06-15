<template>
  <section>
    <div class="page-header"><h1 class="page-title">{{ props.id ? '编辑 WriteUp' : '新建 WriteUp' }}</h1></div>
    <el-card shadow="never">
      <el-form :model="form" label-width="96px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" class="full-width" filterable allow-create default-first-option>
            <el-option label="Web" value="web" />
            <el-option label="Crypto" value="crypto" />
            <el-option label="Pwn" value="pwn" />
            <el-option label="Reverse" value="reverse" />
            <el-option label="Misc" value="misc" />
          </el-select>
        </el-form-item>
        <el-form-item label="赛事"><el-input v-model="form.eventTitle" /></el-form-item>
        <el-form-item label="题目"><el-input v-model="form.challengeTitle" /></el-form-item>
        <el-form-item label="可见性"><el-select v-model="form.visibility"><el-option label="公开" value="public" /><el-option label="团队可见" value="team" /><el-option label="仅自己" value="private" /></el-select></el-form-item>
        <el-form-item label="标签"><el-input v-model="tagInput" placeholder="用英文逗号分隔，例如 Web,SQLi" /></el-form-item>
        <el-form-item label="正文"><el-input v-model="form.content" type="textarea" :rows="14" placeholder="支持后端后可替换为 Markdown 编辑器" /></el-form-item>
        <el-form-item><el-button type="primary" @click="save">保存</el-button><el-button @click="router.back()">取消</el-button></el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockWriteUps } from '@/api/mock'
import type { WriteUp } from '@/api/types'

const props = defineProps<{ id?: string }>()
const router = useRouter()
const tagInput = ref('')
const form = reactive<Omit<WriteUp, 'id' | 'updatedAt' | 'tags'>>({
  title: '',
  category: 'web',
  eventTitle: '',
  challengeTitle: '',
  author: '当前用户',
  visibility: 'team',
  content: '',
})

onMounted(async () => {
  if (!props.id) return
  let item: WriteUp | undefined
  try {
    item = await requestData<WriteUp>(apiClient.get(`/writeups/${props.id}`))
  } catch {
    const list = await getOrMock('/writeups', mockWriteUps)
    item = list.find((writeup) => writeup.id === Number(props.id))
  }
  if (item) {
    Object.assign(form, item)
    tagInput.value = (item.tags || []).join(',')
  }
})

async function save() {
  const tags = tagInput.value.split(',').map((tag) => tag.trim()).filter(Boolean)
  const payload = { ...form, tags, authorId: 1 }
  try {
    if (props.id) await apiClient.put(`/writeups/${props.id}`, payload)
    else await apiClient.post('/writeups', payload)
    ElMessage.success('保存成功')
  } catch {
    ElMessage.success('已在演示模式下模拟保存')
  }
  router.push('/writeups')
}
</script>
