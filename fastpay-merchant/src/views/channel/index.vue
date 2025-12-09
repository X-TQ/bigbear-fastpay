<template>
  <div class="channel-page">
    <div class="page-card">
      <!-- 头部操作栏 -->
      <div class="card-header">
        <div class="header-title">
          <el-icon :size="20"><Connection /></el-icon>
          <span>通道管理</span>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增通道
        </el-button>
      </div>

      <!-- 通道列表 -->
      <el-table :data="channelList" v-loading="loading" stripe>
        <el-table-column prop="id" label="通道ID" width="80" />
        <el-table-column prop="channelName" label="通道名称" min-width="150" />
        <el-table-column prop="payType" label="支付类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.payType === 'wxpay' ? 'success' : 'primary'">
              {{ row.payType === 'wxpay' ? '微信支付' : '支付宝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <span :class="['status-tag', row.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              size="small" 
              @click="handleStatusChange(row, row.status === 1 ? 0 : 1)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleViewTemplate(row)">模版</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="formData.id ? '编辑通道' : '新增通道'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="通道名称" prop="channelName">
          <el-input v-model="formData.channelName" placeholder="请输入通道名称，如：微信收款1号" />
        </el-form-item>
        <el-form-item label="支付类型" prop="payType">
          <div class="pay-type-wrapper">
            <el-radio-group v-model="formData.payType" :disabled="!!formData.id">
              <el-radio value="wxpay">
                <el-icon class="wxpay-icon"><ChatDotRound /></el-icon>
                微信支付
              </el-radio>
              <el-radio value="alipay">
                <el-icon class="alipay-icon"><Wallet /></el-icon>
                支付宝
              </el-radio>
            </el-radio-group>
            <div v-if="formData.id" class="form-tip">
              <el-icon><Warning /></el-icon>
              支付类型创建后不可修改
            </div>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 模版查看弹窗 -->
    <el-dialog
      v-model="templateVisible"
      title="通道消息模版"
      width="700px"
    >
      <div class="template-info">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <span>监听软件配置说明</span>
          </template>
          <p>将以下模版配置到监听软件中，监听软件收到支付通知后会替换占位符并回调服务端。</p>
        </el-alert>
        
        <div class="template-section">
          <div class="section-title">回调地址</div>
          <div class="template-content">
            <code>{{ callbackUrl }}</code>
            <el-button type="primary" link size="small" @click="copyText(callbackUrl)">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
        </div>

        <div class="template-section">
          <div class="section-title">Secret（签名密钥）</div>
          <div class="template-content">
            <code class="secret-code">{{ showSecret ? notifySecret : '********************************' }}</code>
            <div class="secret-actions">
              <el-button type="primary" link size="small" @click="showSecret = !showSecret">
                <el-icon v-if="showSecret"><Hide /></el-icon>
                <el-icon v-else><View /></el-icon>
                {{ showSecret ? '隐藏' : '显示' }}
              </el-button>
              <el-button type="primary" link size="small" @click="copyText(notifySecret)">
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
            </div>
          </div>
          <div class="secret-tip">
            <el-icon><Warning /></el-icon>
            请妥善保管，不要泄露给他人
          </div>
        </div>

        <div class="template-section">
          <div class="section-title">消息模版</div>
          <div class="template-content template-code">
            <pre>{{ currentTemplate }}</pre>
            <el-button type="primary" link size="small" @click="copyText(currentTemplate)">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="templateVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Connection, Plus, ChatDotRound, Wallet, CopyDocument, Warning, View, Hide } from '@element-plus/icons-vue'
import { getChannelList, addChannel, updateChannel, updateChannelStatus, deleteChannel, getNotifyConfig, getMerchantInfo } from '@/api'

// 通道列表
const channelList = ref([])
const loading = ref(false)

// 表单相关
const formVisible = ref(false)
const formLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  channelName: '',
  payType: 'wxpay',
  remark: ''
})

const formRules = {
  channelName: [{ required: true, message: '请输入通道名称', trigger: 'blur' }],
  payType: [{ required: true, message: '请选择支付类型', trigger: 'change' }]
}

// 模版相关
const templateVisible = ref(false)
const currentTemplate = ref('')
const callbackUrl = ref('')
const notifySecret = ref('')
const showSecret = ref(false)

// 加载通道列表
const loadChannelList = async () => {
  loading.value = true
  try {
    const res = await getChannelList()
    channelList.value = res.data || []
  } catch (error) {
    console.error('加载通道列表失败:', error)
  }
  loading.value = false
}

// 新增通道
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    channelName: '',
    payType: 'wxpay',
    remark: ''
  })
  formVisible.value = true
}

// 编辑通道
const handleEdit = (row) => {
  Object.assign(formData, {
    id: row.id,
    channelName: row.channelName,
    payType: row.payType,
    remark: row.remark
  })
  formVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    formLoading.value = true
    try {
      if (formData.id) {
        await updateChannel(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await addChannel(formData)
        ElMessage.success('添加成功')
      }
      formVisible.value = false
      loadChannelList()
    } catch (error) {
      console.error('操作失败:', error)
    }
    formLoading.value = false
  })
}

// 状态变更
const handleStatusChange = async (row, status) => {
  const action = status === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(
    `确定要${action}该通道吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    try {
      await updateChannelStatus(row.id, status)
      ElMessage.success('操作成功')
      loadChannelList()
    } catch (error) {
      console.error('操作失败:', error)
    }
  }).catch(() => {})
}

// 删除通道
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '删除后将无法恢复，关联的二维码将无法使用',
    '确定要删除该通道吗？',
    { type: 'warning' }
  ).then(async () => {
    await deleteChannel(row.id)
    ElMessage.success('删除成功')
    loadChannelList()
  }).catch(() => {})
}

// 查看模版
const handleViewTemplate = async (row) => {
  // 重置 Secret 显示状态
  showSecret.value = false
  
  // 从服务端获取回调地址配置
  try {
    const res = await getNotifyConfig()
    callbackUrl.value = res.data?.callbackUrl || `${window.location.origin}/fastpay-server/api/notify/callback`
  } catch (error) {
    // 如果获取失败，使用默认值
    callbackUrl.value = `${window.location.origin}/fastpay-server/api/notify/callback`
  }
  
  // 获取商户的 API Secret
  try {
    const merchantRes = await getMerchantInfo()
    notifySecret.value = merchantRes.data?.apiSecret || ''
  } catch (error) {
    notifySecret.value = ''
  }
  
  // 生成模版
  currentTemplate.value = row.template || `{"channelId":"${row.id}","title":"[title]","msg":"[msg]","receiveTime":"[receive_time]","timestamp":"[timestamp]","sign":"[sign]"}`
  
  templateVisible.value = true
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
  loadChannelList()
})
</script>

<style scoped>
.channel-page {
  padding: 0;
}

.page-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.wxpay-icon {
  color: #07c160;
}

.alipay-icon {
  color: #1677ff;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 12px;
}

.status-enabled {
  color: #059669;
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.status-disabled {
  color: #6b7280;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

.pay-type-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #e6a23c;
  margin-top: 8px;
  padding: 6px 10px;
  background: #fdf6ec;
  border-radius: 4px;
}

.template-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.template-section {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
}

.section-title {
  font-weight: 500;
  margin-bottom: 12px;
  color: #303133;
}

.template-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.template-content code {
  font-family: monospace;
  color: #1677ff;
  word-break: break-all;
}

.template-code {
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}

.template-code pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 13px;
  line-height: 1.6;
  width: 100%;
}

.secret-code {
  color: #e6a23c !important;
  font-weight: 500;
  letter-spacing: 1px;
}

.secret-actions {
  display: flex;
  gap: 8px;
}

.secret-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
  color: #e6a23c;
}
</style>
