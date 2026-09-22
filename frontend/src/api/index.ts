import request from '@/utils/request'

// 用户相关API
export const userApi = {
  login: (data: any) => request.post('/auth/login', data),
  getCurrentUser: () => request.get('/auth/current'),
  listUsers: (params: any) => request.get('/users', { params }),
  getUserById: (id: number) => request.get(`/users/${id}`),
  createUser: (data: any) => request.post('/users', data),
  updateUser: (id: number, data: any) => request.put(`/users/${id}`, data),
  deleteUser: (id: number) => request.delete(`/users/${id}`),
  getUserRoles: (id: number) => request.get(`/users/${id}/roles`),
  assignRoles: (id: number, roleIds: number[]) => request.post(`/users/${id}/roles`, roleIds),
  updatePassword: (id: number, data: { oldPassword: string; newPassword: string }) => request.put(`/users/${id}/password`, data),
}

export const roleApi = {
  listRoles: (params?: any) => request.get('/roles', { params }),
  createRole: (data: any) => request.post('/roles', data),
  updateRole: (id: number, data: any) => request.put(`/roles/${id}`, data),
  deleteRole: (id: number) => request.delete(`/roles/${id}`),
  getPermissionIds: (id: number) => request.get(`/roles/${id}/permissions`),
  assignPermissions: (id: number, permissionIds: number[]) => request.post(`/roles/${id}/permissions`, permissionIds),
}

export const permissionApi = {
  listPermissions: () => request.get('/permissions'),
  getTree: () => request.get('/permissions/tree'),
}

export const notificationApi = {
  listNotifications: (params?: any) => request.get('/notifications', { params }),
  getUnreadCount: () => request.get('/notifications/unread-count'),
  markAsRead: (id: number) => request.post(`/notifications/${id}/read`),
  markAllAsRead: () => request.post('/notifications/read-all'),
}

// 商品相关API
export const productApi = {
  listProducts: (params: any) => request.get('/products', { params }),
  getProductById: (id: number) => request.get(`/products/${id}`),
  createProduct: (data: any) => request.post('/products', data),
  updateProduct: (id: number, data: any) => request.put(`/products/${id}`, data),
  deleteProduct: (id: number) => request.delete(`/products/${id}`),
}

export const productReferenceApi = {
  listSkus: () => request.get('/product-references/skus'),
  listSuppliers: () => request.get('/product-references/suppliers'),
  listCategories: () => request.get('/product-references/categories'),
}

export const skuApi = {
  listByProduct: (productId: number) => request.get(`/products/${productId}/skus`),
  createSku: (data: any) => request.post('/skus', data),
  updateSku: (id: number, data: any) => request.put(`/skus/${id}`, data),
  deleteSku: (id: number) => request.delete(`/skus/${id}`),
}

// 仓库相关API
export const warehouseApi = {
  listWarehouses: (params: any) => request.get('/warehouses', { params }),
  getWarehouseById: (id: number) => request.get(`/warehouses/${id}`),
  createWarehouse: (data: any) => request.post('/warehouses', data),
  updateWarehouse: (id: number, data: any) => request.put(`/warehouses/${id}`, data),
  deleteWarehouse: (id: number) => request.delete(`/warehouses/${id}`),
}

export const warehouseStructureApi = {
  listZones: (warehouseId: number) => request.get('/zones', { params: { warehouseId } }),
  createZone: (data: any) => request.post('/zones', data),
  updateZone: (id: number, data: any) => request.put(`/zones/${id}`, data),
  deleteZone: (id: number) => request.delete(`/zones/${id}`),
  listRacks: (zoneId: number) => request.get('/racks', { params: { zoneId } }),
  createRack: (data: any) => request.post('/racks', data),
  updateRack: (id: number, data: any) => request.put(`/racks/${id}`, data),
  deleteRack: (id: number) => request.delete(`/racks/${id}`),
  listLocations: (rackId: number) => request.get('/locations', { params: { rackId } }),
  createLocation: (data: any) => request.post('/locations', data),
  updateLocation: (id: number, data: any) => request.put(`/locations/${id}`, data),
  deleteLocation: (id: number) => request.delete(`/locations/${id}`),
}

// 库存相关API
export const inventoryApi = {
  listInventory: (params: any) => request.get('/inventory', { params }),
  getInventoryById: (id: number) => request.get(`/inventory/${id}`),
  getInventoryTransactions: (id: number) => request.get(`/inventory/${id}/transactions`),
}

export const batchApi = {
  listBatches: (params: any) => request.get('/batches', { params }),
}

export const logApi = {
  listLogs: (params: any) => request.get('/logs', { params }),
}

// 入库相关API
export const inboundApi = {
  listInboundOrders: (params: any) => request.get('/inbound-orders', { params }),
  getInboundOrderById: (id: number) => request.get(`/inbound-orders/${id}`),
  getInboundOrderItems: (id: number) => request.get(`/inbound-orders/${id}/items`),
  createInboundOrder: (data: any) => request.post('/inbound-orders', data),
  receiveInboundOrder: (id: number) => request.post(`/inbound-orders/${id}/receive`),
  putawayInboundOrder: (id: number) => request.post(`/inbound-orders/${id}/putaway`),
}

// 出库相关API
export const outboundApi = {
  listOutboundOrders: (params: any) => request.get('/outbound-orders', { params }),
  getOutboundOrderById: (id: number) => request.get(`/outbound-orders/${id}`),
  getOutboundOrderItems: (id: number) => request.get(`/outbound-orders/${id}/items`),
  createOutboundOrder: (data: any) => request.post('/outbound-orders', data),
  approveOutboundOrder: (id: number) => request.post(`/outbound-orders/${id}/approve`),
  pickOutboundOrder: (id: number) => request.post(`/outbound-orders/${id}/pick`),
  shipOutboundOrder: (id: number) => request.post(`/outbound-orders/${id}/ship`),
}

// 调拨相关API
export const transferApi = {
  listTransferOrders: (params: any) => request.get('/transfer-orders', { params }),
  getTransferOrderById: (id: number) => request.get(`/transfer-orders/${id}`),
  getTransferOrderItems: (id: number) => request.get(`/transfer-orders/${id}/items`),
  createTransferOrder: (data: any) => request.post('/transfer-orders', data),
  approveTransferOrder: (id: number) => request.post(`/transfer-orders/${id}/approve`),
  executeTransferOrder: (id: number) => request.post(`/transfer-orders/${id}/execute`),
}

// 盘点相关API
export const stocktakeApi = {
  listStocktakes: (params: any) => request.get('/stocktakes', { params }),
  getStocktakeById: (id: number) => request.get(`/stocktakes/${id}`),
  getStocktakeItems: (id: number) => request.get(`/stocktakes/${id}/items`),
  createStocktake: (data: any) => request.post('/stocktakes', data),
  startStocktake: (id: number) => request.post(`/stocktakes/${id}/start`),
  countStocktakeItem: (itemId: number, actualQty: number) => request.post(`/stocktakes/items/${itemId}/count`, null, { params: { actualQty } }),
  completeStocktake: (id: number) => request.post(`/stocktakes/${id}/complete`),
}

// 任务相关API
export const taskApi = {
  listTasks: (params: any) => request.get('/tasks', { params }),
  getTaskById: (id: number) => request.get(`/tasks/${id}`),
  createTask: (data: any) => request.post('/tasks', data),
  assignTask: (id: number, userId: number) => request.post(`/tasks/${id}/assign`, { assigneeId: userId }),
  completeTask: (id: number) => request.post(`/tasks/${id}/complete`),
}

// 预警相关API
export const alertApi = {
  listAlerts: (params: any) => request.get('/alerts', { params }),
  getSummary: () => request.get('/alerts/summary'),
  getAlertById: (id: number) => request.get(`/alerts/${id}`),
  handleAlert: (id: number, data: { handleResult: string }) => request.post(`/alerts/${id}/handle`, data),
}

// AI相关API
export const aiApi = {
  chat: (data: any) => request.post('/ai/chat', data),
  getConversations: () => request.get('/ai/conversations'),
  createConversation: (title = '新对话') => request.post('/ai/conversations', title, { headers: { 'Content-Type': 'text/plain' } }),
  getMessages: (conversationId: string) => request.get(`/ai/conversations/${conversationId}/messages`),
}

// Dashboard相关API
export const dashboardApi = {
  getSummary: () => request.get('/dashboard/summary'),
  getInboundOutboundTrend: () => request.get('/dashboard/inbound-outbound-trend'),
  getTasks: () => request.get('/dashboard/tasks'),
  getAlerts: () => request.get('/dashboard/alerts'),
  getUtilization: () => request.get('/dashboard/utilization'),
}

export const analyticsApi = {
  getAnalytics: (days = 30) => request.get('/analytics', { params: { days } }),
}
