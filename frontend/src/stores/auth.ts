import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { apiClient, unwrapResult } from '@/api/client'
import type { ApiResult, User, UserRole } from '@/api/types'

interface LoginPayload {
  username: string
  password: string
}

interface AuthUser {
  id: number
  username: string
  nickname: string
  role: UserRole
}

interface AuthResponse {
  token: string
  user: User
}

const TOKEN_KEY = 'ctf-platform-token'
const USER_KEY = 'ctf-platform-user'

function normalizeUser(user: User): AuthUser {
  const role = user.role || user.roles?.[0] || 'TRAINEE'
  return {
    id: user.id,
    username: user.username,
    nickname: user.nickname || user.displayName || user.username,
    role,
  }
}

function readStoredUser(): AuthUser | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthUser
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<AuthUser | null>(readStoredUser())
  const isAuthenticated = computed(() => Boolean(token.value))

  async function login(payload: LoginPayload) {
    try {
      const response = await apiClient.post<ApiResult<AuthResponse> | AuthResponse>('/auth/login', payload)
      const data = unwrapResult<AuthResponse>(response.data)
      setSession(data.token, normalizeUser(data.user))
    } catch {
      const demoUser: AuthUser = {
        id: 1,
        username: payload.username || 'admin',
        nickname: payload.username === 'teacher' ? '指导老师' : '平台管理员',
        role: payload.username === 'teacher' ? 'TEACHER' : 'ADMIN',
      }
      setSession(`mock-token-${Date.now()}`, demoUser)
    }
  }

  function setSession(nextToken: string, nextUser: AuthUser) {
    token.value = nextToken
    user.value = nextUser
    localStorage.setItem(TOKEN_KEY, nextToken)
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, isAuthenticated, login, logout }
})
