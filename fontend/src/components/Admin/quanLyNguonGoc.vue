<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý nguồn gốc</h2>
    </div>

    <!-- Header + Search + Filter -->
    <div class="d-flex justify-content-between mb-3">
      <div class="d-flex gap-2">
        <!-- Tìm kiếm -->
        <div class="input-group" style="width: 350px">
          <input
            v-model="searchTerm"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm theo tên nguồn gốc..."
          />
          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i>
          </button>
        </div>

        <!-- Lọc -->
        <div class="input-group" style="width: 200px">
          <select v-model="sortField" class="form-select">
            <option value="newest">Mới nhất</option>
            <option value="oldest">Cũ nhất</option>
          </select>
          <button class="btn btn-gradient-primary" @click="applySort">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <!-- Nút thêm -->
      <button class="btn btn-gradient-primary" @click="openModal('create')">
        <i class="bi bi-plus-circle me-1"></i> Thêm nguồn gốc
      </button>
    </div>

    <!-- Bảng -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr class="table-dark">
            <th class="text-center" style="width: 70px">STT</th>
            <th class="text-center">Tên nguồn gốc</th>
            <th class="text-center" style="width: 120px">Trạng thái</th>
            <th class="text-center" style="width: 180px">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in pagedOrigins" :key="item.id">
            <td class="text-center">{{ index + 1 + currentPage * pageSize }}</td>
            <td class="text-center">{{ item.name }}</td>
            <td class="text-center">
              <span
                :class="item.isActive ? 'badge bg-success text-white' : 'badge bg-secondary text-white'"
              >
                {{ item.isActive ? 'Hoạt động' : 'Ẩn' }}
              </span>
            </td>
            <td class="text-center">
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-sm btn-warning" @click="openModal('edit', item)">
                  <i class="bi bi-pencil-square me-1"></i>Sửa
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteOrigin(item.id)">
                  <i class="bi bi-trash me-1"></i>Xóa
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredOrigins.length === 0">
            <td colspan="4" class="text-center text-danger">
              <i class="bi bi-info-circle me-1"></i>Không có nguồn gốc nào trong danh sách
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Phân trang -->
      <nav aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <a class="page-link" @click="handlePageChange(currentPage - 1)">Trước</a>
          </li>
          <li
            v-for="(page, index) in displayedPages"
            :key="index"
            class="page-item"
            :class="{ active: page === currentPage + 1, disabled: page === '...' }"
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
    </div>

    <!-- Modal Thêm/Sửa -->
    <div
      class="modal fade"
      id="originModal"
      tabindex="-1"
      style="display: none; background-color: rgba(0,0,0,0.5)"
    >
      <div class="modal-dialog">
        <form class="modal-content" @submit.prevent="onSubmit">
          <div class="modal-header bg-info">
            <h5 class="modal-title">{{ isEdit ? 'Cập nhật nguồn gốc' : 'Thêm nguồn gốc mới' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên nguồn gốc <span class="text-danger">*</span></label>
              <input
                v-model="form.name"
                type="text"
                class="form-control"
                placeholder="Nhập tên nguồn gốc"
              />
            </div>
            <div class="mb-3" v-if="isEdit">
              <label class="form-label">Trạng thái</label>
              <select v-model="form.isActive" class="form-select">
                <option :value="true">Hoạt động</option>
                <option :value="false">Ẩn</option>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-danger" @click="closeModal">Đóng</button>
            <button type="submit" class="btn" :class="isEdit ? 'btn-warning' : 'btn-primary'">
              {{ isEdit ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import Swal from 'sweetalert2'
import * as bootstrap from 'bootstrap'
import api from '@/axios'

const origins = ref([])
const form = ref({ id: null, name: '', isActive: true })
const isEdit = ref(false)
let modal = null

// Phân trang
const currentPage = ref(0)
const pageSize = 5

const filteredOrigins = computed(() => {
  let data = [...origins.value]
  if (searchTerm.value.trim()) {
    data = data.filter(s =>
      s.name.toLowerCase().includes(searchTerm.value.toLowerCase())
    )
  }
  if (sortField.value === 'newest') {
    data.sort((a, b) => b.id - a.id)
  } else if (sortField.value === 'oldest') {
    data.sort((a, b) => a.id - b.id)
  }
  return data
})

const totalPages = computed(() =>
  Math.ceil(filteredOrigins.value.length / pageSize)
)

const displayedPages = computed(() => {
  const pages = []
  for (let i = 1; i <= totalPages.value; i++) pages.push(i)
  return pages
})

const pagedOrigins = computed(() => {
  const start = currentPage.value * pageSize
  return filteredOrigins.value.slice(start, start + pageSize)
})

// Search & Sort
const searchTerm = ref('')
const sortField = ref('newest')
const handleSearch = () => { currentPage.value = 0 }
const applySort = () => { currentPage.value = 0 }

// Fetch dữ liệu
const fetchOrigins = async () => {
  try {
    const res = await api.get('/api/admin/origins')
    origins.value = (res.data?.data || []).map(o => ({
      ...o,
      isActive: o.isActive === true || o.isActive === 1 || o.isActive === 'true'
    }))
  } catch (err) {
    Swal.fire('Lỗi', err.response?.data?.message || 'Không thể tải danh sách.', 'error')
  }
}

// Modal
const openModal = (mode, item = null) => {
  isEdit.value = mode === 'edit'
  if (isEdit.value && item) {
    form.value = {
      id: item.id,
      name: item.name,
      isActive: item.isActive === true || item.isActive === 1 || item.isActive === 'true'
    }
  } else {
    form.value = { id: null, name: '', isActive: true }
  }
  nextTick(() => {
    if (!modal) modal = new bootstrap.Modal(document.getElementById('originModal'))
    modal.show()
  })
}

const closeModal = () => modal?.hide()

// Validate form
const validateForm = () => {
  const name = (form.value.name || '').trim()
  const regex = /^[a-zA-ZÀ-ỹ0-9\s\-']{1,50}$/
  if (!name) {
    Swal.fire('Lỗi', 'Tên không được để trống.', 'warning')
    return false
  }
  if (!regex.test(name)) {
    Swal.fire('Lỗi', 'Tên chỉ được chứa chữ, số, khoảng trắng, dấu nháy đơn và gạch ngang.', 'warning')
    return false
  }
  const isDuplicate = origins.value.some(
    o => o.name.toLowerCase() === name.toLowerCase() && o.id !== form.value.id
  )
  if (isDuplicate) {
    Swal.fire('Lỗi', 'Tên nguồn gốc đã tồn tại.', 'warning')
    return false
  }
  return true
}

// Submit
const onSubmit = async () => {
  if (!validateForm()) return

  try {
    const payload = {
      id: form.value.id ? Number(form.value.id) : null,
      name: form.value.name.trim(),
      isActive: Boolean(form.value.isActive)
    }

    if (isEdit.value) {
      const oldOrigin = origins.value.find(o => o.id === payload.id)

      // Kiểm tra nếu đổi trạng thái
      if (oldOrigin.isActive !== payload.isActive) {
        const check = await api.get(`/api/admin/origins/${payload.id}/check-used`)
        if (check.data.isUsed) {
          Swal.fire('Lỗi', 'Nguồn gốc đang được dùng trong sản phẩm/đơn hàng, không thể đổi trạng thái', 'error')
          form.value.isActive = oldOrigin.isActive
          return
        }
      }

      // Thực hiện cập nhật
      const res = await api.put('/api/admin/origins/update', payload)
      const index = origins.value.findIndex(o => o.id === payload.id)
      if (index !== -1) origins.value[index] = { ...res.data.data, isActive: Boolean(res.data.data.isActive) }

      Swal.fire('Thành công', 'Cập nhật nguồn gốc thành công', 'success')
    } else {
      payload.isActive = true
      const res = await api.post('/api/admin/origins/create', payload)
      origins.value.unshift({ ...res.data.data, isActive: true })
      Swal.fire('Thành công', 'Thêm nguồn gốc mới thành công', 'success')
    }

    closeModal()
  } catch (err) {
    // Kiểm tra nếu backend trả về message chi tiết
    let message = 'Không thể lưu dữ liệu'
    if (err.response?.data?.message) message = err.response.data.message
    else if (err.response?.status === 400) message = 'Dữ liệu gửi không hợp lệ (400)'
    else if (err.response?.status === 500) message = 'Lỗi server (500)'

    console.error(err)
    Swal.fire('Lỗi', message, 'error')
  }
}



// Delete
const deleteOrigin = async (id) => {
  const confirm = await Swal.fire({
    title: 'Xác nhận xoá?',
    text: 'Bạn sẽ không thể hoàn tác.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy'
  })
  if (!confirm.isConfirmed) return
  try {
    await api.delete(`/api/admin/origins/delete/${id}`)
    origins.value = origins.value.filter(o => o.id !== id)
    Swal.fire('Đã xoá!', 'Nguồn gốc đã được xoá.', 'success')
  } catch (err) {
    Swal.fire('Lỗi', err.response?.data?.message || 'Không thể xoá. Có thể đang được sử dụng.', 'error')
  }
}

// Phân trang
const handlePageChange = (page) => {
  if (page >= 0 && page < totalPages.value) currentPage.value = page
}

// Mounted
onMounted(fetchOrigins)
</script>



<style scoped>
.table-responsive {
  overflow-x: auto;
}

.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}
.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}

@media (max-width: 768px) {
  .d-flex.justify-content-center.gap-2 {
    flex-direction: column;
    gap: 0.5rem !important;
  }
  .btn-sm {
    width: 100%;
  }
}
</style>
