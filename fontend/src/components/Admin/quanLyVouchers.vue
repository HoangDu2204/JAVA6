<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý voucher</h2>
    </div>


    <div class="d-flex mb-3 justify-content-between">
     
    <!-- Tìm kiếm + lọc + sắp xếp -->
    <div class="d-flex align-items-center mb-3 flex-wrap gap-2">
      <!-- Ô tìm kiếm -->
      <div class="search-wrapper" style="flex: 1; min-width: 200px; position: relative;">
        <input
          v-model="searchKeyword"
          type="text"
          class="form-control pe-5"
          placeholder="Tìm kiếm theo mã voucher..."
        />
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-search"></i>
        </button>
      </div>

      <!-- Lọc theo trạng thái -->
      <div class="filter-wrapper" style="width: 200px; position: relative;">
        <select v-model="statusFilter" class="form-select pe-5">
          <option value="">Tất cả trạng thái</option>
          <option value="active">Hoạt động</option>
          <option value="inactive">Không hoạt động</option>
        </select>
        <button class="btn btn-gradient-primary" type="button" style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 0.25rem 0.25rem 0;">
          <i class="bi bi-funnel"></i>
        </button>
      </div>

      <!-- Lọc theo ngày tạo -->
      <div class="filter-wrapper" style="width: 200px; position: relative;">
        <select v-model="dateFilter" class="form-select pe-5">
          <option value="">Tất cả ngày tạo</option>
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
      <div >
      <router-link to="/vouchersForm" class="btn btn-gradient-primary ms-2" >
        <i class="bi bi-plus-lg me-1"></i> Thêm voucher
      </router-link>
    </div>
    </div>





    <div  v-if="!isLoading">
        <div class="table-responsive">
          <table class="table table-bordered table-hover table-striped align-middle mb-0">
            <thead  style="color: black; background-color: #f0f0f0">
              <tr class="table-dark">
                <th class="text-center">STT</th>
                <th>Mã voucher</th>
                <th class="text-center">% Giảm</th>
                <th class="text-center">Giảm tối đa</th>
                <th class="text-center">Đơn tối thiểu</th>
                <!-- <th class="text-center">Ngày tạo</th>
                <th class="text-center">Ngày kết thúc</th> -->
                <th class="text-center">Thời gian bắt đầu</th>
                <th class="text-center">Thời gian kết thúc</th>
                <th class="text-center">Số lượng</th>
                <th class="text-center">Trạng thái</th>
                <th class="text-center">Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(voucher, index) in paginatedVouchers" :key="voucher.voucherId">
                <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
                <td>{{ voucher.code }}</td>
                <td class="text-center">{{ formatPercent(voucher.discountPercent) }}</td>
                <td class="text-center">{{ formatCurrency(voucher.maxDiscountAmount) }}</td>
                <td class="text-center">{{ formatCurrency(voucher.minOrderAmount) }}</td>
                <td class="text-center">{{ formatDateTime(voucher.createdDate) }}</td>
                <td class="text-center">{{ formatDateTime(voucher.endDate) }}</td>
                <!-- <td class="text-center">{{ formatDateTime(voucher.startTime) }}</td>
                <td class="text-center">{{ formatDateTime(voucher.endTime) }}</td> -->
                <td class="text-center">{{ voucher.quantity }}</td>
                <td class="text-center">
                  <span class="badge" :class="voucher.isActive ? 'bg-success' : 'bg-warning'">
                    {{ voucher.isActive ? 'Hoạt động' : 'Không hoạt động' }}
                  </span>
                </td>
                <td class="text-center">
                  <button
                    class="btn btn-warning btn-sm"
                    @click="updateVoucher(voucher.voucherId)"
                  >
                    <i class="bi bi-pencil-square me-1"></i> Sửa
                  </button>
                </td>
              </tr>
              <tr v-if="vouchers.length === 0">
                <td colspan="12" class="text-center">Không có voucher nào</td>
              </tr>
            </tbody>
          </table>
          <div v-if="vouchers.length === 0 && !errorMessage" class="text-center p-3">
            Không có voucher nào.
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



    <div v-else class="text-center p-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>
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

const vouchers = ref([])
const isLoading = ref(true)
const isProcessing = ref({})
const errorMessage = ref('')
const router = useRouter()

const searchKeyword = ref('')

const statusFilter = ref('')
const dateFilter = ref('')
const customDateFrom = ref('')
const customDateTo = ref('')

const sortedVouchers = computed(() => {
  let sorted = [...vouchers.value]
  sorted.sort((a, b) => {
    const dateA = new Date(a.createdDate)
    const dateB = new Date(b.createdDate)

    // Sort by createdDate in descending order (newest first)
    if (dateA.getTime() !== dateB.getTime()) {
      return dateB.getTime() - dateA.getTime()
    }

    // If createdDate is the same, sort by voucherId in descending order (highest ID first)
    return b.voucherId - a.voucherId
  })
  return sorted
})

const filteredVouchers = computed(() => {
  const keyword = searchKeyword.value.toLowerCase().trim()
  let result = [...sortedVouchers.value]

  // Lọc theo từ khóa
  if (keyword) {
    result = result.filter(item =>
      item.code.toLowerCase().includes(keyword)
    )
  }

  // Lọc theo trạng thái
  if (statusFilter.value) {
    result = result.filter(item => {
      if (statusFilter.value === 'active') {
        return item.isActive
      } else if (statusFilter.value === 'inactive') {
        return !item.isActive
      }
      return true
    })
  }

  // Lọc theo ngày tạo
  if (dateFilter.value) {
    const today = dayjs().startOf('day')
    result = result.filter(item => {
      const itemDate = dayjs(item.createdDate)
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
const itemsPerPage = 7
const totalPages = computed(() => Math.ceil(filteredVouchers.value.length / itemsPerPage))
const maxVisiblePages = 7
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

const paginatedVouchers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return filteredVouchers.value.slice(start, start + itemsPerPage)
})

const nextPage = () => { if (currentPage.value < totalPages.value) currentPage.value++ }
const prevPage = () => { if (currentPage.value > 1) currentPage.value-- }
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

watch([searchKeyword, statusFilter, dateFilter, customDateFrom, customDateTo], () => {
  currentPage.value = 1
})

watch(filteredVouchers, () => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value || 1
  }
})

// Tải danh sách voucher
const loadVouchers = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const { data } = await api.get('/api/vouchers')
    vouchers.value = Array.isArray(data)
      ? data.map(item => ({
          ...item,
          createdDate: item.createdDate ? new Date(item.createdDate) : null,
          endDate: item.endDate ? new Date(item.endDate) : null,
          // startTime: item.startTime ? new Date(item.startTime) : null,
          // endTime: item.endTime ? new Date(item.endTime) : null
        }))
      : []
  } catch (err) {
    errorMessage.value = 'Không thể tải danh sách voucher: ' +
      (err.response?.data?.message || err.message)
    await Swal.fire({
      title: 'Lỗi!',
      text: errorMessage.value,
      icon: 'error',
      confirmButtonText: 'OK'
    })
  } finally {
    isLoading.value = false
  }
}

// Xoá voucher
const deleteVoucher = async (id) => {
  if (!id) return

  const result = await Swal.fire({
    title: 'Xác nhận xóa',
    text: 'Bạn có chắc chắn muốn xóa voucher này không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#dc3545',
    confirmButtonText: 'OK',
    cancelButtonText: 'Hủy'
  })

  if (result.isConfirmed) {
    try {
      isProcessing.value[id] = true
      await api.delete(`/api/vouchers/${id}`)
      await loadVouchers()
      await Swal.fire({
        title: 'Thành công!',
        text: 'Đã xóa voucher thành công!',
        icon: 'success',
        confirmButtonText: 'OK',
        timer: 2000
      })
    } catch (err) {
      await Swal.fire({
        title: 'Lỗi!',
        text: 'Lỗi: ' + (err.response?.data?.message || err.message),
        icon: 'error',
        confirmButtonText: 'OK'
      })
    } finally {
      isProcessing.value[id] = false
    }
  }
}

// Điều hướng sang form chỉnh sửa
const updateVoucher = (id) => {
  if (!id) {
    Swal.fire({
      title: 'Lỗi!',
      text: 'ID voucher không hợp lệ.',
      icon: 'error',
      confirmButtonText: 'OK'
    })
    return
  }
  router.push(`/vouchersForm?id=${id}`)
}

// Format ngày, phần trăm, tiền tệ
const formatDate = (date) =>
  !date ? 'N/A' : new Date(date).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })

const formatDateTime = (date) =>
  !date ? 'N/A' : new Date(date).toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })

const formatPercent = (value) => (!value ? '0%' : `${value}%`)

const formatCurrency = (value) =>
  !value
    ? '0đ'
    : new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
      }).format(value)

onMounted(loadVouchers)
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

/* Spinner */
.spinner-border {
  width: 3rem;
  height: 3rem;
}



.text-danger {
  color: #dc3545 !important;
}
</style>


