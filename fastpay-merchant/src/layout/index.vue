<template>
  <div class="developer-layout">
    <!-- 顶部导航栏 -->
    <header class="developer-header">
      <div class="header-left">
        <div class="logo" @click="router.push('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" width="32" height="32" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="logo-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#93c5fd"/>
                  <stop offset="50%" style="stop-color:#60a5fa"/>
                  <stop offset="100%" style="stop-color:#3b82f6"/>
                </linearGradient>
              </defs>
              <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#logo-grad)"/>
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
          <span class="logo-badge">商户中心</span>
        </div>
        
        <nav class="nav-menu">
          <div 
            v-for="item in navItems" 
            :key="item.path"
            class="nav-item"
            :class="{ active: isActive(item.path) }"
            @click="router.push(item.path)"
          >
            {{ item.title }}
          </div>
        </nav>
      </div>

      <div class="header-right">
        <div class="header-action" @click="handleOpenDocs">
          <el-icon><Document /></el-icon>
        </div>
        
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-dropdown">
            <span class="user-name">{{ merchantInfo.nickname || merchantInfo.merchantName || '商户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                商户信息
              </el-dropdown-item>
              <el-dropdown-item command="docs">
                <el-icon><Document /></el-icon>
                开发文档
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="developer-main">
      <div class="developer-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 商户平台布局组件
 * 参考微信/支付宝开发者平台设计
 */
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 导航菜单
const navItems = [
  { path: '/console/dashboard', title: '控制台' },
  { path: '/console/shop', title: '店铺管理' },
  { path: '/console/channel', title: '通道管理' },
  { path: '/console/config', title: '开发配置' }
]

// 商户信息
const merchantInfo = computed(() => {
  const info = localStorage.getItem('merchant_info')
  return info ? JSON.parse(info) : {}
})

// 判断是否激活
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

// 打开文档
const handleOpenDocs = () => {
  router.push('/console/docs')
}

// 下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('merchant_token')
      localStorage.removeItem('merchant_info')
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'docs') {
    handleOpenDocs()
  } else if (command === 'profile') {
    router.push('/console/config')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
