export interface User {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  status: number
  createdAt: string
}

export interface Product {
  id: number
  productCode: string
  productName: string
  categoryId: number
  categoryName: string
  brand: string
  description: string
  image: string
  status: number
  skuCount: number
}

export interface SKU {
  id: number
  skuCode: string
  productId: number
  productName: string
  specification: string
  unit: string
  barcode: string
  weight: number
  volume: number
  safetyStock: number
  maxStock: number
  outboundStrategy: string
  status: number
}

export interface Warehouse {
  id: number
  warehouseCode: string
  warehouseName: string
  address: string
  contactPerson: string
  phone: string
  totalCapacity: number
  usedCapacity: number
  status: number
}

export interface Zone {
  id: number
  zoneCode: string
  zoneName: string
  warehouseId: number
  zoneType: string
  status: number
}

export interface Rack {
  id: number
  rackCode: string
  rackName: string
  zoneId: number
  floorCount: number
  status: number
}

export interface Location {
  id: number
  locationCode: string
  rackId: number
  zoneId: number
  warehouseId: number
  floor: number
  position: number
  maxWeight: number
  maxCapacity: number
  usedCapacity: number
  status: string
  isDisabled: number
}

export interface Inventory {
  id: number
  warehouseId: number
  warehouseName: string
  locationId: number
  locationCode: string
  skuId: number
  skuCode: string
  productName: string
  batchId: number
  batchNo: string
  totalQty: number
  availableQty: number
  lockedQty: number
  damagedQty: number
  status: string
}

export interface InboundOrder {
  id: number
  orderNo: string
  orderType: string
  warehouseId: number
  supplierId: number
  supplierName: string
  status: string
  totalQty: number
  receivedQty: number
  putawayQty: number
  expectedDate: string
  createdAt: string
}

export interface OutboundOrder {
  id: number
  orderNo: string
  orderType: string
  warehouseId: number
  customerName: string
  status: string
  totalQty: number
  pickedQty: number
  shippedQty: number
  expectedDate: string
  createdAt: string
}

export interface Task {
  id: number
  taskNo: string
  taskType: string
  sourceOrderNo: string
  warehouseId: number
  status: string
  assigneeId: number
  assigneeName: string
  createdAt: string
}

export interface Alert {
  id: number
  alertType: string
  alertLevel: string
  skuId: number
  warehouseId: number
  title: string
  content: string
  currentValue: string
  thresholdValue: string
  suggestion: string
  isHandled: number
  createdAt: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}
