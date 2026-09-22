<template>
  <div class="page-container">
    <div class="page-header"><h1 class="page-title">角色权限</h1><p class="page-subtitle">管理系统角色和权限配置</p></div>
    <div class="card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索角色名称或编码" prefix-icon="Search" clearable @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreate"><el-icon><Plus /></el-icon> 新增角色</el-button>
      </div>
      <el-table :data="rows" stripe border v-loading="loading">
        <el-table-column prop="roleName" label="角色名称" width="150" /><el-table-column prop="roleCode" label="角色编码" width="170" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip /><el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="240" fixed="right"><template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button><el-button type="warning" link @click="openPermissions(row)">分配权限</el-button><el-button type="danger" link @click="removeRole(row)">删除</el-button>
        </template></el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="roleDialog" :title="editingId ? '编辑角色' : '新增角色'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="角色编码" prop="roleCode"><el-input v-model="form.roleCode" :disabled="!!editingId" placeholder="例如 warehouse_manager" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" :max="999" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">正常</el-radio><el-radio :value="0">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="roleDialog = false">取消</el-button><el-button type="primary" :loading="submitting" @click="submitRole">保存</el-button></template>
    </el-dialog>
    <el-dialog v-model="permissionDialog" :title="`为 ${currentRole?.roleName || ''} 分配权限`" width="600px">
      <el-alert title="保存后，该角色的用户重新登录即可获取最新权限。" type="info" :closable="false" show-icon />
      <el-tree ref="treeRef" class="permission-tree" :data="permissionTree" node-key="id" show-checkbox default-expand-all :props="{ label: 'permissionName', children: 'children' }" v-loading="permissionLoading" />
      <el-empty v-if="!permissionLoading && !permissionTree.length" description="暂无可分配权限" />
      <template #footer><el-button @click="permissionDialog = false">取消</el-button><el-button type="primary" :loading="submitting" @click="savePermissions">保存权限</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type ElTree } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { permissionApi, roleApi } from '@/api'

const keyword = ref(''); const rows = ref<any[]>([]); const loading = ref(false); const submitting = ref(false)
const roleDialog = ref(false); const permissionDialog = ref(false); const permissionLoading = ref(false)
const editingId = ref<number | null>(null); const currentRole = ref<any>(); const permissionTree = ref<any[]>([])
const formRef = ref<FormInstance>(); const treeRef = ref<InstanceType<typeof ElTree>>()
const form = reactive({ roleName: '', roleCode: '', description: '', sortOrder: 0, status: 1 })
const rules: FormRules = { roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }], roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }] }
const resetForm = () => Object.assign(form, { roleName: '', roleCode: '', description: '', sortOrder: 0, status: 1 })
const loadData = async () => { loading.value = true; try { rows.value = (await roleApi.listRoles({ keyword: keyword.value })).data || [] } finally { loading.value = false } }
const openCreate = () => { editingId.value = null; resetForm(); roleDialog.value = true }
const openEdit = (row: any) => { editingId.value = row.id; Object.assign(form, { roleName: row.roleName, roleCode: row.roleCode, description: row.description || '', sortOrder: row.sortOrder || 0, status: row.status }); roleDialog.value = true }
const submitRole = async () => { if (!await formRef.value?.validate().catch(() => false)) return; submitting.value = true; try { editingId.value ? await roleApi.updateRole(editingId.value, form) : await roleApi.createRole(form); ElMessage.success(editingId.value ? '角色更新成功' : '角色创建成功'); roleDialog.value = false; await loadData() } finally { submitting.value = false } }
const removeRole = async (row: any) => { try { await ElMessageBox.confirm(`确认删除角色“${row.roleName}”？`, '删除确认', { type: 'warning' }); await roleApi.deleteRole(row.id); ElMessage.success('角色已删除'); await loadData() } catch (e) { if (e !== 'cancel' && e !== 'close') ElMessage.error('删除失败') } }
const openPermissions = async (row: any) => { currentRole.value = row; permissionDialog.value = true; permissionLoading.value = true; try { const [tree, ids] = await Promise.all([permissionApi.getTree(), roleApi.getPermissionIds(row.id)]); permissionTree.value = tree.data || []; await nextTick(); treeRef.value?.setCheckedKeys(ids.data || [], false) } finally { permissionLoading.value = false } }
const savePermissions = async () => { if (!currentRole.value) return; submitting.value = true; try { const ids = [...(treeRef.value?.getCheckedKeys(false) || []), ...(treeRef.value?.getHalfCheckedKeys() || [])].map(Number); await roleApi.assignPermissions(currentRole.value.id, [...new Set(ids)]); ElMessage.success('权限分配成功'); permissionDialog.value = false } finally { submitting.value = false } }
onMounted(loadData)
</script>

<style scoped>.permission-tree { margin-top: 16px; max-height: 480px; overflow: auto; border: 1px solid var(--el-border-color-lighter); border-radius: 6px; padding: 12px; }</style>
