<template>
  <div class="dashboard-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">控制台</h1>
      <p class="page-desc">欢迎回来，{{ merchantInfo.nickname || merchantInfo.merchantName || '商户' }}</p>
    </div>

    <!-- 统计数据 -->
    <div class="stat-grid">
      <div class="stat-item">
        <div class="stat-label">今日订单</div>
        <div class="stat-value primary">{{ dashboard.todayOrderCount || 0 }}</div>
        <div class="stat-extra">较昨日 --</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">今日成功</div>
        <div class="stat-value success">{{ dashboard.todaySuccessCount || 0 }}</div>
        <div class="stat-extra">成功率 {{ successRate }}%</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">今日交易额</div>
        <div class="stat-value warning">¥{{ formatAmount(dashboard.todayAmount) }}</div>
        <div class="stat-extra">实时更新</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">店铺数量</div>
        <div class="stat-value">{{ dashboard.shopCount || 0 }}</div>
        <div class="stat-extra">二维码 {{ dashboard.qrcodeCount || 0 }} 个</div>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- 累计数据 -->
      <el-col :span="12">
        <div class="dev-card">
          <div class="card-header">
            <span class="card-title">
              <el-icon class="card-icon"><DataLine /></el-icon>
              累计数据
            </span>
          </div>
          <div class="card-body">
            <div class="info-list">
              <div class="info-item">
                <span class="info-label">总订单数</span>
                <span class="info-value">{{ dashboard.totalOrderCount || 0 }} 笔</span>
              </div>
              <div class="info-item">
                <span class="info-label">成功订单</span>
                <span class="info-value" style="color: #52c41a;">{{ dashboard.totalSuccessCount || 0 }} 笔</span>
              </div>
              <div class="info-item">
                <span class="info-label">总交易额</span>
                <span class="info-value amount-text">¥{{ formatAmount(dashboard.totalAmount) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">二维码数量</span>
                <span class="info-value">{{ dashboard.qrcodeCount || 0 }} 个</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 快捷操作 -->
      <el-col :span="12">
        <div class="dev-card">
          <div class="card-header">
            <span class="card-title">
              <el-icon class="card-icon"><Grid /></el-icon>
              快捷操作
            </span>
          </div>
          <div class="card-body">
            <div class="quick-actions">
              <div class="action-item" @click="$router.push('/console/shop')">
                <div class="action-icon" style="background: #e6f7ff; color: #1677ff;">
                  <el-icon><Shop /></el-icon>
                </div>
                <span class="action-text">店铺管理</span>
              </div>
              <div class="action-item" @click="$router.push('/console/qrcode')">
                <div class="action-icon" style="background: #f6ffed; color: #52c41a;">
                  <el-icon><Grid /></el-icon>
                </div>
                <span class="action-text">二维码</span>
              </div>
              <div class="action-item" @click="$router.push('/console/order')">
                <div class="action-icon" style="background: #fff7e6; color: #faad14;">
                  <el-icon><List /></el-icon>
                </div>
                <span class="action-text">订单管理</span>
              </div>
              <div class="action-item" @click="$router.push('/console/config')">
                <div class="action-icon" style="background: #f9f0ff; color: #722ed1;">
                  <el-icon><Setting /></el-icon>
                </div>
                <span class="action-text">开发配置</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- API 密钥信息 -->
    <div style="margin-top: 16px;">
    <div class="dev-card">
      <div class="card-header">
        <span class="card-title">
          <el-icon class="card-icon"><Key /></el-icon>
          API 密钥
        </span>
        <el-button type="primary" link @click="$router.push('/console/config')">
          查看完整配置
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="card-body">
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">商户编号</span>
            <span class="info-value copyable">
              {{ merchantInfo.merchantNo }}
              <el-icon class="copy-btn" @click="copyText(merchantInfo.merchantNo)"><CopyDocument /></el-icon>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">API Key</span>
            <span class="info-value copyable">
              {{ maskKey(merchantInfo.apiKey) }}
              <el-icon class="copy-btn" @click="copyText(merchantInfo.apiKey)"><CopyDocument /></el-icon>
            </span>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 商户控制台
 */
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboard } from '@/api'

const dashboard = ref({})

const merchantInfo = computed(() => {
  const info = localStorage.getItem('merchant_info')
  return info ? JSON.parse(info) : {}
})

// 成功率
const successRate = computed(() => {
  const total = dashboard.value.todayOrderCount || 0
  const success = dashboard.value.todaySuccessCount || 0
  if (total === 0) return 0
  return ((success / total) * 100).toFixed(1)
})

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toFixed(2)
}

// 遮蔽密钥
const maskKey = (key) => {
  if (!key) return '********'
  if (key.length <= 8) return '****' + key.substring(key.length - 4)
  return key.substring(0, 4) + '****' + key.substring(key.length - 4)
}

// 复制文本
const copyText = (text) => {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const loadData = async () => {
  try {
    const res = await getDashboard()
    dashboard.value = res.data || {}
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

/* 让两个卡片等高 */
.el-row {
  display: flex;
  
  .el-col {
    display: flex;
    
    .dev-card {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .card-body {
        flex: 1;
        display: flex;
        align-items: center;
      }
    }
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  width: 100%;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  background: #fafafa;

  &:hover {
    background: #f0f0f0;
  }

  .action-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    margin-bottom: 8px;
  }

  .action-text {
    font-size: 13px;
    color: #666;
  }
}
</style>
