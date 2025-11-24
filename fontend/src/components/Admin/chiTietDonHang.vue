<template>
  <div class="container my-4" v-if="order">
    <h4 class="mb-4">Chi tiết đơn hàng #{{ orderId }}</h4>

    <!-- Thông tin khách hàng -->
    <div class="mb-4">
      <h6 class="fw-bold">Thông Tin Khách Hàng</h6>
      <table class="table table-bordered text-center">
        <thead class="table-light">
          <tr>
            <th>Tên tài khoản</th>
            <!-- <th>Tên khách hàng</th> -->
            <th>{{ order?.agent ? 'Tên đại lý' : 'Tên khách hàng' }}</th>


            <th>Số điện thoại</th>
            <th>Địa chỉ nhận hàng</th>
            <th>Ghi chú</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ order.username }}</td>
            <td>{{ order.name }}</td>
            <td>{{ order.phone }}</td>
            <td>{{ order.address }}</td>
            <td>{{ order.note || 'Không có' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Danh sách sản phẩm -->
    <div class="mb-4">
      <h6 class="fw-bold">Danh Sách Sản Phẩm</h6>
      <table class="table table-bordered text-center align-middle">
        <thead class="table-light">
          <tr>
            <th>Hình ảnh</th>
            <th>Tên sản phẩm</th>
            <th>Số lượng</th>
            <th>Biến thể</th>
            <th>Đơn giá</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in order.items" :key="item.productId">
            <td>
              <img :src="'http://localhost:8080/images/' + item.productImage" alt="SP" width="80" />
            </td>
            <td>{{ item.productName }}</td>
            <td>{{ item.quantity }}</td>
            <td>
              <div v-if="item.size">Kích thước: {{ item.size }}</div>
              <div v-if="item.flavor">Hương vị: {{ item.flavor }}</div>
              <div v-if="item.shape">Hình dạng: {{ item.shape }}</div>
              <div v-if="item.origin">Xuất xứ: {{ item.origin }}</div>
            </td>
            <td>{{ formatCurrency(item.unitPrice) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Trạng thái & Tổng tiền -->
    <div class="row">
      <div class="col-md-6">
        <div class="mb-3">
          <label class="fw-bold">Phương thức thanh toán:</label>
          <div>
            <span class="badge fs-6" :class="order.paymentMethod === 'COD' ? 'bg-success' : 'bg-secondary'">
              {{ order.paymentMethod }}
            </span>
          </div>
        </div>

        <div class="mb-3">
          <label class="fw-bold">Trạng thái đơn hàng:</label>
          <div class="d-flex gap-2 flex-wrap mt-2">
            <input type="radio" class="btn-check" name="status" id="cho-xn" value="Chờ xác nhận"
              v-model="selectedStatus" />
            <label class="btn btn-outline-primary" for="cho-xn">Chờ xác nhận</label>

            <input type="radio" class="btn-check" name="status" id="cho-gh" value="Chờ giao hàng"
              v-model="selectedStatus" />
            <label class="btn btn-outline-success" for="cho-gh">Chờ giao hàng</label>

            <input type="radio" class="btn-check" name="status" id="da-giao" value="Đã giao hàng"
              v-model="selectedStatus" />
            <label class="btn btn-outline-secondary" for="da-giao">Đã giao hàng</label>

            <input type="radio" class="btn-check" name="status" id="da-huy" value="Đã hủy" v-model="selectedStatus" />
            <label class="btn btn-outline-danger" for="da-huy">Đã hủy</label>
          </div>
        </div>
      </div>

      <div class="col-md-6 border rounded p-3">
        <p><strong>Tổng tiền sản phẩm:</strong> {{ formatCurrency(order.totalAmount) }}</p>
        <p><strong>Phí vận chuyển:</strong> {{ formatCurrency(order.shippingFee) }}</p>
        <p v-if="!order.agent && order.discount > 0">
          <strong>Giảm giá voucher:</strong> {{ formatCurrency(order.discount) }}
        </p>
        <!-- <p><strong>Giảm giá voucher:</strong> {{ formatCurrency(order.discount) }}</p> -->
        <!-- <p><strong>Giảm giá đại lý:</strong> {{ formatCurrency(order.discountAmount) }}</p> -->
        <p v-if="order.agent"><strong>Giảm giá đại lý:</strong> {{ formatCurrency(order.discountAmount) }}</p>

        <p class="fw-bold text-danger">Tổng cộng: {{ formatCurrency(order.finalTotal) }}</p>

        <button class="btn btn-primary btn-sm mt-2" @click="updateOrderStatus">Cập nhật</button>


        <!-- Nếu có yêu cầu hủy -->
        <button v-if="cancelRequest && cancelRequest.status === 'Chờ duyệt'" class="btn btn-danger btn-sm mt-2 ms-2"
          @click="showCancelPopup = true">
          Xử lý yêu cầu hủy
        </button>





        <div v-if="showCancelPopup" class="popup-overlay">
          <div class="popup">
            <h5 class="fw-bold">Xử lý yêu cầu hủy</h5>
            <p><strong>Lý do:</strong> {{ cancelRequest.reason }}</p>
            <div class="d-flex justify-content-end gap-2 mt-3">
              <button class="btn btn-success btn-sm" @click="handleCancelRequest('approve')" :disabled="isProcessing">
                Duyệt
              </button>
              <button class="btn btn-secondary btn-sm" @click="handleCancelRequest('reject')" :disabled="isProcessing">
                Từ chối
              </button>
              <button class="btn btn-outline-dark btn-sm" @click="showCancelPopup = false" :disabled="isProcessing">
                Đóng
              </button>
            </div>
          </div>
        </div>



      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/axios'
import Swal from 'sweetalert2'

const route = useRoute()
const orderId = route.params.id

const order = ref(null)
const selectedStatus = ref('')
const cancelRequest = ref(null)
const showCancelPopup = ref(false)

// Format tiền tệ
const formatCurrency = (value) => {
  if (value == null) return '0 đ'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

// Gọi API lấy chi tiết đơn hàng + kiểm tra có yêu cầu hủy không
const fetchOrderDetail = async () => {
  try {
    console.log('==> orderId from route:', orderId)

    // 1. Lấy chi tiết đơn hàng
    const res = await api.get(`/api/order-details/${orderId}`)
    order.value = res.data
    selectedStatus.value = res.data.orderStatus
    console.log('==> order detail:', res.data)

    // 2. Lấy tất cả yêu cầu hủy
    const cancelRes = await api.get(`/api/cancel-requests`)
    console.log('==> cancel-requests:', cancelRes.data)

    // 3. Tìm yêu cầu hủy theo orderId
    cancelRequest.value = cancelRes.data.find(r => {
      console.log('--> So sánh:', r.order?.id, '===', parseInt(orderId), r.order?.id === parseInt(orderId))
      return r?.order?.id === parseInt(orderId)
    })


    console.log('==> cancelRequest matched:', cancelRequest.value)

  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu:', err)
    Swal.fire('Lỗi', 'Không thể tải dữ liệu', 'error')
  }
}

// Cập nhật trạng thái đơn hàng
const updateOrderStatus = async () => {
  const current = order.value?.orderStatus
  const next = selectedStatus.value

  const steps = ['Chờ xác nhận', 'Chờ giao hàng', 'Đã giao hàng', 'Đã hủy']
  const currentIndex = steps.indexOf(current)
  const nextIndex = steps.indexOf(next)

  if (next === current) {
    return Swal.fire('', 'Bạn đang chọn trạng thái hiện tại.', 'warning')
  }

  if (nextIndex < currentIndex) {
    return Swal.fire('Không hợp lệ', 'Không thể cập nhật lùi trạng thái đơn hàng.', 'error')
  }

  if (nextIndex - currentIndex > 1) {
    return Swal.fire('Không hợp lệ', 'Bạn chỉ có thể chuyển trạng thái từng bước một.', 'error')
  }

  if (current === 'Đã giao hàng' && next === 'Đã hủy') {
    return Swal.fire('', 'Đơn hàng đã được giao. Không thể hủy.', 'error')
  }

  try {
    await api.put(`/api/order-details/${orderId}/orderStatus`, null, {
      params: { orderStatus: next }
    })
    order.value.orderStatus = next
    Swal.fire('Thành công', 'Cập nhật trạng thái thành công!', 'success')
  } catch (err) {
    console.error('Cập nhật thất bại:', err)
    Swal.fire('', 'Cập nhật thất bại. Vui lòng thử lại.', 'error')
  }
}
const isProcessing = ref(false)


const handleCancelRequest = async (action) => {
  if (isProcessing.value) return // Ngăn bấm liên tục

  isProcessing.value = true
  try {
    const url = `/api/cancel-requests/${cancelRequest.value.id}/${action}`
    await api.put(url)

    if (action === 'approve') {
      await api.put(`/api/order-details/${orderId}/orderStatus`, null, {
        params: { orderStatus: 'Đã hủy' }
      })
      order.value.orderStatus = 'Đã hủy'
    }

    Swal.fire('Thành công', `Yêu cầu đã được ${action === 'approve' ? 'duyệt' : 'từ chối'}.`, 'success')
    showCancelPopup.value = false
    cancelRequest.value = null
  } catch (err) {
    console.error('Lỗi xử lý yêu cầu hủy:', err)
    Swal.fire('Lỗi', 'Xử lý thất bại. Vui lòng thử lại.', 'error')
  } finally {
    isProcessing.value = false
  }
}


// Gọi khi component được mounted
onMounted(fetchOrderDetail)
</script>

<style scoped>
.table th,
.table td {
  vertical-align: middle;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.popup {
  max-width: 400px;
  width: 90%;
  background: white;
  border-radius: 8px;
  padding: 20px;
}
</style>
