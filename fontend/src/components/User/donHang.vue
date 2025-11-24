<template>
  <div class="lien-he">
    <main>
      <section class="bread-crumb"
        style="background: linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)), url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436) center no-repeat;">
        <div class="container">
          <div class="title-bread-crumb ps-5">Đơn hàng</div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/">Trang chủ ></a>
              <a href="/Cart"> Đơn hàng</a>
            </li>
          </ul>
        </div>
      </section>
    </main>

    <div class="container my-5">
      <h3 class="text-primary pt-3">Đơn hàng của bạn</h3>
      <div class="container-fluid mt-3 border mt-4">
        <ul class="nav nav-tabs pt-2">
          <li class="nav-item" v-for="status in statuses" :key="status.value">
            <a :class="['nav-link', activeTab === status.value ? 'active' : '']" @click="activeTab = status.value"
              style="font-weight: bolder">{{ status.label }}</a>
          </li>
        </ul>

        <div class="tab-content">
          <div class="tab-pane fade show active">
            <div class="card-body pb-1 border rounded px-4 mb-2 mt-4">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Ngày đặt</th>
                    <th>Địa chỉ</th>
                    <th>Trạng thái đơn hàng</th>
                    <th>Phương thức thanh toán</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in filteredOrders" :key="order.id">
                    <td>{{ order.id }}</td>
                    <td>{{ formatDate(order.orderDate) }}</td>
                    <td>{{ order.address }}</td>
                    <td>{{ order.orderStatus }}</td>
                    <td>{{ order.paymentMethod }}</td>
                    <td>
                      <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal"
                        data-bs-target="#orderDetailModal" @click="showOrderDetail(order.id)">Xem Chi Tiết</button>
                    </td>
                  </tr>
                  <tr v-if="filteredOrders.length === 0">
                    <td colspan="6" class="text-center text-muted">Không có đơn hàng nào</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="orderDetailModal" tabindex="-1">
      <div class="modal-dialog modal-xl modal-dialog-scrollable">
        <div class="modal-content" v-if="selectedOrder">
          <div class="modal-header bg-primary">
            <h5 class="modal-title text-white">Chi tiết đơn hàng #{{ selectedOrder.id }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <h6 class="fw-bold mb-2">Thông tin khách hàng</h6>
            <table class="table table-bordered text-center">
              <thead class="table-light">
                <tr>
                  <th>Tài khoản</th>
                  <th>{{ selectedOrder?.agent ? 'Tên đại lý' : 'Tên khách hàng' }}</th>

                  <th>Số điện thoại</th>
                  <th>Địa chỉ</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{{ selectedOrder.username }}</td>
                  <td>{{ selectedOrder.name }}</td>
                  <td>{{ selectedOrder.phone }}</td>
                  <td>{{ selectedOrder.address }}</td>
                </tr>
              </tbody>
            </table>

            <h6 class="fw-bold mt-4 mb-2">Thông tin sản phẩm</h6>
            <table class="table table-bordered text-center">
              <thead class="table-light">
                <tr>
                  <th>Hình ảnh</th>
                  <th>Sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Phân loại</th>
                  <th>Đơn giá</th>
                  <th v-if="selectedOrder.orderStatus === 'Đã giao hàng'">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedOrder.items" :key="item.productName">
                  <td>
                    <img :src="'http://localhost:8080/images/' + item.productImage" alt="Ảnh sản phẩm" width="80"
                      height="80" />
                  </td>
                  <td>{{ item.productName }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ [item.size, item.flavor, item.shape, item.origin].filter(Boolean).join(' - ') }}</td>
                  <td>{{ item.unitPrice.toLocaleString() }} ₫</td>
                  <!-- Rating button - only show when order is delivered -->
                  <!-- Rating button - only show when order is delivered -->
<td v-if="selectedOrder.orderStatus === 'Đã giao hàng' && item.orderItemId">
  <!-- Nếu đã đánh giá -->
  <span v-if="ratingStatuses[item.orderItemId]?.hasRating" class="btn btn-sm btn-success ">
   Đã đánh giá
  </span>
  <!-- Nếu chưa đánh giá -->
  <button v-else class="btn btn-sm btn-outline-primary" @click="goToRating(item.orderItemId)">
    <i class="fas fa-star me-1"></i>Đánh giá sản phẩm
  </button>
</td>

<td v-else-if="selectedOrder.orderStatus === 'Đã giao hàng' && !item.orderItemId">
  <span class="text-muted small">Không thể đánh giá</span>
</td>
                </tr>
              </tbody>
            </table>

            <h6 class="fw-bold mt-4 mb-2">Thông tin thanh toán</h6>

            <ul class="list-unstyled ms-3">
              <li>- Tổng tiền sản phẩm: {{ selectedOrder.totalAmount.toLocaleString() }} ₫</li>
              <li v-if="selectedOrder.discount > 0">- Giảm giá: {{ selectedOrder.discount.toLocaleString() }} ₫</li>
              <li v-if="selectedOrder.discountAmount > 0">- Giảm giá đại lý: {{
                selectedOrder.discountAmount.toLocaleString() }} ₫</li>
              <li>- Phí vận chuyển: {{ selectedOrder.shippingFee.toLocaleString() }} ₫</li>
              <li class="fw-bold text-danger">Tổng thanh toán: {{ selectedOrder.finalTotal.toLocaleString() }} ₫</li>
            </ul>

            <div class="mt-4 d-flex gap-2">
              <!-- <button v-if="['Chờ xác nhận', 'Chờ giao hàng'].includes(selectedOrder.orderStatus)" class="btn btn-danger" @click="confirmUpdateStatus('Đã hủy', 'Bạn có chắc muốn hủy đơn hàng này?')">Hủy đơn hàng</button> -->
              <!-- Nút hủy đơn hàng -->
              <!-- <button
  v-if="['Chờ xác nhận', 'Chờ giao hàng'].includes(selectedOrder.orderStatus)"
  class="btn btn-danger"
  data-bs-toggle="modal"
  data-bs-target="#cancelModal"
>
  Hủy đơn hàng
</button> -->
              <!-- Hủy đơn hàng trực tiếp khi ở trạng thái 'Chờ xác nhận' -->
              <button v-if="selectedOrder.orderStatus === 'Chờ xác nhận'" class="btn btn-danger"
                @click="confirmUpdateStatus('Đã hủy', 'Bạn có chắc muốn hủy đơn hàng này?')">
                Hủy đơn hàng
              </button>

              <!-- Hủy đơn hàng qua modal khi ở trạng thái 'Chờ giao hàng' -->
              <button v-else-if="selectedOrder.orderStatus === 'Chờ giao hàng'" class="btn btn-danger"
                data-bs-toggle="modal" data-bs-target="#cancelModal">
                Hủy đơn hàng
              </button>









              <button v-else-if="selectedOrder.orderStatus === 'Đã giao hàng'" class="btn btn-success"
                @click="confirmUpdateStatus('Hoàn tất', 'Bạn có chắc đã nhận được đơn hàng này?')">
                Xác nhận đã nhận hàng
              </button>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          </div>
        </div>
      </div>
    </div>
  </div>


  <!-- Modal nhập lý do hủy -->
  <div class="modal fade" id="cancelModal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header bg-danger">
          <h5 class="modal-title text-white">Lý do hủy đơn hàng</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <textarea class="form-control" v-model="cancelReason" rows="3" placeholder="Nhập lý do hủy đơn..."></textarea>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          <button class="btn btn-danger" @click="submitCancelRequest">Gửi yêu cầu</button>
        </div>
      </div>
    </div>
  </div>

</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/axios' //  axios custom interceptor đã cấu hình
import Swal from 'sweetalert2'

const orders = ref([])
const selectedOrder = ref(null)
const activeTab = ref('Chờ xác nhận')
const router = useRouter()
const ratingStatuses = ref({}) // { orderItemId: { hasRating: boolean, rating: object } }
//const isCanceling = ref(false)

const statuses = [
  { label: 'Chờ xác nhận', value: 'Chờ xác nhận' },
  { label: 'Chờ giao hàng', value: 'Chờ giao hàng' },
  { label: 'Đã giao hàng', value: 'Đã giao hàng' },
  { label: 'Đã hủy', value: 'Đã hủy' },
    { label: 'Đã nhận hàng', value: 'Hoàn tất' }
]
const checkRatingStatus = async (orderItemId) => {
  try {
    const response = await api.get(`/api/ratings/existing/${orderItemId}`)
    return {
      hasRating: response.data.hasRating,
      rating: response.data.data
    }
  } catch (error) {
    console.error('Lỗi kiểm tra trạng thái đánh giá:', error)
    return { hasRating: false, rating: null }
  }
}
// Gọi API lấy danh sách đơn hàng của người dùng
const fetchOrders = async () => {
  try {
    const res = await api.get('/api/order-details/user')
    orders.value = res.data
  } catch (err) {
    console.error('Lỗi khi tải đơn hàng:', err)
  }
}

//  Format ngày hiển thị
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`
}

//  Lọc đơn hàng theo trạng thái đang chọn
const filteredOrders = computed(() => {
  return orders.value.filter(o => o.orderStatus === activeTab.value)
})

//  Lấy chi tiết đơn hàng
const showOrderDetail = async (orderId) => {
try {
   const res = await api.get(`/api/order-details/user/${orderId}`)
   selectedOrder.value = res.data
      // Kiểm tra trạng thái đánh giá cho từng item
    if (selectedOrder.value.items) {
      for (const item of selectedOrder.value.items) {
        if (item.orderItemId) {
          const status = await checkRatingStatus(item.orderItemId)
          ratingStatuses.value[item.orderItemId] = status
        }
      }
    }
 } catch (err) {
   console.error('Lỗi khi lấy chi tiết đơn hàng:', err)
 }
}



// Đóng modal và điều hướng sang trang đánh giá để tránh lớp overlay tối
const goToRating = async (orderItemId) => {
  try {
    const el = document.getElementById('orderDetailModal')
    if (el && typeof bootstrap !== 'undefined') {
      const modal = bootstrap.Modal.getInstance(el) || new bootstrap.Modal(el)
      modal.hide()
    } else if (el) {
      el.classList.remove('show')
      el.style.display = 'none'
      document.body.classList.remove('modal-open')
      document.body.style.overflow = ''
      document.body.style.paddingRight = ''
      document.querySelectorAll('.modal-backdrop').forEach(bd => bd.remove())
    }

    // chờ một nhịp để DOM cập nhật (loại bỏ backdrop) rồi điều hướng
    await new Promise(r => setTimeout(r, 150))
    router.push(`/danhGiaSP?orderItemId=${orderItemId}`)
  } catch (e) {
    router.push(`/danhGiaSP?orderItemId=${orderItemId}`)
  }
}

//  Xác nhận cập nhật trạng thái đơn hàng
const confirmUpdateStatus = async (newStatus, message) => {
  const status = selectedOrder.value.orderStatus
  const paid = selectedOrder.value.paymentStatus === 'Đã thanh toán'

  // Trường hợp không được hủy: Chờ giao hàng nhưng đã thanh toán
  if (status === 'Chờ giao hàng' && paid) {
    return Swal.fire({
      icon: 'info',
      title: 'Không thể hủy đơn',
      text: 'Đơn hàng đang được giao và đã thanh toán. Vui lòng liên hệ cửa hàng để được hỗ trợ.',
      confirmButtonText: 'OK'
    })
  }

  // Các trường hợp còn lại thì xác nhận
  const result = await Swal.fire({
    title: message,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Xác nhận',
    cancelButtonText: 'Hủy'
  })

  if (result.isConfirmed) updateOrderStatus(newStatus)
}


//  Gửi yêu cầu cập nhật trạng thái đơn hàng
const updateOrderStatus = async (newStatus) => {
  try {
    await api.put(`/api/order-details/${selectedOrder.value.id}/orderStatus`, null, {
      params: { orderStatus: newStatus }
    })
    await fetchOrders()
    Swal.fire('Thành công', `Đơn hàng đã được cập nhật thành: ${newStatus}`, 'success')
  } catch (err) {
    console.error('Lỗi cập nhật trạng thái:', err)
    Swal.fire('Lỗi', 'Không thể cập nhật đơn hàng', 'error')
  }
}


const cancelReason = ref('')

const submitCancelRequest = async () => {
  if (!cancelReason.value.trim()) {
    return Swal.fire('Lỗi', 'Vui lòng nhập lý do hủy.', 'warning')
  }

  if (!selectedOrder.value?.id) {
    return Swal.fire('Lỗi', 'Không xác định được đơn hàng.', 'error')
  }

  try {
    const payload = {
      orderId: selectedOrder.value.id,
      reason: cancelReason.value    // ✅ KHÔNG gửi userId nữa
    }

    console.log("Payload gửi lên:", payload)

    await api.post('/api/cancel-requests', payload)

    cancelReason.value = ''

    // ✅ Cách ẩn modal không dùng bootstrap.Modal
    const modalEl = document.getElementById('cancelModal')
    if (modalEl) {
      modalEl.classList.remove('show')
      modalEl.style.display = 'none'
      document.body.classList.remove('modal-open')
      const backdrop = document.querySelector('.modal-backdrop')
      if (backdrop) backdrop.remove()
    }

    Swal.fire('Đã gửi yêu cầu', 'Yêu cầu hủy đơn đã được gửi cho admin xét duyệt.', 'success')
  } catch (err) {
    console.error(err)
    Swal.fire('Lỗi', 'Không thể gửi yêu cầu hủy đơn', 'error')
  }
}





const reviewRating = ref(5);
const reviewContent = ref("");
const reviewOrderItemId = ref(null)
const showReviewModal = async (orderItemId) => {

  reviewOrderItemId.value = orderItemId
  closeModal('orderDetailModal')
  await new Promise(r => setTimeout(r, 300))
  openModal('reviewModal')
}
const closeModal = (modalId) => {
  const el = document.getElementById(modalId)
  if (!el) return
  if (typeof bootstrap !== 'undefined') {
    const modal = bootstrap.Modal.getInstance(el)
    if (modal) modal.hide()
  } else {
    el.classList.remove('show')
    el.style.display = 'none'
    document.body.classList.remove('modal-open')
    document.body.style.overflow = ''        // ✅ khôi phục scroll
    document.body.style.paddingRight = ''    // ✅ bỏ padding chống scroll-jump
    if (!document.querySelector('.modal.show')) {
      document.querySelectorAll('.modal-backdrop').forEach(bd => bd.remove())
    }
  }
}

const submitReview = async () => {
  if (!selectedOrder.value) return Swal.fire('Lỗi', 'Không tìm thấy đơn hàng để đánh giá.', 'error');
  try {
    await api.post(`/api/reviews`, {
      orderId: selectedOrder.value.id,
      rating: reviewRating.value,
      content: reviewContent.value
    });
    reviewContent.value = '';
    reviewRating.value = 5;
    closeModal('reviewModal');
    Swal.fire('Thành công', 'Cảm ơn bạn đã đánh giá đơn hàng!', 'success');
  } catch (err) {
    console.error('Lỗi khi gửi đánh giá:', err);
    Swal.fire('Lỗi', 'Không thể gửi đánh giá', 'error');
  }
};


//  Tự động gọi khi component được mounted
onMounted(fetchOrders)
</script>


<style scoped>
.modal-backdrop {
  display: none !important;
}


/*Header */
.lien-he {
  font-size: 18px;
  font-family: Roboto;
  background: #fff;
  font-weight: 500;
  color: #000;
  width: 100vw;
}

.bread-crumb .title-bread-crumb {
  text-align: center;
  font-size: 55px;
  color: #cd9b32;
  font-weight: 600;
  font-family: 'Forte';
}

.breadcrumb {
  font-size: 18px;
  padding: 15px 10px;
  line-height: 24px;
  width: 100%;
  text-align: left;
  padding-left: 580px;
}

.breadcrumb .home {
  display: inline-flex;
  align-items: center;
}

.breadcrumb .home a {
  text-decoration: none !important;
  color: white;
  font-size: 18px !important;
  margin-right: 10px;
}

.breadcrumb .home a:nth-child(2) span {
  color: #cd9b32 !important;
  font-size: 18px !important;
}

.breadcrumb li {
  display: inline-block;
  margin: 0 5px;
}

.breadcrumb li:first-child {
  margin-left: 0;
}

.breadcrumb li:last-child {
  margin-right: 0;
}

.breadcrumb li a:hover span,
.breadcrumb li.active span,
.breadcrumb li strong span {
  color: #cd9b32;
}

.breadcrumb li>a>span,
.breadcrumb li span {
  color: #fff;
}

.bread-crumb {
  margin-bottom: 40px;
  min-height: 200px;
  background-attachment: fixed;
  background-position: center center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  padding: 150px 0 50px;
}

header.header {
  padding: 10px 0;
  position: absolute;
  top: 0 !important;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 9;
  background-color: transparent;
}

.header .nav-link {
  color: white;
}

.nav-link li a:hover {
  color: #e4a53d;
}

.header .nav-link:hover {
  color: #e4a53d;
}

.btn-primary-custom {
  background-color: #cd9b32;
  color: #ffffff;
  border: none;
  border-radius: 5px;
  font-size: 23px;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
  width: 300px;
}

.btn-primary-custom:hover {
  background-color: #e8b443;
  color: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btn-primary-custom:active {
  background-color: #003d80;
  transform: scale(0.95);
}

.giatien {
  color: #b37e36;
}

.thanhtien {
  color: #b37e36;
}

.cart-tinhtrang {
  border: 1px solid #cd9b32;
  padding-bottom: 5px !important;
  padding: 20px 15px 15px 15px;
  border-radius: 8px;
  margin-top: 40px;
  margin-bottom: 20px;
  position: relative;
}

.cart-tinhtrang .title_voucher {
  width: auto;
  color: #fff;
  font-weight: 600;
  background: #cd9b32;
  border-radius: 20px;
  border: 1px solid;
  font-size: 23px;
  display: inline-flex;
  padding: 5px 15px;
  align-items: center;
  position: absolute;
  top: -24px;
}

.cart-tinhtrang .title_voucher img {
  width: 30px;
  height: auto;
  margin-right: 10px;
}

img {
  vertical-align: middle;
  border-style: none;
  max-width: 100%;
}

.thump-check {
  height: 10px;
  position: relative;
  font-size: 18px;
  text-align: center;
  background: #e7e7e7;
  margin-top: 10px;
  border-radius: 5px;
  overflow: hidden;
}

.check-bar {
  display: inline-block;
  width: 100%;
  height: 100%;
  z-index: 1;
  color: #000;
  position: relative;
  margin-top: 10px;
}

.thump-check .check-bar1 {
  position: absolute;
  height: 8px;
  top: 0px;
  border-radius: 5px;
  background-color: #cd9b32;
  -webkit-animation: progress_bar_fill 2s linear infinite;
  animation: progress_bar_fill 2s linear infinite;
  background-image: linear-gradient(45deg, rgba(255, 255, 255, 0.2) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.2) 50%, rgba(255, 255, 255, 0.2) 75%, transparent 75%, transparent);
  background-size: 40px 40px;
  transition: width .6s ease;
  height: 100%;
}

.cart-tinhtrang .coupon {
  list-style: disc;
  padding-left: 20px;
  margin-top: 20px;
  font-size: 20px;
}

.cart-tinhtrang .coupon li {
  margin-bottom: 5px;
  opacity: 0.5;
}

.cart-tinhtrang .coupon li .coupon-content {
  display: block;
}

.cart-tinhtrang .coupon li .coupon-code {
  display: inline-block;
  color: #fff;
  background: #cd9b32;
  padding: 2px 15px;
  border-radius: 5px;
  margin-top: 5px;
  margin-left: 0px;
  pointer-events: none;
  cursor: default;
}

.formVAT {
  padding: 10px;
  background: #f3e7cd;
  width: 100%;
  float: left;
  border-radius: 5px;
}

form h4 {
  font-size: 18px;
  font-weight: bold;
}

.timedeli-modal {
  display: flex;
}

.timedeli-modal fieldset:first-child {
  margin-right: 10px;
}

.timedeli-modal fieldset input {
  padding: 0 10px;
  background: #fff;
  position: relative;
  z-index: 2;
  height: 35px;
  line-height: 35px;
  min-height: 35px;
  width: 100%;
  border: initial;
}

.timedeli-modal fieldset {
  border: none;
  padding: 0;
  width: 49%;
  position: relative;
}

.timedeli-modal fieldset select {
  height: 35px;
  line-height: 35px;
  width: 100%;
  border: initial;
  color: #898787;
}

.r-bill {
  margin-top: 10px;
}

.r-bill .checkbox {
  margin: 0;
}

.r-bill .checkbox .regular-checkbox+.box {
  border: 2px solid #727272;
  padding: 7px;
  border-radius: 2px;
  display: inline-block;
  margin-top: 2px;
  position: relative;
}

.r-bill .checkbox .regular-checkbox {
  display: none;
}

.r-bill .checkbox>.title {
  font-size: 18px;
  line-height: 1.5;
  padding-left: 5px;
  vertical-align: top;
  color: #000;
}

.r-bill .checkbox label {
  margin: 0;
}

.r-bill .bill-field {
  display: none;
  margin-top: 5px;
}

.r-bill .bill-field .form-group {
  margin-bottom: 10px;
  padding: 0;
}

.r-bill .bill-field label {
  line-height: 1.8;
  font-size: 18px;
  margin-bottom: 0;
}

.r-bill .bill-field .form-group input {
  margin-bottom: 0;
}

.r-bill .bill-field .form-control {
  box-shadow: none;
  background-clip: padding-box;
  border-radius: 3px;
  border-color: #e6e6e6;
  height: 34px;
  font-size: 18px;
  border: initial;
  width: 100%;
}

/* CSS cho bảng giỏ hàng */
.cart-table {
  width: 100%;
  border-collapse: collapse;
}

.cart-table th,
.cart-table td {
  padding: 10px;
  text-align: center;
  vertical-align: middle;
}

.cart-table th {
  background-color: #f8f8f8;
  font-weight: bold;
  color: #333;
  font-size: 23px;
}

.cart-table .product-info {
  text-align: left;
}

.cart-table .product-image img {
  border: 1px solid #ddd;
  border-radius: 4px;
}

.cart-table .product-details {
  padding-left: 10px;
}

.cart-table .product-name {
  margin: 0;
  font-size: 25px;
  color: #333;
}

.cart-table .delete-link {
  color: #cd9b32;
  cursor: pointer;
  text-decoration: underline;
}

.cart-table .delete-link:hover {
  color: #e4a53d;
}

.cart-table .price-value,
.cart-table .total-value {
  margin: 0;
  font-size: 18px;
  color: #cd9b32;
  font-weight: bold;
}

.cart-table .quantity-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.cart-table .quantity-input {
  width: 60px;
  text-align: center;
  padding: 5px;
  border: 1px solid #cd9b32;
  border-radius: 4px;
}

.cart-table .quantity-btn {
  width: 30px;
  height: 30px;
  background-color: #f3e7cd;
  border: 1px solid #cd9b32;
  color: #cd9b32;
  font-size: 18px;
  border-radius: 4px;
  cursor: pointer;
}

.cart-table .quantity-btn:hover {
  background-color: #cd9b32;
  color: #fff;
}

.bread-crumb .title-bread-crumb[data-v-18c6812d][data-v-18c6812d] {
  text-align: center;
  font-size: 55px;
  color: #cd9b32;
  font-weight: 600;
  font-family: 'Playball';
}
</style>
