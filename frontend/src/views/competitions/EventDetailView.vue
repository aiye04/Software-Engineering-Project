<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">{{ event?.title || '赛事详情' }}</h1>
        <p class="page-subtitle">查看赛事信息，维护题目并跟踪认领状态</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="openChallengeCreate">添加题目</el-button>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </div>

    <el-card v-if="event" shadow="never">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态"><el-tag>{{ statusText[event.status || 'running'] }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="赛制">{{ event.format || 'Jeopardy' }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDate(event.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDate(event.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="主办方">{{ event.organizer || '战队内部' }}</el-descriptions-item>
        <el-descriptions-item label="说明">{{ event.description }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="event" class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>题目列表</span>
          <el-button type="primary" plain @click="openChallengeCreate">添加题目</el-button>
        </div>
      </template>
      <el-table :data="event.challenges || []">
        <el-table-column prop="title" label="题目" min-width="160" />
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column prop="score" label="分值" width="90" />
        <el-table-column prop="solved" label="解出人数" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }"><el-tag :type="challengeStatusType(row.status)">{{ challengeStatusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="认领人" width="110">
          <template #default="{ row }">{{ row.claimedBy ? `用户 ${row.claimedBy}` : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="claimChallenge(row)">认领</el-button>
            <el-button link type="success" @click="markSolved(row)">标记解出</el-button>
            <el-button link @click="openChallengeEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="challengeDialogVisible" :title="editingChallenge ? '编辑题目' : '添加题目'" width="540px">
      <el-form ref="challengeFormRef" :model="challengeForm" :rules="challengeRules" label-width="88px">
        <el-form-item label="题目名称" prop="title">
          <el-input v-model.trim="challengeForm.title" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="challengeForm.category" class="full-width" filterable allow-create default-first-option>
            <el-option label="Web" value="Web" />
            <el-option label="Crypto" value="Crypto" />
            <el-option label="Pwn" value="Pwn" />
            <el-option label="Reverse" value="Reverse" />
            <el-option label="Misc" value="Misc" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="challengeForm.score" :min="1" :max="1000" class="full-width" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="challengeForm.status" class="full-width">
            <el-option label="待认领" value="TODO" />
            <el-option label="已认领" value="CLAIMED" />
            <el-option label="已解出" value="SOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="challengeForm.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="challengeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingChallenge" @click="saveChallenge">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockEvents } from '@/api/mock'
import type { Challenge, EventItem } from '@/api/types'

interface ChallengeForm {
  title: string
  category: string
  score: number
  status: string
  description: string
}

const props = defineProps<{ id: string }>()
const router = useRouter()
const event = ref<EventItem>()
const challengeDialogVisible = ref(false)
const savingChallenge = ref(false)
const editingChallenge = ref<Challenge | null>(null)
const challengeFormRef = ref<FormInstance>()
const challengeForm = reactive<ChallengeForm>({
  title: '',
  category: 'Web',
  score: 100,
  status: 'TODO',
  description: '',
})
const challengeRules: FormRules<ChallengeForm> = {
  title: [{ required: true, message: '请输入题目名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  score: [{ required: true, message: '请输入分值', trigger: 'change' }],
}
const statusText: Record<NonNullable<EventItem['status']>, string> = { upcoming: '未开始', running: '进行中', ended: '已结束' }

function formatDate(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '').replace(/Z$/, '')
}

function challengeStatusText(status?: string) {
  const map: Record<string, string> = { TODO: '待认领', CLAIMED: '已认领', SOLVED: '已解出' }
  return map[status || 'TODO'] || status || '待认领'
}

function challengeStatusType(status?: string): 'info' | 'warning' | 'success' {
  if (status === 'SOLVED') return 'success'
  if (status === 'CLAIMED') return 'warning'
  return 'info'
}

function patchChallenge(nextChallenge: Challenge) {
  if (!event.value) return
  const challenges = event.value.challenges || []
  const index = challenges.findIndex((item) => item.id === nextChallenge.id)
  if (index >= 0) challenges.splice(index, 1, nextChallenge)
  else challenges.unshift(nextChallenge)
  event.value.challenges = [...challenges]
}

async function loadEvent() {
  try {
    event.value = await requestData<EventItem>(apiClient.get(`/competitions/${props.id}`))
  } catch {
    const events = await getOrMock('/competitions', mockEvents)
    event.value = events.find((item) => item.id === Number(props.id)) || events[0]
  }
}

function resetChallengeForm() {
  challengeForm.title = ''
  challengeForm.category = 'Web'
  challengeForm.score = 100
  challengeForm.status = 'TODO'
  challengeForm.description = ''
  challengeFormRef.value?.clearValidate()
}

function openChallengeCreate() {
  editingChallenge.value = null
  resetChallengeForm()
  challengeDialogVisible.value = true
}

function openChallengeEdit(row: Challenge) {
  editingChallenge.value = row
  challengeForm.title = row.title
  challengeForm.category = row.category
  challengeForm.score = row.score
  challengeForm.status = row.status || 'TODO'
  challengeForm.description = row.description || ''
  challengeFormRef.value?.clearValidate()
  challengeDialogVisible.value = true
}

async function saveChallenge() {
  if (!event.value) return
  const valid = await challengeFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingChallenge.value = true
  const payload = { ...challengeForm }
  try {
    const saved = editingChallenge.value
      ? await requestData<Challenge>(apiClient.put(`/competitions/challenges/${editingChallenge.value.id}`, payload))
      : await requestData<Challenge>(apiClient.post(`/competitions/${event.value.id}/challenges`, payload))
    patchChallenge(saved)
    ElMessage.success('题目已保存')
  } catch {
    patchChallenge({
      id: editingChallenge.value?.id || Date.now(),
      competitionId: event.value.id,
      title: challengeForm.title,
      category: challengeForm.category,
      score: challengeForm.score,
      status: challengeForm.status,
      description: challengeForm.description,
      solved: editingChallenge.value?.solved || 0,
      claimedBy: editingChallenge.value?.claimedBy,
    })
    ElMessage.success('已在演示模式下保存题目')
  } finally {
    savingChallenge.value = false
    challengeDialogVisible.value = false
  }
}

async function claimChallenge(row: Challenge) {
  try {
    const saved = await requestData<Challenge>(apiClient.put(`/competitions/challenges/${row.id}/claim`))
    patchChallenge(saved)
    ElMessage.success('题目已认领')
  } catch {
    patchChallenge({ ...row, status: 'CLAIMED', claimedBy: 1 })
    ElMessage.success('已在演示模式下认领题目')
  }
}

async function markSolved(row: Challenge) {
  try {
    const saved = await requestData<Challenge>(apiClient.put(`/competitions/challenges/${row.id}/status`, { status: 'SOLVED' }))
    patchChallenge(saved)
    ElMessage.success('题目已标记解出')
  } catch {
    patchChallenge({ ...row, status: 'SOLVED', solved: Math.max(row.solved || 0, 1) })
    ElMessage.success('已在演示模式下标记解出')
  }
}

onMounted(loadEvent)
</script>

<style scoped>
.section-card { margin-top: 16px; }
.header-actions,
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
</style>
