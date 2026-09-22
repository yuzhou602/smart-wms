import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: '工作台', icon: 'Odometer', permission: 'dashboard' }
        },
        {
          path: 'product',
          name: 'Product',
          component: () => import('@/views/product/ProductView.vue'),
          meta: { title: '商品中心', icon: 'Goods', permission: 'product:center' }
        },
        {
          path: 'warehouse',
          name: 'Warehouse',
          component: () => import('@/views/warehouse/WarehouseView.vue'),
          meta: { title: '仓库中心', icon: 'House', permission: 'warehouse:center' }
        },
        {
          path: 'warehouse/:id/map',
          name: 'WarehouseMap',
          component: () => import('@/views/warehouse/WarehouseMapView.vue'),
          meta: { title: '库位地图', hidden: true, permission: 'warehouse:center' }
        },
        {
          path: 'inbound',
          name: 'Inbound',
          component: () => import('@/views/inbound/InboundView.vue'),
          meta: { title: '入库管理', icon: 'Bottom', permission: 'inbound:management' }
        },
        {
          path: 'inbound/:id',
          name: 'InboundDetail',
          component: () => import('@/views/inbound/InboundDetailView.vue'),
          meta: { title: '入库详情', hidden: true, permission: 'inbound:management' }
        },
        {
          path: 'outbound',
          name: 'Outbound',
          component: () => import('@/views/outbound/OutboundView.vue'),
          meta: { title: '出库管理', icon: 'Top', permission: 'outbound:management' }
        },
        {
          path: 'outbound/:id',
          name: 'OutboundDetail',
          component: () => import('@/views/outbound/OutboundDetailView.vue'),
          meta: { title: '出库详情', hidden: true, permission: 'outbound:management' }
        },
        {
          path: 'inventory',
          name: 'Inventory',
          component: () => import('@/views/inventory/InventoryView.vue'),
          meta: { title: '库存中心', icon: 'Box', permission: 'inventory:center' }
        },
        {
          path: 'transfer',
          name: 'Transfer',
          component: () => import('@/views/transfer/TransferView.vue'),
          meta: { title: '调拨管理', icon: 'Sort', permission: 'transfer:management' }
        },
        {
          path: 'transfer/:id',
          name: 'TransferDetail',
          component: () => import('@/views/transfer/TransferDetailView.vue'),
          meta: { title: '调拨详情', hidden: true, permission: 'transfer:management' }
        },
        {
          path: 'stocktake',
          name: 'Stocktake',
          component: () => import('@/views/stocktake/StocktakeView.vue'),
          meta: { title: '盘点管理', icon: 'Document', permission: 'stocktake:management' }
        },
        {
          path: 'stocktake/:id',
          name: 'StocktakeDetail',
          component: () => import('@/views/stocktake/StocktakeDetailView.vue'),
          meta: { title: '盘点执行', hidden: true, permission: 'stocktake:management' }
        },
        {
          path: 'batch',
          name: 'Batch',
          component: () => import('@/views/batch/BatchView.vue'),
          meta: { title: '批次管理', icon: 'Tickets', permission: 'batch:management' }
        },
        {
          path: 'task',
          name: 'Task',
          component: () => import('@/views/task/TaskView.vue'),
          meta: { title: '任务中心', icon: 'List', permission: 'task:center' }
        },
        {
          path: 'alert',
          name: 'Alert',
          component: () => import('@/views/alert/AlertView.vue'),
          meta: { title: '预警中心', icon: 'Warning', permission: 'alert:center' }
        },
        {
          path: 'analytics',
          name: 'Analytics',
          component: () => import('@/views/analytics/AnalyticsView.vue'),
          meta: { title: '数据分析', icon: 'DataAnalysis', permission: 'analytics' }
        },
        {
          path: 'ai',
          name: 'AI',
          component: () => import('@/views/ai/AIView.vue'),
          meta: { title: 'AI智能助手', icon: 'ChatDotRound', permission: 'ai:assistant' }
        },
        {
          path: 'system/user',
          name: 'SystemUser',
          component: () => import('@/views/system/UserView.vue'),
          meta: { title: '用户管理', icon: 'User', permission: 'user:management' }
        },
        {
          path: 'system/role',
          name: 'SystemRole',
          component: () => import('@/views/system/RoleView.vue'),
          meta: { title: '角色权限', icon: 'Lock', permission: 'role:permission' }
        },
        {
          path: 'system/log',
          name: 'SystemLog',
          component: () => import('@/views/system/LogView.vue'),
          meta: { title: '操作日志', icon: 'Notebook', permission: 'operation:log' }
        },
        {
          path: '403',
          name: 'Forbidden',
          component: () => import('@/views/error/ForbiddenView.vue'),
          meta: { title: '无权访问', hidden: true }
        },
      ]
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || 'SmartWMS'} - 智仓云`

  const userStore = useUserStore()
  if (to.path === '/login') {
    next()
  } else if (!userStore.token) {
    next('/login')
  } else {
    const restored = await userStore.restoreSession()
    if (!restored) return next('/login')
    const permission = to.meta.permission as string | undefined
    if (permission && !userStore.hasPermission(permission)) return next('/403')
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
