<template>
  <div class="p-4">
    <h4 class="mb-3" style="color: #343a40;">Quản Lý Đơn Hàng</h4>

    <!-- Bộ lọc -->
    <div class="d-flex align-items-center gap-2 mb-3 flex-wrap">
      <!-- Trạng thái đơn hàng -->
      <div class="form-group">
        <select v-model="selectedStatus" class="form-select" style="padding-right: 100px;">
          <option value="">Tất cả trạng thái</option>
          <option value="Chờ xác nhận">Chờ Xác Nhận</option>
          <option value="Chờ giao hàng">Chờ Giao Hàng</option>
          <option value="Đã giao hàng">Đã Giao Hàng</option>
          <option value="Đã hủy">Đã Hủy</option>
        </select>
      </div>

      <!-- Loại khách hàng -->
      <div class="form-group">
        <select v-model="selectedType" class="form-select" style="padding-right: 100px;">
          <option value="">Tất cả khách hàng</option>
          <option value="user">Khách hàng thường</option>
          <option value="agent">Đại lý</option>
        </select>
      </div>

      <!-- Nút reset -->
    

       <button class="btn btn-secondary mb-2" @click="resetFilters" title="Đặt lại bộ lọc" style="height: 45px;">
        <i class="bi bi-arrow-counterclockwise"></i>
      </button>
    </div>

    <!-- Bảng đơn hàng -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover text-center align-middle">
        <thead style="color: black; background-color: #f0f0f0;">
          <tr>
            <th>STT</th>
            <th>Tên khách hàng / Đại lý</th>
            <th>Thời gian đặt hàng</th>
            <th>Trạng thái đơn hàng</th>
            <th>Phương thức thanh toán</th>
            <th>Tổng tiền</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(order, index) in orders" :key="order.id">
            <td>{{ index + 1 + currentPage * pageSize }}</td>
            <td>{{ order.name }}</td>
            <td>{{ formatDate(order.orderDate) }}</td>
            <td>{{ order.orderStatus }}</td>
            <td>{{ order.paymentMethod }}</td>
            <td class="text-danger fw-bold">{{ formatCurrency(order.finalTotal) }}</td>
            <td>
              <RouterLink :to="`/order-detail/${order.id}`" class="btn btn-sm btn-primary">
                Xem Chi Tiết
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <nav class="mt-3">
      <ul class="pagination justify-content-center pagination-sm">
        <!-- Nút trước -->
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button class="page-link" @click="prevPage">&laquo;</button>
        </li>

        <!-- Số trang -->
        <li
          v-for="page in totalPages"
          :key="page"
          class="page-item"
          :class="{ active: page - 1 === currentPage }"
        >
          <button class="page-link" @click="goToPage(page - 1)">
            {{ page }}
          </button>
        </li>

        <!-- Nút sau -->
        <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
          <button class="page-link" @click="nextPage">&raquo;</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/axios'

const orders = ref([])
const currentPage = ref(0)
const pageSize = ref(5)
const totalPages = ref(1)

// Lọc theo trạng thái đơn hàng và loại người đặt
const selectedStatus = ref('')
const selectedType = ref('')

// Gọi API lấy danh sách đơn hàng
const fetchOrders = async () => {
  try {
    const res = await api.get('/api/orders/admin', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        status: selectedStatus.value || null,
        type: selectedType.value || null
      }
    })
    orders.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch (error) {
    console.error('❌ Lỗi khi tải danh sách đơn hàng:', error)
  }
}

// Định dạng ngày
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.toLocaleTimeString('vi-VN')} - ${date.toLocaleDateString('vi-VN')}`
}

// Định dạng tiền
const formatCurrency = (amount) => {
  if (!amount) return '0 đ'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

// Chuyển trang
const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchOrders()
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchOrders()
  }
}

const goToPage = (page) => {
  currentPage.value = page
  fetchOrders()
}

// Reset filter
const resetFilters = () => {
  selectedStatus.value = ''
  selectedType.value = ''
  currentPage.value = 0
  fetchOrders()
}

// Theo dõi thay đổi filter
watch([selectedStatus, selectedType, pageSize], () => {
  currentPage.value = 0
  fetchOrders()
})

// Lần đầu load
onMounted(fetchOrders)
</script>

<style scoped>
.table th,
.table td {
  vertical-align: middle;
}

/* Phân trang tròn đẹp */
.pagination .page-link {
  border-radius: 20% !important;
  margin: 0 3px;
  width: 36px;
  height: 36px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
  color: white;
  font-weight: bold;
  box-shadow: 0 0 6px rgba(13, 110, 253, 0.4);
}

</style>
