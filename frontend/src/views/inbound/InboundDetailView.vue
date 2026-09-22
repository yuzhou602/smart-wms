<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header detail-header">
      <div><h1 class="page-title">入库单详情</h1><p class="page-subtitle">查看入库单明细并推进收货、上架流程</p></div>
      <div class="actions"><el-button @click="router.back()">返回</el-button><el-button v-if="['CREATED','RECEIVING'].includes(order.status)" type="primary" :loading="submitting" @click="receive">确认收货</el-button><el-button v-if="order.status === 'RECEIVED'" type="success" :loading="submitting" @click="putaway">确认上架</el-button></div>
    </div>
    <el-result v-if="!loading && loadFailed" icon="error" title="加载失败" sub-title="无法获取入库单信息"><template #extra><el-button type="primary" @click="loadData">重新加载</el-button></template></el-result>
    <div v-else class="card">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="入库单号">{{ order.orderNo || '-' }}</el-descriptions-item><el-descriptions-item label="类型">{{ order.orderType || '-' }}</el-descriptions-item><el-descriptions-item label="状态"><el-tag :type="currentStatus.type">{{ currentStatus.text }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="供应商">{{ order.supplierName || '-' }}</el-descriptions-item><el-descriptions-item label="仓库">{{ order.warehouseName || order.warehouseId || '-' }}</el-descriptions-item><el-descriptions-item label="预计到货">{{ order.expectedDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ order.totalQty ?? 0 }}</el-descriptions-item><el-descriptions-item label="已收货">{{ order.receivedQty ?? 0 }}</el-descriptions-item><el-descriptions-item label="已上架">{{ order.putawayQty ?? 0 }}</el-descriptions-item>
      </el-descriptions>
      <el-divider/><el-steps :active="currentStatus.step" finish-status="success" align-center class="steps"><el-step title="创建"/><el-step title="收货"/><el-step title="上架"/><el-step title="完成"/></el-steps>
      <div class="section-title">商品明细</div>
      <el-table :data="items" border empty-text="暂无商品明细"><el-table-column prop="skuCode" label="SKU" width="150"><template #default="{row}">{{ row.skuCode || row.skuId }}</template></el-table-column><el-table-column prop="productName" label="商品" min-width="160"><template #default="{row}">{{ row.productName || '-' }}</template></el-table-column><el-table-column prop="batchNo" label="批次" width="140"/><el-table-column prop="expectedQty" label="预期数量" width="100" align="right"/><el-table-column prop="receivedQty" label="已收货" width="100" align="right"/><el-table-column prop="putawayQty" label="已上架" width="100" align="right"/><el-table-column prop="status" label="状态" width="110"/></el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed,onMounted,ref } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import { ElMessage,ElMessageBox } from 'element-plus'
import { inboundApi } from '@/api'
const route=useRoute(),router=useRouter(),loading=ref(false),submitting=ref(false),loadFailed=ref(false),order=ref<Record<string,any>>({}),items=ref<any[]>([]),orderId=Number(route.params.id)
const statusMap:Record<string,{text:string;type:''|'success'|'warning'|'info'|'danger';step:number}>={CREATED:{text:'已创建',type:'info',step:1},RECEIVING:{text:'收货中',type:'warning',step:1},RECEIVED:{text:'已收货',type:'',step:2},PUTAWAY:{text:'已上架',type:'success',step:4},COMPLETED:{text:'已完成',type:'success',step:4},CANCELLED:{text:'已取消',type:'danger',step:0}}
const currentStatus=computed(()=>statusMap[order.value.status]||{text:order.value.status||'-',type:'info' as const,step:0})
async function loadData(){if(!Number.isFinite(orderId)){loadFailed.value=true;return}loading.value=true;loadFailed.value=false;try{const[o,i]=await Promise.all([inboundApi.getInboundOrderById(orderId),inboundApi.getInboundOrderItems(orderId)]);order.value=o.data||{};items.value=i.data||[]}catch{loadFailed.value=true}finally{loading.value=false}}
async function run(action:()=>Promise<any>,message:string,confirm:string){await ElMessageBox.confirm(confirm,message,{type:'warning'});submitting.value=true;try{await action();ElMessage.success(`${message}成功`);await loadData()}finally{submitting.value=false}}
const receive=()=>run(()=>inboundApi.receiveInboundOrder(orderId),'收货','确认本单商品已全部收货？'),putaway=()=>run(()=>inboundApi.putawayInboundOrder(orderId),'上架','确认将已收货商品上架并增加库存？')
onMounted(loadData)
</script>
<style scoped>.detail-header,.actions{display:flex;align-items:center;justify-content:space-between;gap:12px}.steps{margin:8px 0 32px}.section-title{font-size:16px;font-weight:600;margin-bottom:16px}</style>
