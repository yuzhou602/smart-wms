import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import router from './index'

vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

vi.mock('nprogress', () => ({
  default: {
    start: vi.fn(),
    done: vi.fn(),
  },
}))

const mockedRequest = vi.mocked(request, true)

async function resetNavigation() {
  setActivePinia(createPinia())
  localStorage.clear()
  await router.replace('/login')
}

describe('router authentication guard', () => {
  beforeEach(async () => {
    await resetNavigation()
  })

  it('redirects anonymous and expired sessions to login', async () => {
    await router.push('/inventory')
    expect(router.currentRoute.value.path).toBe('/login')

    localStorage.setItem('token', 'expired-token')
    setActivePinia(createPinia())
    mockedRequest.get.mockRejectedValue(new Error('expired'))
    await router.push('/inventory')

    expect(router.currentRoute.value.path).toBe('/login')
    expect(localStorage.getItem('token')).toBeNull()
  })

  it('allows an authenticated user to enter an authorized route', async () => {
    localStorage.setItem('token', 'valid-token')
    setActivePinia(createPinia())
    mockedRequest.get.mockResolvedValue({
      data: {
        userId: 21,
        username: 'operator',
        realName: '仓库操作员',
        permissions: ['inventory:center'],
      },
    })

    await router.push('/inventory')

    expect(router.currentRoute.value.path).toBe('/inventory')
    expect(useUserStore().hasPermission('inventory:center')).toBe(true)
  })

  it('redirects an authenticated user without permission to the forbidden page', async () => {
    localStorage.setItem('token', 'limited-token')
    setActivePinia(createPinia())
    mockedRequest.get.mockResolvedValue({
      data: {
        userId: 22,
        username: 'viewer',
        realName: '访客',
        permissions: ['dashboard'],
      },
    })

    await router.push('/inventory')

    expect(router.currentRoute.value.path).toBe('/403')
  })
})
