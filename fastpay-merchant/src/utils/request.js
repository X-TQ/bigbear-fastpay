/**
 * Fast 易支付 - HTTP 请求工具
 */
import axios from 'axios'
import { showToast } from 'vant'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/fastpay-server/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('merchant_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      showToast(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('merchant_token')
        localStorage.removeItem('merchant_info')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    showToast(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
