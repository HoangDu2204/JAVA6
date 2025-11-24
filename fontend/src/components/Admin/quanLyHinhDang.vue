<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý hình dạng bánh</h2>
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
            placeholder="Tìm kiếm theo hình dạng..."
          />
          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i>
          </button>
        </div>

        <!-- Lọc theo trường -->
        <div class="input-group" style="width: 200px">
          <select v-model="filterOption" class="form-select" @change="applyFilter">
            <option value="">Tất cả</option>
            <option value="name_asc">Tên (A-Z)</option>
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
        <i class="bi bi-plus-circle me-1"></i> Thêm hình dạng
      </button>
    </div>

    <!-- Bảng danh sách -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr class="custom-col-table table-dark">
            <th class="text-center" style="width: 70px">STT</th>
            <th class="text-center">Tên hình dạng</th>
            <th class="text-center" style="width: 120px">Trạng thái</th>
            <th class="text-center" style="width: 180px">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in paginatedShapes" :key="item.id">
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
                  <i class="bi bi-pencil-square me-1"></i> Sửa
                </button>
                <button class="btn btn-sm btn-danger" @click="deleteShape(item.id)">
                  <i class="bi bi-trash me-1"></i> Xóa
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="paginatedShapes.length === 0">
            <td colspan="4" class="text-center text-danger">
              <i class="bi bi-info-circle me-1"></i> Không có hình dạng nào
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
          <a class="page-link" href="#" @click.prevent="typeof page === 'number' ? handlePageChange(page - 1) : null">
            {{ page }}
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <a class="page-link" href="#" @click.prevent="handlePageChange(currentPage + 1)">Sau</a>
        </li>
      </ul>
    </nav>

    <!-- Modal Thêm/Sửa -->
    <div v-if="modalVisible" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-info">
            <h5 class="modal-title">{{ isEdit ? 'Cập nhật hình dạng' : 'Thêm hình dạng mới' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>

          <form @submit.prevent="onSubmit">
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Tên hình dạng <span class="text-danger">*</span></label>
                <input v-model="form.name" type="text" class="form-control" placeholder="Nhập tên hình dạng"/>
                <small v-if="errors.name" class="text-danger">{{ errors.name }}</small>
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
              <button type="submit" :class="isEdit ? 'btn btn-warning' : 'btn btn-primary'">
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
const shapes = ref([])
const form = ref({ id: null, name: '', isActive: true })
const isEdit = ref(false)
const modalVisible = ref(false)
const errors = ref({ name: '' })
const searchTerm = ref('')
const filterOption = ref('')
const currentPage = ref(0)
const pageSize = 5

// Fetch shapes
const fetchShapes = async () => {
  try {
    const res = await api.get('/api/admin/shapes')
    // đảm bảo isActive là boolean
    shapes.value = res.data.map(item => ({ ...item, isActive: Boolean(item.isActive) }))
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', 'Không thể tải dữ liệu hình dạng.', 'error')
  }
}

// Computed
const filteredShapes = computed(() => {
  let result = shapes.value
  if (searchTerm.value.trim()) {
    result = result.filter(s => s.name.toLowerCase().includes(searchTerm.value.toLowerCase()))
  }
  if (filterOption.value) {
    if (filterOption.value === 'active') result = result.filter(s => s.isActive)
    else if (filterOption.value === 'inactive') result = result.filter(s => !s.isActive)
    else if (filterOption.value === 'name_asc') result = [...result].sort((a,b)=>a.name.localeCompare(b.name))
    else if (filterOption.value === 'name_desc') result = [...result].sort((a,b)=>b.name.localeCompare(a.name))
    else if (filterOption.value === 'createdDate_asc') result = [...result].sort((a,b)=>new Date(a.createdDate)-new Date(b.createdDate))
    else if (filterOption.value === 'createdDate_desc') result = [...result].sort((a,b)=>new Date(b.createdDate)-new Date(a.createdDate))
  }
  return result
})

const totalPages = computed(() => Math.ceil(filteredShapes.value.length / pageSize))
const paginatedShapes = computed(() => filteredShapes.value.slice(currentPage.value * pageSize, currentPage.value * pageSize + pageSize))

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

// Methods
const openModal = (mode, item=null) => {
  isEdit.value = mode==='edit'
  form.value = isEdit.value && item ? { ...item, isActive: Boolean(item.isActive) } : { id:null, name:'', isActive:true }
  errors.value = { name:'' }
  modalVisible.value = true
}

const closeModal = () => modalVisible.value = false

const validateForm = () => {
  errors.value = { name:'' }
  const name = form.value.name.trim()
  const regex = /^[a-zA-ZÀ-ỹ\s]{1,50}$/
  if(!name){ errors.value.name='Tên không được để trống'; return false }
  if(!regex.test(name)){ errors.value.name='Tên chỉ chứa chữ và khoảng trắng'; return false }
  if(shapes.value.some(s=>s.name.toLowerCase()===name.toLowerCase() && s.id!==form.value.id)){ errors.value.name='Tên đã tồn tại'; return false }
  return true
}

const onSubmit = async () => {
  if(!validateForm()) return
  try{
    if(isEdit.value){
      const oldShape = shapes.value.find(s=>s.id===form.value.id)
      // kiểm tra trạng thái
      if(oldShape.isActive!==form.value.isActive){
        const check = await api.get(`/api/admin/shapes/${form.value.id}/check-used`)
        if(check.data.isUsed){
          Swal.fire('Lỗi','Hình dạng đang được dùng, không thể đổi trạng thái','error')
          form.value.isActive = oldShape.isActive
          return
        }
      }
      const res = await api.put('/api/admin/shapes/update', form.value)
      // update mảng shapes
      const index = shapes.value.findIndex(s=>s.id===form.value.id)
      if(index!==-1) shapes.value[index] = { ...res.data || form.value, isActive: Boolean(form.value.isActive) }
      Swal.fire('Thành công','Cập nhật hình dạng thành công','success')
    } else {
      form.value.isActive = true
      const res = await api.post('/api/admin/shapes/create', form.value)
      shapes.value.unshift({ ...res.data, isActive:true })
      Swal.fire('Thành công','Thêm hình dạng thành công','success')
    }
    closeModal()
  } catch(err){
    console.error(err)
    Swal.fire('Lỗi','Không thể lưu dữ liệu','error')
  }
}

const deleteShape = async (id)=>{
  try{
    const check = await api.get(`/api/admin/shapes/${id}/check-used`)
    if(check.data.isUsed){ Swal.fire('Lỗi','Hình dạng đang dùng, không thể xóa','error'); return }
    const confirm = await Swal.fire({title:'Bạn có chắc?', text:'Xóa vĩnh viễn hình dạng', icon:'warning', showCancelButton:true, confirmButtonText:'Xóa'})
    if(!confirm.isConfirmed) return
    await api.delete(`/api/admin/shapes/${id}`)
    Swal.fire('Thành công','Đã xóa','success')
    fetchShapes()
  } catch(err){ console.error(err); Swal.fire('Lỗi','Không thể xóa','error') }
}

const handleSearchInput = ()=> currentPage.value=0
const handleSearch = ()=> currentPage.value=0
const applyFilter = ()=> currentPage.value=0
const handlePageChange = (page)=> { if(page>=0 && page<totalPages.value) currentPage.value=page }

onMounted(fetchShapes)
</script>


<style scoped>
/* Kế thừa các style từ giao diện trước */
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

.card {
  border: none;
  border-radius: 0.5rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

/* Responsive cho nút trong bảng */
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
