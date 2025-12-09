<template>
  <div class="qrcode-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">二维码管理</h1>
      <p class="page-desc">管理您的收款二维码</p>
    </div>

    <!-- 操作栏 -->
    <div class="dev-card">
      <div class="card-body">
        <div class="table-toolbar">
          <div class="toolbar-left">
            <el-select v-model="queryParams.shopId" placeholder="选择店铺" clearable style="width: 180px" @change="loadData">
              <el-option v-for="shop in shops" :key="shop.id" :label="shop.shopName" :value="shop.id" />
            </el-select>
            <el-select v-model="queryParams.payType" placeholder="支付类型" clearable style="width: 120px" @change="loadData">
              <el-option label="微信支付" value="wxpay" />
              <el-option label="支付宝" value="alipay" />
            </el-select>
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加二维码
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 二维码列表 -->
    <div class="dev-card">
      <div class="card-body">
        <el-table :data="qrcodeList" v-loading="loading" stripe>
          <el-table-column label="二维码" width="80">
            <template #default="{ row }">
              <el-image
                v-if="row.qrcodeImage"
                :src="row.qrcodeImage"
                :preview-src-list="[row.qrcodeImage]"
                fit="cover"
                style="width: 50px; height: 50px; border-radius: 4px;"
              />
              <div v-else class="qr-placeholder">
                <el-icon><Grid /></el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="qrcodeName" label="名称" min-width="120">
            <template #default="{ row }">
              {{ row.qrcodeName || '收款二维码' }}
            </template>
          </el-table-column>
          <el-table-column prop="shopName" label="所属店铺" width="120" />
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
              <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
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
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next"
            @size-change="loadData"
            @current-change="loadData"
          />
        </div>
      </div>
    </div>

    <!-- 添加二维码弹窗 -->
    <el-dialog
      v-model="showForm"
      title="添加收款二维码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="所属店铺" prop="shopId">
          <el-select v-model="formData.shopId" placeholder="请选择店铺" style="width: 100%">
            <el-option v-for="shop in shops" :key="shop.id" :label="shop.shopName" :value="shop.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付类型" prop="payType">
          <el-radio-group v-model="formData.payType">
            <el-radio value="wxpay">微信支付</el-radio>
            <el-radio value="alipay">支付宝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="二维码名称" prop="qrcodeName">
          <el-input v-model="formData.qrcodeName" placeholder="请输入备注名称（可选）" />
        </el-form-item>
        <el-form-item label="二维码图片" prop="qrcodeImage">
          <el-upload
            class="qrcode-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <img v-if="formData.qrcodeImage" :src="formData.qrcodeImage" class="qrcode-preview" />
            <div v-else class="upload-placeholder">
              <el-icon :size="32"><Plus /></el-icon>
              <span>上传二维码</span>
            </div>
          </el-upload>
          <div class="upload-tip">支持 JPG、PNG 格式，建议尺寸 300x300</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看二维码弹窗 -->
    <el-dialog v-model="showView" title="查看二维码" width="400px">
      <div class="qrcode-view">
        <img v-if="currentQrcode.qrcodeImage" :src="currentQrcode.qrcodeImage" class="qrcode-image" />
        <div class="qrcode-info">
          <p class="qrcode-name">{{ currentQrcode.qrcodeName || '收款二维码' }}</p>
          <el-tag :type="currentQrcode.payType === 'wxpay' ? 'success' : 'primary'">
            {{ currentQrcode.payType === 'wxpay' ? '微信支付' : '支付宝' }}
          </el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Fast 易支付 - 二维码管理
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getShopList, getQrcodePage, addQrcode, updateQrcodeStatus, deleteQrcode } from '@/api'
import QRCode from 'qrcode'

const shops = ref([])

const queryParams = reactive({
  current: 1,
  size: 10,
  shopId: undefined,
  payType: undefined
})

const qrcodeList = ref([])
const loading = ref(false)
const total = ref(0)

const showForm = ref(false)
const showView = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const currentQrcode = ref({})

const formData = reactive({
  shopId: undefined,
  payType: 'wxpay',
  qrcodeName: '',
  qrcodeImage: ''
})

const formRules = {
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  payType: [{ required: true, message: '请选择支付类型', trigger: 'change' }],
  qrcodeImage: [{ required: true, message: '请上传二维码图片', trigger: 'change' }]
}

const loadShops = async () => {
  try {
    const res = await getShopList()
    shops.value = res.data || []
  } catch (error) {
    console.error('加载店铺失败:', error)
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
    qrcodeList.value = records
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载二维码列表失败:', error)
  }
  loading.value = false
}

const handleAdd = () => {
  Object.assign(formData, { shopId: undefined, payType: 'wxpay', qrcodeName: '', qrcodeImage: '' })
  showForm.value = true
}

const beforeUpload = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.qrcodeImage = e.target.result
  }
  reader.readAsDataURL(file)
  return false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await addQrcode(formData)
      ElMessage.success('添加成功')
      showForm.value = false
      loadData()
    } catch (error) {
      console.error('添加失败:', error)
    }
    submitting.value = false
  })
}

const handleView = async (qrcode) => {
  const qrcodeData = { ...qrcode }
  // 确保查看时有图片
  if (!qrcodeData.qrcodeImage && qrcodeData.qrcodeUrl) {
    qrcodeData.qrcodeImage = await generateQrcodeImage(qrcodeData.qrcodeUrl)
  }
  currentQrcode.value = qrcodeData
  showView.value = true
}

const handleStatus = (qrcode, status) => {
  ElMessageBox.confirm(
    `确定要${status === 1 ? '启用' : '禁用'}该二维码吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    await updateQrcodeStatus(qrcode.id, status)
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

const handleDelete = (qrcode) => {
  ElMessageBox.confirm(
    '删除后将无法恢复',
    '确定要删除该二维码吗？',
    { type: 'warning' }
  ).then(async () => {
    await deleteQrcode(qrcode.id)
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
  loadShops()
  loadData()
})
</script>

<style scoped>
.qrcode-page {
  padding: 0;
}

.qr-placeholder {
  width: 50px;
  height: 50px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.qrcode-uploader {
  width: 150px;
  height: 150px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.qrcode-uploader:hover {
  border-color: #1677ff;
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

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.qrcode-view {
  text-align: center;
}

.qrcode-image {
  max-width: 250px;
  max-height: 250px;
  border-radius: 8px;
}

.qrcode-info {
  margin-top: 16px;
}

.qrcode-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}
</style>
