<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">赛事列表</h1>
        <p class="page-subtitle">创建赛事、维护比赛时间，并进入详情管理赛题</p>
      </div>
      <el-button type="primary" @click="openCreate">创建赛事</el-button>
    </div>

    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="event in events" :key="event.id" :xs="24" :md="12">
        <el-card class="event-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <strong>{{ event.title }}</strong>
              <el-tag :type="statusType[event.status || 'running']">{{ statusText[event.status || 'running'] }}</el-tag>
            </div>
          </template>
          <p class="muted">{{ event.description }}</p>
          <el-descriptions :column="1" size="small">
            <el-descriptions-item label="赛制">{{ event.format || 'Jeopardy' }}</el-descriptions-item>
            <el-descriptions-item label="时间">{{ formatDateTime(event.startTime) }} - {{ formatDateTime(event.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="主办方">{{ event.organizer || '战队内部' }}</el-descriptions-item>
          </el-descriptions>
          <template #footer>
            <div class="footer-actions">
              <el-button type="primary" @click="router.push(`/competitions/${event.id}`)">查看详情</el-button>
              <el-button @click="openEdit(event)">编辑</el-button>
              <el-button type="danger" @click="removeEvent(event)">删除</el-button>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && events.length === 0" description="暂无赛事" />

    <el-dialog v-model="dialogVisible" :title="editingEvent ? '编辑赛事' : '创建赛事'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="赛事名称" prop="title">
          <el-input v-model.trim="form.title" placeholder="例如 校内 CTF 夏季赛" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="full-width">
            <el-option label="未开始" value="upcoming" />
            <el-option label="进行中" value="running" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-form-item>
        <el-form-item label="赛制">
          <el-input v-model.trim="form.format" placeholder="Jeopardy / AWD / 个人赛" />
        </el-form-item>
        <el-form-item label="主办方">
          <el-input v-model.trim="form.organizer" placeholder="战队内部 / 训练中心" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss[Z]" class="full-width" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss[Z]" class="full-width" />
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveEvent">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockEvents } from '@/api/mock'
import type { EventItem } from '@/api/types'

type EventStatus = NonNullable<EventItem['status']>

interface EventForm {
  title: string
  status: EventStatus
  format: string
  organizer: string
  startTime: string
  endTime: string
  description: string
}

const router = useRouter()
const events = ref<EventItem[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingEvent = ref<EventItem | null>(null)
const formRef = ref<FormInstance>()
const form = reactive<EventForm>({
  title: '',
  status: 'running',
  format: 'Jeopardy',
  organizer: '战队内部',
  startTime: '',
  endTime: '',
  description: '',
})
const rules: FormRules<EventForm> = {
  title: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入赛事说明', trigger: 'blur' }],
}
const statusText: Record<EventStatus, string> = { upcoming: '未开始', running: '进行中', ended: '已结束' }
const statusType: Record<EventStatus, 'warning' | 'success' | 'info'> = { upcoming: 'warning', running: 'success', ended: 'info' }

function toDatetimeValue(value?: string) {
  if (!value) return new Date().toISOString().slice(0, 19) + 'Z'
  return value.includes('T') ? value.slice(0, 19) + 'Z' : value
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '').replace(/Z$/, '')
}

function patchEventInList(nextEvent: EventItem) {
  const index = events.value.findIndex((item) => item.id === nextEvent.id)
  if (index >= 0) events.value.splice(index, 1, nextEvent)
  else events.value.unshift(nextEvent)
}

async function loadEvents() {
  loading.value = true
  try {
    events.value = await getOrMock('/competitions', mockEvents)
  } finally {
    loading.value = false
  }
}

function resetForm() {
  const now = new Date()
  const end = new Date(now.getTime() + 8 * 60 * 60 * 1000)
  form.title = ''
  form.status = 'running'
  form.format = 'Jeopardy'
  form.organizer = '战队内部'
  form.startTime = now.toISOString().slice(0, 19) + 'Z'
  form.endTime = end.toISOString().slice(0, 19) + 'Z'
  form.description = ''
  formRef.value?.clearValidate()
}

function openCreate() {
  editingEvent.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(event: EventItem) {
  editingEvent.value = event
  form.title = event.title
  form.status = event.status || 'running'
  form.format = event.format || 'Jeopardy'
  form.organizer = event.organizer || '战队内部'
  form.startTime = toDatetimeValue(event.startTime)
  form.endTime = toDatetimeValue(event.endTime)
  form.description = event.description
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function saveEvent() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const payload = {
    title: form.title,
    description: form.description,
    startTime: form.startTime,
    endTime: form.endTime,
  }
  try {
    const saved = editingEvent.value
      ? await requestData<EventItem>(apiClient.put(`/competitions/${editingEvent.value.id}`, payload))
      : await requestData<EventItem>(apiClient.post('/competitions', payload))
    patchEventInList({ ...form, ...saved, challenges: saved.challenges || editingEvent.value?.challenges || [] })
    ElMessage.success('赛事已保存')
  } catch {
    patchEventInList({
      id: editingEvent.value?.id || Date.now(),
      title: form.title,
      status: form.status,
      format: form.format,
      organizer: form.organizer,
      startTime: form.startTime,
      endTime: form.endTime,
      description: form.description,
      challenges: editingEvent.value?.challenges || [],
    })
    ElMessage.success('已在演示模式下保存赛事')
  } finally {
    saving.value = false
    dialogVisible.value = false
  }
}

async function removeEvent(event: EventItem) {
  await ElMessageBox.confirm(`确定删除赛事“${event.title}”吗？`, '删除确认', { type: 'warning' })
  try {
    await apiClient.delete(`/competitions/${event.id}`)
    ElMessage.success('赛事已删除')
  } catch {
    ElMessage.success('已在演示模式下删除赛事')
  }
  events.value = events.value.filter((item) => item.id !== event.id)
}

onMounted(loadEvents)
</script>

<style scoped>
.event-card { margin-bottom: 16px; }
.card-header,
.footer-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.footer-actions {
  justify-content: flex-start;
  flex-wrap: wrap;
}
</style>
