<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="home-header" :class="{ scrolled: isScrolled }">
      <div class="header-container">
        <div class="logo">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" width="36" height="36" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="logo-gradient-header" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#93c5fd"/>
                  <stop offset="50%" style="stop-color:#60a5fa"/>
                  <stop offset="100%" style="stop-color:#3b82f6"/>
                </linearGradient>
              </defs>
              <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#logo-gradient-header)"/>
              <circle cx="20" cy="20" r="14" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
              <circle cx="20" cy="20" r="11" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="0.5"/>
              <circle cx="8" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="32" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="8" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <circle cx="32" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
              <text x="20" y="26" text-anchor="middle" font-family="STKaiti, KaiTi, SimSun, serif" font-size="20" font-weight="bold" fill="white">易</text>
            </svg>
          </div>
          <span class="logo-text">FAST 易支付</span>
        </div>
        <nav class="nav-links">
          <a href="#features">产品特性</a>
          <a href="#process">接入流程</a>
          <a @click="goDocs">开发文档</a>
        </nav>
        <div class="header-actions">
          <el-button text @click="goLogin">登录</el-button>
          <el-button type="primary" @click="goLogin">开始使用</el-button>
        </div>
        <div class="mobile-menu" @click="mobileMenuOpen = !mobileMenuOpen">
          <el-icon :size="24"><Menu /></el-icon>
        </div>
      </div>
      <!-- 移动端菜单 -->
      <div class="mobile-nav" v-show="mobileMenuOpen">
        <a href="#features" @click="mobileMenuOpen = false">产品特性</a>
        <a href="#process" @click="mobileMenuOpen = false">接入流程</a>
        <a @click="goDocs">开发文档</a>
        <a @click="goLogin">登录</a>
      </div>
    </header>

    <!-- Hero 区域 - 简洁扁平化 -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">
            个人收款<span class="highlight">免签</span>接入<br/>
            支付从未如此简单
          </h1>
          <p class="hero-subtitle">
            无需企业资质，上传收款码即可开始收款。资金直达个人账户，零手续费，实时到账。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="goLogin" class="btn-main">
              免费开始
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" @click="goDocs" class="btn-secondary">
              查看文档
            </el-button>
          </div>
          <div class="hero-stats">
            <div class="hero-stat">
              <span class="stat-num">1000+</span>
              <span class="stat-text">活跃商户</span>
            </div>
            <div class="hero-stat">
              <span class="stat-num">99.9%</span>
              <span class="stat-text">系统稳定性</span>
            </div>
            <div class="hero-stat">
              <span class="stat-num">0</span>
              <span class="stat-text">手续费</span>
            </div>
          </div>
        </div>
        <div class="hero-visual">
          <div class="code-preview">
            <div class="code-header">
              <span class="code-dot red"></span>
              <span class="code-dot yellow"></span>
              <span class="code-dot green"></span>
              <span class="code-title">创建订单</span>
            </div>
            <div class="code-body">
              <pre><code><span class="code-comment">// 发起支付请求</span>
<span class="code-keyword">POST</span> /api/pay/create

{
  <span class="code-key">"merchantNo"</span>: <span class="code-string">"M12345678"</span>,
  <span class="code-key">"amount"</span>: <span class="code-number">99.00</span>,
  <span class="code-key">"subject"</span>: <span class="code-string">"商品名称"</span>,
  <span class="code-key">"payType"</span>: <span class="code-string">"wxpay"</span>
}</code></pre>
            </div>
          </div>
          <div class="float-card card-success">
            <el-icon class="card-icon"><CircleCheck /></el-icon>
            <div class="card-info">
              <span class="card-label">支付成功</span>
              <span class="card-value">¥ 99.00</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 支付方式 -->
    <section class="payment-section">
      <div class="payment-container">
        <div class="payment-item">
          <div class="payment-icon wechat">
            <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
              <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89l-.002-.002-.404-.03zm-2.81 3.09c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.847 0c.536 0 .97.44.97.982a.976.976 0 0 1-.97.983.976.976 0 0 1-.969-.983c0-.542.433-.982.97-.982z"/>
            </svg>
          </div>
          <span>微信支付</span>
        </div>
        <div class="payment-item">
          <div class="payment-icon alipay">
            <svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
              <path d="M21.422 15.358c-.937-.386-3.47-1.412-5.807-2.34.907-1.603 1.62-3.418 2.057-5.418h-4.956V6.2h6.2V5.2h-6.2V2.6h-2.4v2.6H4.2v1h6.116v1.4H5.2v1h8.156c-.36 1.456-.92 2.826-1.62 4.06-2.59-.86-5.09-1.46-6.69-1.46-2.6 0-4.2 1.4-4.2 3.4 0 2.2 1.8 3.6 4.6 3.6 2.8 0 5.4-1.6 7.6-4.6 2.8 1.2 6.4 2.8 8.2 3.8.6.4 1.2.6 1.8.6 1.4 0 2.4-1 2.4-2.4 0-.6-.2-1.2-.6-1.6-.2-.2-.6-.4-1.02-.6zM5.2 17.2c-1.6 0-2.6-.8-2.6-1.8 0-1.2 1-1.8 2.4-1.8 1.2 0 3.2.4 5.4 1.2-1.6 1.6-3.4 2.4-5.2 2.4z"/>
            </svg>
          </div>
          <span>支付宝</span>
        </div>
      </div>
    </section>

    <!-- 核心优势 -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">为什么选择我们</h2>
          <p class="section-desc">简单、安全、高效的支付解决方案</p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <h3>个人免签</h3>
            <p>无需企业资质，个人即可接入，上传收款码即可开始收款</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon green">
              <el-icon :size="28"><Wallet /></el-icon>
            </div>
            <h3>资金直达</h3>
            <p>资金直接到达您的个人账户，无需提现，无中间环节</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon orange">
              <el-icon :size="28"><Timer /></el-icon>
            </div>
            <h3>实时回调</h3>
            <p>支付成功后毫秒级回调通知，确保业务流程顺畅</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon purple">
              <el-icon :size="28"><Lock /></el-icon>
            </div>
            <h3>安全可靠</h3>
            <p>签名验证机制，保障交易安全，7×24小时稳定运行</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 接入流程 -->
    <section id="process" class="process-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">三步快速接入</h2>
          <p class="section-desc">简单几步，即可完成支付对接</p>
        </div>
        <div class="process-steps">
          <div class="process-step">
            <div class="step-number">1</div>
            <h3>注册账号</h3>
            <p>联系管理员开通商户账号<br/>获取 API 密钥</p>
          </div>
          <div class="process-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
          <div class="process-step">
            <div class="step-number">2</div>
            <h3>配置收款码</h3>
            <p>上传微信/支付宝收款码<br/>设置回调地址</p>
          </div>
          <div class="process-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
          <div class="process-step">
            <div class="step-number">3</div>
            <h3>开始收款</h3>
            <p>调用 API 创建订单<br/>用户扫码完成支付</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="cta-container">
        <h2>准备好开始了吗？</h2>
        <p>立即接入，开启您的收款之旅</p>
        <div class="cta-actions">
          <el-button type="primary" size="large" @click="goLogin">
            免费开始使用
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
          <el-button size="large" @click="goDocs">查看开发文档</el-button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="home-footer">
      <div class="footer-container">
        <div class="footer-main">
          <div class="footer-brand">
            <div class="logo">
              <div class="logo-icon">
                <svg viewBox="0 0 40 40" width="36" height="36" xmlns="http://www.w3.org/2000/svg">
                  <defs>
                    <linearGradient id="logo-gradient-footer" x1="0%" y1="0%" x2="100%" y2="100%">
                      <stop offset="0%" style="stop-color:#93c5fd"/>
                      <stop offset="50%" style="stop-color:#60a5fa"/>
                      <stop offset="100%" style="stop-color:#3b82f6"/>
                    </linearGradient>
                  </defs>
                  <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#logo-gradient-footer)"/>
                  <circle cx="20" cy="20" r="14" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
                  <circle cx="20" cy="20" r="11" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="0.5"/>
                  <circle cx="8" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
                  <circle cx="32" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
                  <circle cx="8" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
                  <circle cx="32" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
                  <text x="20" y="26" text-anchor="middle" font-family="STKaiti, KaiTi, SimSun, serif" font-size="20" font-weight="bold" fill="white">易</text>
                </svg>
              </div>
              <span class="logo-text">FAST 易支付</span>
            </div>
            <p>个人免签支付解决方案</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h5>产品</h5>
              <a href="#features">产品特性</a>
              <a href="#process">接入流程</a>
            </div>
            <div class="link-group">
              <h5>开发者</h5>
              <a @click="goDocs">开发文档</a>
              <a @click="goLogin">控制台</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>@ 2025 FAST 易支付. All rights reserved. by 大熊Bigbear</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 公共首页
 * 现代扁平化 + 内容优先风格
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowRight, 
  Document, 
  Menu, 
  CircleCheck, 
  User, 
  Wallet, 
  Timer, 
  Lock 
} from '@element-plus/icons-vue'

const router = useRouter()
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const goLogin = () => {
  router.push('/login')
}

const goDocs = () => {
  router.push('/docs')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #fff;
  overflow-x: hidden;
}

/* 顶部导航 */
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: #fff;
  z-index: 100;
  transition: all 0.3s;
  border-bottom: 1px solid transparent;
}

.home-header.scrolled {
  border-bottom-color: #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-container {
  max-width: 1140px;
  margin: 0 auto;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.nav-links {
  display: flex;
  gap: 36px;
}

.nav-links a {
  color: #666;
  text-decoration: none;
  font-size: 15px;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-links a:hover {
  color: #2563eb;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.mobile-menu {
  display: none;
  cursor: pointer;
  color: #333;
}

.mobile-nav {
  display: none;
  flex-direction: column;
  background: #fff;
  padding: 16px 24px;
  border-top: 1px solid #eee;
}

.mobile-nav a {
  padding: 12px 0;
  color: #333;
  text-decoration: none;
  border-bottom: 1px solid #f0f0f0;
}

/* Hero 区域 - 扁平化 */
.hero-section {
  padding: 140px 24px 80px;
  background: linear-gradient(180deg, #f8fafc 0%, #fff 100%);
}

.hero-container {
  max-width: 1140px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 60px;
}

.hero-content {
  flex: 1;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
  margin-bottom: 20px;
}

.hero-title .highlight {
  color: #2563eb;
}

.hero-subtitle {
  font-size: 18px;
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 32px;
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 48px;
}

.btn-main {
  background: #2563eb !important;
  border-color: #2563eb !important;
  font-weight: 500 !important;
  padding: 12px 24px !important;
  height: auto !important;
}

.btn-main:hover {
  background: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
}

.btn-secondary {
  background: #fff !important;
  border: 1px solid #e2e8f0 !important;
  color: #334155 !important;
  font-weight: 500 !important;
  padding: 12px 24px !important;
  height: auto !important;
}

.btn-secondary:hover {
  border-color: #cbd5e1 !important;
  background: #f8fafc !important;
}

.hero-stats {
  display: flex;
  gap: 40px;
}

.hero-stat {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
}

.stat-text {
  font-size: 14px;
  color: #94a3b8;
  margin-top: 4px;
}

/* Hero 视觉区 */
.hero-visual {
  position: relative;
  flex-shrink: 0;
  width: 480px;
}

.code-preview {
  background: #1e293b;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.code-header {
  padding: 12px 16px;
  background: #0f172a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.code-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.code-dot.red { background: #ef4444; }
.code-dot.yellow { background: #eab308; }
.code-dot.green { background: #22c55e; }

.code-title {
  margin-left: 12px;
  font-size: 13px;
  color: #64748b;
}

.code-body {
  padding: 20px;
}

.code-body pre {
  margin: 0;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.code-body code {
  color: #e2e8f0;
}

.code-comment { color: #64748b; }
.code-keyword { color: #22d3ee; }
.code-key { color: #a78bfa; }
.code-string { color: #4ade80; }
.code-number { color: #fb923c; }

.float-card {
  position: absolute;
  bottom: -20px;
  right: -20px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.float-card .card-icon {
  width: 40px;
  height: 40px;
  background: #dcfce7;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #22c55e;
  font-size: 20px;
}

.card-info {
  display: flex;
  flex-direction: column;
}

.card-label {
  font-size: 12px;
  color: #94a3b8;
}

.card-value {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

/* 支付方式 */
.payment-section {
  padding: 32px 24px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}

.payment-container {
  max-width: 1140px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  gap: 48px;
}

.payment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #64748b;
  font-size: 14px;
}

.payment-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-icon.wechat {
  background: #dcfce7;
  color: #22c55e;
}

.payment-icon.alipay {
  background: #dbeafe;
  color: #2563eb;
}

/* 功能特性 */
.features-section {
  padding: 100px 24px;
  background: #fff;
}

.section-container {
  max-width: 1140px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 12px;
}

.section-desc {
  font-size: 16px;
  color: #64748b;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  padding: 32px 24px;
  background: #f8fafc;
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s;
}

.feature-card:hover {
  background: #fff;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.feature-icon {
  width: 56px;
  height: 56px;
  background: #dbeafe;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  margin: 0 auto 20px;
}

.feature-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.feature-icon.orange {
  background: #ffedd5;
  color: #f97316;
}

.feature-icon.purple {
  background: #ede9fe;
  color: #7c3aed;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 10px;
}

.feature-card p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

/* 接入流程 */
.process-section {
  padding: 100px 24px;
  background: #f8fafc;
}

.process-steps {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 24px;
}

.process-step {
  flex: 1;
  max-width: 240px;
  text-align: center;
  padding: 32px 24px;
  background: #fff;
  border-radius: 16px;
}

.step-number {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  margin: 0 auto 20px;
}

.process-step h3 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 10px;
}

.process-step p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

.process-arrow {
  display: flex;
  align-items: center;
  color: #cbd5e1;
  font-size: 24px;
  padding-top: 48px;
}

/* CTA */
.cta-section {
  padding: 80px 24px;
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  text-align: center;
}

.cta-container {
  max-width: 600px;
  margin: 0 auto;
}

.cta-section h2 {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
}

.cta-section p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 32px;
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.cta-actions .el-button--primary {
  background: #fff !important;
  color: #2563eb !important;
  border: none !important;
  font-weight: 600 !important;
}

.cta-actions .el-button:not(.el-button--primary) {
  background: transparent !important;
  border: 2px solid rgba(255, 255, 255, 0.4) !important;
  color: #fff !important;
}

/* Footer */
.home-footer {
  background: #0f172a;
  color: #fff;
  padding: 60px 24px 0;
}

.footer-container {
  max-width: 1140px;
  margin: 0 auto;
}

.footer-main {
  display: flex;
  justify-content: space-between;
  padding-bottom: 40px;
  border-bottom: 1px solid #1e293b;
}

.footer-brand .logo-text {
  color: #fff;
}

.footer-brand p {
  margin-top: 12px;
  color: #64748b;
  font-size: 14px;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h5 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #fff;
}

.link-group a {
  display: block;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: color 0.2s;
  text-decoration: none;
}

.link-group a:hover {
  color: #fff;
}

.footer-bottom {
  padding: 24px 0;
  text-align: center;
}

.footer-bottom p {
  color: #475569;
  font-size: 13px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .process-steps {
    flex-wrap: wrap;
  }

  .process-arrow {
    display: none;
  }
}

@media (max-width: 768px) {
  .nav-links,
  .header-actions {
    display: none;
  }

  .mobile-menu {
    display: block;
  }

  .home-header.scrolled .mobile-nav {
    display: flex;
  }

  .hero-section {
    padding: 120px 24px 60px;
  }

  .hero-container {
    flex-direction: column;
    text-align: center;
  }

  .hero-visual {
    width: 100%;
    max-width: 400px;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    max-width: 100%;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-stats {
    justify-content: center;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .process-steps {
    flex-direction: column;
    align-items: center;
  }

  .footer-main {
    flex-direction: column;
    gap: 32px;
  }

  .footer-links {
    gap: 40px;
  }

  .cta-actions {
    flex-direction: column;
  }
}
</style>
