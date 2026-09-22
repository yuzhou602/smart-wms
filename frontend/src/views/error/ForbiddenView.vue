<template><div class="forbidden"><el-result icon="warning" title="403" sub-title="当前账号没有访问此页面的权限"><template #extra><el-button type="primary" @click="goHome">返回可访问页面</el-button></template></el-result></div></template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const store = useUserStore()
const candidates: Array<[string, string]> = [
  ['dashboard', '/dashboard'],
  ['product:center', '/product'],
  ['warehouse:center', '/warehouse'],
  ['inbound:management', '/inbound'],
  ['outbound:management', '/outbound'],
  ['inventory:center', '/inventory'],
  ['task:center', '/task'],
]
const goHome = () => {
  const destination = candidates.find(([permission]) => store.hasPermission(permission))
  router.push(destination ? destination[1] : '/login')
}
</script>
<style scoped>.forbidden{display:flex;align-items:center;justify-content:center;min-height:65vh}</style>
