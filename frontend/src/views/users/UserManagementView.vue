<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">维护战队成员账号、角色和启用状态</p>
      </div>
      <el-button type="primary" @click="openCreate">新增用户</el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="users" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column label="昵称" min-width="140">
          <template #default="{ row }">{{ row.nickname || row.displayName || '-' }}</template>
        </el-table-column>
        <el-table-column label="角色" width="120">
          <template #default="{ row }"><el-tag>{{ roleLabel(row) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="isEnabled(row) ? 'success' : 'danger'">{{ isEnabled(row) ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" min-width="160">
          <template #default="{ row }">{{ row.lastLogin || row.createdAt || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link :type="isEnabled(row) ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ isEnabled(row) ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="removeUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingUser ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" :disabled="Boolean(editingUser)" placeholder="用于登录的账号" />
        </el-form-item>
        <el-form-item label="昵称" prop="displayName">
          <el-input v-model.trim="form.displayName" placeholder="页面展示名称" />
        </el-form-item>
        <el-form-item v-if="!editingUser" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" class="full-width">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.enabled" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiClient, getOrMock, requestData } from '@/api/client'
import { mockUsers } from '@/api/mock'
import type { User, UserRole } from '@/api/types'

interface UserForm {
  username: string
  displayName: string
  password: string
  role: UserRole
  enabled: boolean
}

const users = ref<User[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingUser = ref<User | null>(null)
const formRef = ref<FormInstance>()
const form = reactive<UserForm>({
  username: '',
  displayName: '',
  password: '',
  role: 'TRAINEE',
  enabled: true,
})

const roleMap: Record<string, string> = {
  ADMIN: '管理员',
  TEACHER: '老师',
  CAPTAIN: '队长',
  CORE: '核心队员',
  MEMBER: '普通队员',
  TRAINEE: '预备队员',
  admin: '管理员',
  teacher: '老师',
  captain: '队长',
  core: '核心队员',
  member: '普通队员',
  trainee: '预备队员',
}
const roleOptions = [
  { label: '管理员', value: 'ADMIN' },
  { label: '老师', value: 'TEACHER' },
  { label: '队长', value: 'CAPTAIN' },
  { label: '核心队员', value: 'CORE' },
  { label: '普通队员', value: 'MEMBER' },
  { label: '预备队员', value: 'TRAINEE' },
] as const
const rules: FormRules<UserForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  displayName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '请输入至少 6 位密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

function isEnabled(row: User) {
  return row.enabled !== false && row.status !== 'disabled'
}

function currentRole(row: User): UserRole {
  return row.role || row.roles?.[0] || 'TRAINEE'
}

function roleLabel(row: User) {
  const role = currentRole(row)
  return roleMap[String(role)] || String(role)
}

function patchUserInList(nextUser: User) {
  const index = users.value.findIndex((item) => item.id === nextUser.id)
  if (index >= 0) users.value.splice(index, 1, nextUser)
  else users.value.unshift(nextUser)
}

async function loadUsers() {
  loading.value = true
  try {
    users.value = await getOrMock('/admin/users', mockUsers)
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.username = ''
  form.displayName = ''
  form.password = ''
  form.role = 'TRAINEE'
  form.enabled = true
  formRef.value?.clearValidate()
}

function openCreate() {
  editingUser.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: User) {
  editingUser.value = row
  form.username = row.username
  form.displayName = row.displayName || row.nickname || row.username
  form.password = ''
  form.role = currentRole(row)
  form.enabled = isEnabled(row)
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function saveUser() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  const payload = {
    username: form.username,
    displayName: form.displayName,
    password: form.password,
    roles: [form.role],
    enabled: form.enabled,
  }
  try {
    const saved = editingUser.value
      ? await requestData<User>(apiClient.put(`/admin/users/${editingUser.value.id}`, {
          displayName: form.displayName,
          roles: [form.role],
          enabled: form.enabled,
        }))
      : await requestData<User>(apiClient.post('/admin/users', payload))
    patchUserInList(saved)
    ElMessage.success('用户已保存')
  } catch {
    const fallback: User = {
      id: editingUser.value?.id || Date.now(),
      username: form.username,
      displayName: form.displayName,
      roles: [form.role],
      enabled: form.enabled,
    }
    patchUserInList(fallback)
    ElMessage.success('已在演示模式下保存用户')
  } finally {
    saving.value = false
    dialogVisible.value = false
  }
}

async function toggleStatus(row: User) {
  const enabled = !isEnabled(row)
  try {
    const saved = await requestData<User>(apiClient.put(`/admin/users/${row.id}/status`, { enabled }))
    patchUserInList(saved)
    ElMessage.success(enabled ? '用户已启用' : '用户已禁用')
  } catch {
    patchUserInList({ ...row, enabled, status: enabled ? 'active' : 'disabled' })
    ElMessage.success(enabled ? '已模拟启用用户' : '已模拟禁用用户')
  }
}

async function removeUser(row: User) {
  await ElMessageBox.confirm(`确定删除用户“${row.displayName || row.username}”吗？`, '删除确认', { type: 'warning' })
  try {
    await apiClient.delete(`/admin/users/${row.id}`)
    ElMessage.success('用户已删除')
  } catch {
    ElMessage.success('已在演示模式下删除用户')
  }
  users.value = users.value.filter((item) => item.id !== row.id)
}

onMounted(loadUsers)
</script>
