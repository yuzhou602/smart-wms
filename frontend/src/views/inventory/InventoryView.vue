<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">库存中心</h1>
      <p class="page-subtitle">实时查看所有仓库库存、可用量、锁定量与库存状态</p>
    </div>

    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-label">总库存</div>
        <div class="metric-value">{{ formatQty(metrics.total) }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">可用库存</div>
        <div class="metric-value" style="color: #16A34A;">{{ formatQty(metrics.available) }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">锁定库存</div>
        <div class="metric-value" style="color: #D97706;">{{ formatQty(metrics.locked) }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">损坏库存</div>
        <div class="metric-value" style="color: #DC2626;">{{ formatQty(metrics.damaged) }}</div>
      </div>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索SKU/商品" prefix-icon="Search" clearable />
        <el-select v-model="filterWarehouse" placeholder="仓库" clearable>
          <el-option v-for="warehouse in warehouses" :key="warehouse.id" :label="warehouse.warehouseName" :value="warehouse.id" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="库存状态" clearable>
          <el-option label="正常" value="AVAILABLE" />
          <el-option label="锁定" value="LOCKED" />
          <el-option label="损坏" value="DAMAGED" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>

      <el-table :data="tableData" stripe border v-loading="loading" @row-click="handleRowClick">
        <el-table-column prop="skuCode" label="SKU" width="130" />
        <el-table-column prop="productName" label="商品" min-width="150" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="locationCode" label="库位" width="120" />
        <el-table-column prop="batchNo" label="批次" width="130" />
        <el-table-column prop="totalQty" label="总库存" width="100" align="right" />
        <el-table-column prop="availableQty" label="可用" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #16A34A;">{{ row.availableQty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lockedQty" label="锁定" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #D97706;">{{ row.lockedQty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-drawer v-model="drawerVisible" title="库存详情" size="600px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="库存详情" name="detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="SKU">{{ detailData.skuCode }}</el-descriptions-item>
            <el-descriptions-item label="商品">{{ detailData.productName }}</el-descriptions-item>
            <el-descriptions-item label="仓库">{{ detailData.warehouseName }}</el-descriptions-item>
            <el-descriptions-item label="库位">{{ detailData.locationCode }}</el-descriptions-item>
            <el-descriptions-item label="批次">{{ detailData.batchNo }}</el-descriptions-item>
            <el-descriptions-item label="总库存">{{ detailData.totalQty }}</el-descriptions-item>
            <el-descriptions-item label="可用库存">{{ detailData.availableQty }}</el-descriptions-item>
            <el-descriptions-item label="锁定库存">{{ detailData.lockedQty }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="库存流水" name="transactions">
          <el-table :data="transactions" size="small">
            <el-table-column prop="transactionNo" label="流水号" width="150" />
            <el-table-column prop="transactionType" label="类型" width="100" />
            <el-table-column prop="changeQty" label="变更数量" width="100" />
            <el-table-column prop="createdAt" label="时间" width="160" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import { inventoryApi, warehouseApi } from '@/api'

const loading = ref(false)
const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const filterWarehouse = ref('')
const filterStatus = ref('')

const drawerVisible = ref(false)
const activeTab = ref('detail')
const detailData = ref<any>({})
const transactions = ref<any[]>([])
const warehouses = ref<any[]>([])
const inventorySnapshot = ref<any[]>([])
const metrics = computed(() => inventorySnapshot.value.reduce((sum, row) => ({
  total: sum.total + Number(row.totalQty || 0), available: sum.available + Number(row.availableQty || 0),
  locked: sum.locked + Number(row.lockedQty || 0), damaged: sum.damaged + Number(row.damagedQty || 0)
}), { total: 0, available: 0, locked: 0, damaged: 0 }))
const formatQty = (value: number) => value.toLocaleString('zh-CN')

const loadData = async () => {
  loading.value = true
  try {
    const res = await inventoryApi.listInventory({
        page: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchKeyword.value,
        warehouseId: filterWarehouse.value,
        status: filterStatus.value
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'AVAILABLE': 'success',
    'LOCKED': 'warning',
    'DAMAGED': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'AVAILABLE': '正常',
    'LOCKED': '锁定',
    'DAMAGED': '损坏'
  }
  return map[status] || status
}

const handleRowClick = (row: any) => {
  viewDetail(row)
}

const viewDetail = async (row: any) => {
  detailData.value = row
  drawerVisible.value = true
  try {
    const res = await inventoryApi.getInventoryTransactions(row.id)
    transactions.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const csvCell = (value: unknown) => `"${String(value ?? '').replace(/"/g, '""')}"`
const handleExport = async () => {
  try {
    const res = await inventoryApi.listInventory({ page: 1, pageSize: 10000, keyword: searchKeyword.value, warehouseId: filterWarehouse.value, status: filterStatus.value })
    const rows = res.data?.records || []
    if (!rows.length) return ElMessage.warning('当前条件下没有可导出的库存')
    const headers = ['SKU', '商品', '仓库', '库位', '批次', '总库存', '可用库存', '锁定库存', '损坏库存', '状态']
    const content = [headers, ...rows.map((row: any) => [row.skuCode, row.productName, row.warehouseName, row.locationCode, row.batchNo, row.totalQty, row.availableQty, row.lockedQty, row.damagedQty, getStatusText(row.status)])]
      .map(line => line.map(csvCell).join(',')).join('\r\n')
    const url = URL.createObjectURL(new Blob(['\ufeff' + content], { type: 'text/csv;charset=utf-8' }))
    const link = document.createElement('a'); link.href = url; link.download = `库存明细_${new Date().toISOString().slice(0, 10)}.csv`; link.click(); URL.revokeObjectURL(url)
    ElMessage.success(`已导出 ${rows.length} 条库存记录`)
  } catch { ElMessage.error('库存导出失败') }
}

onMounted(async () => {
  const [warehouseRes, snapshotRes] = await Promise.all([warehouseApi.listWarehouses({ page: 1, pageSize: 1000 }), inventoryApi.listInventory({ page: 1, pageSize: 10000 })])
  warehouses.value = warehouseRes.data?.records || []
  inventorySnapshot.value = snapshotRes.data?.records || []
  await loadData()
})
</script>

<style scoped>
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
</style>
