<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý chương trình giảm giá</h2>
    </div>

    <!-- Tìm kiếm + Thêm mới -->
    <div class="d-flex justify-content-between mb-3">
      <div class="d-flex gap-2">
        <!-- Tìm kiếm -->
        <div class="form-search">
          <div class="input-group" style="width: 200px; font-size: 1.1rem;">
  <input
    v-model="searchKeyword"
    type="text"
    class="form-control"
    placeholder="Tìm kiếm theo tên..."
    style="height: 45px; font-size: 1rem;"
  />
  <button
    class="btn btn-gradient-primary"
    type="button"
    style="height: 45px; font-size: 1.1rem;"
  >
    <i class="bi bi-search"></i>
  </button>
</div>

        </div>

        <!-- Lọc theo ngày -->
        <div class="form-filter">
  <div class="filter-wrapper" style="width: 200px; position: relative;">
    <select v-model="dateFilter" class="form-select pe-5" style="border-radius: 8px;">
      <option value="">Tất cả ngày</option>
      <option value="today">Hôm nay</option>
      <option value="7days">7 ngày qua</option>
      <option value="month">Tháng này</option>
      <option value="custom">Khoảng thời gian...</option>
    </select>
    <button
      class="btn btn-gradient-primary"
      type="button"
      style="position: absolute; right: 0; top: 0; height: 100%; border-radius: 0 8px 8px 0;"
    >
      <i class="bi bi-funnel"></i>
    </button>
  </div>
</div>


        <!-- Lọc theo trạng thái -->
        <div class="input-group">
          <select v-model="statusFilter" class="form-select">
            <option value="">Mặc định</option>
            <option value="active">Hoạt động</option>
            <option value="inactive">Ngừng hoạt động</option>
          </select>
          <button class="btn btn-gradient-primary">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <!-- Nút thêm mới -->
      <button class="btn btn-gradient-primary ms-2" @click="addNewDiscount">
        <i class="bi bi-plus-lg me-1"></i> Thêm chương trình
      </button>
    </div>

    <!-- Bảng danh sách -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead style="color: black; background-color: #f0f0f0">
          <tr>
            <th>STT</th>
            <th>Tên chương trình</th>
            <th class="text-center">Phần trăm</th>
            <th class="text-center">Ngày bắt đầu</th>
            <th class="text-center">Ngày kết thúc</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item,index) in paginatedDiscounts" :key="item.discountId">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td>{{ item.name }}</td>
            <td class="text-center">{{ item.percentage }}%</td>
            <td class="text-center">{{ formatDate(item.startDate) }}</td>
            <td class="text-center">{{ formatDate(item.endDate) }}</td>
            <td class="text-center">
              <span class="badge" :class="item.isActive ? 'bg-success' : 'bg-secondary'">
                {{ item.isActive ? 'Hoạt động' : 'Ẩn' }}
              </span>
            </td>
            <td class="d-flex justify-content-center">
              <button class="btn btn-warning btn-sm me-2" @click="editDiscount(item)">
                <i class="bi bi-pencil-square me-1"></i> Sửa
              </button>
              <!-- <button class="btn btn-success btn-sm me-2" @click="applyToProducts(item)">
                <i class="bi bi-tags me-1"></i> Áp dụng
              </button> -->
              <button
                class="btn btn-sm"
                :class="item.isActive ? 'btn-danger' : 'btn-info'"
                @click="item.isActive ? hideDiscount(item.discountId) : showDiscount(item.discountId)"
              >
                <i :class="item.isActive ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                {{ item.isActive ? 'Ẩn' : 'Hiện' }}
              </button>
            </td>
          </tr>
          <tr v-if="paginatedDiscounts.length === 0">
            <td colspan="7" class="text-center">Không có chương trình giảm giá nào</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click="prevPage">Trước</a>
        </li>

        <li
          v-for="page in displayedPages"
          :key="page"
          class="page-item"
          :class="{ active: page === currentPage, disabled: page === '...' }"
        >
          <a class="page-link" @click="typeof page === 'number' && goToPage(page)">
            {{ page }}
          </a>
        </li>

        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click="nextPage">Sau</a>
        </li>
      </ul>
    </nav>

    <!-- Modal áp dụng giảm giá -->
    <!-- <div v-if="showApplyPanel" class="modal-backdrop">
      <div class="modal-content shadow-lg rounded p-4">
        <h5 class="mb-3">
          Áp dụng giảm giá: <span class="text-primary">{{ selectedDiscount.name }}</span>
        </h5>

        <div class="mb-3" style="max-height: 300px; overflow-y: auto">
          <div class="form-check" v-for="product in productList" :key="product.id">
            <input
              type="checkbox"
              :value="product.id"
              v-model="selectedProductIds"
              :disabled="product.discounts?.some(d => d.id === selectedDiscount.discountId)"
              :id="'product-' + product.id"
              class="custom-checkbox"
            />
            <label
              class="form-check-label"
              :for="'product-' + product.id"
              :class="{ 'text-muted': product.discounts?.some(d => d.id === selectedDiscount.discountId) }"
            >
              {{ product.name }}
            </label>
          </div>
        </div>

        <div class="mb-2">
          <strong>Sản phẩm đã chọn:</strong>
          <div class="text-muted small">
            {{ selectedProductIds.map(id => productList.find(p => p.id === id)?.name).join(', ') || 'Chưa chọn sản phẩm nào' }}
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-3">
          <button class="btn btn-secondary" @click="showApplyPanel = false">Hủy</button>
          <button class="btn btn-success" @click="confirmApplyDiscount">Xác nhận áp dụng</button>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

const router = useRouter()

const discountPrograms = ref([])
const searchKeyword = ref('')
const dateFilter = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = 5

const showApplyPanel = ref(false)
const selectedDiscount = ref({})
const productList = ref([])
const selectedProductIds = ref([])

// Lọc dữ liệu
const filteredDiscounts = computed(() => {
  let list = [...discountPrograms.value]

  // Lọc theo từ khóa
  if (searchKeyword.value.trim()) {
    list = list.filter(d =>
      d.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // Lọc theo trạng thái
  if (statusFilter.value) {
    list = list.filter(d =>
      statusFilter.value === 'active' ? d.isActive : !d.isActive
    )
  }

  // Lọc theo ngày
  const now = new Date()
  if (dateFilter.value === 'today') {
    list = list.filter(d => new Date(d.startDate).toDateString() === now.toDateString())
  } else if (dateFilter.value === '7days') {
    const weekAgo = new Date()
    weekAgo.setDate(now.getDate() - 7)
    list = list.filter(d => new Date(d.startDate) >= weekAgo)
  } else if (dateFilter.value === 'month') {
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
    list = list.filter(d => new Date(d.startDate) >= monthStart)
  }

  return list
})

const totalPages = computed(() => Math.ceil(filteredDiscounts.value.length / pageSize))
const paginatedDiscounts = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredDiscounts.value.slice(start, start + pageSize)
})

const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...', total)
    } else if (current >= total - 2) {
      pages.push(1, '...')
      for (let i = total - 4; i <= total; i++) pages.push(i)
    } else {
      pages.push(1, '...', current - 1, current, current + 1, '...', total)
    }
  }
  return pages
})

const fetchDiscountPrograms = async () => {
  try {
    const res = await api.get('/api/quanLyGiamGia')
    discountPrograms.value = res.data
  } catch (error) {
    console.error(error)
    Swal.fire('Lỗi', 'Không thể tải danh sách chương trình giảm giá', 'error')
  }
}

const formatDate = dateStr => new Date(dateStr).toLocaleDateString('vi-VN')
const editDiscount = item => router.push({ path: '/giamGiaForm', query: { id: item.discountId } })
const addNewDiscount = () => router.push({ path: '/giamGiaForm' })

const applyToProducts = async item => {
  selectedDiscount.value = item
  showApplyPanel.value = true
  try {
    const res = await api.get('/api/quanLyGiamGia/products')
    productList.value = res.data
    selectedProductIds.value = selectedProductIds.value.filter(id => {
      const product = productList.value.find(p => p.id === id)
      return !product?.discounts?.some(d => d.id === item.discountId)
    })
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', 'Không thể tải danh sách sản phẩm', 'error')
  }
}

const confirmApplyDiscount = async () => {
  if (selectedProductIds.value.length === 0) {
    return Swal.fire('Thông báo', 'Vui lòng chọn ít nhất một sản phẩm!', 'warning')
  }

  try {
    await api.post('/api/product-discounts/apply', {
      discountId: selectedDiscount.value.discountId,
      productIds: selectedProductIds.value
    })
    Swal.fire('Thành công', 'Áp dụng giảm giá thành công!', 'success')
    showApplyPanel.value = false
    selectedProductIds.value = []
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', 'Không thể áp dụng giảm giá', 'error')
  }
}

const hideDiscount = async id => {
  const result = await Swal.fire({
    title: 'Bạn có chắc muốn ẩn chương trình này?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Có, ẩn ngay'
  })
  if (result.isConfirmed) {
    await api.put(`/api/quanLyGiamGia/${id}/deactivate`)
    const item = discountPrograms.value.find(d => d.discountId === id)
    if (item) item.isActive = false
    Swal.fire('Đã ẩn', 'Chương trình đã được ẩn', 'success')
  }
}

const showDiscount = async id => {
  const result = await Swal.fire({
    title: 'Bạn có chắc muốn hiển thị lại?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Hiện lại'
  })
  if (result.isConfirmed) {
    await api.put(`/api/quanLyGiamGia/${id}/activate`)
    const item = discountPrograms.value.find(d => d.discountId === id)
    if (item) item.isActive = true
    Swal.fire('Đã hiện', 'Chương trình đã được hiển thị lại', 'success')
  }
}

const nextPage = () => currentPage.value < totalPages.value && currentPage.value++
const prevPage = () => currentPage.value > 1 && currentPage.value--
const goToPage = page => { currentPage.value = page }

onMounted(fetchDiscountPrograms)
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

/* Nút Sửa */
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

/* Nút Áp dụng */
.btn-apdung {
  background: linear-gradient(135deg, #a5d6a7, #80cbc4);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(165, 214, 167, 0.25);
}

.btn-apdung:hover {
  background: linear-gradient(135deg, #81c784, #4db6ac);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(129, 199, 132, 0.3);
}

/* Nút Ẩn/Xóa */
.btn-xoa {
  background: linear-gradient(135deg, #f48fb1, #ffab91);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(244, 143, 177, 0.25);
}

.btn-xoa:hover {
  background: linear-gradient(135deg, #ec407a, #ff8a65);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(236, 64, 122, 0.3);
}

/* Nút Hiện/Mở khóa */
.btn-mo-khoa {
  background: linear-gradient(135deg, #64b5f6, #90caf9);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(100, 181, 246, 0.25);
}

.btn-mo-khoa:hover {
  background: linear-gradient(135deg, #42a5f5, #64b5f6);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(66, 165, 245, 0.3);
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

/* Modal */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 900 !important;
}

.modal-content {
  background: white;
  width: 900px;
  max-width: 90%;
  z-index: 901 !important;
}

.custom-checkbox {
  width: 20px;
  height: 20px;
  accent-color: #3a8ef9;
  margin-right: 8px;
  cursor: pointer;
}
</style>
