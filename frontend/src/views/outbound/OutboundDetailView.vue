<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header detail-header"><div><h1 class="page-title">出库单详情</h1><p class="page-subtitle">查看商品明细并推进审核、拣货和发货流程</p></div><div class="actions"><el-button @click="router.back()">返回</el-button><el-button v-if="order.status==='CREATED'" type="primary" :loading="submitting" @click="approve">审核通过</el-button><el-button v-if="order.status==='APPROVED'" type="warning" :loading="submitting" @click="pick">开始拣货</el-button><el-button v-if="['PICKING','CHECKING'].includes(order.status)" type="success" :loading="submitting" @click="ship">确认发货</el-button></div></div>
    <el-result v-if="!loading&&loadFailed" icon="error" title="加载失败" sub-title="无法获取出库单信息"><template #extra><el-button type="primary" @click="loadData">重新加载</el-button></template></el-result>
    <div v-else class="card">
      <el-descriptions :column="3" border><el-descriptions-item label="出库单号">{{order.orderNo||'-'}}</el-descriptions-item><el-descriptions-item label="类型">{{order.orderType||'-'}}</el-descriptions-item><el-descriptions-item label="状态"><el-tag :type="currentStatus.type">{{currentStatus.text}}</el-tag></el-descriptions-item><el-descriptions-item label="客户">{{order.customerName||'-'}}</el-descriptions-item><el-descriptions-item label="仓库">{{order.warehouseName||order.warehouseId||'-'}}</el-descriptions-item><el-descriptions-item label="预计出库">{{order.expectedDate||'-'}}</el-descriptions-item><el-descriptions-item label="总数量">{{order.totalQty??0}}</el-descriptions-item><el-descriptions-item label="已拣货">{{order.pickedQty??0}}</el-descriptions-item><el-descriptions-item label="已发货">{{order.shippedQty??0}}</el-descriptions-item></el-descriptions>
      <el-divider/><el-steps :active="currentStatus.step" finish-status="success" align-center class="steps"><el-step title="创建"/><el-step title="审核"/><el-step title="拣货"/><el-step title="发货"/><el-step title="完成"/></el-steps>
      <div class="section-title">商品明细</div><el-table :data="items" border empty-text="暂无商品明细"><el-table-column prop="skuCode" label="SKU" min-width="150"><template #default="{row}">{{row.skuCode||row.skuId}}</template></el-table-column><el-table-column prop="productName" label="商品" min-width="160"><template #default="{row}">{{row.productName||'-'}}</template></el-table-column><el-table-column prop="requiredQty" label="需求数量" width="100" align="right"/><el-table-column prop="allocatedQty" label="已分配" width="100" align="right"/><el-table-column prop="lockedQty" label="已锁定" width="100" align="right"/><el-table-column prop="pickedQty" label="已拣货" width="100" align="right"/><el-table-column prop="status" label="状态" width="110"/></el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed,onMounted,ref } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import { ElMessage,ElMessageBox } from 'element-plus'
import { outboundApi } from '@/api'
const route=useRoute(),router=useRouter(),loading=ref(false),submitting=ref(false),loadFailed=ref(false),order=ref<Record<string,any>>({}),items=ref<any[]>([]),orderId=Number(route.params.id)
const statusMap:Record<string,{text:string;type:''|'success'|'warning'|'info'|'danger';step:number}>={CREATED:{text:'已创建',type:'info',step:1},APPROVED:{text:'已审核',type:'',step:2},PICKING:{text:'拣货中',type:'warning',step:3},CHECKING:{text:'复核中',type:'warning',step:3},COMPLETED:{text:'已完成',type:'success',step:5},CANCELLED:{text:'已取消',type:'danger',step:0}}
const currentStatus=computed(()=>statusMap[order.value.status]||{text:order.value.status||'-',type:'info' as const,step:0})
async function loadData(){if(!Number.isFinite(orderId)){loadFailed.value=true;return}loading.value=true;loadFailed.value=false;try{const[o,i]=await Promise.all([outboundApi.getOutboundOrderById(orderId),outboundApi.getOutboundOrderItems(orderId)]);order.value=o.data||{};items.value=i.data||[]}catch{loadFailed.value=true}finally{loading.value=false}}
async function run(action:()=>Promise<any>,message:string,confirm:string){await ElMessageBox.confirm(confirm,message,{type:'warning'});submitting.value=true;try{await action();ElMessage.success(`${message}成功`);await loadData()}finally{submitting.value=false}}
const approve=()=>run(()=>outboundApi.approveOutboundOrder(orderId),'审核','确认审核通过该出库单？'),pick=()=>run(()=>outboundApi.pickOutboundOrder(orderId),'拣货','确认分配并锁定库存，开始拣货？'),ship=()=>run(()=>outboundApi.shipOutboundOrder(orderId),'发货','确认商品复核无误并发货？')
onMounted(loadData)
</script>
<style scoped>.detail-header,.actions{display:flex;align-items:center;justify-content:space-between;gap:12px}.steps{margin:8px 0 32px}.section-title{font-size:16px;font-weight:600;margin-bottom:16px}</style>
