<template>
  <div class="shop-page">
    <!-- 搜索区域 -->
    <div class="page-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-select v-model="queryParams.merchantId" placeholder="所属商户" clearable style="width: 180px">
              <el-option v-for="m in merchants" :key="m.id" :label="m.merchantName" :value="m.id" />
            </el-select>
            <el-input
              v-model="queryParams.shopName"
              placeholder="店铺名称"
              clearable
              style="width: 160px"
              @keyup.enter="loadData"
            />
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增店铺
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-card" style="margin-top: 16px">
      <div class="card-body">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="shopNo" label="店铺编号" width="150" />
          <el-table-column prop="shopName" label="店铺名称" min-width="150" />
          <el-table-column prop="merchantName" label="所属商户" width="120" />
          <el-table-column prop="contactName" label="联系人" width="100" />
          <el-table-column label="累计交易" width="120">
            <template #default="{ row }">
              <span class="amount-text">¥{{ row.totalAmount || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleQrcode(row)">二维码</el-button>
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                link 
                size="small" 
                @click="handleStatus(row, row.status === 1 ? 0 : 1)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadData"
            @current-change="loadData"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="formData.id ? '编辑店铺' : '新增店铺'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item v-if="!formData.id" label="所属商户" prop="merchantId">
          <el-select v-model="formData.merchantId" placeholder="请选择商户" style="width: 100%">
            <el-option v-for="m in merchants" :key="m.id" :label="m.merchantName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="formData.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="店铺描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入店铺描述" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="formData.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleFormSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 二维码管理弹窗 -->
    <el-dialog
      v-model="qrcodeVisible"
      :title="`二维码管理 - ${currentShop?.shopName || ''}`"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="qrcode-toolbar">
        <el-button type="primary" size="small" @click="handleAddQrcode">
          <el-icon><Plus /></el-icon>
          添加二维码
        </el-button>
      </div>
      <el-table :data="qrcodeList" v-loading="qrcodeLoading" stripe>
        <el-table-column label="二维码" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.qrcodeImage"
              :src="row.qrcodeImage"
              :preview-src-list="[row.qrcodeImage]"
              :preview-teleported="true"
              fit="cover"
              class="qrcode-thumbnail"
            />
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="qrcodeName" label="名称" min-width="120" />
        <el-table-column prop="payType" label="支付类型" width="100">
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
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              size="small" 
              @click="handleQrcodeStatus(row, row.status === 1 ? 0 : 1)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleQrcodeDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
                <span class="content">{{ qrcodeParseResult.content.substring(0, 50) }}...</span>
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
            <span>暂无可用通道，请先到通道管理创建</span>
          </div>
          <!-- 通道与二维码类型匹配提示 -->
          <div v-if="qrcodeParseResult.payType && qrcodeFormData.channelId" class="channel-match-tip">
            <template v-if="isChannelMatch">
              <el-icon class="success-icon"><CircleCheck /></el-icon>
              <span class="match-text">通道类型与二维码匹配</span>
            </template>
            <template v-else>
              <el-icon class="warning-icon"><Warning /></el-icon>
              <span class="mismatch-text">通道类型与二维码不匹配，请确认选择正确的通道</span>
            </template>
          </div>
        </el-form-item>
        <el-form-item label="二维码名称">
          <el-input v-model="qrcodeFormData.qrcodeName" placeholder="请输入备注名称（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="qrcodeFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="qrcodeFormLoading" :disabled="qrcodeParsing" @click="handleQrcodeFormSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 店铺管理页面
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getMerchantList, getShopPage, createShop, updateShop, updateShopStatus, deleteShop,
  getQrcodeList, createQrcode, updateQrcodeStatus, deleteQrcode,
  getChannelList
} from '@/api'
import jsQR from 'jsqr'
import QRCode from 'qrcode'

// 商户列表
const merchants = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  merchantId: undefined,
  shopName: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 表单相关
const formVisible = ref(false)
const formLoading = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: null,
  merchantId: undefined,
  shopName: '',
  description: '',
  contactName: '',
  contactPhone: ''
})

const formRules = {
  merchantId: [{ required: true, message: '请选择所属商户', trigger: 'change' }],
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

// 二维码管理相关
const qrcodeVisible = ref(false)
const qrcodeLoading = ref(false)
const qrcodeList = ref([])
const currentShop = ref(null)

const qrcodeFormVisible = ref(false)
const qrcodeFormLoading = ref(false)
const qrcodeFormRef = ref(null)
const qrcodeFormData = reactive({
  shopId: undefined,
  channelId: undefined,
  qrcodeName: '',
  payType: '',
  qrcodeUrl: ''
})
const qrcodeFormRules = {
  channelId: [{ required: true, message: '请选择通道', trigger: 'change' }],
  qrcodeUrl: [{ required: true, message: '请上传二维码图片', trigger: 'blur' }]
}

// 二维码解析相关
const qrcodeImagePreview = ref('')
const qrcodeParsing = ref(false)
const qrcodeParseResult = reactive({
  content: '',
  payType: '',
  payTypeName: ''
})

// 通道相关
const channelList = ref([])
const filteredChannels = ref([])

// 判断通道与二维码类型是否匹配
const isChannelMatch = computed(() => {
  if (!qrcodeFormData.channelId || !qrcodeParseResult.payType) return true
  const selectedChannel = channelList.value.find(c => c.id === qrcodeFormData.channelId)
  if (!selectedChannel) return true
  return selectedChannel.payType === qrcodeParseResult.payType
})

// 加载商户列表
const loadMerchants = async () => {
  try {
    const res = await getMerchantList()
    merchants.value = res.data || []
  } catch (error) {
    console.error('加载商户列表失败:', error)
  }
}

// 加载通道列表
const loadChannelList = async (merchantId) => {
  try {
    const res = await getChannelList({ merchantId })
    channelList.value = (res.data || []).filter(c => c.status === 1) // 只保留启用的通道
    // 初始化时显示所有启用的通道
    filteredChannels.value = channelList.value
  } catch (error) {
    console.error('加载通道列表失败:', error)
  }
}

// 根据支付类型过滤通道
const filterChannelsByPayType = (payType) => {
  if (!payType || payType === 'unknown') {
    filteredChannels.value = channelList.value
  } else {
    filteredChannels.value = channelList.value.filter(c => c.payType === payType)
  }
}

// 通道选择变化
const handleChannelChange = (channelId) => {
  const channel = channelList.value.find(c => c.id === channelId)
  if (channel) {
    qrcodeFormData.payType = channel.payType
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getShopPage(queryParams)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载店铺列表失败:', error)
  }
  loading.value = false
}

// 重置查询
const resetQuery = () => {
  queryParams.merchantId = undefined
  queryParams.shopName = ''
  queryParams.current = 1
  loadData()
}

// 新增店铺
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    merchantId: undefined,
    shopName: '',
    description: '',
    contactName: '',
    contactPhone: ''
  })
  formVisible.value = true
}

// 编辑店铺
const handleEdit = (record) => {
  Object.assign(formData, {
    id: record.id,
    merchantId: record.merchantId,
    shopName: record.shopName,
    description: record.description,
    contactName: record.contactName,
    contactPhone: record.contactPhone
  })
  formVisible.value = true
}

// 提交表单
const handleFormSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    formLoading.value = true
    try {
      if (formData.id) {
        await updateShop(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createShop(formData)
        ElMessage.success('创建成功')
      }
      formVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存店铺失败:', error)
    }
    formLoading.value = false
  })
}

// 更新状态
const handleStatus = (record, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该店铺吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateShopStatus(record.id, status)
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

// 删除店铺
const handleDelete = (record) => {
  ElMessageBox.confirm(
    '删除后将无法恢复',
    '确定要删除该店铺吗？',
    { type: 'warning' }
  ).then(async () => {
    await deleteShop(record.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

// ========== 二维码管理 ==========

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

// 打开二维码管理
const handleQrcode = async (shop) => {
  currentShop.value = shop
  qrcodeVisible.value = true
  await loadQrcodeList(shop.id)
}

// 加载二维码列表
const loadQrcodeList = async (shopId) => {
  qrcodeLoading.value = true
  try {
    const res = await getQrcodeList(shopId)
    const records = res.data || []
    // 为每个二维码生成图片
    for (const item of records) {
      if (item.qrcodeUrl) {
        item.qrcodeImage = await generateQrcodeImage(item.qrcodeUrl)
      }
    }
    qrcodeList.value = records
  } catch (error) {
    console.error('加载二维码列表失败:', error)
  }
  qrcodeLoading.value = false
}

// 添加二维码
const handleAddQrcode = async () => {
  Object.assign(qrcodeFormData, {
    shopId: currentShop.value?.id,
    channelId: undefined,
    qrcodeName: '',
    payType: '',
    qrcodeUrl: ''
  })
  // 重置图片预览和解析结果
  qrcodeImagePreview.value = ''
  Object.assign(qrcodeParseResult, { content: '', payType: '', payTypeName: '' })
  // 加载该店铺所属商户的通道列表
  if (currentShop.value?.merchantId) {
    await loadChannelList(currentShop.value.merchantId)
  }
  qrcodeFormVisible.value = true
}

// 本地解析二维码图片
const parseQrcodeLocal = (imageData) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      canvas.width = img.width
      canvas.height = img.height
      ctx.drawImage(img, 0, 0)
      
      const imgData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const code = jsQR(imgData.data, imgData.width, imgData.height)
      
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
  
  if (lowerContent.includes('wxp://') || 
      lowerContent.includes('weixin://') || 
      lowerContent.includes('wx.tenpay.com')) {
    return { payType: 'wxpay', payTypeName: '微信支付' }
  }
  
  if (lowerContent.includes('alipay') || 
      lowerContent.includes('qr.alipay.com')) {
    return { payType: 'alipay', payTypeName: '支付宝' }
  }
  
  return { payType: 'unknown', payTypeName: '未知类型' }
}

// 上传前处理
const beforeUpload = async (file) => {
  qrcodeParsing.value = true
  
  try {
    // 读取文件为 DataURL
    const reader = new FileReader()
    const imageData = await new Promise((resolve, reject) => {
      reader.onload = (e) => resolve(e.target.result)
      reader.onerror = reject
      reader.readAsDataURL(file)
    })
    
    // 显示预览
    qrcodeImagePreview.value = imageData
    
    // 解析二维码
    const content = await parseQrcodeLocal(imageData)
    const { payType, payTypeName } = detectPayType(content)
    
    // 更新解析结果
    Object.assign(qrcodeParseResult, { content, payType, payTypeName })
    qrcodeFormData.qrcodeUrl = content
    qrcodeFormData.payType = payType !== 'unknown' ? payType : ''
    
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
    } else {
      // 未识别类型，显示所有通道
      filteredChannels.value = channelList.value
    }
    
    ElMessage.success('二维码解析成功')
  } catch (error) {
    console.error('解析二维码失败:', error)
    ElMessage.error('二维码解析失败，请确保图片清晰')
    qrcodeImagePreview.value = ''
  }
  
  qrcodeParsing.value = false
  return false // 阻止自动上传
}

// 提交二维码表单
const handleQrcodeFormSubmit = async () => {
  if (!qrcodeFormRef.value) return
  
  await qrcodeFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    qrcodeFormLoading.value = true
    try {
      await createQrcode(qrcodeFormData)
      ElMessage.success('添加成功')
      qrcodeFormVisible.value = false
      loadQrcodeList(currentShop.value.id)
    } catch (error) {
      console.error('添加二维码失败:', error)
    }
    qrcodeFormLoading.value = false
  })
}

// 更新二维码状态
const handleQrcodeStatus = (record, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该二维码吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateQrcodeStatus(record.id, status)
    ElMessage.success('操作成功')
    loadQrcodeList(currentShop.value.id)
  }).catch(() => {})
}

// 删除二维码
const handleQrcodeDelete = (record) => {
  ElMessageBox.confirm(
    '删除后将无法恢复',
    '确定要删除该二维码吗？',
    { type: 'warning' }
  ).then(async () => {
    await deleteQrcode(record.id)
    ElMessage.success('删除成功')
    loadQrcodeList(currentShop.value.id)
  }).catch(() => {})
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string' && time.includes('-')) {
    return time.replace('T', ' ').substring(0, 19)
  }
  return time
}

onMounted(() => {
  loadMerchants()
  loadData()
})
</script>

<style scoped>
.shop-page {
  padding: 0;
}

.amount-text {
  color: #e6a23c;
  font-weight: 500;
}

.qrcode-toolbar {
  margin-bottom: 16px;
}

.qrcode-thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  cursor: pointer;
}

.no-image {
  color: #999;
}

/* 二维码上传区域 */
.qrcode-upload-area {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.qrcode-uploader {
  flex-shrink: 0;

  :deep(.el-upload) {
    width: 120px;
    height: 120px;
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: #409eff;
    }
  }
}

.qrcode-preview {
  width: 116px;
  height: 116px;
  object-fit: contain;
  display: block;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  gap: 8px;

  span {
    font-size: 12px;
  }
}

.parsing-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #409eff;

  span {
    font-size: 12px;
  }
}

.parse-result {
  flex: 1;
  min-width: 0;
  padding: 12px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 8px;
  font-size: 13px;
}

.parse-result-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
  color: #67c23a;
  font-weight: 500;
}

.success-icon {
  color: #67c23a;
}

.parse-result-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  line-height: 1.5;

  &:last-child {
    margin-bottom: 0;
  }

  .label {
    color: #909399;
    flex-shrink: 0;
    margin-right: 4px;
  }

  .content {
    color: #606266;
    word-break: break-all;
    font-size: 12px;
  }
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.no-channel-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
  color: #e6a23c;
}

.channel-match-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;

  .success-icon {
    color: #67c23a;
  }

  .warning-icon {
    color: #e6a23c;
  }

  .match-text {
    color: #67c23a;
  }

  .mismatch-text {
    color: #e6a23c;
  }
}

.listen-mode-wrapper {
  width: 100%;
}

.listen-mode-tip {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
</style>
