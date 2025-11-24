<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý kích thước</h2>
    </div>

    <!-- Header + Search + Filter -->
    <div class="d-flex justify-content-between mb-3">
      <div class="d-flex gap-2">
        <!-- Tìm kiếm -->
        <div class="input-group" style="width: 350px">
          <input
            v-model="searchTerm"
            @input="handleSearchInput"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm theo tên kích thước..."
          />
          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i>
          </button>
        </div>

        <!-- Lọc -->
        <div class="input-group" style="width: 200px">
          <select v-model="sortField" class="form-select" @change="applySort">
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
        <i class="bi bi-plus-circle me-1"></i> Thêm kích thước
      </button>
    </div>

    <!-- Bảng -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr class="table-dark">
            <th class="text-center" style="width: 70px">STT</th>
            <th class="text-center">Tên kích thước</th>
            <th class="text-center" style="width: 120px">Trạng thái</th>
            <th class="text-center" style="width: 180px">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in paginatedSizes" :key="item.id">
            <td class="text-center">{{ currentPage * pageSize + index + 1 }}</td>
            <td class="text-center">{{ item.name }}</td>
            <td class="text-center">
              <span :class="item.isActive ? 'badge bg-success text-white' : 'badge bg-secondary text-white'">
                {{ item.isActive ? 'Hoạt động' : 'Ẩn' }}
              </span>
            </td>
            <td class="text-center">
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-sm btn-warning" @click="openModal('edit', item)">
                  <i class="bi bi-pencil-square me-1"></i>Sửa
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteSize(item.id)">
                  <i class="bi bi-trash me-1"></i>Xóa
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="paginatedSizes.length === 0">
            <td colspan="4" class="text-center text-danger">
              <i class="bi bi-info-circle me-1"></i>Không có kích thước nào trong danh sách
            </td>
          </tr>
        </tbody>
      </table>

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
    </div>

    <!-- Modal Thêm/Sửa -->
    <div
      v-if="modalVisible"
      class="modal fade show"
      style="display: block; background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-info">
            <h5 class="modal-title">{{ isEdit ? 'Cập nhật kích thước' : 'Thêm kích thước mới' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <form @submit.prevent="onSubmit">
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Tên kích thước <span class="text-danger">*</span></label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control"
                  placeholder="VD: S, M, L, 12cm, 15cm"
                />
              </div>
              <div class="mb-3">
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
import { ref, computed, onMounted } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

// State
const sizes = ref([])
const form = ref({ id: null, name: '', isActive: true })
const isEdit = ref(false)
const modalVisible = ref(false)
const searchTerm = ref('')
const sortField = ref('newest')

// Phân trang
const currentPage = ref(0)
const pageSize = 5

// Lấy danh sách
const fetchSizes = async () => {
  try {
    const res = await api.get('/api/admin/sizes')
    sizes.value = res.data?.data || res.data || []
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', 'Không thể tải danh sách kích thước.', 'error')
  }
}

onMounted(fetchSizes)

// Lọc & tìm kiếm
const filteredSizes = computed(() => {
  let data = [...sizes.value]
  if (searchTerm.value.trim()) {
    data = data.filter(s => s.name.toLowerCase().includes(searchTerm.value.toLowerCase()))
  }
  if (sortField.value === 'newest') data.sort((a,b) => b.id - a.id)
  else if (sortField.value === 'oldest') data.sort((a,b) => a.id - b.id)
  return data
})

// Phân trang
const totalPages = computed(() => Math.ceil(filteredSizes.value.length / pageSize))
const paginatedSizes = computed(() => {
  const start = currentPage.value * pageSize
  return filteredSizes.value.slice(start, start + pageSize)
})

// Hiển thị các số trang
const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value + 1
  if (total <= 7) for(let i=1;i<=total;i++) pages.push(i)
  else {
    if(current<=3){ for(let i=1;i<=5;i++) pages.push(i); pages.push('...'); pages.push(total) }
    else if(current>=total-2){ pages.push(1); pages.push('...'); for(let i=total-4;i<=total;i++) pages.push(i) }
    else{ pages.push(1); pages.push('...'); pages.push(current-1); pages.push(current); pages.push(current+1); pages.push('...'); pages.push(total) }
  }
  return pages
})

// Các phương thức
const handleSearchInput = () => currentPage.value=0
const handleSearch = () => currentPage.value=0
const applySort = () => currentPage.value=0
const handlePageChange = (page) => { if(page>=0 && page<totalPages.value) currentPage.value = page }

// Modal
const openModal = (mode, item = null) => {
  isEdit.value = mode === 'edit'
  form.value = isEdit.value && item ? { ...item, isActive: Boolean(item.isActive) } : { id:null, name:'', isActive:true }
  modalVisible.value = true
}
const closeModal = () => modalVisible.value = false

const onSubmit = async () => {
  const name = form.value.name.trim()
  const regex = /^[a-zA-ZÀ-ỹ0-9\s()]{1,50}$/

  if(!name){ Swal.fire('Lỗi','Tên không được để trống','warning'); return }
  if(!regex.test(name)){ Swal.fire('Lỗi','Tên chỉ chứa chữ, số, khoảng trắng, ()','warning'); return }
  if(sizes.value.some(s=>s.name.toLowerCase()===name.toLowerCase() && s.id!==form.value.id)){
    Swal.fire('Lỗi','Tên kích thước đã tồn tại','warning'); return
  }

  try{
    if(isEdit.value){
      const oldSize = sizes.value.find(s => s.id === form.value.id)

      // Kiểm tra trạng thái isActive có thay đổi không
      if(oldSize.isActive !== form.value.isActive){
        const check = await api.get(`/api/admin/sizes/${form.value.id}/check-used`)
        if(check.data.isUsed){
          Swal.fire('Lỗi', 'Kích thước đang được dùng trong sản phẩm, không thể đổi trạng thái', 'error')
          form.value.isActive = oldSize.isActive
          return
        }
      }

      await api.put('/api/admin/sizes/update', form.value)
      Swal.fire('Thành công','Cập nhật thành công','success')
    } else {
      form.value.isActive = true
      await api.post('/api/admin/sizes/create', form.value)
      Swal.fire('Thành công','Thêm mới thành công','success')
    }

    closeModal()
    fetchSizes()
  } catch(err){
    Swal.fire('Lỗi', err.response?.data?.message || 'Không thể lưu dữ liệu','error')
  }
}


// Xóa
const deleteSize = async (id) => {
  const confirm = await Swal.fire({title:'Bạn có chắc muốn xóa?', icon:'warning', showCancelButton:true, confirmButtonText:'Xóa', cancelButtonText:'Hủy'})
  if(!confirm.isConfirmed) return
  try{
    await api.delete(`/api/admin/sizes/delete/${id}`)
    Swal.fire('Thành công','Đã xóa','success')
    fetchSizes()
  } catch(err){
    Swal.fire('Lỗi', err.response?.data?.message || 'Không thể xóa','error')
  }
}
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
.size-name {
  font-weight: 600;
  color: #333;
}
</style>
