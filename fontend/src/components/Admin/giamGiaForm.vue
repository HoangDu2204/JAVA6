<template>
  <div class="container">
    <h5 class="page-title mb-3">{{ isEdit ? 'Chỉnh sửa giảm giá' : 'Thêm giảm giá' }}</h5>
    <div class="card">
      <div class="card-body p-4">
        <form @submit.prevent="submitForm" class="add-discount-form">
          <div class="mb-3">
            <label class="form-label">Tên chương trình</label>
            <input v-model="form.name" type="text" class="form-control" placeholder="Nhập mã giảm giá" :class="{ 'is-invalid': errors.name }" />
            <div class="invalid-feedback">{{ errors.name }}</div>
          </div>
          <div class="mb-3">
            <label class="form-label">Phần trăm giảm(%)</label>

            <input v-model.number="form.percentage" type="number" class="form-control" min="0" max="100" :class="{ 'is-invalid': errors.percentage }" />
            <div class="invalid-feedback">{{ errors.percentage }}</div>
          </div>
          <div class="mb-3">
            <label class="form-label">Ngày bắt đầu</label>
            <input v-model="form.startDate" type="date" class="form-control" :class="{ 'is-invalid': errors.startDate }" />
            <div class="invalid-feedback">{{ errors.startDate }}</div>
          </div>
          <div class="mb-3">
            <label class="form-label">Ngày kết thúc</label>
            <input v-model="form.endDate" type="date" class="form-control"  :class="{ 'is-invalid': errors.endDate }" />
            <div class="invalid-feedback">{{ errors.endDate }}</div>
          </div>


<!-- <div class="mb-3">
  <label class="form-label">Chọn sản phẩm áp dụng</label>
  <select v-model="selectedProductIds" class="form-select" multiple required>
    <option v-for="product in products" :key="product.id" :value="product.id">
      {{ product.name }}
    </option>
  </select>
  <div class="form-text">Giữ Ctrl (hoặc Cmd trên macOS) để chọn nhiều sản phẩm.</div>
</div> -->




          <div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-success me-2">
              <i class="bi bi-check-lg me-1"></i> Lưu
            </button>
            <RouterLink to="/quanLyGiamGia" class="btn btn-danger">
              <i class="bi bi-x-lg me-1"></i> Hủy
            </RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/axios' // ✅ dùng api thay vì axios thuần
import Swal from 'sweetalert2'

const router = useRouter()
const route = useRoute()

const form = ref({
  name: '',
  percentage: 0,
  startDate: '',
  endDate: ''
})

const products = ref([])
const selectedProductIds = ref([])

const isEdit = ref(false)
const id = route.query.id

const errors = ref({
  name: '',
  percentage: '',
  startDate: '',
  endDate: '',
  products: ''
})

const validateForm = () => {
  let isValid = true
  errors.value = { name: '', percentage: '', startDate: '', endDate: '', products: '' }

  const trimmedName = form.value.name.trim()
  if (!trimmedName) {
    errors.value.name = 'Tên chương trình không được để trống!'
    isValid = false
  } else if (trimmedName.split(/\s+/).length > 250) {
    errors.value.name = 'Tên không được vượt quá 250 từ!'
    isValid = false
  }

  if (form.value.percentage < 5 || form.value.percentage > 50) {
    errors.value.percentage = 'Phần trăm giảm phải từ 5% đến 50%!'
    isValid = false
  }

  const startDate = new Date(form.value.startDate)
  const endDate = new Date(form.value.endDate)

  if (!form.value.startDate) {
    errors.value.startDate = 'Vui lòng chọn ngày bắt đầu!'
    isValid = false
  }

  if (!form.value.endDate) {
    errors.value.endDate = 'Vui lòng chọn ngày kết thúc!'
    isValid = false
  } else if (endDate < startDate) {
    errors.value.endDate = 'Ngày kết thúc phải sau ngày bắt đầu!'
    isValid = false
  }

  // Bỏ comment nếu cần kiểm tra chọn sản phẩm
  // if (selectedProductIds.value.length === 0) {
  //   errors.value.products = 'Vui lòng chọn ít nhất 1 sản phẩm!'
  //   isValid = false
  // }

  return isValid
}

const fetchProducts = async () => {
  try {
    const res = await api.get('/api/admin/products')
    if (res.data?.data?.list && Array.isArray(res.data.data.list)) {
      products.value = res.data.data.list
    } else {
      products.value = []
      console.warn('⚠️ Dữ liệu không đúng định dạng:', res.data)
    }
  } catch (err) {
    console.error('❌ Lỗi khi tải danh sách sản phẩm:', err)
  }
}

const submitForm = async () => {
  if (!validateForm()) return

  try {
    const start = new Date(form.value.startDate)
    const end = new Date(form.value.endDate)

    if (start > end) {
      await Swal.fire('Lỗi', '❌ Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!', 'error')
      return
    }

    const payload = {
      name: form.value.name,
      percentage: form.value.percentage,
      startDate: start.toISOString(),
      endDate: end.toISOString(),
      isActive: true,
      productIds: selectedProductIds.value
    }

    if (isEdit.value) {
      await api.put(`/api/quanLyGiamGia/${id}`, payload)
      await Swal.fire('Thành công', '✅ Cập nhật chương trình thành công!', 'success')
    } else {
      await api.post('/api/quanLyGiamGia', payload)
      await Swal.fire('Thành công', '✅ Thêm mới chương trình thành công!', 'success')
    }

    router.push('/quanLyGiamGia')
  } catch (error) {
    console.error('❌ Lỗi khi lưu:', error)
    await Swal.fire('Lỗi', 'Lưu thất bại. Vui lòng kiểm tra lại.', 'error')
  }
}

onMounted(async () => {
  await fetchProducts()

  if (id) {
    isEdit.value = true
    try {
      const res = await api.get(`/api/quanLyGiamGia/${id}`)
      const data = res.data

      form.value = {
        name: data.name || '',
        percentage: data.percentage || 0,
        startDate: data.startDate?.slice(0, 10) || '',
        endDate: data.endDate?.slice(0, 10) || ''
      }

      selectedProductIds.value = data.products?.map(p => p.id) || []
    } catch (error) {
      console.error('❌ Không thể tải dữ liệu chương trình:', error)
      await Swal.fire('Lỗi', 'Không thể tải dữ liệu chương trình khuyến mãi.', 'error')
    }
  }
})
</script>




<style scoped>
.page-header {
  padding: 1rem 1.5rem;
}

.btn-icon {
  background: none;
  border: none;
  color: #495057;
  font-size: 1.1rem;
  padding: 0.25rem 0.5rem;
}

.btn-icon:hover {
  color: #212529;
}

.header-right-icons .badge {
  font-size: 0.65em;
  padding: 0.25em 0.4em;
  background-color: #17a2b8 !important;
}

.page-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: #343a40;
}

.breadcrumb {
  font-size: 0.85rem;
}

.breadcrumb-item a {
  color: #007bff;
  text-decoration: none;
}

.breadcrumb-item a:hover {
  text-decoration: underline;
}

.breadcrumb-item.active {
  color: #6c757d;
}

.container {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.card-header {
  padding: 1rem 1.25rem;
  min-height: 48px;
}

.card-header h5 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #343a40;
}

.card-body {
  padding: 1.5rem;
}

.btn-sm {
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
}

.text-muted {
  color: #6c757d !important;
}

.ms-2 {
  margin-left: 0.5rem !important;
}

.me-1 {
  margin-right: 0.25rem !important;
}

.me-2 {
  margin-right: 0.5rem !important;
}

.p-4 {
  padding: 1.5rem !important;
}

.btn i {
  vertical-align: middle;
  line-height: 1;
}

.d-flex { display: flex !important; }
.justify-content-between { justify-content: space-between !important; }
.align-items-center { align-items: center !important; }
.mb-4 { margin-bottom: 1.5rem !important; }
.mb-3 { margin-bottom: 1rem !important; }
.mb-1 { margin-bottom: 0.25rem !important; }
.p-0 { padding: 0 !important; }
.m-0 { margin: 0 !important; }
.bg-transparent { background-color: transparent !important; }
.position-relative { position: relative !important; }
.position-absolute { position: absolute !important; }
.top-0 { top: 0 !important; }
.start-100 { left: 100% !important; }
.translate-middle { transform: translate(-50%, -50%) !important; }
.badge { display: inline-block; padding: .35em .65em; font-size: .75em; font-weight: 700; line-height: 1; color: #fff; text-align: center; white-space: nowrap; vertical-align: baseline; border-radius: .25rem; }
.rounded-pill { border-radius: 50rem !important; }
.bg-info { background-color: #17a2b8 !important; }
.visually-hidden { position: absolute !important; width: 1px !important; height: 1px !important; padding: 0 !important; margin: -1px !important; overflow: hidden !important; clip: rect(0, 0, 0, 0) !important; white-space: nowrap !important; border: 0 !important; }

.add-discount-form {
  background-color: #ffffff;
}

.add-discount-form .form-label {
  font-weight: 500;
  color: #495057;
}

.add-discount-form .form-control {
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
}

.add-discount-form .form-control:focus {
  box-shadow: none;
  border-color: #007bff;
}

.settings-dropdown-wrapper:hover .settings-dropdown {
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
}
</style>
