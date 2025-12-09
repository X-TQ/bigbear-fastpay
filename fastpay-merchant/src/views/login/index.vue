<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-left">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 40 40" width="64" height="64" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="login-logo-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#93c5fd"/>
                <stop offset="50%" style="stop-color:#60a5fa"/>
                <stop offset="100%" style="stop-color:#3b82f6"/>
              </linearGradient>
            </defs>
            <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#login-logo-grad)"/>
            <circle cx="20" cy="20" r="14" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            <circle cx="20" cy="20" r="11" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="0.5"/>
            <circle cx="8" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="32" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="8" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="32" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <text x="20" y="26" text-anchor="middle" font-family="STKaiti, KaiTi, SimSun, serif" font-size="20" font-weight="bold" fill="white">易</text>
          </svg>
        </div>
        <h1 class="brand-title">FAST 易支付</h1>
        <p class="brand-desc">安全、快捷的支付解决方案</p>
      </div>
      
      <div class="login-features">
        <div class="feature-item">
          <span class="feature-icon">✓</span>
          <span>支持微信、支付宝多种支付方式</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">✓</span>
          <span>实时订单通知，资金秒到账</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">✓</span>
          <span>完善的开发文档和技术支持</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">✓</span>
          <span>安全稳定，7x24小时服务</span>
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2 class="login-title">商户登录</h2>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
        
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="formData.username"
              size="large"
              placeholder="请输入登录账号"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              size="large"
              placeholder="请输入登录密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <p>还没有账号？请联系管理员开通</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 商户登录页面
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api'

const router = useRouter()
const formRef = ref()

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

const loading = ref(false)

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const res = await login(formData)
      localStorage.setItem('merchant_token', res.data.token)
      localStorage.setItem('merchant_info', JSON.stringify(res.data))
      ElMessage.success('登录成功')
      router.push('/console/dashboard')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>
