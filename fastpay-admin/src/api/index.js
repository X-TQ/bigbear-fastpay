/**
 * Fast 易支付 - 管理后台 API 接口
 */
import request from '@/utils/request'

// ==================== 认证相关 ====================

/**
 * 管理员登录
 */
export function login(data) {
  return request.post('/admin/login', data)
}

/**
 * 获取控制台统计数据
 */
export function getDashboard() {
  return request.get('/admin/dashboard')
}

// ==================== 商户管理 ====================

/**
 * 分页查询商户列表
 */
export function getMerchantPage(params) {
  return request.get('/admin/merchant/page', { params })
}

/**
 * 获取所有商户列表
 */
export function getMerchantList() {
  return request.get('/admin/merchant/list')
}

/**
 * 获取商户详情
 */
export function getMerchantById(id) {
  return request.get(`/admin/merchant/${id}`)
}

/**
 * 创建商户
 */
export function createMerchant(data) {
  return request.post('/admin/merchant', data)
}

/**
 * 更新商户
 */
export function updateMerchant(id, data) {
  return request.put(`/admin/merchant/${id}`, data)
}

/**
 * 更新商户状态
 */
export function updateMerchantStatus(id, status) {
  return request.put(`/admin/merchant/${id}/status`, null, { params: { status } })
}

/**
 * 重置商户API密钥
 */
export function resetMerchantApiKey(id) {
  return request.post(`/admin/merchant/${id}/reset-key`)
}

// ==================== 店铺管理 ====================

/**
 * 分页查询店铺列表
 */
export function getShopPage(params) {
  return request.get('/admin/shop/page', { params })
}

/**
 * 获取商户的店铺列表
 */
export function getShopList(params) {
  return request.get('/admin/shop/list', { params })
}

/**
 * 获取店铺详情
 */
export function getShopById(id) {
  return request.get(`/admin/shop/${id}`)
}

/**
 * 创建店铺
 */
export function createShop(data) {
  return request.post('/admin/shop', data)
}

/**
 * 更新店铺
 */
export function updateShop(id, data) {
  return request.put(`/admin/shop/${id}`, data)
}

/**
 * 更新店铺状态
 */
export function updateShopStatus(id, status) {
  return request.put(`/admin/shop/${id}/status`, null, { params: { status } })
}

/**
 * 删除店铺
 */
export function deleteShop(id) {
  return request.delete(`/admin/shop/${id}`)
}

// ==================== 二维码管理 ====================

/**
 * 分页查询二维码列表
 */
export function getQrcodePage(params) {
  return request.get('/admin/qrcode/page', { params })
}

/**
 * 获取店铺的二维码列表
 */
export function getQrcodeList(shopId) {
  return request.get('/admin/qrcode/list', { params: { shopId } })
}

/**
 * 创建二维码
 */
export function createQrcode(data) {
  return request.post('/admin/qrcode', data)
}

/**
 * 更新二维码
 */
export function updateQrcode(id, data) {
  return request.put(`/admin/qrcode/${id}`, data)
}

/**
 * 删除二维码
 */
export function deleteQrcode(id) {
  return request.delete(`/admin/qrcode/${id}`)
}

/**
 * 更新二维码状态
 */
export function updateQrcodeStatus(id, status) {
  return request.put(`/admin/qrcode/${id}/status`, null, { params: { status } })
}

// ==================== 订单管理 ====================

/**
 * 分页查询订单列表
 */
export function getOrderPage(params) {
  return request.get('/admin/order/page', { params })
}

/**
 * 获取订单详情
 */
export function getOrderByNo(orderNo) {
  return request.get(`/admin/order/${orderNo}`)
}

/**
 * 确认支付
 */
export function confirmOrder(orderNo) {
  return request.post(`/admin/order/${orderNo}/confirm`)
}

/**
 * 关闭订单
 */
export function closeOrder(orderNo) {
  return request.post(`/admin/order/${orderNo}/close`)
}

/**
 * 重新发送回调通知
 */
export function resendNotify(orderNo) {
  return request.post(`/admin/order/${orderNo}/notify`)
}

// ==================== 通道管理 ====================

/**
 * 分页查询通道列表
 */
export function getChannelPage(params) {
  return request.get('/admin/channel/page', { params })
}

/**
 * 获取通道列表
 */
export function getChannelList(params) {
  return request.get('/admin/channel/list', { params })
}

/**
 * 创建通道
 */
export function createChannel(data) {
  return request.post('/admin/channel', data)
}

/**
 * 更新通道
 */
export function updateChannel(id, data) {
  return request.put(`/admin/channel/${id}`, data)
}

/**
 * 更新通道状态
 */
export function updateChannelStatus(id, status) {
  return request.put(`/admin/channel/${id}/status`, null, { params: { status } })
}

/**
 * 删除通道
 */
export function deleteChannel(id) {
  return request.delete(`/admin/channel/${id}`)
}
