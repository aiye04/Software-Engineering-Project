import axios, { AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { ApiResult } from '@/api/types'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 8000,
})

apiClient.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ message?: string }>) => {
    const message = error.response?.data?.message || error.message || '接口请求失败，已尝试使用本地演示数据。'
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.assign(`/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`)
    } else {
      ElMessage.warning(message)
    }
    return Promise.reject(error)
  },
)

export function unwrapResult<T>(payload: ApiResult<T> | T): T {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiResult<T>).data
  }
  return payload as T
}

export async function getOrMock<T>(url: string, mockData: T): Promise<T> {
  try {
    const response = await apiClient.get<ApiResult<T> | T>(url)
    return unwrapResult<T>(response.data)
  } catch {
    return mockData
  }
}
