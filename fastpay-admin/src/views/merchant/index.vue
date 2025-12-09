<template>
  <div class="merchant-page">
    <!-- 搜索区域 -->
    <div class="page-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-input
              v-model="queryParams.merchantName"
              placeholder="商户名称"
              clearable
              style="width: 180px"
              @keyup.enter="loadData"
            />
            <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增商户
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-card" style="margin-top: 16px">
      <div class="card-body">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="merchantNo" label="商户编号" width="150" />
          <el-table-column prop="merchantName" label="商户名称" min-width="120" />
          <el-table-column prop="username" label="登录账号" width="120" />
          <el-table-column prop="contactName" label="联系人" width="100" />
          <el-table-column label="累计交易" width="120">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.totalAmount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                link 
                size="small" 
                @click="handleStatus(row, row.status === 1 ? 0 : 1)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="warning" link size="small" @click="handleResetKey(row)">重置密钥</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadData"
            @current-change="loadData"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="formData.id ? '编辑商户' : '新增商户'"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="商户名称" prop="merchantName">
          <el-input v-model="formData.merchantName" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="formData.username" placeholder="请输入登录账号" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item v-if="!formData.id" label="登录密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入登录密码" show-password />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="formData.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="formData.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>
        <el-form-item label="回调地址" prop="notifyUrl">
          <el-input v-model="formData.notifyUrl" placeholder="支付成功回调通知地址" />
        </el-form-item>
        <el-form-item label="跳转地址" prop="returnUrl">
          <el-input v-model="formData.returnUrl" placeholder="支付成功跳转地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleFormSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="商户详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商户编号">{{ currentMerchant.merchantNo }}</el-descriptions-item>
        <el-descriptions-item label="商户名称">{{ currentMerchant.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="登录账号">{{ currentMerchant.username }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentMerchant.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentMerchant.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱">{{ currentMerchant.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="API Key" :span="2">
          <div class="copy-text">
            <span>{{ currentMerchant.apiKey }}</span>
            <el-button type="primary" link size="small" @click="copyText(currentMerchant.apiKey)">
              <el-icon><CopyDocument /></el-icon>
            </el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="API Secret" :span="2">
          <div class="copy-text">
            <span>{{ currentMerchant.apiSecret }}</span>
            <el-button type="primary" link size="small" @click="copyText(currentMerchant.apiSecret)">
              <el-icon><CopyDocument /></el-icon>
            </el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="回调地址" :span="2">{{ currentMerchant.notifyUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="跳转地址" :span="2">{{ currentMerchant.returnUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="累计交易">
          <span class="amount-text">¥{{ currentMerchant.totalAmount || 0 }}</span>
          （{{ currentMerchant.totalCount || 0 }}笔）
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentMerchant.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentMerchant.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentMerchant.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{ formatTime(currentMerchant.lastLoginTime) || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 商户管理页面
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantPage, createMerchant, updateMerchant, updateMerchantStatus, resetMerchantApiKey } from '@/api'

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  merchantName: '',
  status: undefined
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 表单相关
const formVisible = ref(false)
const formLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  merchantName: '',
  username: '',
  password: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  notifyUrl: '',
  returnUrl: '',
  remark: ''
})

const formRules = {
  merchantName: [{ required: true, message: '请输入商户名称', trigger: 'blur' }],
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 详情弹窗
const detailVisible = ref(false)
const currentMerchant = ref({})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMerchantPage(queryParams)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载商户列表失败:', error)
  }
  loading.value = false
}

// 重置查询
const resetQuery = () => {
  queryParams.merchantName = ''
  queryParams.status = undefined
  queryParams.current = 1
  loadData()
}

// 新增商户
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    merchantName: '',
    username: '',
    password: '',
    contactName: '',
    contactPhone: '',
    contactEmail: '',
    notifyUrl: '',
    returnUrl: '',
    remark: ''
  })
  formVisible.value = true
}

// 编辑商户
const handleEdit = (record) => {
  Object.assign(formData, {
    id: record.id,
    merchantName: record.merchantName,
    username: record.username,
    password: '',
    contactName: record.contactName,
    contactPhone: record.contactPhone,
    contactEmail: record.contactEmail,
    notifyUrl: record.notifyUrl,
    returnUrl: record.returnUrl,
    remark: record.remark
  })
  formVisible.value = true
}

// 查看详情
const handleView = (record) => {
  currentMerchant.value = { ...record }
  detailVisible.value = true
}

// 提交表单
const handleFormSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    formLoading.value = true
    try {
      if (formData.id) {
        await updateMerchant(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createMerchant(formData)
        ElMessage.success('创建成功')
      }
      formVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存商户失败:', error)
    }
    formLoading.value = false
  })
}

// 更新状态
const handleStatus = (record, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该商户吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateMerchantStatus(record.id, status)
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

// 重置API密钥
const handleResetKey = (record) => {
  ElMessageBox.confirm(
    '重置后原密钥将失效，请及时通知商户更新配置',
    '确定要重置该商户的API密钥吗？',
    { type: 'warning' }
  ).then(async () => {
    const res = await resetMerchantApiKey(record.id)
    ElMessage.success('重置成功')
    currentMerchant.value = res.data
    detailVisible.value = true
  }).catch(() => {})
}

// 复制文本
const copyText = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string' && time.includes('-')) {
    return time.replace('T', ' ').substring(0, 19)
  }
  return time
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.merchant-page {
  padding: 0;
}

.amount-text {
  color: #e6a23c;
  font-weight: 500;
}

.copy-text {
  display: flex;
  align-items: center;
  gap: 8px;
  word-break: break-all;
}
</style>
