<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">通知中心</h1>
        <p class="page-subtitle">发布公告，并跟踪重要通知的阅读状态</p>
      </div>
      <div class="header-actions">
        <el-button @click="markAllRead">全部标为已读</el-button>
        <el-button type="primary" @click="openCreate">发布通知</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-empty v-if="notifications.length === 0" description="暂无通知" />
      <el-timeline v-else>
        <el-timeline-item v-for="item in notifications" :key="item.id" :timestamp="formatDate(item.createdAt)" :type="item.read ? 'info' : 'primary'">
          <el-card shadow="never" class="notice-card">
            <div class="notice-title">
              <div>
                <strong>{{ item.title }}</strong>
                <el-tag class="notice-level" :type="levelType(item.level || item.type)">{{ levelLabel(item.level || item.type) }}</el-tag>
              </div>
              <div class="notice-actions">
                <el-tag :type="item.read ? 'info' : 'danger'">{{ item.read ? '已读' : '未读' }}</el-tag>
                <el-button v-if="!item.read" link type="primary" @click="markRead(item)">标为已读</el-button>
              </div>
            </div>
            <p>{{ item.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布通知" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="标题" prop="title">
          <el-input v-model.trim="form.title" />
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="form.level" class="full-width">
            <el-option label="普通" value="INFO" />
            <el-option label="重要" value="WARNING" />
            <el-option label="紧急" value="ERROR" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveNotification">发布</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockNotifications } from '@/api/mock'
import type { NotificationItem } from '@/api/types'

interface NotificationForm {
  title: string
  level: string
  content: string
}

const notifications = ref<NotificationItem[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<NotificationForm>({
  title: '',
  level: 'INFO',
  content: '',
})
const rules: FormRules<NotificationForm> = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
}

function formatDate(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '').replace(/Z$/, '')
}

function levelLabel(level?: string) {
  const labels: Record<string, string> = {
    INFO: '普通',
    WARNING: '重要',
    ERROR: '紧急',
    system: '系统',
    event: '赛事',
    training: '训练',
  }
  return labels[level || 'INFO'] || level || '普通'
}

function levelType(level?: string): 'success' | 'warning' | 'danger' | 'info' | 'primary' {
  if (level === 'ERROR') return 'danger'
  if (level === 'WARNING' || level === 'event') return 'warning'
  if (level === 'training') return 'success'
  if (level === 'system') return 'primary'
  return 'info'
}

function patchNotification(nextNotification: NotificationItem) {
  const index = notifications.value.findIndex((item) => item.id === nextNotification.id)
  if (index >= 0) notifications.value.splice(index, 1, nextNotification)
  else notifications.value.unshift(nextNotification)
}

async function loadNotifications() {
  notifications.value = await getOrMock('/notifications', mockNotifications)
}

function openCreate() {
  form.title = ''
  form.level = 'INFO'
  form.content = ''
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function saveNotification() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const payload = { title: form.title, level: form.level, content: form.content }
  try {
    const saved = await requestData<NotificationItem>(apiClient.post('/notifications', payload))
    patchNotification(saved)
    ElMessage.success('通知已发布')
  } catch {
    patchNotification({
      id: Date.now(),
      title: form.title,
      level: form.level,
      content: form.content,
      read: false,
      createdAt: new Date().toISOString(),
    })
    ElMessage.success('已在演示模式下发布通知')
  } finally {
    saving.value = false
    dialogVisible.value = false
  }
}

async function markRead(item: NotificationItem) {
  try {
    const saved = await requestData<NotificationItem>(apiClient.put(`/notifications/${item.id}/read`))
    patchNotification(saved)
  } catch {
    patchNotification({ ...item, read: true })
  }
}

async function markAllRead() {
  await Promise.all(notifications.value.filter((item) => !item.read).map(markRead))
  ElMessage.success('通知已全部标为已读')
}

onMounted(loadNotifications)
</script>

<style scoped>
.header-actions,
.notice-title,
.notice-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-actions,
.notice-title {
  justify-content: space-between;
}
.notice-card { width: 100%; }
.notice-level { margin-left: 8px; }
p { margin-bottom: 0; color: #4b5563; }
</style>
