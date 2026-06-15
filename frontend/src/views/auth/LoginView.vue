<template>
  <main class="login-page">
    <el-card class="login-card" shadow="always">
      <template #header>
        <div>
          <h1>CTF 赛事训练平台</h1>
          <p>使用账号登录；后端未接入时将进入演示模式。</p>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin / teacher" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="任意密码可进入 mock 模式" autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="full" @click="submit">登录</el-button>
      </el-form>
    </el-card>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.replace(String(route.query.redirect || '/dashboard'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  place-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a, #2563eb);
}
.login-card {
  width: min(420px, calc(100vw - 32px));
}
h1 {
  margin: 0 0 8px;
}
p {
  margin: 0;
  color: #64748b;
}
.full {
  width: 100%;
}
</style>
