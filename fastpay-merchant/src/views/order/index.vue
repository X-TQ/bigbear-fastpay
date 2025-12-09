<template>
  <div class="order-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">订单管理</h1>
      <p class="page-desc">查看和管理您的支付订单</p>
    </div>

    <!-- 筛选条件 -->
    <div class="dev-card filter-card">
      <div class="card-body">
        <el-form :inline="true" :model="queryParams" class="filter-form">
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="平台订单号">
                <el-input v-model="queryParams.orderNo" placeholder="请输入平台订单号" clearable @keyup.enter="loadData" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="商户订单号">
                <el-input v-model="queryParams.outTradeNo" placeholder="请输入商户订单号" clearable @keyup.enter="loadData" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="商品名称">
                <el-input v-model="queryParams.subject" placeholder="请输入商品名称" clearable @keyup.enter="loadData" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="订单状态">
                <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 100%">
                  <el-option label="待支付" :value="0" />
                  <el-option label="已支付" :value="1" />
                  <el-option label="已过期" :value="2" />
                  <el-option label="已关闭" :value="3" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="支付类型">
                <el-select v-model="queryParams.payType" placeholder="全部类型" clearable style="width: 100%">
                  <el-option label="微信支付" value="wxpay" />
                  <el-option label="支付宝" value="alipay" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item class="filter-buttons">
                <el-button type="primary" @click="loadData">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="resetQuery">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="dev-card">
      <div class="card-header">
        <span class="card-title">订单列表</span>
        <span class="card-extra">共 {{ total }} 条记录</span>
      </div>
      <div class="card-body" style="padding-top: 0;">
        <el-table :data="orderList" v-loading="loading" stripe>
          <el-table-column prop="orderNo" label="平台订单号" width="180">
            <template #default="{ row }">
              <span class="order-no">{{ row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="outTradeNo" label="商户订单号" width="140" show-overflow-tooltip />
          <el-table-column prop="shopName" label="店铺" width="100" show-overflow-tooltip />
          <el-table-column prop="subject" label="商品名称" min-width="120" show-overflow-tooltip />
          <el-table-column label="金额" width="90" align="right">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="支付类型" width="85" align="center">
            <template #default="{ row }">
              <div class="pay-type-cell">
                <el-icon v-if="row.payType === 'wxpay'" class="pay-icon wxpay"><ChatDotRound /></el-icon>
                <el-icon v-else class="pay-icon alipay"><Wallet /></el-icon>
                <span>{{ row.payType === 'wxpay' ? '微信' : '支付宝' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="75" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small" effect="light">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="回调状态" width="100" align="center">
            <template #default="{ row }">
              <template v-if="row.status === 1">
                <el-tag :type="getNotifyStatusType(row.notifyStatus)" size="small">
                  {{ getNotifyStatusText(row.notifyStatus) }}
                </el-tag>
                <span v-if="row.notifyCount > 0" class="notify-count">({{ row.notifyCount }})</span>
              </template>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="跳转地址" width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.returnUrl" class="return-url">{{ row.returnUrl }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
              <el-button v-if="row.status === 0" type="success" link size="small" @click="handleConfirm(row)">确认</el-button>
              <el-button v-if="row.status === 0" type="danger" link size="small" @click="handleClose(row)">关闭</el-button>
              <el-button v-if="row.status === 1 && row.notifyStatus !== 1" type="warning" link size="small" @click="handleResendNotify(row)">重发</el-button>
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

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="showDetail" title="订单详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="平台订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商户订单号">{{ currentOrder.outTradeNo }}</el-descriptions-item>
        <el-descriptions-item label="所属店铺">{{ currentOrder.shopName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder.subject }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="amount-text">¥{{ currentOrder.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="amount-text">¥{{ currentOrder.payAmount || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付类型">
          <el-tag :type="currentOrder.payType === 'wxpay' ? 'success' : 'primary'" size="small">
            {{ currentOrder.payType === 'wxpay' ? '微信支付' : '支付宝' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="回调状态">
          <template v-if="currentOrder.status === 1">
            <el-tag :type="getNotifyStatusType(currentOrder.notifyStatus)" size="small">
              {{ getNotifyStatusText(currentOrder.notifyStatus) }}
            </el-tag>
            <span v-if="currentOrder.notifyCount > 0" class="notify-count">(已通知{{ currentOrder.notifyCount }}次)</span>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="回调地址" :span="2">{{ currentOrder.notifyUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="跳转地址" :span="2">{{ currentOrder.returnUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ formatTime(currentOrder.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="最后通知时间">{{ formatTime(currentOrder.lastNotifyTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户端IP">{{ currentOrder.clientIp || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 订单管理
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, confirmOrder, closeOrder, resendNotify } from '@/api'

const queryParams = reactive({
  current: 1,
  size: 10,
  orderNo: '',
  outTradeNo: '',
  subject: '',
  status: undefined,
  payType: undefined
})

const orderList = ref([])
const loading = ref(false)
const total = ref(0)

const showDetail = ref(false)
const currentOrder = ref({})

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已过期', 3: '已关闭' }
  return texts[status] || '未知'
}

// 回调状态类型
const getNotifyStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

// 回调状态文本
const getNotifyStatusText = (status) => {
  const texts = { 0: '未通知', 1: '成功', 2: '失败' }
  return texts[status] || '未通知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string' && time.includes('-')) {
    return time.replace('T', ' ').substring(0, 19)
  }
  return time
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderPage(queryParams)
    orderList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
  }
  loading.value = false
}

const resetQuery = () => {
  queryParams.orderNo = ''
  queryParams.outTradeNo = ''
  queryParams.subject = ''
  queryParams.status = undefined
  queryParams.payType = undefined
  queryParams.current = 1
  loadData()
}

const handleView = (order) => {
  currentOrder.value = { ...order }
  showDetail.value = true
}

const handleConfirm = (order) => {
  ElMessageBox.confirm(
    '确认后将触发回调通知',
    '确定该订单已收到付款吗？',
    { type: 'warning' }
  ).then(async () => {
    await confirmOrder(order.orderNo)
    ElMessage.success('确认成功')
    loadData()
  }).catch(() => {})
}

const handleClose = (order) => {
  ElMessageBox.confirm(
    '关闭后订单将无法支付',
    '确定要关闭该订单吗？',
    { type: 'warning' }
  ).then(async () => {
    await closeOrder(order.orderNo)
    ElMessage.success('关闭成功')
    loadData()
  }).catch(() => {})
}

// 重发通知
const handleResendNotify = async (order) => {
  try {
    await resendNotify(order.orderNo)
    ElMessage.success('通知已发送')
    loadData()
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.order-page {
  padding: 0;
}

/* 筛选卡片 */
.filter-card {
  .card-body {
    padding-bottom: 8px;
  }
}

.filter-form {
  :deep(.el-form-item) {
    margin-bottom: 12px;
    width: 100%;
    
    .el-form-item__label {
      font-size: 13px;
      color: #606266;
    }
    
    .el-input, .el-select {
      width: 100%;
    }
  }
  
  .filter-buttons {
    padding-top: 22px;
  }
}

/* 订单号样式 */
.order-no {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 12px;
  color: #409eff;
}

/* 支付类型单元格 */
.pay-type-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  
  .pay-icon {
    font-size: 14px;
    
    &.wxpay {
      color: #07c160;
    }
    
    &.alipay {
      color: #1677ff;
    }
  }
}

/* 卡片额外信息 */
.card-extra {
  font-size: 13px;
  color: #909399;
}

/* 回调次数 */
.notify-count {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

/* 文本灰色 */
.text-muted {
  color: #909399;
}

/* 跳转地址 */
.return-url {
  font-size: 12px;
  color: #606266;
}
</style>
