<template>
  <div class="page-container">
    <div class="page-header"><h1 class="page-title">批次管理</h1><p class="page-subtitle">跟踪商品批次、质检状态和有效期</p></div>
    <div class="card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索批次号、SKU或商品" prefix-icon="Search" clearable @keyup.enter="search" />
        <el-select v-model="qualityStatus" placeholder="质检状态" clearable style="width:140px"><el-option label="待质检" value="PENDING"/><el-option label="合格" value="QUALIFIED"/><el-option label="不合格" value="UNQUALIFIED"/></el-select>
        <el-select v-model="expiryFilter" placeholder="效期范围" clearable style="width:150px"><el-option label="30天内到期" :value="30"/><el-option label="90天内到期" :value="90"/><el-option label="180天内到期" :value="180"/></el-select>
        <el-button type="primary" @click="search">查询</el-button>
      </div>
      <el-table :data="rows" stripe border v-loading="loading" empty-text="暂无批次">
        <el-table-column prop="batchNo" label="批次号" width="155"/><el-table-column prop="skuCode" label="SKU" width="145"/><el-table-column prop="productName" label="商品" min-width="160" show-overflow-tooltip/>
        <el-table-column prop="supplierName" label="供应商" min-width="140" show-overflow-tooltip/><el-table-column prop="quantity" label="批次数量" width="100" align="right"/>
        <el-table-column prop="productionDate" label="生产日期" width="115"/><el-table-column prop="expiryDate" label="到期日期" width="115">
          <template #default="{row}"><span :class="expiryClass(row.expiryDate)">{{row.expiryDate || '-'}}</span></template>
        </el-table-column>
        <el-table-column label="效期" width="100"><template #default="{row}"><el-tag :type="expiryInfo(row.expiryDate).type">{{expiryInfo(row.expiryDate).text}}</el-tag></template></el-table-column>
        <el-table-column label="质检" width="100"><template #default="{row}"><el-tag :type="qualityInfo(row.qualityStatus).type">{{qualityInfo(row.qualityStatus).text}}</el-tag></template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'停用'}}</el-tag></template></el-table-column>
      </el-table>
      <div class="pagination-wrapper"><el-pagination v-model:current-page="page" v-model:page-size="pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData"/></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { batchApi } from '@/api'
const keyword=ref(''); const qualityStatus=ref(''); const expiryFilter=ref<number|''>(''); const rows=ref<any[]>([]); const loading=ref(false); const page=ref(1); const pageSize=ref(20); const total=ref(0)
const expiryBefore=()=>{if(!expiryFilter.value)return undefined;const d=new Date();d.setDate(d.getDate()+expiryFilter.value);return d.toISOString().slice(0,10)}
const loadData=async()=>{loading.value=true;try{const res=await batchApi.listBatches({page:page.value,pageSize:pageSize.value,keyword:keyword.value,qualityStatus:qualityStatus.value,expiryBefore:expiryBefore()});rows.value=res.data?.records||[];total.value=Number(res.data?.total||0)}finally{loading.value=false}}
const search=()=>{page.value=1;loadData()}
const daysUntil=(value:string)=>value?Math.ceil((new Date(`${value}T23:59:59`).getTime()-Date.now())/86400000):null
const expiryInfo=(value:string)=>{const days=daysUntil(value);if(days===null)return{type:'info',text:'无期限'};if(days<0)return{type:'danger',text:'已过期'};if(days<=30)return{type:'danger',text:`${days}天`};if(days<=90)return{type:'warning',text:`${days}天`};return{type:'success',text:'正常'}}
const expiryClass=(value:string)=>{const days=daysUntil(value);return days!==null&&days<=30?'expiry-danger':days!==null&&days<=90?'expiry-warning':''}
const qualityInfo=(value:string)=>({PENDING:{type:'warning',text:'待质检'},QUALIFIED:{type:'success',text:'合格'},UNQUALIFIED:{type:'danger',text:'不合格'}}[value]||{type:'info',text:value||'-'})
onMounted(loadData)
</script>
<style scoped>.expiry-danger{color:var(--el-color-danger);font-weight:600}.expiry-warning{color:var(--el-color-warning)}</style>
