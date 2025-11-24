<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

// Vue Router hooks
const route = useRoute()
const router = useRouter()

// State management
const contacts = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const searchTerm = ref('')
const sortField = ref('createdAt')
const sortDirection = ref('desc')
const searchTimeout = ref(null)

// Modal states
const showResponseModal = ref(false)
const showDetailModal = ref(false)
const currentContact = ref({
  id: null,
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: '',
  adminResponse: '',
  createdAt: null,
  respondedAt: null,
})
const currentSortField = computed({
  get() {
    return `${sortField.value}_${sortDirection.value}`
  },
  set(newValue) {
    const [field, direction] = newValue.split('_')
    sortField.value = field
    sortDirection.value = direction
  },
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

// Fetch contacts with pagination
const fetchContacts = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sort: `${sortField.value},${sortDirection.value}`,
    }

    if (searchTerm.value.trim()) {
      // Sử dụng API tìm kiếm với keyword
      const response = await axios.get('http://localhost:8080/api/contact-inquiries', {
        params: {
          keyword: searchTerm.value.trim(),
          ...params,
        },
      })
      contacts.value = response.data.content
      totalPages.value = response.data.totalPages
    } else {
      // Gọi API bình thường khi không có từ khóa
      const response = await axios.get('http://localhost:8080/api/contact-inquiries', { params })
      contacts.value = response.data.content
      totalPages.value = response.data.totalPages
    }
  } catch (error) {
    console.error('Error fetching contacts:', error)
    let errorMessage = 'Không thể tải danh sách liên hệ. Vui lòng thử lại sau.'

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 400) {
        errorMessage = errorData.message || 'Dữ liệu yêu cầu không hợp lệ'
      } else if (error.response.status === 500) {
        errorMessage = 'Lỗi hệ thống, vui lòng thử lại sau'
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
      confirmButtonColor: '#3a8ef9',
    })
  }
}

// Handle page change
const handlePageChange = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    updateRoute()
  }
}

const handleSearch = () => {
  currentPage.value = 0
  updateRoute()
}

const handleSearchInput = () => {
  clearTimeout(searchTimeout.value)
  searchTimeout.value = setTimeout(() => {
    handleSearch()
  }, 500)
}

// Handle sorting
const handleSort = () => {
  currentPage.value = 0
  updateRoute()
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN')
}

// Modal handlers
const openResponseModal = (contact) => {
  currentContact.value = { ...contact }
  showResponseModal.value = true
}

const openDetailModal = (contact) => {
  currentContact.value = { ...contact }
  showDetailModal.value = true
}

// Respond to contact
const handleResponse = async () => {
  try {
    const response = await axios.put('http://localhost:8080/api/contact-inquiries/respond', {
      id: currentContact.value.id,
      adminResponse: currentContact.value.adminResponse,
    })

    Swal.fire({
      icon: 'success',
      title: 'Thành công!',
      text: 'Phản hồi đã được gửi thành công',
      confirmButtonColor: '#3a8ef9',
    })

    showResponseModal.value = false
    fetchContacts()
  } catch (error) {
    console.error('Error responding to contact:', error)
    let errorMessage = 'Không thể gửi phản hồi. Vui lòng thử lại sau.'

    if (error.response) {
      const errorData = error.response.data

      if (error.response.status === 404) {
        errorMessage = errorData.message || 'Liên hệ không tồn tại'
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
      confirmButtonColor: '#3a8ef9',
    })
  }
}

// Initialize
onMounted(() => {
  // Read query parameters from URL
  currentPage.value = parseInt(route.query.page) || 0
  pageSize.value = parseInt(route.query.size) || 10
  searchTerm.value = route.query.keyword || ''

  // Parse sortField and sortDirection from URL
  const urlSortField = route.query.sortField || 'createdAt_desc'
  const [field, direction] = urlSortField.split('_')
  sortField.value = field
  sortDirection.value = direction

  fetchContacts()
})

// Watch for changes in route query and update state
watch(
  () => route.query,
  (newQuery) => {
    currentPage.value = parseInt(newQuery.page) || 0
    pageSize.value = parseInt(newQuery.size) || 10
    searchTerm.value = newQuery.keyword || ''

    const urlSortField = newQuery.sortField || 'createdAt_desc'
    const [field, direction] = urlSortField.split('_')
    sortField.value = field
    sortDirection.value = direction

    fetchContacts()
  },
)

// Update route with current pagination and search parameters
const updateRoute = () => {
  router.push({
    query: {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchTerm.value,
      sortField: `${sortField.value}_${sortDirection.value}`,
    },
  })
}
</script>

<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý liên hệ</h2>
    </div>

    <div class="d-flex mb-3">
      <div class="form-seach">
        <div class="input-group">
          <input
            v-model="searchTerm"
            @input="handleSearchInput"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm theo tên, email hoặc tiêu đề..."
          />

          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
      <div class="form-fillter">
        <div class="input-group">
          <select v-model="currentSortField" @change="handleSort" class="form-select">
            <option value="createdAt_desc">Ngày gửi mới nhất</option>
            <option value="createdAt_asc">Ngày gửi cũ nhất</option>
            <option value="fullName_asc">Tên người gửi (A - Z)</option>
            <option value="fullName_desc">Tên người gửi (Z - A)</option>
            <option value="adminResponse_desc">Đã phản hồi</option>
            <option value="adminResponse_asc">Chưa phản hồi</option>
            <option value="createdAt_desc">Mặc định</option>
          </select>
          <button class="btn btn-gradient-primary"><i class="bi bi-funnel"></i></button>
        </div>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle ">
        <thead class="" style="color: black; background-color: #f0f0f0">
          <tr class="table-dark  ">
            <th class="text-center ">STT</th>
            <th class="cursor-pointer">
              Tên
              <span v-if="sortField === 'fullName'"> </span>
            </th>
            <th>Email</th>
            <th class="cursor-pointer">Ngày gửi</th>
            <th class="cursor-pointer">Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr class="text-center" v-for="(contact, index) in contacts" :key="contact.id">
            <td>{{ currentPage * pageSize + index + 1 }}</td>
            <td>{{ contact.fullName }}</td>
            <td>{{ contact.email }}</td>
            <td>{{ formatDate(contact.createdAt) }}</td>
            <td class="text-center">
              <span class="badge" :class="contact.adminResponse ? 'bg-success' : 'bg-warning'">
                {{ contact.adminResponse ? 'Đã phản hồi' : 'Chưa phản hồi' }}
              </span>
            </td>
            <td class="d-flex justify-content-center">
              <button @click="openDetailModal(contact)" class="btn btn-sm btn-warning me-2">
                <i class="bi bi-eye me-1"></i>Xem
              </button>
              <button
                @click="openResponseModal(contact)"
                class="btn btn-sm "
                :class="contact.adminResponse? 'btn-info':'btn-danger text-white'"
                :disabled="!!contact.adminResponse"
              >
                {{ contact.adminResponse ? 'Đã phản hồi' : 'Chưa phản hồi' }}
              </button>
            </td>
          </tr>
          <tr v-if="contacts.length === 0">
            <td colspan="7" class="text-center">Không có dữ liệu</td>
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

    <!-- Response Modal -->
    <div
      v-if="showResponseModal"
      class="modal fade show"
      style="display: block; background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info">
            <h5 class="modal-title">Phản hồi liên hệ</h5>
            <button type="button" class="btn-close" @click="showResponseModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên người gửi</label>
              <input v-model="currentContact.fullName" type="text" class="form-control" readonly />
            </div>
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="currentContact.email" type="text" class="form-control" readonly />
            </div>
            <div class="mb-3">
              <label class="form-label">Nội dung</label>
              <textarea
                v-model="currentContact.content"
                class="form-control"
                rows="3"
                readonly
              ></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Phản hồi của bạn <span class="text-danger">*</span></label>
              <textarea
                v-model="currentContact.adminResponse"
                class="form-control"
                rows="5"
                placeholder="Nhập phản hồi của bạn..."
                required
              ></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-danger" @click="showResponseModal = false">
              Đóng
            </button>
            <button type="button" class="btn btn-info" @click="handleResponse">Gửi phản hồi</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <div
      v-if="showDetailModal"
      class="modal fade show"
      style="display: block; background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info">
            <h5 class="modal-title">Chi tiết liên hệ</h5>
            <button type="button" class="btn-close" @click="showDetailModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">Tên người gửi</label>
                <input
                  v-model="currentContact.fullName"
                  type="text"
                  class="form-control"
                  readonly
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Email</label>
                <input v-model="currentContact.email" type="text" class="form-control" readonly />
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">Số điện thoại</label>
                <input v-model="currentContact.phone" type="text" class="form-control" readonly />
              </div>
              <div class="col-md-6">
                <label class="form-label">Ngày gửi</label>
                <input
                  :value="formatDate(currentContact.createdAt)"
                  type="text"
                  class="form-control"
                  readonly
                />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Nội dung</label>
              <textarea
                v-model="currentContact.content"
                class="form-control"
                rows="5"
                readonly
              ></textarea>
            </div>
            <div class="mb-3" v-if="currentContact.adminResponse">
              <label class="form-label">Phản hồi từ quản trị viên</label>
              <textarea
                v-model="currentContact.adminResponse"
                class="form-control"
                rows="5"
                readonly
              ></textarea>
            </div>
            <div class="col-mb-12" v-if="currentContact.responseDate">
              <label class="form-label">Ngày phản hồi</label>
              <input
                :value="formatDate(currentContact.responseDate)"
                type="text"
                class="form-control"
                readonly
              />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-danger" @click="showDetailModal = false">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
th {
  text-align: center;
  font-weight: bold;
}
.form-seach {
  width: 350px;
  margin-right: 10px;
}
.form-fillter {
  width: 350px !important;
}
.input-group {
  border-radius: 8px;
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

.btn-gradient-info {
  background-image: linear-gradient(-135deg, #a8edea 0%, #fed6e3 100%);
  color: #333;
  border: none;
}

.btn-gradient-info:hover {
  background-image: linear-gradient(-135deg, #fed6e3 0%, #a8edea 100%);
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

.modal-lg {
  max-width: 800px;
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
  opacity: 0.5;
  cursor: pointer;
}

/* Responsive cho modal */
@media (max-width: 576px) {
  .modal-dialog {
    margin: 0.5rem auto;
    max-width: 95%;
  }
}

/* Responsive cho nút trong bảng */
@media (max-width: 576px) {
  table td button {
    margin-bottom: 6px;
    width: 100%;
  }
}

.pagination .page-link {
  background: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  margin: 0 4px;
  transition:
    background 0.3s ease,
    color 0.3s ease;
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

/* Input */
.form-control {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.form-control:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58, 142, 249, 0.3);
}

textarea.form-control {
  min-height: 100px;
}

/* Select box */
.form-select {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.form-select:focus {
  border-color: #1dc4e9;
  box-shadow: 0 0 5px rgba(29, 196, 233, 0.3);
}

.text-danger {
  color: #dc3545;
}

.cursor-pointer {
  cursor: pointer;
}

.bg-success {
  background-color: #28a745 !important;
}

.bg-warning {
  background-color: #ffc107 !important;
  color: #212529 !important;
}

/* Disabled button */
.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
/* Add this to your style section */
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px; /* Adjust this value as needed */
}
.form-select:not([size]):not([multiple]) {
  background-image: none;
  padding-right: 0.75rem;
}

.form-select:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 0 0.25rem rgba(58, 142, 249, 0.25);
}
</style>
