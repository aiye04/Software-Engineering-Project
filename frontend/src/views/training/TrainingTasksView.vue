<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">训练任务</h1>
        <p class="page-subtitle">发布训练任务、跟踪进度，并提交训练结果</p>
      </div>
      <el-button type="primary" @click="openCreate">发布任务</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="tasks" stripe>
        <el-table-column prop="title" label="任务" min-width="180" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }"><el-tag :type="difficultyType[taskDifficulty(row)]">{{ taskDifficulty(row) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="负责人" width="120">
          <template #default="{ row }">{{ row.assignee || (row.assigneeId ? `用户 ${row.assigneeId}` : '-') }}</template>
        </el-table-column>
        <el-table-column label="截止日期" min-width="150">
          <template #default="{ row }">{{ formatDate(row.dueDate || row.dueAt) }}</template>
        </el-table-column>
        <el-table-column label="进度" width="220">
          <template #default="{ row }"><el-progress :percentage="taskProgress(row)" /></template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ statusText[taskStatus(row)] }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openSubmit(row)">提交</el-button>
            <el-button link @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="removeTask(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingTask ? '编辑任务' : '发布任务'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model.trim="form.title" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" class="full-width" filterable allow-create default-first-option>
            <el-option label="Web" value="Web" />
            <el-option label="Crypto" value="Crypto" />
            <el-option label="Pwn" value="Pwn" />
            <el-option label="Reverse" value="Reverse" />
            <el-option label="Misc" value="Misc" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty" class="full-width">
            <el-option label="入门" value="入门" />
            <el-option label="进阶" value="进阶" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model.trim="form.assignee" placeholder="例如 陈同学 / 训练一队" />
        </el-form-item>
        <el-form-item label="负责人ID">
          <el-input-number v-model="form.assigneeId" :min="1" class="full-width" />
        </el-form-item>
        <el-form-item label="截止日期" prop="dueAt">
          <el-date-picker v-model="form.dueAt" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss[Z]" class="full-width" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="full-width">
            <el-option label="未开始" value="todo" />
            <el-option label="进行中" value="doing" />
            <el-option label="已完成" value="done" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度">
          <el-slider v-model="form.progress" :step="5" show-input />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTask">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitVisible" title="提交训练结果" width="520px">
      <el-form label-width="88px">
        <el-form-item label="任务">
          <el-input :model-value="submittingTask?.title" disabled />
        </el-form-item>
        <el-form-item label="提交内容">
          <el-input v-model="submissionContent" type="textarea" :rows="6" placeholder="填写 WriteUp 链接、解题记录或完成说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitTask">提交</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockTrainingTasks } from '@/api/mock'
import type { TrainingTask } from '@/api/types'

type TaskStatus = NonNullable<TrainingTask['status']>
type TaskDifficulty = NonNullable<TrainingTask['difficulty']>

interface TaskForm {
  title: string
  description: string
  category: string
  difficulty: TaskDifficulty
  status: TaskStatus
  dueAt: string
  assignee: string
  assigneeId: number
  progress: number
}

const tasks = ref<TrainingTask[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const submitVisible = ref(false)
const submitting = ref(false)
const editingTask = ref<TrainingTask | null>(null)
const submittingTask = ref<TrainingTask | null>(null)
const submissionContent = ref('')
const formRef = ref<FormInstance>()
const form = reactive<TaskForm>({
  title: '',
  description: '',
  category: 'Web',
  difficulty: '入门',
  status: 'todo',
  dueAt: '',
  assignee: '',
  assigneeId: 1,
  progress: 0,
})
const rules: FormRules<TaskForm> = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  dueAt: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
}
const statusText: Record<TaskStatus, string> = { todo: '未开始', doing: '进行中', done: '已完成' }
const difficultyType: Record<TaskDifficulty, 'success' | 'warning' | 'danger'> = { 入门: 'success', 进阶: 'warning', 困难: 'danger' }

function taskStatus(row: TrainingTask): TaskStatus {
  return row.status || 'todo'
}

function taskDifficulty(row: TrainingTask): TaskDifficulty {
  return row.difficulty || '入门'
}

function taskProgress(row: TrainingTask) {
  if (typeof row.progress === 'number') return row.progress
  return taskStatus(row) === 'done' ? 100 : taskStatus(row) === 'doing' ? 50 : 0
}

function formatDate(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '').replace(/Z$/, '')
}

function toDatetimeValue(value?: string) {
  if (!value) return new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 19) + 'Z'
  return value.includes('T') ? value.slice(0, 19) + 'Z' : value
}

function patchTaskInList(nextTask: TrainingTask) {
  const index = tasks.value.findIndex((item) => item.id === nextTask.id)
  if (index >= 0) tasks.value.splice(index, 1, nextTask)
  else tasks.value.unshift(nextTask)
}

async function loadTasks() {
  loading.value = true
  try {
    tasks.value = await getOrMock('/training/tasks', mockTrainingTasks)
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.title = ''
  form.description = ''
  form.category = 'Web'
  form.difficulty = '入门'
  form.status = 'todo'
  form.dueAt = toDatetimeValue()
  form.assignee = ''
  form.assigneeId = 1
  form.progress = 0
  formRef.value?.clearValidate()
}

function openCreate() {
  editingTask.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: TrainingTask) {
  editingTask.value = row
  form.title = row.title
  form.description = row.description || ''
  form.category = row.category || 'Web'
  form.difficulty = taskDifficulty(row)
  form.status = taskStatus(row)
  form.dueAt = toDatetimeValue(row.dueAt || row.dueDate)
  form.assignee = row.assignee || ''
  form.assigneeId = row.assigneeId || 1
  form.progress = taskProgress(row)
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function saveTask() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const payload = {
    title: form.title,
    description: form.description,
    category: form.category,
    dueAt: form.dueAt,
    assigneeId: form.assigneeId,
  }
  try {
    const saved = editingTask.value
      ? await requestData<TrainingTask>(apiClient.put(`/training/tasks/${editingTask.value.id}`, payload))
      : await requestData<TrainingTask>(apiClient.post('/training/tasks', payload))
    patchTaskInList({
      ...saved,
      difficulty: form.difficulty,
      status: form.status,
      progress: form.progress,
      assignee: form.assignee,
    })
    ElMessage.success('训练任务已保存')
  } catch {
    patchTaskInList({
      id: editingTask.value?.id || Date.now(),
      title: form.title,
      description: form.description,
      category: form.category,
      difficulty: form.difficulty,
      status: form.status,
      dueAt: form.dueAt,
      dueDate: form.dueAt,
      assignee: form.assignee,
      assigneeId: form.assigneeId,
      progress: form.progress,
    })
    ElMessage.success('已在演示模式下保存训练任务')
  } finally {
    saving.value = false
    dialogVisible.value = false
  }
}

function openSubmit(row: TrainingTask) {
  submittingTask.value = row
  submissionContent.value = ''
  submitVisible.value = true
}

async function submitTask() {
  if (!submittingTask.value || !submissionContent.value.trim()) {
    ElMessage.warning('请填写提交内容')
    return
  }
  submitting.value = true
  try {
    await apiClient.post(`/training/tasks/${submittingTask.value.id}/submissions`, { content: submissionContent.value })
    ElMessage.success('训练结果已提交')
  } catch {
    ElMessage.success('已在演示模式下模拟提交')
  } finally {
    submitting.value = false
    submitVisible.value = false
  }
}

async function removeTask(row: TrainingTask) {
  await ElMessageBox.confirm(`确定删除训练任务“${row.title}”吗？`, '删除确认', { type: 'warning' })
  try {
    await apiClient.delete(`/training/tasks/${row.id}`)
    ElMessage.success('训练任务已删除')
  } catch {
    ElMessage.success('已在演示模式下删除训练任务')
  }
  tasks.value = tasks.value.filter((item) => item.id !== row.id)
}

onMounted(loadTasks)
</script>
