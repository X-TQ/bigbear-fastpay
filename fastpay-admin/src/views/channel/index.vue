<template>
  <div class="channel-page">
    <!-- 搜索工具栏 -->
    <div class="page-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-select v-model="searchParams.merchantId" placeholder="选择商户" clearable style="width: 180px" @change="handleSearch">
              <el-option v-for="m in merchantList" :key="m.id" :label="m.merchantName" :value="m.id" />
            </el-select>
            <el-select v-model="searchParams.payType" placeholder="支付类型" clearable style="width: 130px" @change="handleSearch">
              <el-option label="微信支付" value="wxpay" />
              <el-option label="支付宝" value="alipay" />
            </el-select>
            <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 100px" @change="handleSearch">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增通道
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-card" style="margin-top: 16px;">
      <div class="card-body">
        <el-table :data="channelList" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="channelName" label="通道名称" min-width="140">
            <template #default="{ row }">
              <span class="channel-name">{{ row.channelName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="merchantName" label="所属商户" min-width="120" />
          <el-table-column prop="payType" label="支付类型" width="110">
            <template #default="{ row }">
              <div class="pay-type-cell">
                <span :class="['pay-type-icon', row.payType]">
                  <el-icon v-if="row.payType === 'wxpay'"><ChatDotRound /></el-icon>
                  <el-icon v-else><Wallet /></el-icon>
                </span>
                <span>{{ row.payType === 'wxpay' ? '微信' : '支付宝' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                link 
                size="small" 
                @click="handleStatusChange(row, row.status === 1 ? 0 : 1)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="searchParams.current"
            v-model:page-size="searchParams.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadChannelList"
            @current-change="loadChannelList"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="formVisible" 
      :title="formData.id ? '编辑通道' : '新增通道'" 
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="所属商户" prop="merchantId">
          <el-select v-model="formData.merchantId" placeholder="请选择商户" style="width: 100%" :disabled="!!formData.id">
            <el-option v-for="m in merchantList" :key="m.id" :label="m.merchantName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="通道名称" prop="channelName">
          <el-input v-model="formData.channelName" placeholder="请输入通道名称" />
        </el-form-item>
        <el-form-item label="支付类型" prop="payType">
          <el-select v-model="formData.payType" placeholder="请选择支付类型" style="width: 100%" :disabled="!!formData.id">
            <el-option label="微信支付" value="wxpay" />
            <el-option label="支付宝" value="alipay" />
          </el-select>
          <div v-if="formData.id" class="form-tip">支付类型创建后不可修改</div>
        </el-form-item>
        <el-form-item label="消息模版" prop="template">
          <el-input 
            v-model="formData.template" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入消息模版（JSON格式）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * FAST 易支付 - 后台通道管理
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getChannelPage, createChannel, updateChannel, updateChannelStatus, deleteChannel,
  getMerchantList 
} from '@/api'

// 商户列表
const merchantList = ref([])

// 搜索参数
const searchParams = reactive({
  current: 1,
  size: 10,
  merchantId: undefined,
  payType: undefined,
  status: undefined
})

// 列表数据
const loading = ref(false)
const channelList = ref([])
const total = ref(0)

// 表单相关
const formVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  merchantId: undefined,
  channelName: '',
  payType: '',
  template: ''
})
const formRules = {
  merchantId: [{ required: true, message: '请选择商户', trigger: 'change' }],
  channelName: [{ required: true, message: '请输入通道名称', trigger: 'blur' }],
  payType: [{ required: true, message: '请选择支付类型', trigger: 'change' }]
}

// 加载商户列表
const loadMerchantList = async () => {
  try {
    const res = await getMerchantList()
    merchantList.value = res.data || []
  } catch (error) {
    console.error('加载商户列表失败:', error)
  }
}

// 加载通道列表
const loadChannelList = async () => {
  loading.value = true
  try {
    const res = await getChannelPage(searchParams)
    channelList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载通道列表失败:', error)
  }
  loading.value = false
}

// 搜索
const handleSearch = () => {
  searchParams.current = 1
  loadChannelList()
}

// 重置
const handleReset = () => {
  searchParams.merchantId = undefined
  searchParams.payType = undefined
  searchParams.status = undefined
  searchParams.current = 1
  loadChannelList()
}

// 新增
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    merchantId: undefined,
    channelName: '',
    payType: '',
    template: ''
  })
  formVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(formData, {
    id: row.id,
    merchantId: row.merchantId,
    channelName: row.channelName,
    payType: row.payType,
    template: row.template || ''
  })
  formVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (formData.id) {
        await updateChannel(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createChannel(formData)
        ElMessage.success('创建成功')
      }
      formVisible.value = false
      loadChannelList()
    } catch (error) {
      console.error('操作失败:', error)
    }
    submitting.value = false
  })
}

// 状态变更
const handleStatusChange = async (row, status) => {
  const action = status === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确定要${action}该通道吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await updateChannelStatus(row.id, status)
        ElMessage.success('操作成功')
        loadChannelList()
      } catch (error) {
        console.error('操作失败:', error)
      }
    }).catch(() => {})
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该通道吗？删除后不可恢复', '警告', { type: 'warning' })
    .then(async () => {
      try {
        await deleteChannel(row.id)
        ElMessage.success('删除成功')
        loadChannelList()
      } catch (error) {
        console.error('删除失败:', error)
      }
    }).catch(() => {})
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
  loadMerchantList()
  loadChannelList()
})
</script>

<style scoped>
.channel-page {
  padding: 0;
}

.channel-name {
  font-weight: 500;
  color: #333;
}

.pay-type-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pay-type-icon {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;

  &.wxpay {
    background: #07c160;
  }

  &.alipay {
    background: #1677ff;
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
