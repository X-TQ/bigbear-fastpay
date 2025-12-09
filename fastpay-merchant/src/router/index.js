/**
 * Fast 易支付 - 商户平台路由配置
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 公开页面
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页', public: true }
  },
  {
    path: '/docs',
    name: 'PublicDocs',
    component: () => import('@/views/docs/public.vue'),
    meta: { title: '开发文档', public: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '商户登录', public: true }
  },
  // 支付页面（公开）
  {
    path: '/pay/:orderNo',
    name: 'Pay',
    component: () => import('@/views/pay/index.vue'),
    meta: { title: '订单支付', public: true }
  },
  {
    path: '/pay/result/:orderNo',
    name: 'PayResult',
    component: () => import('@/views/pay/result.vue'),
    meta: { title: '支付结果', public: true }
  },
  {
    path: '/pay/error',
    name: 'PayError',
    component: () => import('@/views/pay/error.vue'),
    meta: { title: '支付失败', public: true }
  },
  // 需要登录的页面
  {
    path: '/console',
    component: () => import('@/layout/index.vue'),
    redirect: '/console/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'shop',
        name: 'Shop',
        component: () => import('@/views/shop/index.vue'),
        meta: { title: '店铺管理' }
      },
      {
        path: 'channel',
        name: 'Channel',
        component: () => import('@/views/channel/index.vue'),
        meta: { title: '通道管理' }
      },
      {
        path: 'qrcode',
        name: 'Qrcode',
        component: () => import('@/views/qrcode/index.vue'),
        meta: { title: '二维码管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/config/index.vue'),
        meta: { title: 'API配置' }
      },
      {
        path: 'docs',
        name: 'Docs',
        component: () => import('@/views/docs/index.vue'),
        meta: { title: '开发文档' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/fastpay-merchant/'),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - FAST 易支付` : 'FAST 易支付'
  
  // 公开页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  // 检查登录状态
  const token = localStorage.getItem('merchant_token')
  if (!token) {
    next('/login')
    return
  }
  
  next()
})

export default router
