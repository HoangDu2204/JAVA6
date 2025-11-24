<template>
  <div class="container  py-5">
    <div class="store-card">
      <div class="store-card-header">
        <i class="bi bi-shop me-2"></i> Quản lý thông tin cửa hàng
      </div>
      <div v-if="isLoading" class="store-card-body text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="mt-3 text-muted">Đang tải thông tin cửa hàng...</p>
      </div>
      <div v-else class="store-card-body">
        <form @submit.prevent="onSubmit">
          <div class="row g-4">
            <div class="col-md-6">
              <label class="form-label">Tên cửa hàng <span class="text-danger">*</span></label>
              <input v-model="form.storeName" required class="form-control" placeholder="Nhập tên cửa hàng" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input v-model="form.email" class="form-control" placeholder="Email liên hệ" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Số điện thoại</label>
              <input
                v-model="form.phone"
                class="form-control"
                :class="{ 'is-invalid': phoneError }"
                placeholder="Số điện thoại"
                @blur="validatePhone"
              />
              <div v-if="phoneError" class="invalid-feedback d-block">{{ phoneError }}</div>
            </div>
            <div class="col-md-6">
              <label class="form-label">Địa chỉ</label>
              <input v-model="form.address" class="form-control" placeholder="Địa chỉ cửa hàng" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Logo URL</label>
              <input v-model="form.logoUrl" class="form-control" placeholder="Link ảnh logo" />
              <div v-if="form.logoUrl" class="logo-preview mt-2">
                <img :src="form.logoUrl" alt="Logo" @error="handleImageError" />
              </div>
              <small class="text-muted">Nhập URL ảnh logo (hỗ trợ: jpg, png, gif, webp)</small>
            </div>
            <div class="col-md-6">
              <label class="form-label">Facebook</label>
              <input v-model="form.facebookUrl" class="form-control" placeholder="Link Facebook" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Zalo</label>
              <input v-model="form.zaloUrl" class="form-control" placeholder="Link Zalo" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Instagram</label>
              <input v-model="form.instagramUrl" class="form-control" placeholder="Link Instagram" />
            </div>
            <div class="col-12">
              <label class="form-label">Mô tả</label>
              <textarea v-model="form.description" class="form-control" rows="2" placeholder="Mô tả về cửa hàng" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Giờ mở cửa</label>
              <input v-model="form.openTime" class="form-control" placeholder="VD: 07:00" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Giờ đóng cửa</label>
              <input v-model="form.closeTime" class="form-control" placeholder="VD: 21:00" />
            </div>
            <div class="col-md-6 d-flex align-items-center">
              <label class="form-label me-2 mb-0">Hoạt động:</label>
              <input type="checkbox" v-model="form.isActive" class="form-check-input" />
              <span class="ms-2">{{ form.isActive ? 'Đang hoạt động' : 'Tạm dừng' }}</span>
            </div>
          </div>
          <div class="mt-4 text-end">
            <button type="submit" class="btn btn-warning" :disabled="isSubmitting">
              <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status"></span>
              {{ isSubmitting ? 'Đang xử lý...' : 'Cập nhật' }}
            </button>
          </div>
        </form>
        <div v-if="successMessage" class="alert alert-success mt-3">
          <i class="bi bi-check-circle me-2"></i>{{ successMessage }}
        </div>
        <div v-if="errorMessage" class="alert alert-danger mt-3">
          <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/axios' // ✅ Dùng axios đã cấu hình JWT

const form = ref({
  storeName: '',
  email: '',
  phone: '',
  address: '',
  logoUrl: '',
  facebookUrl: '',
  zaloUrl: '',
  instagramUrl: '',
  description: '',
  openTime: '',
  closeTime: '',
  isActive: true
})
const successMessage = ref('')
const errorMessage = ref('')
const logoPreview = ref('')
const isLoading = ref(false)
const isSubmitting = ref(false)
const phoneError = ref('')

// Lấy thông tin cửa hàng
const fetchStoreInfo = async () => {
  isLoading.value = true
  try {
    const res = await api.get('/api/store-info')
    if (res.data) {
      form.value = { ...form.value, ...res.data }
      logoPreview.value = form.value.logoUrl || ''
    }
  } catch (err) {
    console.error('Fetch store info error:', err)
    errorMessage.value = 'Không thể tải thông tin cửa hàng: ' + (err.response?.data?.message || err.message)
    setTimeout(() => (errorMessage.value = ''), 5000)
  } finally {
    isLoading.value = false
  }
}

const onLogoFileChange = (e) => {

  const file = e.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (ev) => {
      logoPreview.value = ev.target.result
      form.value.logoUrl = ev.target.result
    }
    reader.readAsDataURL(file)
  }
}

const onSubmit = async () => {
  // Validate form
  if (!form.value.storeName?.trim()) {
    errorMessage.value = 'Tên cửa hàng không được để trống'
    setTimeout(() => (errorMessage.value = ''), 3000)
    return
  }

  // Validate email format
  if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    errorMessage.value = 'Email không đúng định dạng'
    setTimeout(() => (errorMessage.value = ''), 3000)
    return
  }

  // Validate phone format
  validatePhone()
  if (phoneError.value) {
    return
  }

  // Validate URL formats
  const urlFields = ['logoUrl', 'facebookUrl', 'zaloUrl', 'instagramUrl']
  for (const field of urlFields) {
    if (form.value[field] && !isValidUrl(form.value[field])) {
      errorMessage.value = `URL ${field.replace('Url', '')} không đúng định dạng`
      setTimeout(() => (errorMessage.value = ''), 3000)
      return
    }
  }

  try {
    isSubmitting.value = true
    successMessage.value = ''
    errorMessage.value = ''

    if (form.value.id) {
      await api.put('/api/store-info', form.value)
      successMessage.value = 'Cập nhật thông tin cửa hàng thành công!'
    } else {
      await api.post('/api/store-info', form.value)
      successMessage.value = 'Tạo thông tin cửa hàng thành công!'
    }

    // Refresh data
    await fetchStoreInfo()

    setTimeout(() => (successMessage.value = ''), 3000)
  } catch (err) {
    console.error('Submit error:', err)
    errorMessage.value = 'Lỗi khi cập nhật: ' + (err.response?.data?.message || err.message)
    successMessage.value = ''

    setTimeout(() => (errorMessage.value = ''), 5000)
  } finally {
    isSubmitting.value = false
  }
}

// Helper function to validate URL
const isValidUrl = (string) => {
  try {
    new URL(string)
    return true
  } catch (_) {
    return false
  }
}

// Handle image error
const handleImageError = (event) => {
  event.target.style.display = 'none'
  errorMessage.value = 'Không thể tải ảnh logo. Vui lòng kiểm tra lại URL.'
  setTimeout(() => (errorMessage.value = ''), 3000)
}

// Phone validation helpers
const phoneRegexList = [
  /^0\d{9}$/,      // 10 số bắt đầu bằng 0 (VD: 0987654321)
  /^\+84\d{9}$/   // +84 và 9 số tiếp theo (VD: +84987654321)
]

const validatePhone = () => {
  const value = (form.value.phone || '').trim()
  if (!value) {
    phoneError.value = ''
    return
  }
  const isValid = phoneRegexList.some(r => r.test(value))
  phoneError.value = isValid ? '' : 'Số điện thoại không hợp lệ. VD: 0987654321 hoặc +84987654321'
}

watch(() => form.value.phone, () => {
  if (phoneError.value) validatePhone()
})

onMounted(fetchStoreInfo)
</script>

<style scoped>
.container-admin-store {
  background: #f8f9fa;
  min-height: 100vh;
}

.store-card-header {
  background: linear-gradient(90deg, #3a8ef9 0%, #5ee7df 100%);
  color: #fff;
  font-size: 1.5rem;
  font-weight: 700;
  padding: 1.25rem 1.5rem;
  display: flex;
  align-items: center;
  letter-spacing: 0.5px;
}

.form-label {
  font-weight: 500;
  font-size: 1rem;
  margin-bottom: 4px;
  color: #495057;
}
.form-control, .form-check-input {
  font-size: 1rem;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #ccc;
  transition: border-color 0.3s, box-shadow 0.3s;
}
.form-control:focus, .form-check-input:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58,142,249,0.3);
}
textarea.form-control {
  min-height: 60px;
}


.logo-preview img {
  background: #fff;
  border: 1px solid #eee;
  padding: 4px;
  border-radius: 8px;
  max-height: 60px;
  max-width: 120px;
  box-shadow: 0 1px 4px #ccc;
}
@media (max-width: 900px) {
  .store-card { max-width: 98vw; }
  .store-card-body { padding: 1.2rem; }
  .store-card-header { font-size: 1.2rem; padding: 1rem; }
}
@media (max-width: 576px) {
  .store-card-body { padding: 1rem; }
  .form-label { font-size: 0.9rem; }
  .form-control { font-size: 0.9rem; padding: 8px 12px; }
  .btn-gradient { font-size: 1rem; padding: 8px 24px; }
}
</style>
