<template>
  <div class="container py-5">
    <!-- Tiêu đề -->
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý hương vị</h2>
    </div>

    <!-- Tìm kiếm + Lọc + Thêm mới -->
    <div class="d-flex justify-content-between mb-3">
      <div class="d-flex gap-2">
        <!-- Tìm kiếm -->
        <div class="input-group" style="width: 350px">
          <input
            v-model="searchTerm"
            @input="handleSearchInput"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm theo tên hương vị..."
          />
          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i>
          </button>
        </div>

        <!-- Lọc -->
        <div class="input-group" style="width: 200px">
          <select v-model="filterOption" class="form-select" @change="applyFilter">
            <option value="">Tất cả</option>
            <option value="name_desc">Tên (Z-A)</option>
            <option value="createdDate_desc">Mới nhất</option>
            <option value="createdDate_asc">Cũ nhất</option>
            <option value="active">Hoạt động</option>
            <option value="inactive">Ẩn</option>
          </select>
          <button class="btn btn-gradient-primary" @click="applyFilter">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <!-- Nút thêm -->
      <button class="btn btn-gradient-primary" @click="openModal('create')">
        <i class="bi bi-plus-circle me-1"></i> Thêm hương vị
      </button>
    </div>

    <!-- Bảng -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr class="table-dark">
            <th class="text-center" style="width: 70px">STT</th>
            <th>Tên hương vị</th>
            <th class="text-center" style="width: 120px">Trạng thái</th>
            <th class="text-center" style="width: 180px">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in paginatedFlavors" :key="item.id">
          <td class="text-center">{{ currentPage * pageSize + index + 1 }}</td>

            <td class="text-center">{{ item.name }}</td>
            <td class="text-center">
              <span :class="item.isActive ? 'badge bg-success' : 'badge bg-secondary'">
                {{ item.isActive ? 'Hoạt động' : 'Ẩn' }}
              </span>
            </td>
            <td class="text-center">
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-sm btn-warning" @click="openModal('edit', item)">
                  <i class="bi bi-pencil-square me-1"></i> Sửa
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteFlavor(item.id)">
                  <i class="bi bi-trash me-1"></i> Xóa
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="paginatedFlavors.length === 0">
            <td colspan="4" class="text-center text-danger">
              <i class="bi bi-info-circle me-1"></i> Không có hương vị nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <a class="page-link" href="#" @click.prevent="handlePageChange(currentPage - 1)">Trước</a>
        </li>
        <li
          v-for="(page, index) in displayedPages"
          :key="index"
          class="page-item"
          :class="{ active: page === currentPage + 1, disabled: page === '...' }"
        >
          <a
            class="page-link"
            href="#"
            @click.prevent="typeof page === 'number' ? handlePageChange(page - 1) : null"
          >
            {{ page }}
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <a class="page-link" href="#" @click.prevent="handlePageChange(currentPage + 1)">Sau</a>
        </li>
      </ul>
    </nav>

    <!-- Modal -->
     <div
    v-if="modalVisible"
    class="modal fade show"
    style="display:block;background:rgba(0,0,0,0.5)"
  >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header bg-info">
          <h5 class="modal-title">{{ isEdit ? 'Cập nhật hương vị' : 'Thêm hương vị' }}</h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
        </div>
        <form @submit.prevent="onSubmit">
          <div class="modal-body">
            <!-- Tên -->
            <div class="mb-3">
              <label class="form-label">Tên hương vị <span class="text-danger">*</span></label>
              <input
                v-model="form.name"
                type="text"
                class="form-control"
                placeholder="Nhập tên hương vị"
              />
              <small v-if="errors.name" class="text-danger">{{ errors.name }}</small>
            </div>
            <!-- Trạng thái (chỉ hiển thị khi chỉnh sửa) -->
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

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

const flavors = ref([])
const form = ref({ id: null, name: '', isActive: true })
const isEdit = ref(false)
const modalVisible = ref(false)
const errors = ref({ name: '' })
const searchTerm = ref('')
const filterOption = ref('')
const currentPage = ref(0)
const pageSize = 5

const filteredFlavors = computed(() => {
  let result = flavors.value
  if (searchTerm.value.trim()) {
    result = result.filter(f => f.name.toLowerCase().includes(searchTerm.value.toLowerCase()))
  }
  if (filterOption.value) {
    if (filterOption.value === 'active') result = result.filter(f => f.isActive)
    else if (filterOption.value === 'inactive') result = result.filter(f => !f.isActive)
    else if (filterOption.value === 'name_desc') result = [...result].sort((a,b) => b.name.localeCompare(a.name))
    else if (filterOption.value === 'createdDate_asc') result = [...result].sort((a,b) => new Date(a.createdDate) - new Date(b.createdDate))
    else if (filterOption.value === 'createdDate_desc') result = [...result].sort((a,b) => new Date(b.createdDate) - new Date(a.createdDate))
  }
  return result
})

const totalPages = computed(() => Math.ceil(filteredFlavors.value.length / pageSize))
const paginatedFlavors = computed(() => {
  const start = currentPage.value * pageSize
  return filteredFlavors.value.slice(start, start + pageSize)
})

const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value + 1
  if (total <= 7) for (let i = 1; i <= total; i++) pages.push(i)
  else {
    if (current <= 3) { for (let i = 1; i <= 5; i++) pages.push(i); pages.push('...'); pages.push(total) }
    else if (current >= total - 2) { pages.push(1); pages.push('...'); for (let i = total - 4; i <= total; i++) pages.push(i) }
    else { pages.push(1); pages.push('...'); pages.push(current-1); pages.push(current); pages.push(current+1); pages.push('...'); pages.push(total) }
  }
  return pages
})

const fetchFlavors = async () => {
  try {
    const res = await api.get('/api/admin/flavors')
    flavors.value = res.data?.data || []
    flavors.value.sort((a,b) => new Date(b.createdDate) - new Date(a.createdDate))
  } catch (err) {
    console.error('Lỗi khi tải danh sách hương vị:', err)
    Swal.fire('Lỗi', 'Không thể tải danh sách hương vị', 'error')
  }
}

const validateForm = () => {
  let valid = true
  errors.value = { name: '' }
  const name = form.value.name.trim()
  const regex = /^[a-zA-ZÀ-ỹ\s]{1,50}$/
  if (!name) { errors.value.name = 'Tên hương vị không được để trống.'; valid = false }
  else if (!regex.test(name)) { errors.value.name = 'Tên chỉ chứa chữ và khoảng trắng.'; valid = false }
  else if (flavors.value.some(f => f.name.toLowerCase() === name.toLowerCase() && f.id !== form.value.id)) {
    errors.value.name = 'Tên hương vị đã tồn tại.'; valid = false
  }
  return valid
}

const openModal = (mode, item = null) => {
  isEdit.value = mode === 'edit'
  form.value = isEdit.value && item ? { ...item } : { id: null, name: '', isActive: true }
  modalVisible.value = true
}

const closeModal = () => modalVisible.value = false

const onSubmit = async () => {
  if (!validateForm()) return

  try {
    if (isEdit.value) {
      // Lấy bản gốc trước khi sửa
      const oldFlavor = flavors.value.find(f => f.id === form.value.id)

      // Kiểm tra nếu đổi trạng thái isActive và đang được dùng
      if (oldFlavor && oldFlavor.isActive !== form.value.isActive) {
        const check = await api.get(`/api/admin/flavors/${form.value.id}/check-used`)
        if (check.data.isUsed) {
          Swal.fire('Lỗi', 'Hương vị đang được dùng trong đơn hàng, không thể đổi trạng thái', 'error')
          form.value.isActive = oldFlavor.isActive
          return
        }
      }

      // Cập nhật dữ liệu
      const res = await api.put('/api/admin/flavors/update', form.value)
      const index = flavors.value.findIndex(f => f.id === form.value.id)
      if (index !== -1) {
        flavors.value[index] = {
          ...res.data || form.value,
          isActive: Boolean(form.value.isActive)
        }
      }

      Swal.fire('Thành công', 'Cập nhật hương vị thành công', 'success')

    } else {
      // Thêm mới
      form.value.isActive = true
      await api.post('/api/admin/flavors/create', form.value)

      // Gọi lại danh sách từ server để đồng bộ dữ liệu
      await fetchFlavors()
      currentPage.value = 0

      Swal.fire('Thành công', 'Thêm hương vị thành công', 'success')
    }

    closeModal()
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', err.response?.data?.message || 'Không thể lưu dữ liệu', 'error')
  }
}


const deleteFlavor = async (id) => {
  const confirm = await Swal.fire({ title: 'Xóa?', text: 'Hành động này không thể hoàn tác!', icon: 'warning', showCancelButton: true })
  if (!confirm.isConfirmed) return
  try {
    await api.delete(`/api/admin/flavors/delete/${id}`)
    Swal.fire('Đã xóa!', 'Hương vị đã được xóa', 'success')
    fetchFlavors()
  } catch (e) {
    Swal.fire('Thất bại', e.response?.data?.message || 'Không thể xóa', 'error')
  }
}

const handleSearchInput = () => currentPage.value = 0
const handleSearch = () => currentPage.value = 0
const applyFilter = () => currentPage.value = 0
const handlePageChange = (p) => { if (p >= 0 && p < totalPages.value) currentPage.value = p }

onMounted(fetchFlavors)
</script>


<style scoped>
.table-responsive {
  overflow-x: auto;
}

.table {
  width: 100%;
  margin-bottom: 1rem;
  color: #212529;
}

.table-bordered {
  border: 1px solid #dee2e6;
}

.table-hover tbody tr:hover {
  background-color: rgba(0, 0, 0, 0.075);
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: rgba(0, 0, 0, 0.05);
}

.align-middle {
  vertical-align: middle !important;
}

.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}

.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}

.modal-content {
  border-radius: 0.3rem;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-body {
  padding: 1rem;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #dee2e6;
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
