<template>
  <div class="dashboard-page">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>欢迎回来，管理员</h1>
        <p>Fast 易支付管理后台 · {{ currentDate }}</p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" :loading="loading" @click="loadData">
          <el-icon v-if="!loading"><Refresh /></el-icon>
          {{ loading ? '刷新中...' : '刷新数据' }}
        </el-button>
      </div>
    </div>

    <!-- 今日数据概览 -->
    <div class="section-header">
      <h2>今日数据</h2>
      <span class="section-desc">实时更新</span>
    </div>
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card gradient-blue">
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.todayOrderCount || 0 }}</div>
            <div class="stat-label">今日订单</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-trend" v-if="dashboard.orderTrend">
            <el-icon v-if="dashboard.orderTrend > 0"><Top /></el-icon>
            <el-icon v-else><Bottom /></el-icon>
            <span>较昨日 {{ Math.abs(dashboard.orderTrend || 0) }}%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card gradient-green">
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.todaySuccessCount || 0 }}</div>
            <div class="stat-label">成功订单</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><CircleCheck /></el-icon>
          </div>
          <div class="stat-extra">
            <span>成功率 {{ todaySuccessRate }}%</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card gradient-orange">
          <div class="stat-content">
            <div class="stat-value">¥{{ formatAmount(dashboard.todayAmount) }}</div>
            <div class="stat-label">今日交易额</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><Coin /></el-icon>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card gradient-purple clickable" @click="$router.push('/merchant')">
          <div class="stat-content">
            <div class="stat-value">{{ dashboard.merchantCount || 0 }}</div>
            <div class="stat-label">活跃商户</div>
          </div>
          <div class="stat-icon">
            <el-icon :size="48"><UserFilled /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 累计数据 -->
    <div class="section-header">
      <h2>累计数据</h2>
    </div>
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card-simple">
          <div class="stat-icon-small blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.totalOrderCount || 0 }}</div>
            <div class="stat-label">总订单数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card-simple">
          <div class="stat-icon-small green">
            <el-icon><Select /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.totalSuccessCount || 0 }}</div>
            <div class="stat-label">成功订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card-simple">
          <div class="stat-icon-small orange">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatAmount(dashboard.totalAmount) }}</div>
            <div class="stat-label">总交易额</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card-simple">
          <div class="stat-icon-small purple">
            <el-icon><Grid /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.shopCount || 0 }} / {{ dashboard.qrcodeCount || 0 }}</div>
            <div class="stat-label">店铺 / 二维码</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表和快捷操作区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="16">
        <div class="page-card chart-card">
          <div class="card-header">
            <span class="card-title">最近7天订单趋势</span>
          </div>
          <div class="card-body">
            <div class="chart-container" ref="chartRef"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="page-card">
          <div class="card-header">
            <span class="card-title">支付方式占比</span>
          </div>
          <div class="card-body">
            <div class="pay-type-stats">
              <div class="pay-type-item">
                <div class="pay-type-info">
                  <div class="pay-type-icon wechat">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <div class="pay-type-detail">
                    <span class="pay-type-name">微信支付</span>
                    <span class="pay-type-count">{{ dashboard.wxpayCount || 0 }} 笔</span>
                  </div>
                </div>
                <div class="pay-type-amount">¥{{ formatAmount(dashboard.wxpayAmount) }}</div>
              </div>
              <div class="pay-type-item">
                <div class="pay-type-info">
                  <div class="pay-type-icon alipay">
                    <el-icon><Wallet /></el-icon>
                  </div>
                  <div class="pay-type-detail">
                    <span class="pay-type-name">支付宝</span>
                    <span class="pay-type-count">{{ dashboard.alipayCount || 0 }} 笔</span>
                  </div>
                </div>
                <div class="pay-type-amount">¥{{ formatAmount(dashboard.alipayAmount) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="page-card" style="margin-top: 16px;">
          <div class="card-header">
            <span class="card-title">快捷操作</span>
          </div>
          <div class="card-body">
            <div class="quick-actions">
              <div class="quick-action" @click="$router.push('/merchant')">
                <el-icon :size="24"><User /></el-icon>
                <span>商户管理</span>
              </div>
              <div class="quick-action" @click="$router.push('/shop')">
                <el-icon :size="24"><Shop /></el-icon>
                <span>店铺管理</span>
              </div>
              <div class="quick-action" @click="$router.push('/order')">
                <el-icon :size="24"><List /></el-icon>
                <span>订单管理</span>
              </div>
              <div class="quick-action" @click="$router.push('/channel')">
                <el-icon :size="24"><Connection /></el-icon>
                <span>通道管理</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 最近7天订单统计卡片 -->
    <div class="section-header stats-header">
      <h2>最近7天订单统计</h2>
    </div>
    <div v-if="recentStatsList.length > 0" class="recent-stats-grid">
      <div 
        v-for="(item, index) in recentStatsList" 
        :key="item.date || index" 
        class="recent-stat-card"
        :class="{ 'today': index === recentStatsList.length - 1 }"
      >
        <div class="stat-date">
          <span class="date-text">{{ formatDate(item.date) }}</span>
          <span v-if="index === recentStatsList.length - 1" class="today-badge">今天</span>
        </div>
        <div class="stat-main">
          <div class="stat-orders">
            <span class="stat-number">{{ item.order_count || item.orderCount || 0 }}</span>
            <span class="stat-label">订单</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-success">
            <span class="stat-number success">{{ item.success_count || item.successCount || 0 }}</span>
            <span class="stat-label">成功</span>
          </div>
        </div>
        <div class="stat-bottom">
          <div class="stat-rate">
            <div class="rate-bar">
              <div class="rate-fill" :style="{ width: getSuccessRate(item) + '%', background: getProgressColor(item) }"></div>
            </div>
            <span class="rate-text">{{ getSuccessRate(item) }}%</span>
          </div>
          <div class="stat-amount">¥{{ formatAmount(item.total_amount || item.totalAmount) }}</div>
        </div>
      </div>
    </div>
    <div v-else class="empty-stats">
      <el-empty description="暂无统计数据" :image-size="80" />
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 控制台页面
 */
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getDashboard } from '@/api'
import * as echarts from 'echarts'

// 统计数据
const dashboard = ref({})
const chartRef = ref(null)
const loading = ref(false)
let chartInstance = null

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

// 今日成功率
const todaySuccessRate = computed(() => {
  const total = dashboard.value.todayOrderCount || 0
  const success = dashboard.value.todaySuccessCount || 0
  if (total === 0) return 0
  return ((success / total) * 100).toFixed(1)
})

// 最近统计列表（处理数据格式）
const recentStatsList = computed(() => {
  const stats = dashboard.value.recentStats || []
  // 反转数组使最新的在最后
  return [...stats].reverse()
})

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 格式化日期显示
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const parts = dateStr.split('-')
  if (parts.length === 3) {
    return `${parts[1]}/${parts[2]}`
  }
  return dateStr
}

// 计算成功率
const getSuccessRate = (row) => {
  const orderCount = row.order_count || row.orderCount || 0
  const successCount = row.success_count || row.successCount || 0
  if (orderCount === 0) return 0
  return ((successCount / orderCount) * 100).toFixed(1)
}

// 进度条颜色
const getProgressColor = (row) => {
  const rate = Number(getSuccessRate(row))
  if (rate >= 90) return '#52c41a'
  if (rate >= 70) return '#faad14'
  return '#ff4d4f'
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return
  
  const stats = dashboard.value.recentStats || []
  const dates = stats.map(s => s.date).reverse()
  const orders = stats.map(s => s.order_count || 0).reverse()
  const success = stats.map(s => s.success_count || 0).reverse()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['订单数', '成功数'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#666' }
    },
    series: [
      {
        name: '订单数',
        type: 'bar',
        barWidth: '35%',
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#1677ff' },
            { offset: 1, color: '#69b1ff' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: orders
      },
      {
        name: '成功数',
        type: 'bar',
        barWidth: '35%',
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#52c41a' },
            { offset: 1, color: '#95de64' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: success
      }
    ]
  }
  
  chartInstance.setOption(option)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    dashboard.value = res.data || {}
    updateChart()
  } catch (error) {
    console.error('加载控制台数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  loadData()
  setTimeout(initChart, 100)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

/* 欢迎区域 */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #1677ff 0%, #69b1ff 100%);
  border-radius: 8px;
  margin-bottom: 24px;
  color: #fff;

  h1 {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  p {
    opacity: 0.85;
    font-size: 14px;
  }
}

/* 区块标题 */
.section-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;

  h2 {
    font-size: 16px;
    font-weight: 600;
    color: #1a1a1a;
  }

  .section-desc {
    font-size: 12px;
    color: #999;
  }
}

/* 统计卡片行 */
.stat-row {
  margin-bottom: 24px;
}

/* 渐变统计卡片 */
.stat-card {
  position: relative;
  padding: 20px;
  border-radius: 12px;
  color: #fff;
  overflow: hidden;
  min-height: 120px;

  .stat-content {
    position: relative;
    z-index: 1;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  .stat-label {
    font-size: 14px;
    opacity: 0.9;
  }

  .stat-icon {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    opacity: 0.3;
  }

  .stat-trend, .stat-extra {
    margin-top: 12px;
    font-size: 12px;
    opacity: 0.85;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.gradient-blue {
  background: linear-gradient(135deg, #1677ff 0%, #4096ff 100%);
}

.gradient-green {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.gradient-orange {
  background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%);
}

.gradient-purple {
  background: linear-gradient(135deg, #722ed1 0%, #9254de 100%);
}

.stat-card.clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
}

/* 简洁统计卡片 */
.stat-card-simple {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;

  .stat-icon-small {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;

    &.blue {
      background: #e6f4ff;
      color: #1677ff;
    }

    &.green {
      background: #f6ffed;
      color: #52c41a;
    }

    &.orange {
      background: #fff7e6;
      color: #fa8c16;
    }

    &.purple {
      background: #f9f0ff;
      color: #722ed1;
    }
  }

  .stat-info {
    .stat-value {
      font-size: 22px;
      font-weight: 600;
      color: #1a1a1a;
    }

    .stat-label {
      font-size: 13px;
      color: #999;
      margin-top: 2px;
    }
  }
}

/* 图表区域 */
.chart-row {
  margin-bottom: 0;
}

.chart-card {
  height: 100%;
}

.chart-container {
  height: 280px;
}

/* 统计标题 */
.stats-header {
  margin-top: 20px;
  margin-bottom: 16px;
}

/* 支付方式统计 */
.pay-type-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pay-type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.pay-type-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pay-type-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;

  &.wechat {
    background: #07c160;
  }

  &.alipay {
    background: #1677ff;
  }
}

.pay-type-detail {
  display: flex;
  flex-direction: column;

  .pay-type-name {
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }

  .pay-type-count {
    font-size: 12px;
    color: #999;
  }
}

.pay-type-amount {
  font-size: 16px;
  font-weight: 600;
  color: #fa8c16;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;

  &:hover {
    background: #e6f4ff;
    color: #1677ff;
  }

  span {
    font-size: 13px;
  }
}

/* 最近7天统计卡片网格 */
.recent-stats-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 12px;
}

.recent-stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }

  &.today {
    background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
    border-color: #93c5fd;
  }
}

.stat-date {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  .date-text {
    font-size: 14px;
    font-weight: 600;
    color: #333;
  }

  .today-badge {
    font-size: 10px;
    padding: 2px 6px;
    background: #3b82f6;
    color: #fff;
    border-radius: 4px;
  }
}

.stat-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.stat-orders, .stat-success {
  text-align: center;

  .stat-number {
    display: block;
    font-size: 20px;
    font-weight: 700;
    color: #3b82f6;

    &.success {
      color: #10b981;
    }
  }

  .stat-label {
    font-size: 11px;
    color: #999;
  }
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: #e5e7eb;
}

.stat-bottom {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-rate {
  display: flex;
  align-items: center;
  gap: 8px;

  .rate-bar {
    flex: 1;
    height: 4px;
    background: #f0f0f0;
    border-radius: 2px;
    overflow: hidden;
  }

  .rate-fill {
    height: 100%;
    border-radius: 2px;
    transition: width 0.3s;
  }

  .rate-text {
    font-size: 11px;
    color: #666;
    min-width: 36px;
    text-align: right;
  }
}

.stat-amount {
  font-size: 13px;
  font-weight: 600;
  color: #f59e0b;
  text-align: center;
}

/* 空状态 */
.empty-stats {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  border: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 1400px) {
  .recent-stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1000px) {
  .recent-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
