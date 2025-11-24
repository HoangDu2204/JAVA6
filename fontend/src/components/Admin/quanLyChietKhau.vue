<template>
  <div class="container mt-4">
    <h2 class="mb-4">Quản lý chiết khấu đại lý #{{ agentId }}</h2>

    <!-- BẢNG CHIẾT KHẤU -->
      <div class="d-flex justify-content-between mb-3">
        <div class="d-flex">
      <div class="form-seach">
       <div class="input-group">
       <input
  v-model="searchTerm"
  @input="handleSearchInput"
  type="text"
  class="form-control"
  placeholder="Tìm kiếm theo mô tả chiết khấu..."
/>
<button @click="handleSearch" class="btn btn-gradient-primary" type="button">
  <i class="bi bi-search"></i>
</button>
<button @click="clearSearch" class="btn btn-secondary" type="button" v-if="searchTerm">
  Xóa
</button>

        </div>
      </div>
      <!-- ✅ THÊM LỚP `ms-2` VÀO ĐÂY ĐỂ TẠO KHOẢNG CÁCH -->
      <div class="form-fillter ms-2">
        <div class="input-group">
          <select  class="form-select">
            <option value="">Mặc định</option>
            <option value="">Đang hoạt động</option>
            <option value="">Ngừng hoạt động</option>
          </select>
          <button class="btn btn-gradient-primary"><i class="bi bi-funnel"></i></button>
        </div>
</div>
      </div>

      <button class="btn btn-info mt-3 mb-2" @click="openModal">
      Thêm chiết khấu
    </button>

            </div>

    <table class="table table-bordered table-striped">
      <thead>
        <tr class="text-center">
          <th>STT</th>
          <th>Đại lý</th>
          <th>Phần trăm (%)</th>
          <th>Ngày bắt đầu</th>
          <th>Ngày kết thúc</th>
          <th>Mô tả</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr class="text-center" v-for="(discount,index) in discounts" :key="discount.id">
           <td>{{ index + 1 + currentPage * pageSize }}</td>
          <td>{{ discount.agentName }}</td>
          <td>{{ discount.discountPercentage }}%</td>
          <td>{{ formatDate(discount.startDate) }}</td>
          <td>{{ formatDate(discount.endDate) }}</td>
          <td style="white-space: pre-wrap;">{{ discount.description }}</td>
          <td>

            <span class="badge" :class="isCurrentlyActive(discount) ? 'bg-success' : 'bg-secondary'">
              {{ isCurrentlyActive(discount) ? 'Đang áp dụng' : 'Đã ngừng' }}
            </span>

          </td>
          <td>

            <button class="btn btn-sm" :class="isCurrentlyActive(discount) ? 'btn-danger' : 'btn-success'"
              :disabled="!canToggleStatus(discount)" @click="toggleStatus(discount.id, !discount.isActive)">
              {{ isCurrentlyActive(discount) ? 'Ngừng áp dụng' : 'Áp dụng lại' }}
            </button>

          </td>
        </tr>
      </tbody>
    </table>
              <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <a class="page-link" @click="handlePageChange(currentPage - 1)">Trước</a>
        </li>

        <li
          v-for="(page, index) in displayedPages"
          :key="index"
          class="page-item"
          :class="{
            active: page === currentPage + 1,
            disabled: page === '...',
          }"
        >
          <a
            class="page-link"
            @click="typeof page === 'number' ? handlePageChange(page - 1) : null"
          >
            {{ page }}
          </a>
        </li>

        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <a class="page-link" @click="handlePageChange(currentPage + 1)">Sau</a>
        </li>
      </ul>
    </nav>

 <div class="btn btn-warning mt-3 mb-2 float-end">
        <router-link to="/quanlyDaiLy"  >
           Quay lại
        </router-link>
      </div>

    <!-- MODAL THÊM CHIẾT KHẤU - THAY THẾ BẰNG VUE MODAL -->
    <div v-if="showAddModal" class="custom-modal-overlay" @click="closeModal">
      <div class="custom-modal-content" @click.stop>
        <div class="custom-modal-header">
          <h5 class="custom-modal-title">Thêm chiết khấu</h5>
          <button type="button" class="custom-modal-close" @click="closeModal">&times;</button>
        </div>
        <form @submit.prevent="createDiscount">
          <div class="custom-modal-body">
            <div class="row g-3">
              <!-- Ẩn agentId -->
              <input v-model="form.agentId" type="hidden" />

              <!-- Phần trăm -->
              <div class="col-md-6">
                <label class="form-label">Phần trăm (%)</label>
                <input v-model.number="form.discountPercentage" type="number" class="form-control" />
                <div v-if="errors.discountPercentage" class="text-danger mt-1">
                  {{ errors.discountPercentage }}
                </div>
              </div>

              <!-- Ngày bắt đầu -->
              <div class="col-md-6">
                <label class="form-label">Ngày bắt đầu</label>
                <input v-model="form.startDate" type="date" class="form-control" />
                <div v-if="errors.startDate" class="text-danger mt-1">
                  {{ errors.startDate }}
                </div>
              </div>

              <!-- Ngày kết thúc -->
              <div class="col-md-6">
                <label class="form-label">Ngày kết thúc</label>
                <input v-model="form.endDate" type="date" class="form-control" />
                <div v-if="errors.endDate" class="text-danger mt-1">
                  {{ errors.endDate }}
                </div>
              </div>

              <!-- Mô tả -->
              <div class="col-12">
                <label class="form-label">Mô tả</label>
                <textarea v-model="form.description" class="form-control" rows="3" maxlength="200" />
                <div v-if="errors.description" class="text-danger mt-1">
                  {{ errors.description }}
                </div>
              </div>
            </div>
          </div>
          <div class="custom-modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Huỷ</button>
            <button type="submit" class="btn btn-primary">Lưu</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

const discounts = ref([])
const route = useRoute()
const agentId = route.params.agentId
const currentPage = ref(0)      // trang hiện tại, backend tính từ 0
const pageSize = ref(10)        // số item 1 trang
const totalPages = ref(1)       // tổng số trang trả về từ backend

// Thêm biến để kiểm soát modal
const showAddModal = ref(false)

const form = ref({
  agentId: agentId,
  discountPercentage: '',
  startDate: '',
  endDate: '',
  description: ''
})

const errors = ref({
  discountPercentage: '',
  startDate: '',
  endDate: '',
  description: ''
})

// Hàm đóng modal
const closeModal = () => {
  showAddModal.value = false
  // Reset form và errors
  form.value = {
    agentId: agentId,
    discountPercentage: '',
    startDate: '',
    endDate: '',
    description: ''
  }
  errors.value = {
    discountPercentage: '',
    startDate: '',
    endDate: '',
    description: ''
  }
}

// Hàm mở modal
const openModal = () => {
  showAddModal.value = true
  // Reset form khi mở modal
  form.value = {
    agentId: agentId,
    discountPercentage: '',
    startDate: '',
    endDate: '',
    description: ''
  }
  errors.value = {
    discountPercentage: '',
    startDate: '',
    endDate: '',
    description: ''
  }
}

const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value + 1 // hiện tại là 1-based

  if (total <= 7) {
    // Nếu tổng số trang <= 7 thì hiển thị hết
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // Trường hợp nhiều hơn 7 trang
    if (current <= 4) {
      pages.push(1, 2, 3, 4, 5, '...', total)
    } else if (current >= total - 3) {
      pages.push(1, '...', total - 4, total - 3, total - 2, total - 1, total)
    } else {
      pages.push(1, '...', current - 1, current, current + 1, '...', total)
    }
  }

  return pages
})
// Thêm biến tìm kiếm
const searchTerm = ref('')
let searchTimeout = null

// ✅ Hàm kiểm tra discount hiện tại có đang áp dụng không
const isCurrentlyActive = (discount) => {
  const today = new Date().setHours(0, 0, 0, 0)
  const start = new Date(discount.startDate).setHours(0, 0, 0, 0)
  const end = new Date(discount.endDate).setHours(0, 0, 0, 0)
  return discount.isActive && today >= start && today <= end
}

// ✅ Chỉ cho phép toggle nếu ngày hiện tại nằm trong khoảng
const canToggleStatus = (discount) => {
  const today = new Date().setHours(0, 0, 0, 0)
  const start = new Date(discount.startDate).setHours(0, 0, 0, 0)
  const end = new Date(discount.endDate).setHours(0, 0, 0, 0)
  return today >= start && today <= end
}

// ✅ Toggle trạng thái
const toggleStatus = async (id, active) => {
  const discount = discounts.value.find(d => d.id === id)
  if (!canToggleStatus(discount)) {
    Swal.fire('Không hợp lệ', 'Chỉ được thay đổi trạng thái trong khoảng thời gian áp dụng', 'warning')
    return
  }

  const result = await Swal.fire({
    title: active ? 'Bạn có chắc muốn áp dụng lại chiết khấu?' : 'Bạn có chắc muốn ngừng áp dụng chiết khấu?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Huỷ',
    reverseButtons: true
  })

  if (result.isConfirmed) {
    try {
      await api.put(`/api/admin/agent-discounts/${id}/status`, null, { params: { active } })
      await fetchDiscounts()
      Swal.fire({
        icon: 'success',
        title: active ? 'Áp dụng chiết khấu thành công' : 'Ngừng áp dụng chiết khấu',
        timer: 2000,
        showConfirmButton: false
      })
    } catch {
      Swal.fire('Lỗi', 'Không thể cập nhật trạng thái', 'error')
    }
  }
}

// ✅ Validate trước khi gửi
const validateForm = () => {
  errors.value = {
    discountPercentage: '',
    startDate: '',
    endDate: '',
    description: ''
  }

  let valid = true
  const percentage = form.value.discountPercentage

  if (typeof percentage === 'number') {
    form.value.discountPercentage = Math.round(percentage)
  }

  if (percentage === '' || isNaN(percentage)) {
    errors.value.discountPercentage = 'Vui lòng nhập phần trăm chiết khấu'
    valid = false
  } else if (percentage < 1) {
    errors.value.discountPercentage = 'Không được nhập số âm hoặc bằng 0'
    valid = false
  } else if (!Number.isInteger(percentage)) {
    errors.value.discountPercentage = 'Chỉ cho phép số nguyên'
    valid = false
  } else if (percentage > 50) {
    errors.value.discountPercentage = 'Không được vượt quá 50%'
    valid = false
  }

  if (!form.value.startDate) {
    errors.value.startDate = 'Vui lòng chọn ngày bắt đầu'
    valid = false
  }

  if (!form.value.endDate) {
    errors.value.endDate = 'Vui lòng chọn ngày kết thúc'
    valid = false
  } else if (form.value.startDate > form.value.endDate) {
    errors.value.endDate = 'Ngày kết thúc phải sau ngày bắt đầu'
    valid = false
  }

  if (!form.value.description || form.value.description.trim().length === 0) {
    errors.value.description = 'Vui lòng nhập mô tả'
    valid = false
  } else if (form.value.description.length > 200) {
    errors.value.description = 'Mô tả không vượt quá 200 ký tự'
    valid = false
  }

  return valid
}

// ✅ Tạo chiết khấu
const createDiscount = async () => {
  if (!validateForm()) return

  try {
    await api.post('/api/admin/agent-discounts', form.value)
    Swal.fire('', 'Thêm chiết khấu thành công!', 'success')
    closeModal()
    fetchDiscounts()
  } catch (err) {
    if (
      err.response &&
      err.response.status === 400 &&
      typeof err.response.data === 'string'
    ) {
      if (err.response.data.includes('trong khoảng thời gian')) {
        errors.value.endDate = err.response.data
      } else {
        Swal.fire('Lỗi', err.response.data, 'error')
      }
    } else {
      Swal.fire('Lỗi', 'Không thể thêm chiết khấu', 'error')
    }
  }
}

// ✅ Fetch danh sách chiết khấu
//const fetchDiscounts = async () => {
//  try {
//    const res = await api.get(`/api/admin/agent-discounts/by-agent/${agentId}`)
//    discounts.value = res.data
//  } catch {
//    Swal.fire('Lỗi', 'Không thể tải danh sách chiết khấu', 'error')
//  }
//}
const fetchDiscounts = async () => {
  try {
    const res = await api.get('/api/admin/agent-discounts/search', {
      params: {
        agentId: agentId,
        keyword: searchTerm.value || undefined,
        page: currentPage.value,
        size: 5
      }
    })
    discounts.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch {
    Swal.fire('Lỗi', 'Không thể tải danh sách chiết khấu', 'error')
  }
}

const handlePageChange = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchDiscounts()
  }
}


const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('vi-VN')
}
const handleSearchInput = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0
    fetchDiscounts()
  }, 500)
}

const handleSearch = () => {
  currentPage.value = 0
  fetchDiscounts()
}

const clearSearch = () => {
  searchTerm.value = ''
  currentPage.value = 0
  fetchDiscounts()
}
// const handlePageChange = (page) => {
//   if (page >= 0 && page < totalPages.value) {
//     currentPage.value = page
//     fetchDiscounts()
//   }
// }

onMounted(() => {
  fetchDiscounts()
})
</script>
<style scoped>
.modal {
  z-index: 1070 !important;
}
.modal-backdrop {
  z-index: 1060 !important;
}
.swal2-container {
  z-index: 1050 !important;
}
.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}

.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}

/* Custom Modal Styles */
.custom-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.custom-modal-content {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 600px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalSlideIn 0.3s ease-out;
  /* Đảm bảo modal content cũng ở giữa */
  margin: auto;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.custom-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px 12px 0 0;
}

.custom-modal-title {
  margin-bottom: 0;
  color: #333;
  font-weight: 600;
  font-size: 1.25rem;
}

.custom-modal-close {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #6c757d;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.custom-modal-close:hover {
  background-color: #e9ecef;
  color: #495057;
  transform: scale(1.1);
}

.custom-modal-body {
  padding: 25px;
  background-color: #fff;
}

.custom-modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px 25px;
  border-top: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 0 0 12px 12px;
}

.custom-modal-footer .btn {
  margin-left: 12px;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.custom-modal-footer .btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Ensure form elements are properly styled */
.custom-modal-body .form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
}

.custom-modal-body .form-control {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.custom-modal-body .form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  outline: none;
}

.custom-modal-body .form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  outline: none;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .custom-modal-content {
    width: 95%;
    margin: 0 auto;
    max-height: 80vh;
  }
  
  .custom-modal-header,
  .custom-modal-body,
  .custom-modal-footer {
    padding: 15px 20px;
  }
  
  .custom-modal-title {
    font-size: 1.1rem;
  }
}

/* Đảm bảo modal luôn ở giữa trên mọi thiết bị */
@media (max-width: 576px) {
  .custom-modal-content {
    width: 98%;
    margin: 0 auto;
    max-height: 85vh;
  }
}

/* Đảm bảo modal không bị lệch khi scroll */
.custom-modal-overlay {
  overflow: hidden;
}

.custom-modal-content {
  /* Đảm bảo modal content không bị ảnh hưởng bởi scroll */
  position: relative;
  top: 0;
  left: 0;
  transform: none;
}

/* Prevent body scroll when modal is open */
body.modal-open {
  overflow: hidden;
}

/* Ensure modal is always visible */
.custom-modal-overlay {
  position: fixed !important;
  z-index: 9999 !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  /* Đảm bảo modal luôn ở giữa màn hình */
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

.custom-modal-content {
  position: relative !important;
  z-index: 10000 !important;
  background-color: #fff !important;
  color: #000 !important;
}

/* Force modal to be visible */
.custom-modal-overlay,
.custom-modal-content {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* Đảm bảo modal luôn ở giữa màn hình */
.custom-modal-overlay {
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  /* Đảm bảo modal luôn ở giữa màn hình */
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
}

.custom-modal-content {
  /* Đảm bảo modal content không bị lệch */
  position: relative !important;
  top: 0 !important;
  left: 0 !important;
  transform: none !important;
  margin: 0 auto !important;
  /* Đảm bảo modal content luôn ở giữa */
  flex-shrink: 0 !important;
}

/* Prevent any background interference */
.custom-modal-overlay::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: -1;
}

/* Ensure form elements are visible */
.custom-modal-body input,
.custom-modal-body textarea,
.custom-modal-body select {
  background-color: #fff !important;
  color: #000 !important;
  border: 2px solid #e9ecef !important;
}

.custom-modal-body input:focus,
.custom-modal-body textarea:focus,
.custom-modal-body select:focus {
  background-color: #fff !important;
  color: #000 !important;
  border-color: #007bff !important;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25) !important;
}

/* Đảm bảo modal luôn ở giữa màn hình - CSS mới */
.custom-modal-overlay {
  /* Sử dụng CSS Grid để đảm bảo căn giữa hoàn hảo */
  display: grid !important;
  place-items: center !important;
  /* Đảm bảo modal luôn ở giữa màn hình */
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  z-index: 9999 !important;
}

.custom-modal-content {
  /* Đảm bảo modal content luôn ở giữa */
  position: relative !important;
  top: 0 !important;
  left: 0 !important;
  transform: none !important;
  margin: 0 auto !important;
  /* Đảm bảo modal content không bị lệch */
  grid-column: 1 !important;
  grid-row: 1 !important;
  justify-self: center !important;
  align-self: center !important;
}

/* Đảm bảo modal luôn ở giữa trên mọi thiết bị */
@media (max-width: 1200px) {
  .custom-modal-overlay {
    display: grid !important;
    place-items: center !important;
  }
}

@media (max-width: 768px) {
  .custom-modal-overlay {
    display: grid !important;
    place-items: center !important;
  }
}

@media (max-width: 576px) {
  .custom-modal-overlay {
    display: grid !important;
    place-items: center !important;
  }
}
</style>
