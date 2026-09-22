<template>
  <div class="page-container">
    <div class="page-header"><h1 class="page-title">操作日志</h1><p class="page-subtitle">审计系统写操作、执行结果与异常信息</p></div>
    <div class="card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索用户、操作或目标" prefix-icon="Search" clearable @keyup.enter="search" />
        <el-input v-model="module" placeholder="模块" clearable style="width:150px" />
        <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DDTHH:mm:ss" />
        <el-button type="primary" @click="search">查询</el-button><el-button @click="reset">重置</el-button>
      </div>
      <el-table :data="rows" stripe border v-loading="loading" empty-text="暂无操作日志" @row-click="openDetail">
        <el-table-column prop="username" label="操作人" width="110"><template #default="{row}">{{row.username||'系统'}}</template></el-table-column>
        <el-table-column prop="module" label="模块" width="130" show-overflow-tooltip/><el-table-column prop="operation" label="操作" width="140" show-overflow-tooltip/>
        <el-table-column prop="description" label="目标/说明" min-width="180" show-overflow-tooltip/><el-table-column prop="url" label="请求地址" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="ip" label="IP" width="130"/><el-table-column label="耗时" width="90" align="right"><template #default="{row}">{{row.duration??0}} ms</template></el-table-column>
        <el-table-column label="结果" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'成功':'失败'}}</el-tag></template></el-table-column>
        <el-table-column prop="createdAt" label="时间" width="175"/><el-table-column label="操作" width="80" fixed="right"><template #default="{row}"><el-button link type="primary" @click.stop="openDetail(row)">详情</el-button></template></el-table-column>
      </el-table>
      <div class="pagination-wrapper"><el-pagination v-model:current-page="page" v-model:page-size="pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @size-change="loadData" @current-change="loadData"/></div>
    </div>
    <el-drawer v-model="drawerVisible" title="日志详情" size="620px">
      <el-descriptions :column="1" border><el-descriptions-item label="操作人">{{detail.username||'系统'}}（ID: {{detail.userId||'-'}}）</el-descriptions-item><el-descriptions-item label="模块/操作">{{detail.module||'-'}} / {{detail.operation||'-'}}</el-descriptions-item><el-descriptions-item label="请求URL">{{detail.url||'-'}}</el-descriptions-item><el-descriptions-item label="执行方法">{{detail.method||'-'}}</el-descriptions-item><el-descriptions-item label="来源IP">{{detail.ip||'-'}}</el-descriptions-item><el-descriptions-item label="执行结果"><el-tag :type="detail.status===1?'success':'danger'">{{detail.status===1?'成功':'失败'}}</el-tag></el-descriptions-item><el-descriptions-item label="耗时">{{detail.duration??0}} ms</el-descriptions-item><el-descriptions-item label="时间">{{detail.createdAt||'-'}}</el-descriptions-item></el-descriptions>
      <h4>请求参数</h4><pre class="detail-code">{{prettyParams(detail.params)}}</pre><template v-if="detail.errorMsg"><h4>异常信息</h4><pre class="detail-code error">{{detail.errorMsg}}</pre></template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { logApi } from '@/api'
const keyword=ref('');const module=ref('');const dateRange=ref<string[]>([]);const rows=ref<any[]>([]);const loading=ref(false);const page=ref(1);const pageSize=ref(20);const total=ref(0);const drawerVisible=ref(false);const detail=ref<any>({})
const loadData=async()=>{loading.value=true;try{const res=await logApi.listLogs({page:page.value,pageSize:pageSize.value,keyword:keyword.value,module:module.value,startTime:dateRange.value?.[0],endTime:dateRange.value?.[1]});rows.value=res.data?.records||[];total.value=Number(res.data?.total||0)}finally{loading.value=false}}
const search=()=>{page.value=1;loadData()};const reset=()=>{keyword.value='';module.value='';dateRange.value=[];search()};const openDetail=(row:any)=>{detail.value=row;drawerVisible.value=true}
const prettyParams=(value:string)=>{if(!value)return '-';try{return JSON.stringify(JSON.parse(value),null,2)}catch{return value}}
onMounted(loadData)
</script>
<style scoped>.detail-code{white-space:pre-wrap;word-break:break-word;background:var(--el-fill-color-light);border-radius:6px;padding:12px;max-height:300px;overflow:auto}.detail-code.error{color:var(--el-color-danger)}</style>
