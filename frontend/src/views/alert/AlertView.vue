<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">预警中心</h1>
      <p class="page-subtitle">监控库存预警、临期商品、呆滞库存等</p>
    </div>

    <div class="metrics-grid">
      <div class="metric-card"><div class="metric-label">高风险</div><div class="metric-value" style="color: #DC2626;">{{ metrics.high }}</div></div>
      <div class="metric-card"><div class="metric-label">中风险</div><div class="metric-value" style="color: #D97706;">{{ metrics.medium }}</div></div>
      <div class="metric-card"><div class="metric-label">低风险</div><div class="metric-value" style="color: #2563EB;">{{ metrics.low }}</div></div>
      <div class="metric-card"><div class="metric-label">已处理</div><div class="metric-value" style="color: #16A34A;">{{ metrics.handled }}</div></div>
    </div>

    <div class="card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="已处理" name="handled" />
        <el-tab-pane label="低库存" name="LOW_STOCK" />
        <el-tab-pane label="临期" name="EXPIRING" />
        <el-tab-pane label="呆滞" name="SLOW_MOVING" />
        <el-tab-pane label="超储" name="OVERSTOCK" />
      </el-tabs>
      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="alertType" label="预警类型" width="120">
          <template #default="{ row }">
            {{ getAlertTypeText(row.alertType) }}
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getAlertLevelType(row.alertLevel)">{{ getAlertLevelText(row.alertLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" width="150" />
        <el-table-column prop="content" label="预警内容" min-width="200" />
        <el-table-column prop="currentValue" label="当前值" width="100" />
        <el-table-column prop="thresholdValue" label="阈值" width="100" />
        <el-table-column prop="suggestion" label="建议" min-width="150" />
        <el-table-column prop="isHandled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isHandled === 1 ? 'success' : 'danger'">{{ row.isHandled === 1 ? '已处理' : '待处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.isHandled === 0" type="success" link @click="handleAlert(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { alertApi } from '@/api'

const activeTab = ref('all')
const tableData = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const metrics = ref({
  high: 0,
  medium: 0,
  low: 0,
  handled: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (activeTab.value === 'pending') {
      params.isHandled = 0
    } else if (activeTab.value === 'handled') {
      params.isHandled = 1
    } else if (activeTab.value !== 'all') {
      params.alertType = activeTab.value
    }
    const res = await alertApi.listAlerts(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0

    const summaryRes = await alertApi.getSummary()
    metrics.value = { high: Number(summaryRes.data?.high || 0), medium: Number(summaryRes.data?.medium || 0), low: Number(summaryRes.data?.low || 0), handled: Number(summaryRes.data?.handled || 0) }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  loadData()
}

const getAlertTypeText = (type: string) => {
  const map: Record<string, string> = {
    'LOW_STOCK': '低库存',
    'EXPIRING': '临期',
    'SLOW_MOVING': '呆滞',
    'OVERSTOCK': '超储'
  }
  return map[type] || type
}

const getAlertLevelType = (level: string) => {
  const map: Record<string, string> = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'info'
  }
  return map[level] || 'info'
}

const getAlertLevelText = (level: string) => {
  const map: Record<string, string> = {
    'HIGH': '高',
    'MEDIUM': '中',
    'LOW': '低'
  }
  return map[level] || level
}

const handleAlert = async (row: any) => {
  try {
    const { value: handleResult } = await ElMessageBox.prompt('请输入处理结果', '处理预警', {
      inputPlaceholder: '请输入处理结果'
    })
    await alertApi.handleAlert(row.id, { handleResult })
    ElMessage.success('已处理')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.metrics-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
</style>
