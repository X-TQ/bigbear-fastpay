<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar" :class="{ collapsed }">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 40 40" width="32" height="32" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="admin-logo-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#93c5fd"/>
                <stop offset="50%" style="stop-color:#60a5fa"/>
                <stop offset="100%" style="stop-color:#3b82f6"/>
              </linearGradient>
            </defs>
            <rect x="2" y="2" width="36" height="36" rx="10" ry="10" fill="url(#admin-logo-grad)"/>
            <circle cx="20" cy="20" r="14" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            <circle cx="20" cy="20" r="11" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="0.5"/>
            <circle cx="8" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="32" cy="8" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="8" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <circle cx="32" cy="32" r="1.5" fill="rgba(255,255,255,0.4)"/>
            <text x="20" y="26" text-anchor="middle" font-family="STKaiti, KaiTi, SimSun, serif" font-size="20" font-weight="bold" fill="white">易</text>
          </svg>
        </div>
        <span v-if="!collapsed" class="logo-text">FAST 易支付</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        background-color="transparent"
        text-color="#606266"
        active-text-color="#3b82f6"
        :collapse-transition="false"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/merchant">
          <el-icon><User /></el-icon>
          <span>商户管理</span>
        </el-menu-item>
        <el-menu-item index="/shop">
          <el-icon><Shop /></el-icon>
          <span>店铺管理</span>
        </el-menu-item>
        <el-menu-item index="/channel">
          <el-icon><Connection /></el-icon>
          <span>通道管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 作者信息 -->
      <div class="sidebar-footer" v-if="!collapsed">
        <span class="author-text">by 大熊Bigbear</span>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="admin-main">
      <!-- 顶部导航 -->
      <header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="collapsed = !collapsed">
            <Fold v-if="!collapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <div class="avatar">
                <el-icon><User /></el-icon>
              </div>
              <span class="username">{{ userInfo.nickname || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 管理后台布局组件
 */
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 侧边栏折叠状态
const collapsed = ref(false)

// 当前选中的菜单
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentTitle = computed(() => route.meta.title || '')

// 用户信息
const userInfo = computed(() => {
  const user = localStorage.getItem('admin_user')
  return user ? JSON.parse(user) : {}
})

// 菜单选择
const handleMenuSelect = (index) => {
  router.push(index)
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    router.push('/login')
  }).catch(() => {})
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
