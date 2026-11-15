import { createApp, type DirectiveBinding, type ObjectDirective } from 'vue'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { installPermissionDirective } from './permission'

vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

const mockedRequest = vi.mocked(request, true)

function permissionDirective(): ObjectDirective<HTMLElement, string | string[]> {
  const app = createApp({})
  installPermissionDirective(app)
  return app._context.directives.permission as ObjectDirective<HTMLElement, string | string[]>
}

function mountDirective(
  directive: ObjectDirective<HTMLElement, string | string[]>,
  element: HTMLElement,
  value: string | string[],
) {
  const mounted = directive.mounted as (el: HTMLElement, binding: DirectiveBinding<string | string[]>) => void
  mounted(element, { value } as DirectiveBinding<string | string[]>)
}

describe('permission directive', () => {
  beforeEach(async () => {
    localStorage.clear()
    setActivePinia(createPinia())
    mockedRequest.post.mockResolvedValue({
      data: {
        token: 'signed-token',
        userId: 7,
        username: 'operator',
        realName: '仓库操作员',
        permissions: ['inventory:center', 'outbound:management'],
      },
    })
    await useUserStore().login('operator', 'password')
  })

  it('keeps elements when any required permission matches', () => {
    const singlePermissionElement = document.createElement('button')
    const permissionListElement = document.createElement('button')
    document.body.append(singlePermissionElement, permissionListElement)

    const directive = permissionDirective()
    mountDirective(directive, singlePermissionElement, 'inventory:center')
    mountDirective(directive, permissionListElement, ['system:user', 'outbound:management'])

    expect(singlePermissionElement.isConnected).toBe(true)
    expect(permissionListElement.isConnected).toBe(true)
    singlePermissionElement.remove()
    permissionListElement.remove()
  })

  it('removes elements when no required permission is available', () => {
    const unavailablePermissionElement = document.createElement('button')
    const emptyRequirementElement = document.createElement('button')
    document.body.append(unavailablePermissionElement, emptyRequirementElement)

    const directive = permissionDirective()
    mountDirective(directive, unavailablePermissionElement, ['system:user', 'system:role'])
    mountDirective(directive, emptyRequirementElement, [])

    expect(unavailablePermissionElement.isConnected).toBe(false)
    expect(emptyRequirementElement.isConnected).toBe(false)
  })
})
