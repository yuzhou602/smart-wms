<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">商品中心</h1>
      <p class="page-subtitle">管理商品信息、SKU和分类</p>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索商品名称/编码" prefix-icon="Search" clearable />
        <el-select v-model="filterCategory" placeholder="商品分类" clearable>
          <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'product:create'" type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增商品
        </el-button>
      </div>

      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="productCode" label="商品编码" width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="skuCount" label="SKU数量" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'product:update'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="viewSkus(row)">SKU</el-button>
            <el-button v-permission="'product:delete'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品编码">
          <el-input v-model="form.productCode" placeholder="请输入商品编码" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="form.brand" placeholder="请输入品牌" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="skuDrawerVisible" :title="`${currentProduct?.productName || ''} · SKU管理`" size="760px">
      <div class="sku-toolbar"><el-button v-permission="'product:create'" type="primary" @click="openSkuForm()"><el-icon><Plus /></el-icon> 新增SKU</el-button></div>
      <el-table :data="skus" border stripe v-loading="skuLoading" empty-text="暂无SKU">
        <el-table-column prop="skuCode" label="SKU编码" width="140" /><el-table-column prop="specification" label="规格" min-width="130" />
        <el-table-column prop="unit" label="单位" width="70" /><el-table-column prop="barcode" label="条码" width="150" />
        <el-table-column prop="safetyStock" label="安全库存" width="95" align="right" /><el-table-column prop="maxStock" label="最大库存" width="95" align="right" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'停用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="130" fixed="right"><template #default="{row}"><el-button v-permission="'product:update'" link type="primary" @click="openSkuForm(row)">编辑</el-button><el-button v-permission="'product:delete'" link type="danger" @click="deleteSku(row)">删除</el-button></template></el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog v-model="skuDialogVisible" :title="skuForm.id ? '编辑SKU' : '新增SKU'" width="620px" append-to-body>
      <el-form :model="skuForm" label-width="90px">
        <el-form-item label="SKU编码" required><el-input v-model="skuForm.skuCode" /></el-form-item>
        <el-form-item label="规格"><el-input v-model="skuForm.specification" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="skuForm.unit" /></el-form-item>
        <el-form-item label="条码"><el-input v-model="skuForm.barcode" /></el-form-item>
        <el-form-item label="重量(kg)"><el-input-number v-model="skuForm.weight" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="体积(m³)"><el-input-number v-model="skuForm.volume" :min="0" :precision="4" /></el-form-item>
        <el-form-item label="安全库存"><el-input-number v-model="skuForm.safetyStock" :min="0" :precision="0" /></el-form-item>
        <el-form-item label="最大库存"><el-input-number v-model="skuForm.maxStock" :min="0" :precision="0" /></el-form-item>
        <el-form-item label="出库策略"><el-select v-model="skuForm.outboundStrategy"><el-option label="先进先出 FIFO" value="FIFO"/><el-option label="先到期先出 FEFO" value="FEFO"/><el-option label="人工指定" value="MANUAL"/></el-select></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="skuForm.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="skuDialogVisible=false">取消</el-button><el-button type="primary" :loading="skuSubmitting" @click="submitSku">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { productReferenceApi, skuApi } from '@/api'

const loading = ref(false)
const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const categories = ref<any[]>([])
const skuDrawerVisible = ref(false)
const skuDialogVisible = ref(false)
const skuLoading = ref(false)
const skuSubmitting = ref(false)
const currentProduct = ref<any>()
const skus = ref<any[]>([])
const skuForm = reactive<any>({})

const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const form = reactive({
  id: null,
  productCode: '',
  productName: '',
  categoryId: null,
  brand: '',
  description: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/products', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchKeyword.value,
        categoryId: filterCategory.value,
        status: filterStatus.value
      }
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增商品'
  Object.assign(form, { id: null, productCode: '', productName: '', categoryId: null, brand: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑商品'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/products/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  try {
    if (form.id) {
      await request.put(`/products/${form.id}`, form)
    } else {
      await request.post('/products', form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const loadSkus = async () => {
  if (!currentProduct.value) return
  skuLoading.value = true
  try { skus.value = (await skuApi.listByProduct(currentProduct.value.id)).data || [] }
  finally { skuLoading.value = false }
}
const viewSkus = async (row: any) => {
  currentProduct.value = row
  skuDrawerVisible.value = true
  await loadSkus()
}
const openSkuForm = (row?: any) => {
  Object.assign(skuForm, { id: null, productId: currentProduct.value.id, skuCode: '', specification: '', unit: '件', barcode: '', weight: 0, volume: 0, safetyStock: 0, maxStock: 0, outboundStrategy: 'FIFO', status: 1 }, row || {})
  skuDialogVisible.value = true
}
const submitSku = async () => {
  if (!skuForm.skuCode.trim()) return ElMessage.warning('请输入SKU编码')
  if (skuForm.maxStock > 0 && skuForm.maxStock < skuForm.safetyStock) return ElMessage.warning('最大库存不能小于安全库存')
  skuSubmitting.value = true
  try {
    skuForm.id ? await skuApi.updateSku(skuForm.id, skuForm) : await skuApi.createSku(skuForm)
    ElMessage.success('SKU保存成功'); skuDialogVisible.value = false; await loadSkus(); await loadData()
  } finally { skuSubmitting.value = false }
}
const deleteSku = async (row: any) => {
  try { await ElMessageBox.confirm(`确认删除SKU“${row.skuCode}”？有库存的SKU不能删除。`, '删除确认', {type:'warning'}); await skuApi.deleteSku(row.id); ElMessage.success('SKU已删除'); await loadSkus(); await loadData() }
  catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error('删除失败') }
}

onMounted(async () => {
  const res = await productReferenceApi.listCategories()
  categories.value = res.data || []
  await loadData()
})
</script>

<style scoped>.sku-toolbar{display:flex;justify-content:flex-end;margin-bottom:16px}</style>
