<template>
  <section>
    <div class="page-header"><h1 class="page-title">用户管理</h1><el-button type="primary">新增用户</el-button></div>
    <el-card shadow="never">
      <el-table :data="users" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="昵称"><template #default="{ row }">{{ row.nickname || row.displayName }}</template></el-table-column>
        <el-table-column label="角色"><template #default="{ row }"><el-tag>{{ roleLabel(row) }}</el-tag></template></el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态"><template #default="{ row }"><el-tag :type="row.enabled === false || row.status === 'disabled' ? 'danger' : 'success'">{{ row.enabled === false || row.status === 'disabled' ? '禁用' : '启用' }}</el-tag></template></el-table-column>
        <el-table-column prop="lastLogin" label="最后登录" />
        <el-table-column label="操作" width="180"><template #default><el-button link type="primary">编辑</el-button><el-button link type="danger">重置密码</el-button></template></el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getOrMock } from '@/api/client'
import { mockUsers } from '@/api/mock'
import type { User } from '@/api/types'

const users = ref<User[]>([])
const roleMap: Record<string, string> = { ADMIN: '管理员', TEACHER: '老师', CAPTAIN: '队长', CORE: '核心队员', MEMBER: '普通队员', TRAINEE: '预备队员', admin: '管理员', teacher: '老师', captain: '队长', core: '核心队员', member: '普通队员', trainee: '预备队员' }
function roleLabel(row: User) {
  const role = row.role || row.roles?.[0] || 'TRAINEE'
  return roleMap[String(role)] || String(role)
}
onMounted(async () => { users.value = await getOrMock('/admin/users', mockUsers) })
</script>
