<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">盘点管理</h1>
      <p class="page-subtitle">管理盘点计划和盘点任务</p>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索盘点单号" prefix-icon="Search" clearable @keyup.enter="loadData" />
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 120px">
          <el-option label="已创建" value="CREATED" />
          <el-option label="盘点中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="showCreateDialog"><el-icon><Plus /></el-icon> 新建盘点</el-button>
      </div>
      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="stocktakeNo" label="盘点单号" width="160" />
        <el-table-column prop="stocktakeType" label="类型" width="100">
          <template #default="{ row }">
            {{ row.stocktakeType === 'FULL' ? '全仓盘点' : '抽样盘点' }}
          </template>
        </el-table-column>
        <el-table-column prop="warehouseName" label="仓库" min-width="140"><template #default="{ row }">{{ row.warehouseName || row.warehouseId }}</template></el-table-column>
        <el-table-column prop="totalItems" label="总项数" width="100" />
        <el-table-column prop="countedItems" label="已盘点" width="100" />
        <el-table-column prop="diffItems" label="差异项" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-red-500': row.diffItems > 0 }">{{ row.diffItems }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'CREATED'" type="success" link @click="startStocktake(row)">开始</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" type="warning" link @click="completeStocktake(row)">完成</el-button>
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

    <el-dialog v-model="createDialogVisible" title="新建盘点" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="盘点类型">
          <el-select v-model="createForm.stocktakeType" style="width: 100%">
            <el-option label="全仓盘点（自动生成库存快照）" value="FULL" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点仓库">
          <el-select v-model="createForm.warehouseId" filterable style="width:100%"><el-option v-for="item in warehouses" :key="item.id" :label="item.warehouseName" :value="item.id"/></el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { stocktakeApi, warehouseApi } from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const warehouses = ref<any[]>([])

const searchKeyword = ref('')
const searchStatus = ref('')
const tableData = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const createDialogVisible = ref(false)
const createForm = ref({
  stocktakeType: 'FULL',
  warehouseId: null as number | null,
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await stocktakeApi.listStocktakes({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      status: searchStatus.value
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'CREATED': 'info',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'CREATED': '已创建',
    'IN_PROGRESS': '盘点中',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const showCreateDialog = () => {
  createForm.value = {
    stocktakeType: 'FULL',
    warehouseId: null,
    remark: ''
  }
  createDialogVisible.value = true
  warehouseApi.listWarehouses({ page: 1, pageSize: 1000 }).then(res => { warehouses.value = res.data?.records || res.data || [] })
}

const submitCreate = async () => {
  if (!createForm.value.warehouseId) { ElMessage.warning('请选择盘点仓库'); return }
  try {
    await stocktakeApi.createStocktake({
      stocktake: createForm.value,
      items: []
    })
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

const startStocktake = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认开始盘点？', '开始确认')
    await stocktakeApi.startStocktake(row.id)
    ElMessage.success('已开始')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const completeStocktake = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认完成盘点？', '完成确认')
    await stocktakeApi.completeStocktake(row.id)
    ElMessage.success('已完成')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const viewDetail = (row: any) => router.push(`/stocktake/${row.id}`)

onMounted(() => {
  loadData()
})
</script>
