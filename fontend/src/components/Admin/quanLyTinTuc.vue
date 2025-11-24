<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý tin tức</h2>
    </div>

     <!-- Hàng trên: nút thêm mới -->
    <div class="mb-3 d-flex justify-content-end align-items-center flex-wrap gap-2">
      <button class="btn btn-gradient-primary ms-2" @click="router.push('/tinTucForm')">
        + Thêm tin tức mới
      </button>
    </div>
   <div class="d-flex align-items-center mb-3 flex-wrap gap-2">
      <!-- Ô tìm kiếm -->
      <div class="search-wrapper" style="flex: 1; min-width: 200px; position: relative;">
        <input
          v-model="searchKeyword"
          type="text"
          class="form-control pe-5"
          placeholder="Tìm kiếm theo tên..."
        />
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-search"></i>
        </button>
      </div>

      <!-- Chọn tiêu chí sắp xếp -->
      <div class="filter-wrapper" style="width: 200px; position: relative;">
        <select v-model="sortBy" class="form-select pe-5">
          <option value="date">Sắp xếp theo ngày</option>
          <option value="title">Sắp xếp theo tên</option>
        </select>
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Chọn thứ tự -->
      <div class="filter-wrapper" style="width: 150px; position: relative;">
        <select v-model="sortOrder" class="form-select pe-5">
          <option value="asc">Tăng dần</option>
          <option value="desc">Giảm dần</option>
        </select>
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Lọc theo ngày đăng -->
      <div class="filter-wrapper" style="width: 200px; position: relative;">
        <select v-model="dateFilter" class="form-select pe-5">
          <option value="">Tất cả ngày</option>
          <option value="today">Hôm nay</option>
          <option value="7days">7 ngày qua</option>
          <option value="month">Tháng này</option>
          <option value="custom">Khoảng thời gian...</option>
        </select>
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Nếu chọn khoảng thời gian -->
      <div v-if="dateFilter === 'custom'" class="d-flex gap-2 align-items-center">
        <input type="date" v-model="customDateFrom" class="form-control" style="width: 180px;" />
        <input type="date" v-model="customDateTo" class="form-control" style="width: 180px;" />
      </div>
    </div>
     
    

    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead class="" style="color: black; background-color: #f0f0f0">
          <tr class="table-dark">
            <th class="text-center">STT</th>
            <th>Hình ảnh</th>
            <th>Tiêu đề</th>
            <th>Mô tả</th>
            <th>Ngày đăng</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
         <tr v-for="(item, index) in paginatedNews" :key="item.newsId">
                <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
            <td class="text-center">
              <img
              class="img-fluid object-fit-cover rounded " height="50" width="50"
                v-if="item.image"
                :src="getImageUrl(item.image)"

                alt="News image"
                @error="handleImageError"
              />
              <span v-else class="text-muted">Không có ảnh</span>
            </td>
            <td class="text-center">{{ item.title }}</td>
            <td class="text-center">{{ truncateContent(item.content, 100) }}</td>
            <td >{{ formatDate(item.createdAt) }}</td>
            <td class="text-center">
              <button class="btn btn-warning btn-sm me-2" @click="updateNews(item.newsId)">
                <i class="bi bi-pencil-square me-1"></i> Sửa
              </button>
              <button class="btn btn-danger btn-sm" @click="deleteNews(item.newsId)">
                <i class="bi bi-trash me-1"></i> Xóa
              </button>
            </td>
          </tr>
          <tr v-if="filteredNews.length === 0">
            <td colspan="6" class="text-center">Không có tin tức nào.</td>
          </tr>
        </tbody>
      </table>
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

    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import api from '@/axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import isBetween from 'dayjs/plugin/isBetween'

dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.extend(isBetween)

const router = useRouter()

const news = ref([])
const searchKeyword = ref('')
const loading = ref(false)
const error = ref(null)

const sortBy = ref('date')
const sortOrder = ref('desc')

// Lọc theo ngày
const dateFilter = ref('')
const customDateFrom = ref('')
const customDateTo = ref('')

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
const IMAGE_BASE_URL = 'http://localhost:8080/images'

const sortedNews = computed(() => {
  let sorted = [...news.value]
  if (sortBy.value === 'date') {
    sorted.sort((a, b) => {
      const dateA = new Date(a.createdAt)
      const dateB = new Date(b.createdAt)
      return sortOrder.value === 'asc' ? dateA - dateB : dateB - dateA
    })
  } else if (sortBy.value === 'title') {
    sorted.sort((a, b) => {
      const nameA = a.title.toLowerCase()
      const nameB = b.title.toLowerCase()
      return sortOrder.value === 'asc'
        ? nameA.localeCompare(nameB)
        : nameB.localeCompare(nameA)
    })
  }
  return sorted
})

const filteredNews = computed(() => {
  const keyword = searchKeyword.value.toLowerCase().trim()
  let result = [...sortedNews.value]

  // Lọc theo từ khóa
  if (keyword) {
    result = result.filter(item =>
      item.title.toLowerCase().includes(keyword)
    )
  }

  // Lọc theo ngày đăng
  if (dateFilter.value) {
    const today = dayjs().startOf('day')
    result = result.filter(item => {
      const itemDate = dayjs(item.createdAt)
      if (dateFilter.value === 'today') {
        return itemDate.isSame(today, 'day')
      } else if (dateFilter.value === '7days') {
        return itemDate.isAfter(today.subtract(7, 'day'))
      } else if (dateFilter.value === 'month') {
        return itemDate.isSame(today, 'month')
      } else if (dateFilter.value === 'custom' && customDateFrom.value && customDateTo.value) {
        const from = dayjs(customDateFrom.value).startOf('day')
        const to = dayjs(customDateTo.value).endOf('day')
        return itemDate.isBetween(from, to, null, '[]')
      }
      return true
    })
  }

  return result
})

// Phân trang
const currentPage = ref(1)
const itemsPerPage = 5
const totalPages = computed(() => Math.ceil(filteredNews.value.length / itemsPerPage))
const maxVisiblePages = 5
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

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return filteredNews.value.slice(start, start + itemsPerPage)
})

const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++ }
const prevPage = () => { if (currentPage.value > 1) currentPage.value-- }
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

watch([searchKeyword, dateFilter, customDateFrom, customDateTo], () => {
  currentPage.value = 1
})

watch(filteredNews, () => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value || 1
  }
})

// Tải dữ liệu
const loadNews = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await api.get(`${API_URL}/news`)
    news.value = Array.isArray(response.data)
      ? response.data.map(item => ({
          ...item,
          createdAt: item.createdAt || null
        }))
      : []
  } catch (err) {
    error.value = `Lỗi khi tải tin tức: ${err.response?.data?.error || err.message}`
    if (err.response?.status === 500) {
      error.value += ' - Vui lòng kiểm tra backend.'
    }
    news.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadNews)

// Hành động
const deleteNews = async (id) => {
  const result = await Swal.fire({
    title: 'Xác nhận xóa',
    text: 'Bạn có chắc chắn muốn xóa tin tức này không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#dc3545',
    confirmButtonText: 'OK',
    cancelButtonText: 'Hủy'
  })

  if (result.isConfirmed) {
    try {
      await api.delete(`${API_URL}/news/${id}`)
      news.value = news.value.filter(item => item.newsId !== id)
      await Swal.fire({
        title: 'Thành công!',
        text: 'Đã xóa tin tức thành công!',
        icon: 'success',
        timer: 2000,
        timerProgressBar: true,
        showConfirmButton: false
      })
    } catch (err) {
      await Swal.fire({
        title: 'Lỗi!',
        text: `Lỗi khi xóa tin tức: ${err.response?.data?.error || err.message}`,
        icon: 'error',
        confirmButtonText: 'OK'
      })
    }
  }
}

const updateNews = (id) => {
  router.push(`/tinTucForm?id=${id}`)
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return dayjs.utc(date).tz('Asia/Ho_Chi_Minh').format('HH:mm DD/MM/YYYY')
}

const decodeHtmlEntities = (text) => {
  if (typeof text !== 'string') return text
  const textarea = document.createElement('textarea')
  textarea.innerHTML = text
  return textarea.value
}

const truncateContent = (content, maxLength) => {
  if (!content) return ''
  const decodedText = decodeHtmlEntities(content)
  const plainText = decodedText.replace(/<[^>]+>/g, '')
  return plainText.length > maxLength
    ? plainText.substring(0, maxLength) + '...'
    : plainText
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return `${IMAGE_BASE_URL}/fallback-image.jpg`
  return imagePath.startsWith('/images')
    ? `http://localhost:8080${imagePath}`
    : `${IMAGE_BASE_URL}/${imagePath}`
}

const handleImageError = (event) => {
  const fallbackUrl = `${IMAGE_BASE_URL}/fallback-image.jpg`
  if (event.target.src !== fallbackUrl) {
    event.target.src = fallbackUrl
  }
}
</script>

<style scoped>

/* Thêm các style từ component quản lý liên hệ */
.form-seach {
  width: 350px;
  margin-right: 10px;
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

/* Nút Sửa - Xóa */
.btn-sua {
  background: linear-gradient(135deg, #80cbc4, #a5d6a7);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(128, 203, 196, 0.25);
}

.btn-sua:hover {
  background: linear-gradient(135deg, #4db6ac, #81c784);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(77, 182, 172, 0.3);
}

.btn-xoa {
  background: linear-gradient(135deg, #f48fb1, #ffab91);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(244, 143, 177, 0.25);
}



/* Breadcrumb */
.breadcrumb {
  font-size: 0.85rem;
  background-color: transparent;
  padding: 0;
  margin: 0;
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

/* Table adjustments */
.table tbody td:nth-child(4) {
  max-width: 350px;
  white-space: normal;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

/* Cột tiêu đề (th) và nội dung (td) */
.table th:nth-child(3),
.table td:nth-child(3) {
  max-width: 250px; /* Giới hạn chiều rộng */
  white-space: normal; /* Cho phép xuống dòng */
  word-wrap: break-word; /* Ngắt từ khi quá dài */
  overflow-wrap: break-word;
}

</style>
