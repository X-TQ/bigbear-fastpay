/**
 * Fast 易支付 - 商户平台 API 接口
 */
import request from '@/utils/request'

// ==================== 认证相关 ====================

/**
 * 商户登录
 */
export function login(data) {
  return request.post('/merchant/login', data)
}

/**
 * 获取商户信息
 */
export function getMerchantInfo() {
  return request.get('/merchant/info')
}

/**
 * 获取控制台统计数据
 */
export function getDashboard() {
  return request.get('/merchant/dashboard')
}

/**
 * 更新回调配置
 */
export function updateCallbackConfig(data) {
  return request.put('/merchant/callback-config', data)
}

/**
 * 重置API密钥
 */
export function resetApiKey() {
  return request.post('/merchant/reset-key')
}

// ==================== 店铺管理 ====================

/**
 * 分页查询店铺列表
 */
export function getShopPage(params) {
  return request.get('/merchant/shop/page', { params })
}

/**
 * 获取店铺列表
 */
export function getShopList() {
  return request.get('/merchant/shop/list')
}

/**
 * 获取店铺详情
 */
export function getShopById(id) {
  return request.get(`/merchant/shop/${id}`)
}

/**
 * 创建店铺
 */
export function createShop(data) {
  return request.post('/merchant/shop', data)
}

/**
 * 更新店铺
 */
export function updateShop(id, data) {
  return request.put(`/merchant/shop/${id}`, data)
}

/**
 * 更新店铺状态
 */
export function updateShopStatus(id, status) {
  return request.put(`/merchant/shop/${id}/status`, null, { params: { status } })
}

/**
 * 删除店铺
 */
export function deleteShop(id) {
  return request.delete(`/merchant/shop/${id}`)
}

// ==================== 通道管理 ====================

/**
 * 获取通道列表
 */
export function getChannelList() {
  return request.get('/merchant/channel/list')
}

/**
 * 获取指定支付类型的通道列表
 */
export function getChannelListByPayType(payType) {
  return request.get(`/merchant/channel/list/${payType}`)
}

/**
 * 获取通道详情
 */
export function getChannelById(id) {
  return request.get(`/merchant/channel/${id}`)
}

/**
 * 添加通道
 */
export function addChannel(data) {
  return request.post('/merchant/channel', data)
}

/**
 * 更新通道
 */
export function updateChannel(id, data) {
  return request.put(`/merchant/channel/${id}`, data)
}

/**
 * 更新通道状态
 */
export function updateChannelStatus(id, status) {
  return request.put(`/merchant/channel/${id}/status`, null, { params: { status } })
}

/**
 * 删除通道
 */
export function deleteChannel(id) {
  return request.delete(`/merchant/channel/${id}`)
}

/**
 * 生成通道模版
 */
export function generateChannelTemplate(id) {
  return request.get(`/merchant/channel/${id}/template`)
}

/**
 * 获取监听回调配置
 */
export function getNotifyConfig() {
  return request.get('/merchant/notify/config')
}

// ==================== 二维码管理 ====================

/**
 * 分页查询二维码列表
 */
export function getQrcodePage(params) {
  return request.get('/merchant/qrcode/page', { params })
}

/**
 * 获取店铺的二维码列表
 */
export function getQrcodeList(shopId) {
  return request.get('/merchant/qrcode/list', { params: { shopId } })
}

/**
 * 添加收款二维码
 */
export function addQrcode(data) {
  return request.post('/merchant/qrcode', data)
}

/**
 * 更新收款二维码
 */
export function updateQrcode(id, data) {
  return request.put(`/merchant/qrcode/${id}`, data)
}

/**
 * 更新二维码状态
 */
export function updateQrcodeStatus(id, status) {
  return request.put(`/merchant/qrcode/${id}/status`, null, { params: { status } })
}

/**
 * 删除二维码
 */
export function deleteQrcode(id) {
  return request.delete(`/merchant/qrcode/${id}`)
}

// ==================== 订单管理 ====================

/**
 * 分页查询订单列表
 */
export function getOrderPage(params) {
  return request.get('/merchant/order/page', { params })
}

/**
 * 获取订单详情
 */
export function getOrderByNo(orderNo) {
  return request.get(`/merchant/order/${orderNo}`)
}

/**
 * 确认支付
 */
export function confirmOrder(orderNo) {
  return request.post(`/merchant/order/${orderNo}/confirm`)
}

/**
 * 关闭订单
 */
export function closeOrder(orderNo) {
  return request.post(`/merchant/order/${orderNo}/close`)
}

/**
 * 重新发送回调通知
 */
export function resendNotify(orderNo) {
  return request.post(`/merchant/order/${orderNo}/notify`)
}

// ==================== 支付页面（公开接口） ====================

/**
 * 获取支付页面数据
 */
export function getPayPageData(orderNo) {
  return request.get(`/pay/page/${orderNo}`)
}

/**
 * 查询支付状态
 */
export function getPayStatus(orderNo) {
  return request.get(`/pay/status/${orderNo}`)
}

/**
 * 获取支付结果
 */
export function getPayResult(orderNo) {
  return request.get(`/pay/result/${orderNo}`)
}
