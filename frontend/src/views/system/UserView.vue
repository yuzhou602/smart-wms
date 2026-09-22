<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-subtitle">管理系统用户账号和权限</p>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索用户名/姓名" prefix-icon="Search" clearable @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-permission="'user:create'" type="success" @click="showCreateDialog"><el-icon><Plus /></el-icon> 新增用户</el-button>
      </div>
      <el-table :data="tableData" stripe border v-loading="loading">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'user:update'" type="primary" link @click="editUser(row)">编辑</el-button>
            <el-button v-permission="'user:assign'" type="warning" link @click="openRoleDialog(row)">角色</el-button>
            <el-button v-permission="'user:delete'" type="danger" link @click="deleteUser(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" :title="`为 ${roleUser?.realName || roleUser?.username || ''} 分配角色`" width="480px">
      <el-checkbox-group v-model="selectedRoleIds" v-loading="roleLoading" class="role-list">
        <el-checkbox v-for="role in availableRoles" :key="role.id" :value="role.id" :disabled="role.status !== 1">
          {{ role.roleName }} <span class="role-code">{{ role.roleCode }}</span>
        </el-checkbox>
      </el-checkbox-group>
      <el-empty v-if="!roleLoading && !availableRoles.length" description="暂无角色" />
      <template #footer><el-button @click="roleDialogVisible = false">取消</el-button><el-button type="primary" :loading="roleSubmitting" @click="saveUserRoles">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { roleApi, userApi } from '@/api'

const searchKeyword = ref('')
const tableData = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<any>({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  status: 1
})
const editId = ref<number | null>(null)
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleSubmitting = ref(false)
const roleUser = ref<any>(null)
const availableRoles = ref<any[]>([])
const selectedRoleIds = ref<number[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.listUsers({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editId.value = null
  form.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    status: 1
  }
  dialogVisible.value = true
}

const editUser = (row: any) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row, password: '' }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value && editId.value) {
      await userApi.updateUser(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await userApi.createUser(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteUser = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认删除该用户？', '删除确认')
    await userApi.deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const openRoleDialog = async (row: any) => {
  roleUser.value = row
  roleDialogVisible.value = true
  roleLoading.value = true
  try {
    const [rolesRes, selectedRes] = await Promise.all([roleApi.listRoles(), userApi.getUserRoles(row.id)])
    availableRoles.value = rolesRes.data || []
    selectedRoleIds.value = selectedRes.data || []
  } finally { roleLoading.value = false }
}

const saveUserRoles = async () => {
  if (!roleUser.value) return
  roleSubmitting.value = true
  try {
    await userApi.assignRoles(roleUser.value.id, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } finally { roleSubmitting.value = false }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-list { display: flex; flex-direction: column; gap: 12px; min-height: 80px; }
.role-code { color: var(--el-text-color-secondary); margin-left: 8px; font-size: 12px; }
</style>
