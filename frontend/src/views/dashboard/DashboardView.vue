<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">仓储运营驾驶舱</h1>
      <p class="page-subtitle">实时监控仓库运营状态，掌握库存动态</p>
    </div>

    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-label">SKU总数</div>
        <div class="metric-value" style="color: #2563EB;">{{ summary.totalSku || 0 }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">库存总量</div>
        <div class="metric-value" style="color: #16A34A;">{{ summary.totalInventory || 0 }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">今日入库</div>
        <div class="metric-value" style="color: #2563EB;">{{ summary.todayInbound || 0 }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">今日出库</div>
        <div class="metric-value" style="color: #D97706;">{{ summary.todayOutbound || 0 }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">待处理任务</div>
        <div class="metric-value" style="color: #DC2626;">{{ summary.pendingTasks || 0 }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">库存预警</div>
        <div class="metric-value" style="color: #DC2626;">{{ summary.alerts || 0 }}</div>
      </div>
    </div>

    <div class="charts-row">
      <div class="card chart-card">
        <div class="card-title">出入库趋势</div>
        <v-chart :option="inboundOutboundOption" style="height: 300px;" />
      </div>
      <div class="card chart-card">
        <div class="card-title">今日任务</div>
        <div class="task-list">
          <div class="task-item" v-for="task in todayTasks" :key="task.id">
            <div class="task-info">
              <span class="task-type" :class="task.type">{{ task.typeName }}</span>
              <span class="task-no">{{ task.taskNo }}</span>
            </div>
            <el-tag :type="task.statusType" size="small">{{ task.status }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="card chart-card">
        <div class="card-title">仓库利用率</div>
        <v-chart :option="utilizationOption" style="height: 300px;" />
      </div>
      <div class="card chart-card">
        <div class="card-title">库存预警</div>
        <div class="alert-list">
          <div class="alert-item" v-for="alert in alerts" :key="alert.id">
            <div class="alert-icon" :class="alert.level">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="alert-content">
              <div class="alert-title">{{ alert.title }}</div>
              <div class="alert-desc">{{ alert.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card recent-activities">
      <div class="card-title">最近业务动态</div>
      <el-timeline>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="activity.time"
          placement="top"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { Warning } from '@element-plus/icons-vue'
import { dashboardApi, taskApi, alertApi } from '@/api'

use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const summary = ref<any>({})
const todayTasks = ref<any[]>([])
const alerts = ref<any[]>([])
const recentActivities = ref<any[]>([])

const inboundOutboundOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['入库', '出库'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [
    { name: '入库', type: 'line', smooth: true, data: [] },
    { name: '出库', type: 'line', smooth: true, data: [] }
  ]
})

const utilizationOption = ref<any>({
  tooltip: { trigger: 'item' },
  legend: { bottom: '5%' },
  series: [{
    name: '仓库利用率',
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
    label: { show: false },
    emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
    labelLine: { show: false },
    data: []
  }]
})

const loadData = async () => {
  try {
    const summaryRes = await dashboardApi.getSummary()
    summary.value = summaryRes.data || {}

    const [trendRes, utilizationRes] = await Promise.all([
      dashboardApi.getInboundOutboundTrend(),
      dashboardApi.getUtilization()
    ])
    const trend = trendRes.data || { days: [], inbound: [], outbound: [] }
    inboundOutboundOption.value = {
      ...inboundOutboundOption.value,
      xAxis: { type: 'category', data: trend.days },
      series: [
        { name: '入库', type: 'line', smooth: true, data: trend.inbound },
        { name: '出库', type: 'line', smooth: true, data: trend.outbound }
      ]
    }
    const warehouses = utilizationRes.data?.warehouses || []
    utilizationOption.value = {
      ...utilizationOption.value,
      series: [{
        name: '仓库利用率', type: 'pie', radius: ['40%', '70%'],
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        data: warehouses.map((item: any) => ({ value: item.value, name: item.name }))
      }]
    }

    const tasksRes = await taskApi.listTasks({ page: 1, pageSize: 5, status: 'IN_PROGRESS' })
    todayTasks.value = (tasksRes.data?.records || []).map((t: any) => ({
      id: t.id,
      taskNo: t.taskNo,
      typeName: getTaskTypeText(t.taskType),
      type: t.taskType?.toLowerCase() || 'picking',
      status: getStatusText(t.status),
      statusType: getStatusType(t.status)
    }))

    const alertsRes = await alertApi.listAlerts({ page: 1, pageSize: 5, status: 'ACTIVE' })
    alerts.value = (alertsRes.data?.records || []).map((a: any) => ({
      id: a.id,
      title: getAlertTypeText(a.alertType),
      content: a.content,
      level: a.alertLevel?.toLowerCase() || 'low'
    }))

    recentActivities.value = [
      { id: 1, content: '系统运行正常', time: '刚刚' }
    ]
  } catch (e) {
    console.error(e)
  }
}

const getTaskTypeText = (type: string) => {
  const map: Record<string, string> = {
    'PUTAWAY': '上架',
    'PICKING': '拣货',
    'STOCKTAKE': '盘点',
    'REPLENISHMENT': '补货'
  }
  return map[type] || type
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': '待处理',
    'IN_PROGRESS': '处理中',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': 'info',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  min-height: 350px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--bg-color);
  border-radius: 8px;
}

.task-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-type {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.task-type.picking { background: #DBEAFE; color: #2563EB; }
.task-type.putaway { background: #D1FAE5; color: #16A34A; }
.task-type.stocktake { background: #FEF3C7; color: #D97706; }

.task-no {
  font-size: 14px;
  color: var(--text-body);
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--bg-color);
  border-radius: 8px;
}

.alert-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.alert-icon.high { background: #FEE2E2; color: #DC2626; }
.alert-icon.medium { background: #FEF3C7; color: #D97706; }
.alert-icon.low { background: #DBEAFE; color: #2563EB; }

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.alert-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.recent-activities {
  margin-bottom: 24px;
}
</style>
