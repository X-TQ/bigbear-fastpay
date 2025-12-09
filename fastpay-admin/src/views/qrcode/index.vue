<template>
  <div class="qrcode-page">
    <!-- 搜索区域 -->
    <div class="page-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-select v-model="queryParams.merchantId" placeholder="所属商户" clearable style="width: 180px" @change="handleMerchantChange">
              <el-option v-for="m in merchants" :key="m.id" :label="m.merchantName" :value="m.id" />
            </el-select>
            <el-select v-model="queryParams.shopId" placeholder="所属店铺" clearable style="width: 180px">
              <el-option v-for="s in shops" :key="s.id" :label="s.shopName" :value="s.id" />
            </el-select>
            <el-select v-model="queryParams.payType" placeholder="支付类型" clearable style="width: 120px">
              <el-option label="微信" value="wxpay" />
              <el-option label="支付宝" value="alipay" />
            </el-select>
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
              新增二维码
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="page-card" style="margin-top: 16px">
      <div class="card-body">
        <el-table :data="tableData" v-loading="loading" stripe>
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
            </template>
          </el-table-column>
          <el-table-column prop="qrcodeName" label="名称" min-width="120" />
          <el-table-column prop="shopName" label="所属店铺" width="120" />
          <el-table-column label="支付类型" width="90">
            <template #default="{ row }">
              <el-tag :type="row.payType === 'wxpay' ? 'success' : 'primary'" size="small">
                {{ row.payType === 'wxpay' ? '微信' : '支付宝' }}
              </el-tag>
            </template>
          </el-table-column>
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
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
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
      :title="formData.id ? '编辑二维码' : '新增二维码'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item v-if="!formData.id" label="所属店铺" prop="shopId">
          <el-select v-model="formData.shopId" placeholder="请选择店铺" style="width: 100%">
            <el-option v-for="s in allShops" :key="s.id" :label="`${s.merchantName} - ${s.shopName}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="二维码图片" prop="qrcodeUrl">
          <div class="qrcode-upload-area">
            <el-upload
              class="qrcode-uploader"
              :show-file-list="false"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <img v-if="qrcodeImagePreview" :src="qrcodeImagePreview" class="upload-preview" />
              <div v-else class="upload-placeholder">
                <el-icon :size="32"><Plus /></el-icon>
                <span>上传二维码</span>
              </div>
              <div v-if="qrcodeParsing" class="parsing-overlay">
                <el-icon class="is-loading" :size="24"><Loading /></el-icon>
                <span>解析中...</span>
              </div>
            </el-upload>
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
        <el-form-item label="支付类型" prop="payType">
          <el-radio-group v-model="formData.payType">
            <el-radio value="wxpay">微信支付</el-radio>
            <el-radio value="alipay">支付宝</el-radio>
          </el-radio-group>
          <div v-if="qrcodeParseResult.payType && qrcodeParseResult.payType !== formData.payType" class="type-warning">
            <el-icon><Warning /></el-icon>
            <span>检测到的类型与选择不符，请确认</span>
          </div>
        </el-form-item>
        <el-form-item label="二维码名称">
          <el-input v-model="formData.qrcodeName" placeholder="请输入备注名称（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" :disabled="qrcodeParsing" @click="handleFormSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 二维码管理页面
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantList, getShopList, getQrcodePage, createQrcode, updateQrcode, updateQrcodeStatus, deleteQrcode } from '@/api'
import QRCode from 'qrcode'
import jsQR from 'jsqr'

// 商户列表
const merchants = ref([])
// 店铺列表（筛选用）
const shops = ref([])
// 所有店铺（表单用）
const allShops = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  merchantId: undefined,
  shopId: undefined,
  payType: undefined
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
  shopId: undefined,
  qrcodeName: '',
  payType: undefined,
  qrcodeUrl: ''  // 二维码解析内容
})
// 二维码图片预览
const qrcodeImagePreview = ref('')
const qrcodeParsing = ref(false)
const qrcodeParseResult = reactive({
  content: '',
  payType: '',
  payTypeName: ''
})

const formRules = {
  shopId: [{ required: true, message: '请选择所属店铺', trigger: 'change' }],
  payType: [{ required: true, message: '请选择支付类型', trigger: 'change' }],
  qrcodeUrl: [{ required: true, message: '请上传二维码图片', trigger: 'change' }]
}

// 加载商户列表
const loadMerchants = async () => {
  try {
    const res = await getMerchantList()
    merchants.value = res.data || []
  } catch (error) {
    console.error('加载商户列表失败:', error)
  }
}

// 加载所有店铺
const loadAllShops = async () => {
  try {
    const res = await getShopList()
    allShops.value = res.data || []
  } catch (error) {
    console.error('加载店铺列表失败:', error)
  }
}

// 商户变化时加载店铺
const handleMerchantChange = async (merchantId) => {
  queryParams.shopId = undefined
  if (merchantId) {
    try {
      const res = await getShopList({ merchantId })
      shops.value = res.data || []
    } catch (error) {
      console.error('加载店铺列表失败:', error)
    }
  } else {
    shops.value = []
  }
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
  if (lowerContent.includes('wxp://') || lowerContent.includes('weixin://') || lowerContent.includes('wx.tenpay.com')) {
    return { payType: 'wxpay', payTypeName: '微信支付' }
  }
  if (lowerContent.includes('alipay') || lowerContent.includes('qr.alipay.com')) {
    return { payType: 'alipay', payTypeName: '支付宝' }
  }
  return { payType: 'unknown', payTypeName: '未知类型' }
}

// 上传二维码图片
const beforeUpload = async (file) => {
  const reader = new FileReader()
  reader.onload = async (e) => {
    const base64Image = e.target.result
    qrcodeImagePreview.value = base64Image
    
    qrcodeParsing.value = true
    try {
      const content = await parseQrcodeLocal(base64Image)
      const { payType, payTypeName } = detectPayType(content)
      
      formData.qrcodeUrl = content
      Object.assign(qrcodeParseResult, { content, payType, payTypeName })
      
      if (payType !== 'unknown') {
        formData.payType = payType
      }
      ElMessage.success('二维码解析成功')
    } catch (error) {
      console.error('解析二维码失败:', error)
      ElMessage.warning('无法识别二维码内容，请确保图片清晰')
      formData.qrcodeUrl = ''
      Object.assign(qrcodeParseResult, { content: '', payType: '', payTypeName: '' })
    }
    qrcodeParsing.value = false
  }
  reader.readAsDataURL(file)
  return false
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getQrcodePage(queryParams)
    const records = res.data.records || []
    // 为每个二维码生成图片
    for (const item of records) {
      if (item.qrcodeUrl) {
        item.qrcodeImage = await generateQrcodeImage(item.qrcodeUrl)
      }
    }
    tableData.value = records
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载二维码列表失败:', error)
  }
  loading.value = false
}

// 重置查询
const resetQuery = () => {
  queryParams.merchantId = undefined
  queryParams.shopId = undefined
  queryParams.payType = undefined
  queryParams.current = 1
  shops.value = []
  loadData()
}

// 新增二维码
const handleAdd = () => {
  Object.assign(formData, {
    id: null,
    shopId: undefined,
    qrcodeName: '',
    payType: undefined,
    qrcodeUrl: ''
  })
  qrcodeImagePreview.value = ''
  Object.assign(qrcodeParseResult, { content: '', payType: '', payTypeName: '' })
  formVisible.value = true
}

// 编辑二维码
const handleEdit = async (record) => {
  Object.assign(formData, {
    id: record.id,
    shopId: record.shopId,
    qrcodeName: record.qrcodeName,
    payType: record.payType,
    qrcodeUrl: record.qrcodeUrl
  })
  // 生成预览图片
  qrcodeImagePreview.value = record.qrcodeImage || await generateQrcodeImage(record.qrcodeUrl)
  Object.assign(qrcodeParseResult, { 
    content: record.qrcodeUrl, 
    payType: record.payType, 
    payTypeName: record.payType === 'wxpay' ? '微信支付' : '支付宝' 
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
        await updateQrcode(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createQrcode(formData)
        ElMessage.success('创建成功')
      }
      formVisible.value = false
      loadData()
    } catch (error) {
      console.error('保存二维码失败:', error)
    }
    formLoading.value = false
  })
}

// 更新状态
const handleStatus = (record, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该二维码吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateQrcodeStatus(record.id, status)
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

// 删除二维码
const handleDelete = (record) => {
  ElMessageBox.confirm(
    '删除后将无法恢复',
    '确定要删除该二维码吗？',
    { type: 'warning' }
  ).then(async () => {
    await deleteQrcode(record.id)
    ElMessage.success('删除成功')
    loadData()
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
  loadAllShops()
  loadData()
})
</script>

<style scoped>
.qrcode-page {
  padding: 0;
}

.amount-text {
  color: #e6a23c;
  font-weight: 500;
}

/* 二维码缩略图 */
.qrcode-thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #eee;
}

/* 二维码上传区域 */
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

.upload-preview {
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

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.listen-mode-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
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
