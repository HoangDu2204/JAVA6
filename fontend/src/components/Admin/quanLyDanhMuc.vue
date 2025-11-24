<!-- <script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="container py-5">
   <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40;">Quản lý danh mục</h2>
       <RouterLink to="/danhMucForm" class="btn btn-sm  btn-gradient-blue">+ Thêm danh mục mới</RouterLink>
    </div>

    <div class="row mb-3">
      <div class="col-md-6 mb-2">
        <input type="text" class="form-control mb-2" placeholder="Tìm kiếm danh mục...">
      </div>
      <div class="col-md-3 mb-2">
        <select class="form-select">
          <option selected>Sắp xếp theo</option>
          <option value="id">Mới nhất</option>
          <option value="name">Cũ nhất</option>
          <option value="date">A-Z</option>
          <option value="date">Z-A</option>
        </select>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle">
        <thead class="" style=" color: black;
  background-color: #f0f0f0;">
          <tr>
            <th>ID</th>
            <th>Tên danh mục</th>
            <th>Ngày tạo</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>Thực phẩm</td>
            <td>2025-06-01</td>
            <td><span class="badge bg-success">Đang kinh doanh</span></td>
            <td>
              <button class="btn btn-sm me-2 btn-gradient-info ">Xem</button>
               <button class="btn btn-sm btn-gradient-warning">Sửa</button>
            </td>
          </tr>
          <tr>
            <td>2</td>
            <td>Đồ uống</td>
            <td>2025-06-02</td>
            <td><span class="badge bg-secondary">Ngừng kinh doanh</span></td>
            <td>
              <button class="btn btn-sm me-2 btn-gradient-info">Xem</button>

               <button class="btn btn-sm btn-gradient-warning">Sửa</button>
            </td>
          </tr>
          
        </tbody>
      </table>
    </div>
    <nav aria-label="Page navigation" class="mt-4">
  <ul class="pagination justify-content-center">
    <li class="page-item disabled">
      <a class="page-link">Trước</a>
    </li>
    <li class="page-item active"><a class="page-link" href="#">1</a></li>
    <li class="page-item"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">3</a></li>
    <li class="page-item">
      <a class="page-link" href="#">Sau</a>
    </li>
  </ul>
</nav>

  </div>

</template>

<style>
/* Responsive cho header */
@media (max-width: 576px) {
  .d-flex.justify-content-between.align-items-center {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  h2 {
    text-align: center;
  }

  .btn-gradient-blue {
    width: 100%;
    text-align: center;
  }
}

/* Responsive cho input và select */
@media (max-width: 768px) {
  .row .col-md-6,
  .row .col-md-3 {
    flex: 0 0 100%;
    max-width: 100%;
  }
}

/* Table responsive fix: thêm scroll nhẹ ở mobile nếu bảng quá rộng */
.table-responsive {
  overflow-x: auto;
}

/* Responsive cho nút trong bảng */
@media (max-width: 576px) {
  table td button {
    margin-bottom: 6px;
    width: 100%;
  }
}
.btn-gradient-info {
  background-image: linear-gradient(-135deg, #1de9b6 0%, #1dc4e9 100%);
  color: white;
  border: none;
}
.btn-gradient-warning {
  background-image: linear-gradient(-135deg, #899fd4 0%, #a389d4 100%);
  color: white;
  border: none;
}
.pagination .page-link {
  background: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  margin: 0 4px;
  transition: background 0.3s ease, color 0.3s ease;
  border-radius: 8px;
}

.pagination .page-link:hover {
  background: linear-gradient(-135deg, #1de9b6 0%, #1dc4e9 100%);
  color: black;
}

.pagination .page-item.active .page-link {
  background: linear-gradient(-135deg, #899fd4 0%, #a389d4 100%);
  color: white;
  border: none;
}

.pagination .page-item.disabled .page-link {
  background: #ccc;
  color: #666;
}
/* Phần header */
h2 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #3a3a3a;
}

/* Input tìm kiếm */
.form-control {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58, 142, 249, 0.3);
}

/* Select box */
.form-select {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-select:focus {
  border-color: #1dc4e9;
  box-shadow: 0 0 5px rgba(29, 196, 233, 0.3);
}

/* Nút thêm danh mục */
.btn-gradient-blue {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 6px 14px;
  font-weight: 500;
  transition: color 0.3s ease, box-shadow 0.3s ease;
}

.btn-gradient-blue:hover {
  color: black;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

</style> -->
<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'

// State management
const categories = ref([])
const currentPage = ref(0)
const pageSize = ref(5)
const totalPages = ref(0)
const searchTerm = ref('')
const sortField = ref('createdDate')
const sortDirection = ref('desc')
const searchTimeout = ref(null)

// Modal states
const showAddModal = ref(false)
const showEditModal = ref(false)
const currentCategory = ref({
  id: null,
  name: '',
  isActive: true,
  createdDate: null
})

// Error state
const fieldErrors = ref({
  name: ''
})

// Computed property for pagination display
const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value + 1

  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 2) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      pages.push(current - 1)
      pages.push(current)
      pages.push(current + 1)
      pages.push('...')
      pages.push(total)
    }
  }

  return pages
})

// Clear field errors
const clearErrors = () => {
  fieldErrors.value = {
    name: ''
  }
}

// Fetch categories with pagination
const fetchCategories = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sort: `${sortField.value},${sortDirection.value}`
    }

    // Thêm tham số tìm kiếm nếu có
    if (searchTerm.value.trim()) {
      params.search = searchTerm.value.trim()
    }

    const response = await axios.get('http://localhost:8080/api/categories1', { params })
    categories.value = response.data.content
    totalPages.value = response.data.totalPages

  } catch (error) {
    console.error('Error fetching categories:', error)
    let errorMessage = 'Không thể tải danh sách danh mục. Vui lòng thử lại sau.'

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 400) {
        errorMessage = errorData.message || 'Dữ liệu yêu cầu không hợp lệ'
      } else if (error.response.status === 500) {
        errorMessage = 'Lỗi hệ thống, vui lòng thử lại sau'
      } else if (error.response.status === 401) {
        errorMessage = 'Vui lòng đăng nhập để tiếp tục'
        // Có thể thêm chuyển hướng đến trang đăng nhập ở đây
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.'
    }

    Swal.fire({
      icon: 'error',
      title: 'Lỗi!',
      text: errorMessage,
      confirmButtonColor: '#3a8ef9'
    })
  }
}

// Handle page change
const handlePageChange = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchCategories()
  }
}


const handleSearch = () => {
  // Reset về trang đầu tiên khi tìm kiếm
  currentPage.value = 0
  fetchCategories()
}

// Hàm xóa tìm kiếm
const clearSearch = () => {
  searchTerm.value = ''
  handleSearch()
}

// Hàm tìm kiếm tự động sau khi ngừng nhập 500ms
const handleSearchInput = () => {
  clearTimeout(searchTimeout.value)
  searchTimeout.value = setTimeout(() => {
    handleSearch()
  }, 500)
}

// Handle sorting
const handleSort = (field) => {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortDirection.value = 'desc'
  }
  currentPage.value = 0
  fetchCategories()
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN')
}

// Modal handlers
const openAddModal = () => {
  clearErrors()
  currentCategory.value = {
    id: null,
    name: '',
    isActive: true,
    createdDate: null
  }
  showAddModal.value = true
}

const openEditModal = (category) => {
  clearErrors()
  currentCategory.value = { ...category }
  showEditModal.value = true
}

// Toggle category status
const toggleStatus = async (category) => {
  try {
    const result = await Swal.fire({
      title: 'Xác nhận',
      text: `Bạn có chắc muốn ${category.isActive ? 'ngừng kinh doanh' : 'kích hoạt'} danh mục "${category.name}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3a8ef9',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Xác nhận',
      cancelButtonText: 'Hủy'
    })

    if (result.isConfirmed) {
      const updatedCategory = {
        ...category,
        isActive: !category.isActive
      }

      await axios.put(`http://localhost:8080/api/categories1/${category.id}`, updatedCategory)

      Swal.fire({
        icon: 'success',
        title: 'Thành công!',
        text: `Danh mục "${category.name}" đã được ${updatedCategory.isActive ? 'kích hoạt' : 'ngừng kinh doanh'}`,
        confirmButtonColor: '#3a8ef9'
      })

      fetchCategories()
    }
  } catch (error) {
    console.error('Error toggling category status:', error)
    let errorMessage = 'Không thể cập nhật trạng thái danh mục. Vui lòng thử lại sau.'

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 404) {
        errorMessage = errorData.message || 'Danh mục không tồn tại'
      } else if (error.response.status === 400) {
        errorMessage = errorData.message || 'Dữ liệu yêu cầu không hợp lệ'
      } else if (error.response.status === 401) {
        errorMessage = 'Vui lòng đăng nhập để tiếp tục'
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.'
    }

    Swal.fire({
      icon: 'error',
      title: 'Lỗi!',
      text: errorMessage,
      confirmButtonColor: '#3a8ef9'
    })
  }
}

// Add new category
const handleAdd = async () => {
  clearErrors()

  if (!currentCategory.value.name.trim()) {
    fieldErrors.value.name = 'Tên danh mục là bắt buộc'
    return
  }

  try {
    const response = await axios.post('http://localhost:8080/api/categories1', currentCategory.value)

    Swal.fire({
      icon: 'success',
      title: 'Thành công!',
      text: `Danh mục "${currentCategory.value.name}" đã được thêm thành công`,
      confirmButtonColor: '#3a8ef9'
    })

    showAddModal.value = false
    fetchCategories()
  } catch (error) {
    console.error('Error adding category:', error)

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 400) {
        if (errorData.error === 'VALIDATION_ERROR') {
          fieldErrors.value[errorData.field] = errorData.message
          return
        } else if (errorData.error === 'DUPLICATE_CATEGORY') {
          fieldErrors.value.name = errorData.message
          return
        }
      } else if (error.response.status === 401) {
        Swal.fire({
          icon: 'error',
          title: 'Lỗi!',
          text: 'Vui lòng đăng nhập để tiếp tục',
          confirmButtonColor: '#3a8ef9'
        })
        return
      }
    }

    Swal.fire({
      icon: 'error',
      title: 'Lỗi!',
      text: error.response?.data?.message || 'Không thể thêm danh mục. Vui lòng thử lại sau.',
      confirmButtonColor: '#3a8ef9'
    })
  }
}

// Edit category
const handleEdit = async () => {
  clearErrors()

  if (!currentCategory.value.name.trim()) {
    fieldErrors.value.name = 'Tên danh mục là bắt buộc'
    return
  }

  try {
    await axios.put(`http://localhost:8080/api/categories1/${currentCategory.value.id}`, currentCategory.value)

    Swal.fire({
      icon: 'success',
      title: 'Thành công!',
      text: `Danh mục "${currentCategory.value.name}" đã được cập nhật thành công`,
      confirmButtonColor: '#3a8ef9'
    })

    showEditModal.value = false
    fetchCategories()
  } catch (error) {
    console.error('Error updating category:', error)

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 400) {
        if (errorData.error === 'VALIDATION_ERROR') {
          fieldErrors.value[errorData.field] = errorData.message
          return
        } else if (errorData.error === 'DUPLICATE_CATEGORY') {
          fieldErrors.value.name = errorData.message
          return
        }
      } else if (error.response.status === 404) {
        Swal.fire({
          icon: 'error',
          title: 'Lỗi!',
          text: errorData.message || 'Danh mục không tồn tại',
          confirmButtonColor: '#3a8ef9'
        })
        return
      } else if (error.response.status === 401) {
        Swal.fire({
          icon: 'error',
          title: 'Lỗi!',
          text: 'Vui lòng đăng nhập để tiếp tục',
          confirmButtonColor: '#3a8ef9'
        })
        return
      }
    }

    Swal.fire({
      icon: 'error',
      title: 'Lỗi!',
      text: error.response?.data?.message || 'Không thể cập nhật danh mục. Vui lòng thử lại sau.',
      confirmButtonColor: '#3a8ef9'
    })
  }
}

// Initialize
onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40;">Quản lý danh mục</h2>
      <button @click="openAddModal" class="btn btn-sm btn-gradient-blue">+ Thêm danh mục mới</button>
    </div>

<div class="row mb-3">
  <div class="col-md-8 mb-2">
    <div class="input-group">
      <input
  v-model="searchTerm"
  @input="handleSearchInput"
  type="text"
  class="form-control"
  placeholder="Tìm kiếm theo tên danh mục..."
>
      <button
        @click="handleSearch"
        class="btn btn-gradient-primary"
        type="button"
      >
        <i class="fas fa-search"></i> Tìm kiếm
      </button>
      <button
        v-if="searchTerm"
        @click="clearSearch"
        class="btn btn-outline-secondary"
        type="button"
      >
        <i class="fas fa-times"></i> Xóa
      </button>
    </div>
  </div>
  <div class="col-md-2 mb-2">
    <select v-model="sortField" @change="handleSort(sortField)" class="form-select">
      <option value="name">Sắp xếp theo tên</option>
      <option value="createdDate">Sắp xếp theo ngày</option>
    </select>
  </div>
  <div class="col-md-2 mb-2">
    <select v-model="sortDirection" @change="handleSort(sortField)" class="form-select">
      <option value="asc">Tăng dần</option>
      <option value="desc">Giảm dần</option>
    </select>
  </div>
</div>

    <div class="table-responsive">
      <table class="table table-bordered table-striped align-middle">
        <thead class="" style="color: black; background-color: #f0f0f0;">
          <tr>
            <th>STT</th>
            <th @click="handleSort('name')" class="cursor-pointer">Tên danh mục
              <span v-if="sortField === 'name'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th @click="handleSort('createdDate')" class="cursor-pointer">Ngày tạo
              <span v-if="sortField === 'createdDate'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(category, index) in categories" :key="category.id">
             <td>{{ (currentPage * pageSize) + index + 1 }}</td>
            <td>{{ category.name }}</td>
            <td>{{ formatDate(category.createdDate) }}</td>
            <td>
               <span
                @click="toggleStatus(category)"
                class="badge"
                :class="category.isActive ? 'bg-success' : 'bg-secondary'"
                style="cursor: pointer;"
              >
                {{ category.isActive ? 'Đang kinh doanh' : 'Ngừng kinh doanh' }}
              </span>
            </td>
            <td>
              <button @click="openEditModal(category)" class="btn btn-sm btn-gradient-warning">Sửa</button>
            </td>
          </tr>
          <tr v-if="categories.length === 0">
            <td colspan="5" class="text-center">Không có dữ liệu</td>
          </tr>
        </tbody>
      </table>
    </div>

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
            disabled: page === '...'
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

    <!-- Add Modal -->
    <div v-if="showAddModal" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Thêm danh mục mới</h5>
            <button type="button" class="btn-close" @click="showAddModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên danh mục <span class="text-danger">*</span></label>
              <input
                v-model="currentCategory.name"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': fieldErrors.name }"
                required
                placeholder="Nhập tên danh mục"
                @keyup.enter="handleAdd"
              >
              <div class="invalid-feedback">{{ fieldErrors.name }}</div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showAddModal = false">Đóng</button>
            <button type="button" class="btn btn-primary" @click="handleAdd">Lưu</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="showEditModal" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chỉnh sửa danh mục</h5>
            <button type="button" class="btn-close" @click="showEditModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên danh mục <span class="text-danger">*</span></label>
              <input
                v-model="currentCategory.name"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': fieldErrors.name }"
                required
                placeholder="Nhập tên danh mục"
                @keyup.enter="handleEdit"
              >
              <div class="invalid-feedback">{{ fieldErrors.name }}</div>
            </div>
            <div class="form-check form-switch">
              <input
                class="form-check-input"
                type="checkbox"
                role="switch"
                id="editActiveSwitch"
                v-model="currentCategory.isActive"
                style="width: 3em; height: 1.5em;"
              >
              <label class="form-check-label" for="editActiveSwitch">
                {{ currentCategory.isActive ? 'Đang kinh doanh' : 'Ngừng kinh doanh' }}
              </label>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showEditModal = false">Đóng</button>
            <button type="button" class="btn btn-primary" @click="handleEdit">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.input-group {
  border-radius: 8px;
  overflow: hidden;
}

.input-group .form-control {
  border-radius: 8px 0 0 8px;
}

.input-group .btn {
  border-radius: 0;
}

.input-group .btn:last-child {
  border-radius: 0 8px 8px 0;
}

.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}

.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}
/* Modal styles */
/* .modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1050;
}

.modal-dialog {
  margin: 1.75rem auto;
  max-width: 500px;
}

.modal-content {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  background-color: #fff;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 0.3rem;
  outline: 0;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-title {
  margin-bottom: 0;
  line-height: 1.5;
}

.modal-body {
  position: relative;
  flex: 1 1 auto;
  padding: 1rem;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 1rem;
  border-top: 1px solid #dee2e6;
} */

.btn-close {
  padding: 0.5rem;
  margin: -0.5rem -0.5rem -0.5rem auto;
  background-color: transparent;
  border: 0;
  font-size: 1.5rem;
  line-height: 1;
  color: #000;
  opacity: 0.5;
  cursor: pointer;
}

.btn-close:hover {
  opacity: 0.75;
}

/* Responsive cho modal */
@media (max-width: 576px) {
  .modal-dialog {
    margin: 0.5rem auto;
    max-width: 95%;
  }
}

/* Responsive cho header */
@media (max-width: 576px) {
  .d-flex.justify-content-between.align-items-center {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  h2 {
    text-align: center;
  }

  .btn-gradient-blue {
    width: 100%;
    text-align: center;
  }
}

/* Responsive cho input và select */
@media (max-width: 768px) {
  .row .col-md-6,
  .row .col-md-3 {
    flex: 0 0 100%;
    max-width: 100%;
  }
}

/* Table responsive fix: thêm scroll nhẹ ở mobile nếu bảng quá rộng */
.table-responsive {
  overflow-x: auto;
}

/* Responsive cho nút trong bảng */
@media (max-width: 576px) {
  table td button {
    margin-bottom: 6px;
    width: 100%;
  }
}

.btn-gradient-warning {
  background-image: linear-gradient(-135deg, #899fd4 0%, #a389d4 100%);
  color: white;
  border: none;
}

.pagination .page-link {
  background: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  margin: 0 4px;
  transition: background 0.3s ease, color 0.3s ease;
  border-radius: 8px;
}

.pagination .page-link:hover {
  background: linear-gradient(-135deg, #1de9b6 0%, #1dc4e9 100%);
  color: black;
}

.pagination .page-item.active .page-link {
  background: linear-gradient(-135deg, #899fd4 0%, #a389d4 100%);
  color: white;
  border: none;
}

.pagination .page-item.disabled .page-link {
  background: #ccc;
  color: #666;
}

/* Phần header */
h2 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #3a3a3a;
}

/* Input tìm kiếm */
.form-control {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58, 142, 249, 0.3);
}

.form-control.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  color: #dc3545;
  font-size: 0.875em;
}

/* Select box */
.form-select {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-select:focus {
  border-color: #1dc4e9;
  box-shadow: 0 0 5px rgba(29, 196, 233, 0.3);
}

/* Nút thêm danh mục */
.btn-gradient-blue {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 6px 14px;
  font-weight: 500;
  transition: color 0.3s ease, box-shadow 0.3s ease;
}

.btn-gradient-blue:hover {
  color: black;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

/* Switch toggle */
.form-switch .form-check-input {
  width: 3em;
  height: 1.5em;
  cursor: pointer;
}

.form-switch .form-check-input:checked {
  background-color: #28a745;
  border-color: #28a745;
}

.text-danger {
  color: #dc3545;
}

.cursor-pointer {
  cursor: pointer;
}

/* Pagination ellipsis */
.page-item.disabled .page-link {
  pointer-events: none;
}
</style>
