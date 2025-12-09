<template>
  <div class="error-page">
    <div class="error-container">
      <van-icon name="warning-o" size="60" color="#ee0a24" />
      <div class="error-title">支付失败</div>
      <div class="error-message">{{ errorMessage }}</div>
      <van-button type="primary" block @click="goBack">返回</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const errorMessage = ref('未知错误')

onMounted(() => {
  const message = route.query.message
  if (message) {
    errorMessage.value = decodeURIComponent(message)
  }
})

const goBack = () => {
  // 尝试返回上一页，如果没有历史记录则跳转到首页
  if (window.history.length > 1) {
    router.back()
  } else {
    window.location.href = '/'
  }
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-container {
  text-align: center;
  padding: 40px 20px;
  background: #fff;
  border-radius: 12px;
  margin: 20px;
  max-width: 400px;
  width: 100%;
}

.error-title {
  font-size: 20px;
  font-weight: 500;
  color: #ee0a24;
  margin: 16px 0 8px;
}

.error-message {
  font-size: 14px;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.6;
}
</style>
