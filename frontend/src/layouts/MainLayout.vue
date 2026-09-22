<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '240px'" class="layout-aside">
      <div class="logo-container">
        <img src="@/assets/logo.svg" alt="Logo" class="logo-icon" v-if="!isCollapse" />
        <span class="logo-text" v-if="!isCollapse">SmartWMS</span>
        <span class="logo-text-sm" v-else>SW</span>
      </div>
      <el-menu
        :default-active="currentPath"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="aside-menu"
      >
        <template v-for="item in visibleMenuItems" :key="item.path">
          <el-sub-menu v-if="item.children" :index="item.path">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">{{ currentRoute.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-button :icon="Bell" circle @click="openNotifications" />
          </el-badge>
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.realName?.charAt(0) || 'A' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.realName || '管理员' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="openPasswordDialog">修改密码</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
    <el-drawer v-model="notificationDrawer" title="消息通知" size="420px" @open="loadNotifications">
      <template #header>
        <div class="drawer-header"><span>消息通知</span><el-button v-if="unreadCount" type="primary" link :loading="markingAll" @click="markAllRead">全部已读</el-button></div>
      </template>
      <div v-loading="notificationLoading" class="notification-list">
        <button v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: item.isRead === 0 }" @click="markRead(item)">
          <span class="notice-dot" /><span class="notice-body"><strong>{{ item.title }}</strong><span>{{ item.content }}</span><small>{{ formatTime(item.createdAt) }}</small></span>
        </button>
        <el-empty v-if="!notificationLoading && !notifications.length" description="暂无通知" />
      </div>
      <div v-if="notificationTotal > notificationPageSize" class="notice-pagination"><el-pagination v-model:current-page="notificationPage" small background layout="prev, pager, next" :page-size="notificationPageSize" :total="notificationTotal" @current-change="loadNotifications" /></div>
    </el-drawer>
    <el-dialog v-model="passwordDialog" title="修改密码" width="460px" append-to-body>
      <el-form label-width="95px">
        <el-form-item label="当前密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="current-password" /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" /></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" @keyup.enter="changePassword" /></el-form-item>
      </el-form>
      <el-alert title="密码至少8位，并同时包含大小写字母、数字和特殊字符。" type="info" :closable="false" show-icon />
      <template #footer><el-button @click="passwordDialog=false">取消</el-button><el-button type="primary" :loading="passwordSubmitting" @click="changePassword">确认修改</el-button></template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Bell, Fold, Expand } from '@element-plus/icons-vue'
import { notificationApi, userApi } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const notificationDrawer = ref(false)
const notifications = ref<any[]>([])
const unreadCount = ref(0)
const notificationTotal = ref(0)
const notificationPage = ref(1)
const notificationPageSize = 10
const notificationLoading = ref(false)
const markingAll = ref(false)
const passwordDialog = ref(false)
const passwordSubmitting = ref(false)
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const currentPath = computed(() => route.path)
const currentRoute = computed(() => route)

interface MenuItem {
  path: string
  title: string
  icon: string
  permission?: string
  children?: MenuItem[]
}

const menuItems: MenuItem[] = [
  { path: '/dashboard', title: '工作台', icon: 'Odometer', permission: 'dashboard' },
  {
    path: '/warehouse-business',
    title: '仓储业务',
    icon: 'House',
    children: [
      { path: '/product', title: '商品中心', icon: 'Goods', permission: 'product:center' },
      { path: '/warehouse', title: '仓库中心', icon: 'OfficeBuilding', permission: 'warehouse:center' },
      { path: '/inbound', title: '入库管理', icon: 'Bottom', permission: 'inbound:management' },
      { path: '/outbound', title: '出库管理', icon: 'Top', permission: 'outbound:management' },
      { path: '/inventory', title: '库存中心', icon: 'Box', permission: 'inventory:center' },
      { path: '/transfer', title: '调拨管理', icon: 'Sort', permission: 'transfer:management' },
      { path: '/stocktake', title: '盘点管理', icon: 'Document', permission: 'stocktake:management' },
    ]
  },
  {
    path: '/operation',
    title: '运营管理',
    icon: 'Setting',
    children: [
      { path: '/batch', title: '批次管理', icon: 'Tickets', permission: 'batch:management' },
      { path: '/task', title: '任务中心', icon: 'List', permission: 'task:center' },
      { path: '/alert', title: '预警中心', icon: 'Warning', permission: 'alert:center' },
    ]
  },
  {
    path: '/analysis',
    title: '智能分析',
    icon: 'DataAnalysis',
    children: [
      { path: '/analytics', title: '数据分析', icon: 'TrendCharts', permission: 'analytics' },
      { path: '/ai', title: 'AI智能助手', icon: 'ChatDotRound', permission: 'ai:assistant' },
    ]
  },
  {
    path: '/system',
    title: '系统',
    icon: 'Tools',
    children: [
      { path: '/system/user', title: '用户管理', icon: 'User', permission: 'user:management' },
      { path: '/system/role', title: '角色权限', icon: 'Lock', permission: 'role:permission' },
      { path: '/system/log', title: '操作日志', icon: 'Notebook', permission: 'operation:log' },
    ]
  },
]
const visibleMenuItems = computed<MenuItem[]>(() => menuItems.reduce<MenuItem[]>((visible, item) => {
  if (item.children) {
    const children = item.children.filter(child => !!child.permission && userStore.hasPermission(child.permission))
    if (children.length) visible.push({ ...item, children })
  } else if (item.permission && userStore.hasPermission(item.permission)) {
    visible.push(item)
  }
  return visible
}, []))

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const loadUnreadCount = async () => {
  try { unreadCount.value = Number((await notificationApi.getUnreadCount()).data || 0) } catch { unreadCount.value = 0 }
}
const loadNotifications = async () => {
  notificationLoading.value = true
  try {
    const res = await notificationApi.listNotifications({ page: notificationPage.value, pageSize: notificationPageSize })
    notifications.value = res.data?.records || []
    notificationTotal.value = Number(res.data?.total || 0)
  } finally { notificationLoading.value = false }
}
const openNotifications = () => { notificationDrawer.value = true }
const markRead = async (item: any) => {
  if (item.isRead === 1) return
  await notificationApi.markAsRead(item.id)
  item.isRead = 1
  unreadCount.value = Math.max(0, unreadCount.value - 1)
}
const markAllRead = async () => {
  markingAll.value = true
  try {
    await notificationApi.markAllAsRead()
    notifications.value.forEach(item => { item.isRead = 1 })
    unreadCount.value = 0
    ElMessage.success('所有通知已标记为已读')
  } finally { markingAll.value = false }
}
const formatTime = (value: string) => value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : ''
const openPasswordDialog = () => {
  Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  passwordDialog.value = true
}
const changePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) return ElMessage.warning('请完整填写密码信息')
  if (passwordForm.newPassword !== passwordForm.confirmPassword) return ElMessage.warning('两次输入的新密码不一致')
  if (!userStore.userInfo?.userId) return
  passwordSubmitting.value = true
  try {
    await userApi.updatePassword(userStore.userInfo.userId, { oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialog.value = false
    handleLogout()
  } finally { passwordSubmitting.value = false }
}
onMounted(loadUnreadCount)
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-icon {
  width: 32px;
  height: 32px;
}

.logo-text {
  color: white;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.logo-text-sm {
  color: white;
  font-size: 16px;
  font-weight: 600;
}

.aside-menu {
  border-right: none;
}

.aside-menu:not(.el-menu--collapse) {
  width: 240px;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border-bottom: 1px solid var(--border-color);
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-search {
  width: 200px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
}

.layout-main {
  background: var(--bg-color);
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.drawer-header { width: 100%; display: flex; align-items: center; justify-content: space-between; font-size: 18px; font-weight: 600; }
.notification-list { min-height: 160px; }
.notification-item { width: 100%; display: flex; gap: 12px; border: 0; border-bottom: 1px solid var(--el-border-color-lighter); background: transparent; padding: 16px 8px; text-align: left; cursor: pointer; }
.notification-item:hover { background: var(--el-fill-color-light); }
.notification-item.unread { background: var(--el-color-primary-light-9); }
.notice-dot { width: 8px; height: 8px; flex: 0 0 8px; margin-top: 7px; border-radius: 50%; background: transparent; }
.notification-item.unread .notice-dot { background: var(--el-color-primary); }
.notice-body { min-width: 0; display: flex; flex: 1; flex-direction: column; gap: 6px; color: var(--el-text-color-regular); }
.notice-body strong { color: var(--el-text-color-primary); }
.notice-body small { color: var(--el-text-color-secondary); }
.notice-pagination { display: flex; justify-content: center; padding-top: 18px; }
</style>
