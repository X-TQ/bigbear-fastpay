<template>
  <div class="shop-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">店铺管理</h1>
      <p class="page-desc">管理您的收款店铺、二维码和订单</p>
    </div>

    <!-- 店铺列表 -->
    <div class="dev-card">
      <div class="card-header">
        <span class="card-title">我的店铺</span>
        <el-button type="primary" size="small" @click="handleAddShop">
          <el-icon><Plus /></el-icon>
          新增店铺
        </el-button>
      </div>
      <div class="card-body">
        <div v-if="shopList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无店铺，请先创建一个店铺" />
        </div>
        <div v-else class="shop-list-wrapper" ref="shopListRef" @scroll="handleShopScroll">
          <div class="shop-list">
            <div 
              v-for="shop in shopList" 
              :key="shop.id" 
              class="shop-item"
              :class="{ active: currentShop?.id === shop.id }"
              @click="selectShop(shop)"
            >
              <div class="shop-info">
                <div class="shop-name">{{ shop.shopName }}</div>
                <div class="shop-meta">
                  <span>{{ shop.shopNo }}</span>
                  <span :class="['status-tag', shop.status === 1 ? 'status-enabled' : 'status-disabled']">
                    {{ shop.status === 1 ? '启用' : '禁用' }}
                  </span>
                </div>
              </div>
              <div class="shop-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ shop.qrcodeCount || 0 }}</span>
                  <span class="stat-label">二维码</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value amount">¥{{ shop.totalAmount || 0 }}</span>
                  <span class="stat-label">累计交易</span>
                </div>
              </div>
              <div class="shop-actions">
                <el-button type="primary" link size="small" @click.stop="handleEditShop(shop)">编辑</el-button>
                <el-button 
                  :type="shop.status === 1 ? 'warning' : 'success'" 
                  link 
                  size="small" 
                  @click.stop="handleShopStatus(shop, shop.status === 1 ? 0 : 1)"
                >
                  {{ shop.status === 1 ? '禁用' : '启用' }}
                </el-button>
              </div>
            </div>
          </div>
          <!-- 加载更多提示 -->
          <div v-if="shopLoadingMore" class="load-more-tip">
            <el-icon class="is-loading"><Loading /></el-icon>
            加载中...
          </div>
          <div v-else-if="!shopHasMore && shopList.length > 0" class="load-more-tip no-more">
            没有更多了
          </div>
        </div>
      </div>
    </div>

    <!-- 店铺详情区域 -->
    <template v-if="currentShop">
      <!-- Tab 切换 -->
      <el-tabs v-model="activeTab" class="shop-tabs">
        <el-tab-pane label="二维码管理" name="qrcode">
          <div class="dev-card">
            <div class="card-header">
              <span class="card-title">收款二维码</span>
              <el-button type="primary" size="small" @click="handleAddQrcode">
                <el-icon><Plus /></el-icon>
                添加二维码
              </el-button>
            </div>
            <div class="card-body">
              <el-table :data="qrcodeList" v-loading="qrcodeLoading" stripe>
                <el-table-column label="二维码" width="80">
                  <template #default="{ row }">
                    <el-image
                      v-if="row.qrcodeImage"
                      :src="row.qrcodeImage"
                      :preview-src-list="[row.qrcodeImage]"
                      :preview-teleported="true"
                      :initial-index="0"
                      fit="cover"
                      class="qrcode-thumbnail"
                    >
                      <template #error>
                        <div class="image-error">
                          <el-icon><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </template>
                </el-table-column>
                <el-table-column prop="qrcodeName" label="名称" min-width="120" />
                <el-table-column label="支付类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.payType === 'wxpay' ? 'success' : 'primary'" size="small">
                      {{ row.payType === 'wxpay' ? '微信' : '支付宝' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="累计收款" width="120">
                  <template #default="{ row }">
                    <span class="amount-text">¥{{ row.totalAmount || 0 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="80">
                  <template #default="{ row }">
                    <span :class="['status-tag', row.status === 1 ? 'status-enabled' : 'status-disabled']">
                      {{ row.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="140" fixed="right">
                  <template #default="{ row }">
                    <el-button 
                      :type="row.status === 1 ? 'warning' : 'success'" 
                      link 
                      size="small" 
                      @click="handleQrcodeStatus(row, row.status === 1 ? 0 : 1)"
                    >
                      {{ row.status === 1 ? '禁用' : '启用' }}
                    </el-button>
                    <el-button type="danger" link size="small" @click="handleDeleteQrcode(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="订单记录" name="order">
          <div class="dev-card">
            <div class="card-header">
              <span class="card-title">订单记录</span>
            </div>
            <div class="card-body">
              <!-- 筛选条件 -->
              <div class="order-filter">
                <el-input v-model="orderParams.outTradeNo" placeholder="商户订单号" clearable size="small" style="width: 150px" @keyup.enter="loadOrders" />
                <el-input v-model="orderParams.subject" placeholder="商品名称" clearable size="small" style="width: 140px" @keyup.enter="loadOrders" />
                <el-select v-model="orderParams.status" placeholder="订单状态" clearable size="small" style="width: 110px">
                  <el-option label="待支付" :value="0" />
                  <el-option label="已支付" :value="1" />
                  <el-option label="已过期" :value="2" />
                </el-select>
                <el-select v-model="orderParams.payType" placeholder="支付类型" clearable size="small" style="width: 110px">
                  <el-option label="微信支付" value="wxpay" />
                  <el-option label="支付宝" value="alipay" />
                </el-select>
                <el-button type="primary" size="small" @click="loadOrders">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button size="small" @click="resetOrderQuery">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </div>

              <!-- 订单列表 -->
              <el-table :data="orderList" v-loading="orderLoading" stripe style="margin-top: 16px;">
                <el-table-column prop="orderNo" label="平台订单号" width="170">
                  <template #default="{ row }">
                    <span class="order-no-text">{{ row.orderNo }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="outTradeNo" label="商户订单号" width="130" show-overflow-tooltip />
                <el-table-column prop="subject" label="商品名称" min-width="100" show-overflow-tooltip />
                <el-table-column label="金额" width="85" align="right">
                  <template #default="{ row }">
                    <span class="amount-text">¥{{ row.amount }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="支付类型" width="80" align="center">
                  <template #default="{ row }">
                    <el-tag :type="row.payType === 'wxpay' ? 'success' : 'primary'" size="small" effect="light">
                      {{ row.payType === 'wxpay' ? '微信' : '支付宝' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="70" align="center">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)" size="small" effect="light">
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="回调状态" width="95" align="center">
                  <template #default="{ row }">
                    <template v-if="row.status === 1">
                      <el-tag :type="getNotifyStatusType(row.notifyStatus)" size="small">
                        {{ getNotifyStatusText(row.notifyStatus) }}
                      </el-tag>
                      <span v-if="row.notifyCount > 0" class="notify-count">({{ row.notifyCount }})</span>
                    </template>
                    <span v-else class="text-muted">-</span>
                  </template>
                </el-table-column>
                <el-table-column label="跳转地址" width="120" show-overflow-tooltip>
                  <template #default="{ row }">
                    <span v-if="row.returnUrl" class="return-url">{{ row.returnUrl }}</span>
                    <span v-else class="text-muted">-</span>
                  </template>
                </el-table-column>
                <el-table-column label="创建时间" width="150">
                  <template #default="{ row }">
                    {{ formatTime(row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right" align="center">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="handleViewOrder(row)">详情</el-button>
                    <el-button v-if="row.status === 0" type="success" link size="small" @click="handleConfirmOrder(row)">确认</el-button>
                  </template>
                </el-table-column>
              </el-table>

              <div class="pagination-wrapper">
                <el-pagination
                  v-model:current-page="orderParams.current"
                  v-model:page-size="orderParams.size"
                  :page-sizes="[10, 20, 50]"
                  :total="orderTotal"
                  layout="total, sizes, prev, pager, next"
                  small
                  @size-change="loadOrders"
                  @current-change="loadOrders"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- 新增/编辑店铺弹窗 -->
    <el-dialog
      v-model="shopFormVisible"
      :title="shopFormData.id ? '编辑店铺' : '新增店铺'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="shopFormRef" :model="shopFormData" :rules="shopFormRules" label-width="100px">
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="shopFormData.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="店铺描述" prop="description">
          <el-input v-model="shopFormData.description" type="textarea" :rows="3" placeholder="请输入店铺描述" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="shopFormData.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="shopFormData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shopFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="shopSubmitting" @click="handleShopSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加二维码弹窗 -->
    <el-dialog
      v-model="qrcodeFormVisible"
      title="添加收款二维码"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form ref="qrcodeFormRef" :model="qrcodeFormData" :rules="qrcodeFormRules" label-width="100px">
        <el-form-item label="二维码图片" prop="qrcodeUrl">
          <div class="qrcode-upload-area">
            <el-upload
              class="qrcode-uploader"
              :show-file-list="false"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <img v-if="qrcodeImagePreview" :src="qrcodeImagePreview" class="qrcode-preview" />
              <div v-else class="upload-placeholder">
                <el-icon :size="32"><Plus /></el-icon>
                <span>上传二维码</span>
              </div>
              <div v-if="qrcodeParsing" class="parsing-overlay">
                <el-icon class="is-loading" :size="24"><Loading /></el-icon>
                <span>解析中...</span>
              </div>
            </el-upload>
            <!-- 解析结果展示 -->
            <div v-if="qrcodeParseResult.content" class="parse-result">
              <div class="parse-result-header">
                <el-icon :size="16" class="success-icon"><CircleCheck /></el-icon>
                <span>解析成功</span>
              </div>
              <div class="parse-result-item">
                <span class="label">支付类型：</span>
                <el-tag :type="qrcodeParseResult.payType === 'wxpay' ? 'success' : 'primary'" size="small">
                  {{ qrcodeParseResult.payTypeName }}
                </el-tag>
              </div>
              <div class="parse-result-item">
                <span class="label">二维码内容：</span>
                <span class="content">{{ qrcodeParseResult.content }}</span>
              </div>
            </div>
          </div>
          <div class="upload-tip">支持 JPG、PNG 格式，上传后自动解析二维码内容</div>
        </el-form-item>
        <el-form-item label="选择通道" prop="channelId">
          <el-select 
            v-model="qrcodeFormData.channelId" 
            placeholder="请选择通道" 
            style="width: 100%"
            @change="handleChannelChange"
          >
            <el-option 
              v-for="channel in filteredChannels" 
              :key="channel.id" 
              :label="channel.channelName" 
              :value="channel.id"
            >
              <span>{{ channel.channelName }}</span>
              <el-tag :type="channel.payType === 'wxpay' ? 'success' : 'primary'" size="small" style="margin-left: 8px">
                {{ channel.payType === 'wxpay' ? '微信' : '支付宝' }}
              </el-tag>
            </el-option>
          </el-select>
          <div v-if="filteredChannels.length === 0" class="no-channel-tip">
            <el-icon><Warning /></el-icon>
            <span>暂无可用通道，请先到<router-link to="/console/channel">通道管理</router-link>创建</span>
          </div>
          <div v-if="qrcodeParseResult.payType && qrcodeFormData.channelId" class="channel-match-tip">
            <template v-if="qrcodeFormData.payType === qrcodeParseResult.payType">
              <el-icon class="success-icon"><CircleCheck /></el-icon>
              <span>通道类型与二维码匹配</span>
            </template>
            <template v-else>
              <el-icon class="warning-icon"><Warning /></el-icon>
              <span>通道类型与二维码不匹配，请确认</span>
            </template>
          </div>
        </el-form-item>
        <el-form-item label="二维码名称">
          <el-input v-model="qrcodeFormData.qrcodeName" placeholder="请输入备注名称（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="qrcodeFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="qrcodeSubmitting" :disabled="qrcodeParsing" @click="handleQrcodeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="orderDetailVisible" title="订单详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="平台订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="商户订单号">{{ currentOrder.outTradeNo }}</el-descriptions-item>
        <el-descriptions-item label="商品名称" :span="2">{{ currentOrder.subject }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="amount-text">¥{{ currentOrder.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="amount-text">¥{{ currentOrder.payAmount || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付类型">
          <el-tag :type="currentOrder.payType === 'wxpay' ? 'success' : 'primary'" size="small">
            {{ currentOrder.payType === 'wxpay' ? '微信支付' : '支付宝' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="回调状态">
          <template v-if="currentOrder.status === 1">
            <el-tag :type="getNotifyStatusType(currentOrder.notifyStatus)" size="small">
              {{ getNotifyStatusText(currentOrder.notifyStatus) }}
            </el-tag>
            <span v-if="currentOrder.notifyCount > 0" class="notify-count">(已通知{{ currentOrder.notifyCount }}次)</span>
          </template>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="回调地址" :span="2">{{ currentOrder.notifyUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="跳转地址" :span="2">{{ currentOrder.returnUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ formatTime(currentOrder.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="最后通知时间">{{ formatTime(currentOrder.lastNotifyTime) || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 店铺管理（整合二维码和订单）
 */
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getShopPage, createShop, updateShop, updateShopStatus,
  getQrcodePage, addQrcode, updateQrcodeStatus, deleteQrcode,
  getOrderPage, confirmOrder,
  getChannelList
} from '@/api'
import jsQR from 'jsqr'
import QRCode from 'qrcode'

// 店铺相关
const loading = ref(false)
const shopList = ref([])
const currentShop = ref(null)
const activeTab = ref('qrcode')
const shopListRef = ref(null)
const shopLoadingMore = ref(false)
const shopHasMore = ref(true)
const shopParams = reactive({
  current: 1,
  size: 2  // 默认每页加载2条
})

// 店铺表单
const shopFormVisible = ref(false)
const shopSubmitting = ref(false)
const shopFormRef = ref(null)
const shopFormData = reactive({
  id: null,
  shopName: '',
  description: '',
  contactName: '',
  contactPhone: ''
})
const shopFormRules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

// 通道相关
const channelList = ref([])
const filteredChannels = ref([])

// 二维码相关
const qrcodeLoading = ref(false)
const qrcodeList = ref([])
const qrcodeFormVisible = ref(false)
const qrcodeSubmitting = ref(false)
const qrcodeFormRef = ref(null)
const qrcodeFormData = reactive({
  shopId: undefined,
  channelId: undefined,  // 通道ID
  payType: '',  // 由通道决定
  qrcodeName: '',
  qrcodeUrl: ''  // 二维码解析内容（收款链接）
})
// 二维码图片预览（仅用于显示，不提交）
const qrcodeImagePreview = ref('')
const qrcodeFormRules = {
  channelId: [{ required: true, message: '请选择通道', trigger: 'change' }],
  qrcodeUrl: [{ required: true, message: '请上传二维码图片', trigger: 'change' }]
}

// 二维码解析结果
const qrcodeParsing = ref(false)
const qrcodeParseResult = reactive({
  content: '',
  payType: '',
  payTypeName: ''
})

// 订单相关
const orderLoading = ref(false)
const orderList = ref([])
const orderTotal = ref(0)
const orderParams = reactive({
  current: 1,
  size: 10,
  shopId: undefined,
  outTradeNo: '',
  subject: '',
  status: undefined,
  payType: undefined
})
const orderDetailVisible = ref(false)
const currentOrder = ref({})

// 加载通道列表
const loadChannels = async () => {
  try {
    const res = await getChannelList()
    channelList.value = (res.data || []).filter(c => c.status === 1)  // 只显示启用的通道
  } catch (error) {
    console.error('加载通道列表失败:', error)
  }
}

// 根据支付类型过滤通道
const filterChannelsByPayType = (payType) => {
  if (!payType) {
    filteredChannels.value = channelList.value
  } else {
    filteredChannels.value = channelList.value.filter(c => c.payType === payType)
  }
}

// 通道选择变化时更新 payType
const handleChannelChange = (channelId) => {
  const channel = channelList.value.find(c => c.id === channelId)
  if (channel) {
    qrcodeFormData.payType = channel.payType
  }
}

// 加载店铺列表（首次加载）
const loadShops = async () => {
  loading.value = true
  shopParams.current = 1
  shopHasMore.value = true
  try {
    const res = await getShopPage(shopParams)
    const { records, total } = res.data || {}
    shopList.value = records || []
    shopHasMore.value = shopList.value.length < total
    if (shopList.value.length > 0 && !currentShop.value) {
      selectShop(shopList.value[0])
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
  }
  loading.value = false
}

// 加载更多店铺
const loadMoreShops = async () => {
  if (shopLoadingMore.value || !shopHasMore.value) return
  shopLoadingMore.value = true
  shopParams.current++
  try {
    const res = await getShopPage(shopParams)
    const { records, total } = res.data || {}
    const newRecords = records || []
    shopList.value = [...shopList.value, ...newRecords]
    shopHasMore.value = shopList.value.length < total
  } catch (error) {
    console.error('加载更多店铺失败:', error)
    shopParams.current--
  }
  shopLoadingMore.value = false
}

// 滚动加载
const handleShopScroll = (e) => {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  // 距离底部 20px 时触发加载
  if (scrollHeight - scrollTop - clientHeight < 20) {
    loadMoreShops()
  }
}

// 选择店铺
const selectShop = (shop) => {
  currentShop.value = shop
  loadQrcodes()
  loadOrders()
}

// 根据内容生成二维码图片
const generateQrcodeImage = async (content) => {
  if (!content) return ''
  try {
    return await QRCode.toDataURL(content, { width: 200, margin: 1 })
  } catch (error) {
    console.error('生成二维码失败:', error)
    return ''
  }
}

// 加载二维码
const loadQrcodes = async () => {
  if (!currentShop.value) return
  qrcodeLoading.value = true
  try {
    const res = await getQrcodePage({ shopId: currentShop.value.id, size: 100 })
    const records = res.data.records || []
    // 为每个二维码生成图片
    for (const item of records) {
      if (item.qrcodeUrl) {
        item.qrcodeImage = await generateQrcodeImage(item.qrcodeUrl)
      }
    }
    qrcodeList.value = records
  } catch (error) {
    console.error('加载二维码失败:', error)
  }
  qrcodeLoading.value = false
}

// 加载订单
const loadOrders = async () => {
  if (!currentShop.value) return
  orderLoading.value = true
  orderParams.shopId = currentShop.value.id
  try {
    const res = await getOrderPage(orderParams)
    orderList.value = res.data.records || []
    orderTotal.value = res.data.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
  }
  orderLoading.value = false
}

// 重置订单查询
const resetOrderQuery = () => {
  orderParams.outTradeNo = ''
  orderParams.subject = ''
  orderParams.status = undefined
  orderParams.payType = undefined
  orderParams.current = 1
  loadOrders()
}

// 店铺操作
const handleAddShop = () => {
  Object.assign(shopFormData, { id: null, shopName: '', description: '', contactName: '', contactPhone: '' })
  shopFormVisible.value = true
}

const handleEditShop = (shop) => {
  Object.assign(shopFormData, {
    id: shop.id,
    shopName: shop.shopName,
    description: shop.description,
    contactName: shop.contactName,
    contactPhone: shop.contactPhone
  })
  shopFormVisible.value = true
}

const handleShopSubmit = async () => {
  if (!shopFormRef.value) return
  await shopFormRef.value.validate(async (valid) => {
    if (!valid) return
    shopSubmitting.value = true
    try {
      if (shopFormData.id) {
        await updateShop(shopFormData.id, shopFormData)
        ElMessage.success('更新成功')
      } else {
        await createShop(shopFormData)
        ElMessage.success('创建成功')
      }
      shopFormVisible.value = false
      loadShops()
    } catch (error) {
      console.error('保存失败:', error)
    }
    shopSubmitting.value = false
  })
}

const handleShopStatus = (shop, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该店铺吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateShopStatus(shop.id, status)
    ElMessage.success('操作成功')
    loadShops()
  }).catch(() => {})
}

// 二维码操作
const handleAddQrcode = () => {
  Object.assign(qrcodeFormData, { 
    shopId: currentShop.value.id, 
    channelId: undefined,
    payType: '', 
    qrcodeName: '', 
    qrcodeUrl: ''
  })
  // 重置图片预览和解析结果
  qrcodeImagePreview.value = ''
  Object.assign(qrcodeParseResult, { content: '', payType: '', payTypeName: '' })
  // 初始化通道列表
  filteredChannels.value = channelList.value
  qrcodeFormVisible.value = true
}

// 本地解析二维码图片
const parseQrcodeLocal = (imageData) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      // 创建 canvas 获取图片像素数据
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      canvas.width = img.width
      canvas.height = img.height
      ctx.drawImage(img, 0, 0)
      
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const code = jsQR(imageData.data, imageData.width, imageData.height)
      
      if (code) {
        resolve(code.data)
      } else {
        reject(new Error('无法识别二维码'))
      }
    }
    img.onerror = () => reject(new Error('图片加载失败'))
    img.src = imageData
  })
}

// 根据二维码内容识别支付类型
const detectPayType = (content) => {
  if (!content) return { payType: 'unknown', payTypeName: '未知类型' }
  
  const lowerContent = content.toLowerCase()
  
  // 微信支付特征
  if (lowerContent.includes('wxp://') || 
      lowerContent.includes('weixin://') || 
      lowerContent.includes('wx.tenpay.com')) {
    return { payType: 'wxpay', payTypeName: '微信支付' }
  }
  
  // 支付宝特征
  if (lowerContent.includes('alipay') || 
      lowerContent.includes('qr.alipay.com') ||
      lowerContent.includes('HTTPS://QR.ALIPAY.COM')) {
    return { payType: 'alipay', payTypeName: '支付宝' }
  }
  
  return { payType: 'unknown', payTypeName: '未知类型' }
}

const beforeUpload = async (file) => {
  const reader = new FileReader()
  reader.onload = async (e) => {
    const base64Image = e.target.result
    qrcodeImagePreview.value = base64Image  // 仅用于预览
    
    // 本地解析二维码
    qrcodeParsing.value = true
    try {
      const content = await parseQrcodeLocal(base64Image)
      const { payType, payTypeName } = detectPayType(content)
      
      // 将解析出的内容存入 qrcodeUrl
      qrcodeFormData.qrcodeUrl = content
      Object.assign(qrcodeParseResult, { content, payType, payTypeName })
      
      // 根据解析出的支付类型过滤通道
      if (payType !== 'unknown') {
        filterChannelsByPayType(payType)
        // 如果只有一个匹配的通道，自动选中
        if (filteredChannels.value.length === 1) {
          qrcodeFormData.channelId = filteredChannels.value[0].id
          qrcodeFormData.payType = filteredChannels.value[0].payType
        } else if (filteredChannels.value.length === 0) {
          ElMessage.warning(`没有${payTypeName}类型的通道，请先创建通道`)
        }
      }
      ElMessage.success('二维码解析成功')
    } catch (error) {
      console.error('解析二维码失败:', error)
      ElMessage.warning('无法识别二维码内容，请确保图片清晰')
      qrcodeFormData.qrcodeUrl = ''
      Object.assign(qrcodeParseResult, { content: '', payType: '', payTypeName: '' })
    }
    qrcodeParsing.value = false
  }
  reader.readAsDataURL(file)
  return false
}

const handleQrcodeSubmit = async () => {
  if (!qrcodeFormRef.value) return
  await qrcodeFormRef.value.validate(async (valid) => {
    if (!valid) return
    qrcodeSubmitting.value = true
    try {
      await addQrcode(qrcodeFormData)
      ElMessage.success('添加成功')
      qrcodeFormVisible.value = false
      loadQrcodes()
    } catch (error) {
      console.error('添加失败:', error)
    }
    qrcodeSubmitting.value = false
  })
}

const handleQrcodeStatus = (qrcode, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该二维码吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateQrcodeStatus(qrcode.id, status)
    ElMessage.success('操作成功')
    loadQrcodes()
  }).catch(() => {})
}

const handleDeleteQrcode = (qrcode) => {
  ElMessageBox.confirm('删除后将无法恢复', '确定要删除该二维码吗？', { type: 'warning' })
    .then(async () => {
      await deleteQrcode(qrcode.id)
      ElMessage.success('删除成功')
      loadQrcodes()
    }).catch(() => {})
}

// 订单操作
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已过期', 3: '已关闭' }
  return texts[status] || '未知'
}

// 回调状态类型
const getNotifyStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

// 回调状态文本
const getNotifyStatusText = (status) => {
  const texts = { 0: '未通知', 1: '成功', 2: '失败' }
  return texts[status] || '未通知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string' && time.includes('-')) {
    return time.replace('T', ' ').substring(0, 19)
  }
  return time
}

const handleViewOrder = (order) => {
  currentOrder.value = { ...order }
  orderDetailVisible.value = true
}

const handleConfirmOrder = (order) => {
  ElMessageBox.confirm('确认后将触发回调通知', '确定该订单已收到付款吗？', { type: 'warning' })
    .then(async () => {
      await confirmOrder(order.orderNo)
      ElMessage.success('确认成功')
      loadOrders()
    }).catch(() => {})
}

onMounted(() => {
  loadShops()
  loadChannels()
})
</script>

<style scoped>
.shop-page {
  padding: 0;
}

/* 店铺列表 */
.shop-list-wrapper {
  max-height: 240px;
  overflow-y: auto;
  padding-right: 4px;
}

.shop-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.shop-list-wrapper::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.shop-list-wrapper::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

.shop-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.load-more-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  font-size: 13px;
  color: #909399;
}

.load-more-tip.no-more {
  color: #c0c4cc;
}

.shop-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;

  &:hover {
    background: #f0f5ff;
  }

  &.active {
    background: #e6f4ff;
    border-color: #1677ff;
  }
}

.shop-info {
  flex: 1;

  .shop-name {
    font-size: 15px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }

  .shop-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #999;
  }
}

.shop-stats {
  display: flex;
  gap: 24px;
  margin-right: 24px;

  .stat-item {
    text-align: center;

    .stat-value {
      font-size: 18px;
      font-weight: 600;
      color: #333;

      &.amount {
        color: #fa8c16;
      }
    }

    .stat-label {
      font-size: 12px;
      color: #999;
    }
  }
}

.shop-actions {
  display: flex;
  gap: 8px;
}

/* Tabs */
.shop-tabs {
  margin-top: 16px;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }

  :deep(.el-tabs__content) {
    padding-top: 16px;
  }
}

/* 空状态 */
.empty-state {
  padding: 40px 0;
}

/* 二维码上传 */
.qrcode-upload-area {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.qrcode-uploader {
  width: 150px;
  height: 150px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
  position: relative;
  flex-shrink: 0;

  &:hover {
    border-color: #1677ff;
  }
}

.qrcode-preview {
  width: 150px;
  height: 150px;
  object-fit: cover;
}

.upload-placeholder {
  width: 150px;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 8px;
}

.parsing-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #1677ff;
  font-size: 13px;
}

.parse-result {
  flex: 1;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 12px;
  min-width: 200px;

  .parse-result-header {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #52c41a;
    font-weight: 500;
    margin-bottom: 10px;
  }

  .success-icon {
    color: #52c41a;
  }

  .parse-result-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 8px;
    font-size: 13px;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      color: #666;
      flex-shrink: 0;
    }

    .content {
      color: #333;
      word-break: break-all;
      max-height: 60px;
      overflow-y: auto;
    }
  }
}

.type-warning {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #e6a23c;
  font-size: 12px;
  margin-top: 4px;
}

.listen-mode-wrapper {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.listen-mode-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.header-filters {
  display: flex;
  gap: 8px;
}

/* 二维码缩略图 */
.qrcode-thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #eee;
}

.image-error {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

/* 通道选择相关 */
.no-channel-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
  color: #e6a23c;
}

.no-channel-tip a {
  color: #1677ff;
  text-decoration: none;
}

.channel-match-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
}

.channel-match-tip .success-icon {
  color: #67c23a;
}

.channel-match-tip .warning-icon {
  color: #e6a23c;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 10px;
}

.status-enabled {
  color: #059669;
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.status-disabled {
  color: #6b7280;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

/* 订单筛选区域 */
.order-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

/* 订单号样式 */
.order-no-text {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 12px;
  color: #409eff;
}

/* 回调次数 */
.notify-count {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

/* 文本灰色 */
.text-muted {
  color: #909399;
}

/* 跳转地址 */
.return-url {
  font-size: 12px;
  color: #606266;
}

</style>

<!-- 全局样式用于预览弹窗 -->
<style>
.el-image-viewer__wrapper .el-image-viewer__canvas {
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-image-viewer__wrapper .el-image-viewer__img {
  max-width: 300px !important;
  max-height: 300px !important;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}
</style>
