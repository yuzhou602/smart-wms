import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import request from '@/utils/request'
import { useUserStore } from './user'

vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

const mockedRequest = vi.mocked(request, true)

describe('user store', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('restores and normalizes a stored session with one shared request', async () => {
    const anonymousStore = useUserStore()
    await expect(anonymousStore.restoreSession()).resolves.toBe(false)

    localStorage.setItem('token', 'existing-token')
    setActivePinia(createPinia())
    let resolveRequest!: (value: { data: Record<string, unknown> }) => void
    mockedRequest.get.mockReturnValue(new Promise((resolve) => {
      resolveRequest = resolve
    }))

    const store = useUserStore()
    const firstRestore = store.restoreSession()
    const secondRestore = store.restoreSession()

    expect(mockedRequest.get).toHaveBeenCalledTimes(1)
    resolveRequest({
      data: {
        id: 12,
        username: 'picker',
        realName: '拣货员',
        permissions: undefined,
      },
    })

    await expect(Promise.all([firstRestore, secondRestore])).resolves.toEqual([true, true])
    expect(mockedRequest.get).toHaveBeenCalledWith('/auth/current')
    expect(store.userInfo).toMatchObject({
      userId: 12,
      username: 'picker',
      permissions: [],
    })
    await expect(store.restoreSession()).resolves.toBe(true)
    expect(mockedRequest.get).toHaveBeenCalledTimes(1)

    localStorage.setItem('token', 'complete-profile-token')
    setActivePinia(createPinia())
    mockedRequest.get.mockResolvedValue({
      data: {
        userId: 13,
        username: 'manager',
        realName: '仓库主管',
        avatar: '/avatars/manager.png',
        permissions: ['warehouse:center'],
      },
    })
    const completeProfileStore = useUserStore()
    await expect(completeProfileStore.restoreSession()).resolves.toBe(true)
    expect(completeProfileStore.userInfo).toMatchObject({
      userId: 13,
      avatar: '/avatars/manager.png',
      permissions: ['warehouse:center'],
    })
  })

  it('clears an invalid session when restoration fails', async () => {
    localStorage.setItem('token', 'expired-token')
    mockedRequest.get.mockRejectedValue(new Error('unauthorized'))

    const store = useUserStore()
    await expect(store.restoreSession()).resolves.toBe(false)

    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()

    localStorage.setItem('token', 'new-token')
    store.token = 'new-token'
    mockedRequest.get.mockResolvedValue({
      data: {
        userId: 14,
        username: 'auditor',
        realName: '盘点员',
        permissions: ['stocktake:management'],
      },
    })
    await expect(store.restoreSession()).resolves.toBe(true)
    expect(mockedRequest.get).toHaveBeenCalledTimes(2)
  })

  it('logs in, evaluates permissions, and logs out', async () => {
    mockedRequest.post.mockResolvedValue({
      data: {
        token: 'signed-token',
        userId: 7,
        username: 'operator',
        realName: '仓库操作员',
        permissions: ['inventory:center'],
      },
    })

    const store = useUserStore()
    await store.login('operator', 'password')
    expect(mockedRequest.post).toHaveBeenCalledWith('/auth/login', {
      username: 'operator',
      password: 'password',
    })
    expect(store.token).toBe('signed-token')
    expect(store.userInfo?.realName).toBe('仓库操作员')
    expect(localStorage.getItem('token')).toBe('signed-token')
    expect(store.hasPermission('inventory:center')).toBe(true)
    expect(store.hasPermission('system:user')).toBe(false)

    store.logout()
    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
    expect(store.hasPermission('inventory:center')).toBe(false)

    localStorage.setItem('token', 'previous-token')
    setActivePinia(createPinia())
    mockedRequest.post.mockRejectedValue(new Error('invalid credentials'))
    const existingSessionStore = useUserStore()
    await expect(existingSessionStore.login('operator', 'wrong-password')).rejects.toThrow('invalid credentials')
    expect(existingSessionStore.token).toBe('previous-token')
    expect(localStorage.getItem('token')).toBe('previous-token')
  })
})
