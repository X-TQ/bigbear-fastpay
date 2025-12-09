<template>
  <div class="config-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">开发配置</h1>
      <p class="page-desc">管理您的 API 密钥和回调配置</p>
    </div>

    <el-row :gutter="16">
      <!-- 商户信息 -->
      <el-col :span="12">
        <div class="dev-card">
          <div class="card-header">
            <span class="card-title">
              <el-icon class="card-icon"><User /></el-icon>
              商户信息
            </span>
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
                <span class="info-label">商户名称</span>
                <span class="info-value">{{ merchantInfo.merchantName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">联系人</span>
                <span class="info-value">{{ merchantInfo.contactName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">联系电话</span>
                <span class="info-value">{{ merchantInfo.contactPhone || '-' }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- API 密钥 -->
      <el-col :span="12">
        <div class="dev-card">
          <div class="card-header">
            <span class="card-title">
              <el-icon class="card-icon"><Key /></el-icon>
              API 密钥
            </span>
            <el-button type="danger" link @click="handleResetKey">
              <el-icon><Refresh /></el-icon>
              重置密钥
            </el-button>
          </div>
          <div class="card-body">
            <div class="key-item">
              <div class="key-label">API Key</div>
              <div class="api-key-display">
                <span class="key-text">{{ merchantInfo.apiKey }}</span>
                <div class="key-actions">
                  <el-button type="primary" link size="small" @click="copyText(merchantInfo.apiKey)">
                    <el-icon><CopyDocument /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <div class="key-item" style="margin-top: 16px;">
              <div class="key-label">
                API Secret
                <el-button type="primary" link size="small" @click="showSecret = !showSecret">
                  {{ showSecret ? '隐藏' : '显示' }}
                </el-button>
              </div>
              <div class="api-key-display">
                <span class="key-text">{{ showSecret ? merchantInfo.apiSecret : '********************************' }}</span>
                <div class="key-actions">
                  <el-button type="primary" link size="small" @click="copyText(merchantInfo.apiSecret)">
                    <el-icon><CopyDocument /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <el-alert type="warning" :closable="false" style="margin-top: 16px;">
              <template #title>
                请妥善保管您的 API Secret，不要泄露给他人
              </template>
            </el-alert>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 回调配置 -->
    <div class="dev-card">
      <div class="card-header">
        <span class="card-title">
          <el-icon class="card-icon"><Link /></el-icon>
          回调配置
        </span>
      </div>
      <div class="card-body">
        <el-form :model="configForm" label-width="120px" style="max-width: 600px;">
          <el-form-item label="回调通知地址">
            <el-input v-model="configForm.notifyUrl" placeholder="支付成功后的回调通知地址（POST 请求）" />
            <div class="form-tip">支付成功后，系统会向此地址发送 POST 请求通知</div>
          </el-form-item>
          <el-form-item label="支付跳转地址">
            <el-input v-model="configForm.returnUrl" placeholder="支付成功后的页面跳转地址" />
            <div class="form-tip">用户支付成功后，页面会自动跳转到此地址</div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSaveConfig">保存配置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 签名说明 -->
    <div class="dev-card">
      <div class="card-header">
        <span class="card-title">
          <el-icon class="card-icon"><Document /></el-icon>
          签名说明
        </span>
        <el-button type="primary" link @click="$router.push('/console/docs')">
          查看完整文档
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="card-body">
        <div class="sign-steps">
          <div class="sign-step">
            <div class="step-num">1</div>
            <div class="step-content">
              <h4>参数排序</h4>
              <p>将所有非空参数（不含sign）按参数名ASCII码从小到大排序</p>
            </div>
          </div>
          <div class="sign-step">
            <div class="step-num">2</div>
            <div class="step-content">
              <h4>拼接字符串</h4>
              <p>使用 key=value&key=value 格式拼接</p>
            </div>
          </div>
          <div class="sign-step">
            <div class="step-num">3</div>
            <div class="step-content">
              <h4>追加密钥</h4>
              <p>在字符串末尾追加 &key=API_SECRET</p>
            </div>
          </div>
          <div class="sign-step">
            <div class="step-num">4</div>
            <div class="step-content">
              <h4>MD5 加密</h4>
              <p>对最终字符串进行 MD5 加密并转大写</p>
            </div>
          </div>
        </div>

        <div class="code-example">
          <div class="code-header">
            <span>签名示例（API接口支付）</span>
            <el-button type="primary" link size="small" @click="copyCode">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
          <pre class="code-block">// 请求参数（参数名使用驼峰命名）
params = {
  merchantNo: "{{ merchantInfo.merchantNo || 'M12345678' }}",
  outTradeNo: "ORDER202512050001",
  amount: "10.00",
  subject: "测试商品",
  payType: "wxpay",
  timestamp: "1733400000"
}

// 1. 按参数名ASCII码排序并拼接（注意：驼峰命名时大写字母排在小写字母前面）
str = "amount=10.00&merchantNo={{ merchantInfo.merchantNo || 'M12345678' }}&outTradeNo=ORDER202512050001&payType=wxpay&subject=测试商品&timestamp=1733400000"

// 2. 追加密钥
str = str + "&key=YOUR_API_SECRET"

// 3. MD5 加密并转大写
sign = MD5(str).toUpperCase()

// 最终请求时带上 sign 参数</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 开发配置
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantInfo, updateCallbackConfig, resetApiKey } from '@/api'

const merchantInfo = ref({})
const showSecret = ref(false)
const saving = ref(false)

const configForm = reactive({
  notifyUrl: '',
  returnUrl: ''
})

const loadMerchantInfo = async () => {
  try {
    const res = await getMerchantInfo()
    merchantInfo.value = res.data || {}
    configForm.notifyUrl = merchantInfo.value.notifyUrl || ''
    configForm.returnUrl = merchantInfo.value.returnUrl || ''
    localStorage.setItem('merchant_info', JSON.stringify(res.data))
  } catch (error) {
    console.error('加载商户信息失败:', error)
  }
}

const copyText = (text) => {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const copyCode = () => {
  const code = document.querySelector('.code-block').textContent
  navigator.clipboard.writeText(code).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const handleResetKey = () => {
  ElMessageBox.confirm(
    '重置后原密钥将立即失效，请确保您已做好准备',
    '确定要重置 API 密钥吗？',
    { type: 'warning' }
  ).then(async () => {
    const res = await resetApiKey()
    merchantInfo.value = res.data
    localStorage.setItem('merchant_info', JSON.stringify(res.data))
    ElMessage.success('重置成功，请及时更新您的系统配置')
  }).catch(() => {})
}

const handleSaveConfig = async () => {
  saving.value = true
  try {
    await updateCallbackConfig(configForm)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  }
  saving.value = false
}

onMounted(() => {
  loadMerchantInfo()
})
</script>

<style scoped>
.config-page {
  padding: 0;
}

.key-item {
  .key-label {
    font-size: 13px;
    color: #666;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.sign-steps {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.sign-step {
  flex: 1;
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;

  .step-num {
    width: 28px;
    height: 28px;
    background: #1677ff;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    flex-shrink: 0;
  }

  .step-content {
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
    }

    p {
      font-size: 12px;
      color: #666;
    }
  }
}

.code-example {
  background: #1e1e1e;
  border-radius: 8px;
  overflow: hidden;

  .code-header {
    padding: 12px 16px;
    background: #2d2d2d;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #ccc;
    font-size: 13px;
  }

  .code-block {
    padding: 16px;
    margin: 0;
    color: #d4d4d4;
    font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
    font-size: 13px;
    line-height: 1.6;
    overflow-x: auto;
  }
}
</style>
