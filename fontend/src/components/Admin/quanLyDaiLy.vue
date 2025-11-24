
<template>
  <div class="container py-5">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý đại lý</h2>
    </div>

    <!-- Search and Filter Section -->
    <div class="d-flex mb-3 gap-3">
      <!-- Search Input -->
      <div class="form-search flex-grow-1">
        <div class="input-group">
          <input
            v-model="searchTerm"
            @input="handleSearchInput"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm theo tên đại lý hoặc email..."
          />
          <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
            <i class="bi bi-search"></i> Tìm
          </button>
        </div>
      </div>

      <!-- Sort Filter -->
      <div class="form-filter">
        <div class="input-group">
          <select v-model="sortBy" @change="handleFilter" class="form-select">
            <option value="createdAt_desc">Ngày gửi mới nhất</option>
            <option value="createdAt_asc">Ngày gửi cũ nhất</option>
            <option value="fullName_asc">Tên người gửi (A - Z)</option>
            <option value="fullName_desc">Tên người gửi (Z - A)</option>
            <option value="isApproved_desc">Đã duyệt trước</option>
            <option value="isApproved_asc">Chưa duyệt trước</option>
            <option value="createdAt_desc">Mặc định</option>
          </select>
          <button @click="handleFilter" class="btn btn-gradient-primary">
            <i class="bi bi-funnel"></i> Lọc
          </button>
        </div>
      </div>

      <!-- Status Filter -->
      <div class="form-status-filter" v-if="showStatusFilter">
        <div class="input-group">
          <select v-model="statusFilter" @change="handleFilter" class="form-select">
            <option value="">Tất cả trạng thái</option>
            <option value="true">Đã duyệt</option>
            <option value="false">Chờ duyệt</option>
          </select>
          <button @click="handleFilter" class="btn btn-gradient-primary">
            <i class="bi bi-funnel"></i> Lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr class="table-dark">
            <th class="text-center">STT</th>
            <th class="text-center">Tài khoản</th>
            <th class="text-center">Họ tên</th>
            <th class="text-center">Tên đại lý</th>
            <th class="text-center">SĐT</th>
            <th class="text-center">Email</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="agents.length > 0" v-for="(agent, index) in agents" :key="agent.id">
            <td>{{ index + 1 + currentPage * pageSize }}</td>
            <td class="text-center">{{ agent.username }}</td>
            <td class="text-center">{{ agent.fullName }}</td>
            <td class="text-center">{{ agent.agentName }}</td>
            <td class="text-center">{{ agent.phone }}</td>
            <td class="text-center">{{ agent.email }}</td>
            <td class="text-center">
              <span class="badge" :class="agent.isApproved ? 'bg-success' : 'bg-secondary'">
                {{ agent.isApproved ? 'Đã duyệt' : 'Chờ duyệt' }}
              </span>
            </td>
            <td class="d-flex justify-content-center gap-2">
              <button
                v-if="!agent.isApproved"
                class="btn btn-duyet btn-sm"
                @click="approveAgent(agent.id)"
                :disabled="approvingId === agent.id"
              >
                <span v-if="approvingId === agent.id" class="spinner-border spinner-border-sm me-1"></span>
                <i class="bi bi-check-circle me-1"></i> Duyệt
              </button>
              <button
                class="btn btn-danger btn-sm"
                @click="deleteAgent(agent.id)"
                :disabled="deletingId === agent.id"
              >
                <span v-if="deletingId === agent.id" class="spinner-border spinner-border-sm me-1"></span>
                <i class="bi bi-slash-circle me-1"></i> Ngưng
              </button>
              <button class="btn btn-info btn-sm" @click="goToApplyDiscount(agent.id)">
                <i class="bi bi-percent me-1"></i> Chiết khấu
              </button>
            </td>
          </tr>
          <tr v-else>
            <td colspan="8" class="text-center py-4">
              <i class="bi bi-exclamation-circle me-2"></i>
              Không tìm thấy đại lý nào phù hợp.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <a class="page-link" @click.prevent="prevPage">Trước</a>
        </li>
        <li
          v-for="page in displayedPages"
          :key="page"
          class="page-item"
          :class="{ active: page === currentPage + 1, disabled: page === '...' }"
        >
          <a class="page-link" @click.prevent="typeof page === 'number' ? goToPage(page) : null">
            {{ page }}
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage + 1 === totalPages }">
          <a class="page-link" @click.prevent="nextPage">Sau</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

// State
const router = useRouter()
const agents = ref([])
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = 5
const approvingId = ref(null)
const deletingId = ref(null)
const searchTerm = ref('')
const sortBy = ref('createdAt_desc')
const statusFilter = ref('')
const showStatusFilter = ref(true)
let searchTimeout = null

// Computed
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

// Methods
const goToApplyDiscount = (agentId) => {
  router.push(`/admin/apply-discount/${agentId}`)
}

const fetchAgents = async () => {
  try {
    let query = `/api/admin/agents?page=${currentPage.value}&size=${pageSize}`
    const [field, direction] = sortBy.value.split('_')
    query += `&sort=${field},${direction}`
    if (searchTerm.value) {
      query += `&keyword=${encodeURIComponent(searchTerm.value)}`
    }
    if (statusFilter.value) {
      query += `&isApproved=${statusFilter.value}`
    }
    console.log("Fetching with URL:", query)
    const res = await api.get(query)
    console.log("API Response:", res.data)
    agents.value = res.data.content || []
    totalPages.value = res.data.totalPages || 1
  } catch (err) {
    console.error("AxiosError Details:", {
      message: err.message,
      code: err.code,
      response: err.response ? {
        status: err.response.status,
        data: err.response.data,
        headers: err.response.headers
      } : null,
      request: err.request
    })
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: err.response?.data?.message || 'Không thể tải danh sách đại lý',
      confirmButtonColor: '#3a8ef9',
    })
  }
}

const approveAgent = async (id) => {
  const result = await Swal.fire({
    title: 'Bạn có chắc muốn duyệt đại lý này?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Duyệt',
    cancelButtonText: 'Hủy',
    reverseButtons: true,
    confirmButtonColor: '#3a8ef9',
    cancelButtonColor: '#dc3545'
  })

  if (result.isConfirmed) {
    approvingId.value = id
    try {
      await api.put(`/api/admin/agents/${id}/approve`)
      Swal.fire({
        icon: 'success',
        title: 'Duyệt đại lý thành công!',
        timer: 2000,
        showConfirmButton: false,
      })
      await fetchAgents()
    } catch (err) {
      Swal.fire({
        icon: 'error',
        title: 'Lỗi',
        text: err.response?.data?.message || 'Không thể duyệt đại lý',
        confirmButtonColor: '#3a8ef9',
      })
    } finally {
      approvingId.value = null
    }
  }
}

const deleteAgent = async (id) => {
  const result = await Swal.fire({
    title: 'Bạn có chắc muốn ngưng hoạt động đại lý này?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Ngưng hoạt động',
    cancelButtonText: 'Hủy',
    reverseButtons: true,
    confirmButtonColor: '#dc3545',
    cancelButtonColor: '#6c757d'
  })

  if (result.isConfirmed) {
    deletingId.value = id
    try {
      await api.delete(`/api/admin/agents/${id}`)
      Swal.fire({
        icon: 'success',
        title: 'Đã ngưng hoạt động đại lý.',
        timer: 2000,
        showConfirmButton: false,
      })
      await fetchAgents()
    } catch (err) {
      Swal.fire({
        icon: 'error',
        title: 'Lỗi',
        text: err.response?.data?.message || 'Không thể ngưng hoạt động đại lý',
        confirmButtonColor: '#3a8ef9',
      })
    } finally {
      deletingId.value = null
    }
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchAgents()
  }
}

const nextPage = () => {
  if (currentPage.value + 1 < totalPages.value) {
    currentPage.value++
    fetchAgents()
  }
}

const goToPage = (page) => {
  currentPage.value = page - 1
  fetchAgents()
}

const handleSearchInput = () => {
  console.log("Search Term:", searchTerm.value)
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0
    fetchAgents()
  }, 500)
}

const handleSearch = () => {
  console.log("Search Triggered:", searchTerm.value)
  currentPage.value = 0
  fetchAgents()
}

const handleFilter = () => {
  console.log("Filter Applied - Sort By:", sortBy.value, "Status Filter:", statusFilter.value)
  currentPage.value = 0
  fetchAgents()
}

const clearSearch = () => {
  searchTerm.value = ''
  statusFilter.value = ''
  sortBy.value = 'createdAt_desc'
  currentPage.value = 0
  fetchAgents()
}

// Lifecycle
onMounted(() => {
  fetchAgents()
})
</script>

<style scoped>
/* Pagination */
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

/* Table */
.table-responsive {
  border-radius: 8px;
  overflow: hidden;
}

/* Buttons */
.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}

.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}

.btn-duyet {
  background: linear-gradient(135deg, #28a745, #218838);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(40, 167, 69, 0.25);
}

.btn-duyet:hover {
  background: linear-gradient(135deg, #218838, #1e7e34);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(33, 136, 56, 0.3);
}

/* Badge */
.badge {
  padding: 0.35em 0.65em;
  font-size: 0.75em;
  font-weight: 600;
  border-radius: 0.25rem;
}

.bg-success {
  background-color: #28a745 !important;
}

.bg-secondary {
  background-color: #6c757d !important;
}

/* Search and Filter Spacing */
.d-flex.gap-3 {
  gap: 1rem !important;
}

.form-search,
.form-filter,
.form-status-filter {
  min-width: 200px;
}

/* Responsive */
@media (max-width: 768px) {
  td {
    white-space: nowrap;
  }

  .btn-sm {
    padding: 0.2rem 0.5rem;
    font-size: 0.7rem;
  }

  .btn-sm i {
    margin-right: 0.25rem !important;
  }
}
</style>
