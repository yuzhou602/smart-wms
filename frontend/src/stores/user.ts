import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface UserInfo {
  userId: number
  username: string
  realName: string
  avatar?: string
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  let restoring: Promise<boolean> | null = null

  async function login(username: string, password: string) {
    const res = await request.post('/auth/login', { username, password })
    const data = res.data
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      permissions: data.permissions
    }
    localStorage.setItem('token', data.token)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  async function restoreSession(): Promise<boolean> {
    if (!token.value) return false
    if (userInfo.value) return true
    if (restoring) return restoring
    restoring = request.get('/auth/current').then((res) => {
      const data = res.data
      userInfo.value = {
        userId: data.userId ?? data.id,
        username: data.username,
        realName: data.realName,
        avatar: data.avatar,
        permissions: data.permissions || []
      }
      return true
    }).catch(() => {
      logout()
      return false
    }).finally(() => { restoring = null })
    return restoring
  }

  function hasPermission(permission: string): boolean {
    return userInfo.value?.permissions?.includes(permission) || false
  }

  return {
    token,
    userInfo,
    login,
    logout,
    restoreSession,
    hasPermission
  }
})
