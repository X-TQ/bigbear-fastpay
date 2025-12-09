<template>
  <div class="order-page">
    <!-- 搜索区域 -->
    <div class="page-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-select v-model="queryParams.merchantId" placeholder="所属商户" clearable style="width: 180px">
              <el-option v-for="m in merchants" :key="m.id" :label="m.merchantName" :value="m.id" />
            </el-select>
            <el-input
              v-model="queryParams.orderNo"
              placeholder="平台/商户订单号"
              clearable
              style="width: 180px"
              @keyup.enter="loadData"
            />
            <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 120px">
              <el-option label="待支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已过期" :value="2" />
              <el-option label="已关闭" :value="3" />
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
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-card" style="margin-top: 16px">
      <div class="card-body">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="orderNo" label="平台订单号" width="190" />
          <el-table-column prop="outTradeNo" label="商户订单号" width="150" />
          <el-table-column prop="merchantName" label="商户" width="100" />
          <el-table-column prop="shopName" label="店铺" width="100" show-overflow-tooltip />
          <el-table-column label="金额" width="90">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="支付类型" width="80">
            <template #default="{ row }">
              <el-tag :type="row.payType === 'wxpay' ? 'success' : 'primary'" size="small">
                {{ row.payType === 'wxpay' ? '微信' : '支付宝' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="75">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="回调状态" width="90">
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
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="支付时间" width="160">
            <template #default="{ row }">
              <span v-if="row.payTime">{{ formatTime(row.payTime) }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
              <el-button v-if="row.status === 0" type="success" link size="small" @click="handleConfirm(row)">确认支付</el-button>
              <el-button v-if="row.status === 0" type="danger" link size="small" @click="handleClose(row)">关闭</el-button>
              <el-button v-if="row.status === 1 && row.notifyStatus !== 1" type="warning" link size="small" @click="handleResendNotify(row)">重发通知</el-button>
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
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="平台订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商户订单号">{{ currentOrder.outTradeNo }}</el-descriptions-item>
        <el-descriptions-item label="所属商户">{{ currentOrder.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="所属店铺">{{ currentOrder.shopName || '-' }}</el-descriptions-item>
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
        <el-descriptions-item label="支付方式">
          {{ currentOrder.payMethod === 'page' ? '页面跳转' : 'API接口' }}
        </el-descriptions-item>
        <el-descriptions-item label="商品名称" :span="2">{{ currentOrder.subject }}</el-descriptions-item>
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
        <el-descriptions-item label="客户端IP">{{ currentOrder.clientIp }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 订单管理页面
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantList, getOrderPage, confirmOrder, closeOrder, resendNotify } from '@/api'

// 商户列表
const merchants = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  merchantId: undefined,
  orderNo: '',
  status: undefined
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 详情弹窗
const detailVisible = ref(false)
const currentOrder = ref({})

// 状态类型
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

// 状态文本
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
  // 如果已经是格式化的字符串，直接返回
  if (typeof time === 'string' && time.includes('-')) {
    // 处理 ISO 格式或 LocalDateTime 格式
    return time.replace('T', ' ').substring(0, 19)
  }
  return time
}

// 加载商户列表
const loadMerchants = async () => {
  try {
    const res = await getMerchantList()
    merchants.value = res.data || []
  } catch (error) {
    console.error('加载商户列表失败:', error)
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderPage(queryParams)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
  }
  loading.value = false
}

// 重置查询
const resetQuery = () => {
  queryParams.merchantId = undefined
  queryParams.orderNo = ''
  queryParams.status = undefined
  queryParams.current = 1
  loadData()
}

// 查看详情
const handleView = (record) => {
  currentOrder.value = { ...record }
  detailVisible.value = true
}

// 确认支付
const handleConfirm = (record) => {
  ElMessageBox.confirm(
    '确认后将触发回调通知',
    '确定要确认该订单已支付吗？',
    { type: 'warning' }
  ).then(async () => {
    await confirmOrder(record.orderNo)
    ElMessage.success('确认成功')
    loadData()
  }).catch(() => {})
}

// 关闭订单
const handleClose = (record) => {
  ElMessageBox.confirm(
    '关闭后订单将无法支付',
    '确定要关闭该订单吗？',
    { type: 'warning' }
  ).then(async () => {
    await closeOrder(record.orderNo)
    ElMessage.success('关闭成功')
    loadData()
  }).catch(() => {})
}

// 重发通知
const handleResendNotify = async (record) => {
  await resendNotify(record.orderNo)
  ElMessage.success('通知已发送')
  loadData()
}

onMounted(() => {
  loadMerchants()
  loadData()
})
</script>

<style scoped>
.order-page {
  padding: 0;
}

.amount-text {
  color: #e6a23c;
  font-weight: 500;
}

.text-muted {
  color: #909399;
}

.notify-count {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}
</style>
