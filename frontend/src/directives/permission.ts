import type { App, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

type PermissionValue = string | string[]

export function installPermissionDirective(app: App) {
  app.directive('permission', {
    mounted(el: HTMLElement, binding: DirectiveBinding<PermissionValue>) {
      const required = Array.isArray(binding.value) ? binding.value : [binding.value]
      const userStore = useUserStore()
      if (!required.some(permission => userStore.hasPermission(permission))) {
        el.remove()
      }
    },
  })
}
