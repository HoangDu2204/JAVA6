<template>
  <div class="container">
    <!-- Page Header -->
    <div class="page-header mb-4">
      <div class="header-breadcrumb-row">
        <h5 class="page-title mb-1">Quản lý bình luận</h5>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb bg-transparent p-0 m-0">
            <li class="breadcrumb-item"><a href="#">Home</a></li>
            <li class="breadcrumb-item active" aria-current="page">Quản lý bình luận</li>
          </ol>
        </nav>
      </div>
    </div>

    <!-- Hàng bộ lọc và tìm kiếm -->
    <div class="d-flex align-items-center mb-3 flex-wrap gap-2">
      <!-- Ô tìm kiếm -->
      <!-- <div class="input-group" style="flex: 1; min-width: 250px;">
        <input
          v-model="searchKeyword"
          type="text"
          class="form-control"
          placeholder="Tìm theo nội dung, email..."
        />
        <button class="btn btn-gradient-primary" type="button">
          <i class="bi bi-search"></i>
        </button>
      </div> -->

      <!-- Lọc theo tin tức -->
      <div class="input-group" style="width: 350px;">
        <select v-model="selectedNewsId" class="form-select">
          <option value="">Tất cả tin tức</option>
          <option v-for="news in newsList" :key="news.id" :value="news.id">
            {{ news.title }}
          </option>
        </select>
        <button class="btn btn-gradient-primary" type="button">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Lọc theo trạng thái -->
      <!-- <select v-model="filterStatus" class="form-select" style="width: 180px;">
        <option value="">Tất cả trạng thái</option>
        <option value="APPROVED">Đã duyệt</option>
        <option value="HIDDEN">Bị ẩn</option>
      </select> -->

      <!-- Chọn tiêu chí sắp xếp -->
      <div class="input-group" style="width: 250px;">
        <select v-model="sortBy" class="form-select">
          <option value="date">Sắp xếp theo ngày</option>
          <option value="name">Sắp xếp theo tên</option>
        </select>
        <button class="btn btn-gradient-primary" type="button">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Chọn thứ tự -->
      <div class="input-group" style="width: 200px;">
        <select v-model="sortOrder" class="form-select">
          <option value="desc">Giảm dần</option>
          <option value="asc">Tăng dần</option>
        </select>
        <button class="btn btn-gradient-primary" type="button">
          <i class="bi bi-funnel"></i>
        </button>
      </div>
    </div>

    <!-- Bảng dữ liệu -->
    <div class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-dark">
              <tr>
                <th>STT</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Nội dung</th>
                <th>Ngày đăng</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in paginatedComments" :key="item.commentId">
                <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
                <td>{{ item.userFullName || 'N/A' }}</td>
                <td>{{ item.userEmail || 'N/A' }}</td>
                <td>{{ truncateContent(item.content, 80) }}</td>
                <td>{{ formatDate(item.createdDate) }}</td>
                <td>
                  <button
                    class="btn"
                    :class="item.status === 'HIDDEN' ? 'btn-an' : 'btn-duyet'"
                    @click="toggleCommentStatus(item.commentId, item.status)"
                    :disabled="isProcessing[item.commentId]"
                  >
                    <i :class="[
                      'bi',
                      item.status === 'HIDDEN' ? 'bi-eye' : 'bi-eye-slash',
                    ]"></i>
                    {{ item.status === 'HIDDEN' ? 'Hiện' : 'Ẩn' }}
                  </button>
                  <button
                    class="btn btn-xoa btn-sm ms-1"
                    @click="deleteComment(item.commentId)"
                    :disabled="isProcessing[item.commentId]"
                  >
                    <i class="bi bi-trash"></i> Xóa
                  </button>
                </td>
              </tr>
              <tr v-if="paginatedComments.length === 0">
                <td colspan="6" class="text-center">Không có bình luận nào phù hợp.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Phân trang -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click.prevent="prevPage">Trước</a>
        </li>
        <li
          v-for="page in displayedPages"
          :key="page"
          class="page-item"
          :class="{ active: page === currentPage, disabled: page === '...' }"
        >
          <a class="page-link" @click.prevent="typeof page === 'number' ? goToPage(page) : null">
            {{ page }}
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click.prevent="nextPage">Sau</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import api from '@/axios'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'

// State
const comments = ref([])
const newsList = ref([])
const isProcessing = ref({})

// State cho bộ lọc, tìm kiếm, sắp xếp
const searchKeyword = ref('')
const selectedNewsId = ref('')
const filterStatus = ref('')
const sortBy = ref('date')
const sortOrder = ref('desc')

// State cho phân trang
const currentPage = ref(1)
const itemsPerPage = 7
const maxVisiblePages = 5

// Computed properties
const processedComments = computed(() => {
  let list = [...comments.value] // Tạo bản sao để tránh thay đổi mảng gốc
  const keyword = searchKeyword.value.toLowerCase().trim()

  // 1. Lọc (Filtering)
  if (keyword) {
    list = list.filter(c =>
      (c.content || '').toLowerCase().includes(keyword) ||
      (c.userEmail || '').toLowerCase().includes(keyword)
    )
  }
  if (selectedNewsId.value) {
    list = list.filter(c => c.newsId === selectedNewsId.value)
  }
  if (filterStatus.value) {
    list = list.filter(c => c.status === filterStatus.value)
  }

  // 2. Sắp xếp (Sorting)
  list.sort((a, b) => {
    if (sortBy.value === 'date') {
      const dateA = new Date(a.createdDate)
      const dateB = new Date(b.createdDate)
      return sortOrder.value === 'asc' ? dateA - dateB : dateB - dateA
    }
    if (sortBy.value === 'name') {
      const nameA = (a.userFullName || '').toLowerCase()
      const nameB = (b.userFullName || '').toLowerCase()
      return sortOrder.value === 'asc' ? nameA.localeCompare(nameB) : nameB.localeCompare(nameA)
    }
    return 0
  })

  return list
})

const totalPages = computed(() => Math.ceil(processedComments.value.length / itemsPerPage))
const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  const half = Math.floor(maxVisiblePages / 2)

  let start = Math.max(1, current - half)
  let end = Math.min(total, start + maxVisiblePages - 1)

  if (end - start + 1 < maxVisiblePages) {
    start = Math.max(1, end - maxVisiblePages + 1)
  }

  if (start > 1) {
    pages.push(1)
    if (start > 2) pages.push('...')
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  if (end < total) {
    if (end < total - 1) pages.push('...')
    pages.push(total)
  }

  return pages
})

const paginatedComments = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return processedComments.value.slice(start, end)
})

// Watchers
watch([searchKeyword, selectedNewsId, filterStatus, sortBy, sortOrder], () => {
  currentPage.value = 1 // Reset về trang 1 khi thay đổi bộ lọc hoặc sắp xếp
})

watch(totalPages, (newTotal) => {
  if (currentPage.value > newTotal && newTotal > 0) {
    currentPage.value = newTotal
  }
})

// Methods
const loadData = async () => {
  try {
    const [commentsRes, newsRes] = await Promise.all([
      api.get('/api/news-comments'),
      api.get('/api/news')
    ])
    comments.value = commentsRes.data.map(item => ({
      ...item,
      createdDate: new Date(item.createdDate)
    }))
    newsList.value = newsRes.data.map(item => ({
      id: item.newsId || item.id,
      title: item.title
    }))
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu:', error)
    Swal.fire('Lỗi!', 'Không thể tải dữ liệu cho trang.', 'error')
  }
}

const toggleCommentStatus = async (id, currentStatus) => {
  const newStatus = currentStatus === 'HIDDEN' ? 'APPROVED' : 'HIDDEN'
  const actionText = newStatus === 'HIDDEN' ? 'ẩn' : 'hiển thị'

  const result = await Swal.fire({
    title: `Xác nhận ${actionText} bình luận?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy bỏ'
  })

  if (result.isConfirmed) {
    isProcessing.value[id] = true
    try {
      await api.put(`/api/news-comments/${id}/status`, { status: newStatus })
      const comment = comments.value.find(c => c.commentId === id)
      if (comment) comment.status = newStatus // Cập nhật trạng thái trong mảng gốc
      Swal.fire('Thành công!', `Bình luận đã được ${actionText}.`, 'success')
    } catch (error) {
      Swal.fire('Lỗi!', 'Không thể cập nhật trạng thái.', 'error')
    } finally {
      isProcessing.value[id] = false
    }
  }
}

const deleteComment = async (id) => {
  const result = await Swal.fire({
    title: 'Bạn có chắc chắn muốn xóa?',
    text: "Hành động này không thể hoàn tác!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    confirmButtonText: 'Xóa ngay!',
    cancelButtonText: 'Hủy bỏ'
  })

  if (result.isConfirmed) {
    isProcessing.value[id] = true
    try {
      await api.delete(`/api/news-comments/${id}`)
      comments.value = comments.value.filter(c => c.commentId !== id) // Cập nhật mảng gốc
      if (currentPage.value > totalPages.value && totalPages.value > 0) {
        currentPage.value = totalPages.value // Quay về trang cuối nếu trang hiện tại không còn dữ liệu
      }
      Swal.fire('Đã xóa!', 'Bình luận đã được xóa.', 'success')
    } catch (error) {
      Swal.fire('Lỗi!', 'Không thể xóa bình luận.', 'error')
    } finally {
      isProcessing.value[id] = false
    }
  }
}

const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++ }
const prevPage = () => { if (currentPage.value > 1) currentPage.value-- }
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return dayjs(date).format('HH:mm DD/MM/YYYY')
}

const truncateContent = (content, maxLength) => {
  if (!content) return ''
  return content.length > maxLength ? content.substring(0, maxLength) + '...' : content
}

onMounted(loadData)
</script>


<style scoped>
/* Định nghĩa cụ thể cho table-dark để ghi đè */
.table-dark {
  background-color: #212529 !important; /* Nền đen */
  color: #ffffff !important; /* Chữ trắng */
}

.table-dark th {
  font-weight: 600;
  font-size: 0.85rem;
  border-bottom: 1px solid #dee2e6;
  padding: 0.75rem 1.25rem;
  text-align: left;
  vertical-align: middle;
}

/* Loại bỏ định nghĩa cũ của .table thead th để tránh xung đột */
.table thead th {
  /* Xóa các thuộc tính nền và màu chữ để tránh ghi đè */
}

/* Phân trang */
.pagination .page-item .page-link {
  border-radius: 8px;
  margin: 0 4px;
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  color: #495057;
  border: 1px solid #dee2e6;
  transition: all 0.2s ease;
}

.pagination .page-item.active .page-link {
  background: linear-gradient(135deg, #4299e1, #63b3ed);
  color: #fff;
  border-color: transparent;
}

.pagination .page-item.disabled .page-link {
  color: #adb5bd;
  cursor: not-allowed;
  background: #f8f9fa;
  border-color: #dee2e6;
}

.pagination .page-item .page-link:hover:not(.disabled) {
  background: #e9ecef;
  border-color: #ced4da;
}

/* Giữ lại các style khác */
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
  padding: 0;
}

.table {
  margin-bottom: 0;
  border-collapse: collapse;
}

.table tbody td {
  padding: 0.75rem 1.25rem;
  vertical-align: middle;
  font-size: 0.9rem;
  border-top: 1px solid #e9ecef;
  color: #495057;
}

.table tbody tr:first-child td {
  border-top: none;
}

.table-hover tbody tr:hover {
  background-color: #f1f3f5;
}

.btn {
  border: none;
  color: #3d3a3a;
  border-radius: 1rem;
  padding: 0.35rem 0.8rem;
  font-weight: 500;
  transition: opacity 0.2s ease;
  cursor: pointer;
}

.btn:hover {
  opacity: 0.9;
  color: rgb(61, 58, 58);
}

/* Nút Duyệt */
.btn-duyet {
  background: linear-gradient(135deg, #1de9b6, #1dc4e9);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(29, 233, 182, 0.25);
}

.btn-duyet:hover {
  background: linear-gradient(135deg, #1dc4e9, #1de9b6);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(29, 196, 233, 0.3);
}

/* Nút Ẩn */
.btn-an {
  background: linear-gradient(135deg, #d7dde7, #b6c1d1);
  
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(215, 221, 231, 0.25);
}

.btn-an:hover {
  background: linear-gradient(135deg, #b6c1d1, #d7dde7);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(182, 193, 209, 0.3);
}

/* Nút Xóa */
.btn-xoa {
  background: linear-gradient(135deg, #ff6a6a, #ff4757);

  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(255, 106, 106, 0.25);
}

.btn-xoa:hover {
  background: linear-gradient(135deg, #ff4757, #ff6a6a);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(255, 71, 87, 0.3);
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
.me-2 { margin-right: 0.5rem !important; }
.ms-2 { margin-left: 0.5rem !important; }
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

.btn-add-discount {
  background-color: #b2ff78;
  color: rgb(0, 0, 0);
}

.add-discount-form {
  background-color: #ffffff;
}

.add-discount-form .form-label {
  font-weight: 500;
  color: #495057;
}

.add-discount-form .input-group-text {
  background-color: #e9ecef;
  border-right: 0;
}

.add-discount-form .form-control {
  border-left: 0;
}

.add-discount-form .input-group .form-control:focus {
  box-shadow: none;
  border-color: #ced4da;
}

.settings-dropdown-wrapper:hover .settings-dropdown {
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
}

.table tbody td:nth-child(4) {
  max-width: 350px;
  white-space: normal;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.table tbody td:last-child {
  white-space: nowrap;
  width: auto;
}

.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(66, 153, 225, 0.25);
}

.btn-gradient-primary:hover {
   background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);

}
</style>