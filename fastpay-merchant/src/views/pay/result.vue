<template>
  <div class="result-page">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <van-loading size="36px" vertical>加载中...</van-loading>
    </div>

    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <van-empty image="error" :description="error" />
      <van-button type="primary" @click="goHome">返回首页</van-button>
    </div>

    <!-- 支付结果 -->
    <div v-else class="result-container">
      <!-- 结果图标 -->
      <div class="result-icon" :class="statusClass">
        <van-icon :name="statusIcon" size="60" />
      </div>

      <!-- 结果文字 -->
      <div class="result-text" :class="statusClass">{{ statusText }}</div>

      <!-- 金额 -->
      <div class="result-amount" v-if="resultData.status === 1">
        <span class="currency">¥</span>
        <span class="amount">{{ resultData.payAmount || resultData.amount }}</span>
      </div>

      <!-- 订单信息 -->
      <div class="order-detail">
        <div class="detail-item">
          <span class="label">商品名称</span>
          <span class="value">{{ resultData.subject }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商户名称</span>
          <span class="value">{{ resultData.merchantName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">订单金额</span>
          <span class="value">¥{{ resultData.amount }}</span>
        </div>
        <div class="detail-item" v-if="resultData.status === 1">
          <span class="label">实付金额</span>
          <span class="value highlight">¥{{ resultData.payAmount || resultData.amount }}</span>
        </div>
        <div class="detail-item">
          <span class="label">订单号</span>
          <span class="value">{{ resultData.orderNo }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商户订单号</span>
          <span class="value">{{ resultData.outTradeNo }}</span>
        </div>
        <div class="detail-item">
          <span class="label">支付方式</span>
          <span class="value">
            <van-tag :type="resultData.payType === 'wxpay' ? 'success' : 'primary'" size="medium">
              {{ resultData.payType === 'wxpay' ? '微信支付' : '支付宝支付' }}
            </van-tag>
          </span>
        </div>
        <div class="detail-item" v-if="resultData.payTime">
          <span class="label">支付时间</span>
          <span class="value">{{ resultData.payTime }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button v-if="resultData.returnUrl" type="primary" block @click="handleReturn">
          返回商户
        </van-button>
        <van-button v-else type="primary" block @click="goHome">
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 支付结果页面
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPayResult } from '@/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref('')
const resultData = ref({})

// 状态相关计算属性
const statusClass = computed(() => {
  switch (resultData.value.status) {
    case 1: return 'success'
    case 2: return 'expired'
    case 3: return 'closed'
    default: return 'pending'
  }
})

const statusIcon = computed(() => {
  switch (resultData.value.status) {
    case 1: return 'checked'
    case 2: return 'clock-o'
    case 3: return 'close'
    default: return 'info-o'
  }
})

const statusText = computed(() => {
  switch (resultData.value.status) {
    case 1: return '支付成功'
    case 2: return '订单已过期'
    case 3: return '订单已关闭'
    default: return '等待支付'
  }
})

// 获取支付结果数据
const loadResultData = async () => {
  const orderNo = route.params.orderNo
  if (!orderNo) {
    error.value = '订单号不能为空'
    loading.value = false
    return
  }

  try {
    const res = await getPayResult(orderNo)
    if (res.code === 200) {
      resultData.value = res.data
    } else {
      error.value = res.message || '获取订单信息失败'
    }
  } catch (e) {
    error.value = '网络错误，请稍后重试'
  }
  loading.value = false
}

// 返回商户
const handleReturn = () => {
  if (resultData.value.returnUrl) {
    let url = resultData.value.returnUrl
    if (!url.includes('?')) {
      url += '?'
    } else {
      url += '&'
    }
    url += `order_no=${resultData.value.orderNo}&out_trade_no=${resultData.value.outTradeNo}&status=${resultData.value.status}`
    window.location.href = url
  }
}

// 返回首页
const goHome = () => {
  router.push('/')
}

onMounted(() => {
  loadResultData()
})
</script>

<style lang="less" scoped>
.result-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
}

.result-container {
  padding: 40px 20px 20px;
}

.result-icon {
  text-align: center;
  margin-bottom: 16px;

  &.success { color: #07c160; }
  &.expired { color: #fa8c16; }
  &.closed { color: #ee0a24; }
  &.pending { color: #1989fa; }
}

.result-text {
  text-align: center;
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 16px;

  &.success { color: #07c160; }
  &.expired { color: #fa8c16; }
  &.closed { color: #ee0a24; }
  &.pending { color: #1989fa; }
}

.result-amount {
  text-align: center;
  margin-bottom: 24px;

  .currency {
    font-size: 20px;
    color: #333;
  }
  .amount {
    font-size: 36px;
    font-weight: bold;
    color: #333;
  }
}

.order-detail {
  background: #fff;
  border-radius: 12px;
  padding: 16px;

  .detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: #999;
      font-size: 14px;
    }

    .value {
      color: #333;
      font-size: 14px;
      text-align: right;
      max-width: 60%;
      word-break: break-all;

      &.highlight {
        color: #ee0a24;
        font-weight: 500;
      }
    }
  }
}

.action-buttons {
  margin-top: 24px;
  padding: 0 20px;
}
</style>
