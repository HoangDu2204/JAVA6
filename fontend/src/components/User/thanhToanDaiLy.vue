<template>
  <div class="checkout-container">
    <!-- Header -->
    <div class="checkout-header">
      <div class="container">
        <div class="d-flex align-items-center justify-content-between">
          <div class="d-flex align-items-center">
            <router-link to="/gioHang" class="back-btn">
              <i class="bi bi-arrow-left"></i>
              <span>Quay lại giỏ hàng</span>
            </router-link>
          </div>
          <div class="checkout-title">
            <h2 class="mb-0">Thanh toán đại lý</h2>
            <p class="text-muted mb-0">Đặt hàng với ưu đãi đại lý</p>
          </div>
          <div class="checkout-steps">
            <div class="step active">
              <div class="step-number">1</div>
              <span>Giỏ hàng</span>
            </div>
            <div class="step-line"></div>
            <div class="step active">
              <div class="step-number">2</div>
              <span>Thanh toán</span>
            </div>
            <div class="step-line"></div>
            <div class="step">
              <div class="step-number">3</div>
              <span>Hoàn thành</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container py-4">
      <div class="row g-4">
        <!-- Cột trái: Thông tin giao hàng -->
        <div class="col-lg-8">
          <div class="checkout-card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-person-badge-fill text-primary me-2"></i>
                Thông tin đại lý
              </h5>
            </div>
            <div class="card-body">
              <div class="row g-3">
                <!-- Email -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-envelope text-muted me-1"></i>
                    Email
                  </label>
                  <input type="email" class="form-control form-control-lg" placeholder="Email" v-model="form.email" readonly />
                  <div class="text-danger small mt-1" v-if="errors.email">{{ errors.email }}</div>
                </div>

                <!-- Tên đại lý -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-person text-muted me-1"></i>
                    Tên đại lý
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Tên đại lý" v-model="form.agentName" readonly />
                  <div class="text-danger small mt-1" v-if="errors.agentName">{{ errors.agentName }}</div>
                </div>

                <!-- Số điện thoại -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-telephone text-muted me-1"></i>
                    Số điện thoại
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Số điện thoại" v-model="form.phone" readonly />
                  <div class="text-danger small mt-1" v-if="errors.phone">{{ errors.phone }}</div>
                </div>

                <!-- Địa chỉ chi tiết -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-house text-muted me-1"></i>
                    Địa chỉ chi tiết
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Số nhà, đường..." v-model="form.addressDetail" readonly />
                  <div class="text-danger small mt-1" v-if="errors.addressDetail">{{ errors.addressDetail }}</div>
                </div>

                <!-- Tỉnh/Thành phố -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Tỉnh/Thành phố</label>
                  <select class="form-select form-select-lg readonly" v-model="selectedProvince" @change="onProvinceChange">
                    <option disabled value="">Chọn Tỉnh/Thành phố</option>
                    <option v-for="p in provinces" :key="p.code" :value="String(p.code)">{{ p.name }}</option>
                  </select>
                </div>

                <!-- Quận/Huyện -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Quận/Huyện</label>
                  <select class="form-select form-select-lg readonly" v-model="selectedDistrictId" @change="onDistrictChange">
                    <option disabled value="">Chọn Quận/Huyện</option>
                    <option v-for="d in districts" :key="d.code" :value="String(d.code)">{{ d.name }}</option>
                  </select>
                </div>

                <!-- Phường/Xã -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Phường/Xã</label>
                  <select class="form-select form-select-lg readonly" v-model="selectedWardId" @change="calculateShippingFee">
                    <option disabled value="">Chọn Phường/Xã</option>
                    <option v-for="w in wards" :key="w.id" :value="String(w.id)">{{ w.name }}</option>
                  </select>
                </div>

                <!-- Ghi chú -->
                <div class="col-12">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-chat-text text-muted me-1"></i>
                    Ghi chú (tùy chọn)
                  </label>
                  <textarea class="form-control" rows="3" placeholder="Ghi chú về đơn hàng..." v-model="form.note"></textarea>
                </div>
              </div>
            </div>
          </div>

          <!-- Phương thức thanh toán -->
          <div class="checkout-card mt-4">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-credit-card-fill text-primary me-2"></i>
                Phương thức thanh toán
              </h5>
            </div>
            <div class="card-body">
              <div class="row g-3">
                <div class="col-md-6">
                  <div class="payment-method" :class="{ active: form.paymentMethod === 'BANK' }">
                    <input class="form-check-input" type="radio" name="payment" id="bank" value="BANK" v-model="form.paymentMethod" />
                    <label class="form-check-label" for="bank">
                      <div class="payment-icon">
                        <i class="bi bi-bank2"></i>
                      </div>
                      <div class="payment-info">
                        <h6 class="mb-1">Chuyển khoản</h6>
                        <p class="text-muted mb-0">Thanh toán qua VNPay</p>
                      </div>
                    </label>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="payment-method" :class="{ active: form.paymentMethod === 'COD' }">
                    <input class="form-check-input" type="radio" name="payment" id="cod" value="COD" v-model="form.paymentMethod" />
                    <label class="form-check-label" for="cod">
                      <div class="payment-icon">
                        <i class="bi bi-truck"></i>
                      </div>
                      <div class="payment-info">
                        <h6 class="mb-1">Thu hộ (COD)</h6>
                        <p class="text-muted mb-0">Thanh toán khi nhận hàng</p>
                      </div>
                    </label>
                  </div>
                </div>
              </div>
              <div class="text-danger small mt-2" v-if="errors.paymentMethod">{{ errors.paymentMethod }}</div>
            </div>
          </div>
        </div>

        <!-- Cột phải: Tóm tắt đơn hàng -->
        <div class="col-lg-4">
          <div class="checkout-card sticky-top" style="top: 20px;">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-cart-check-fill text-primary me-2"></i>
                Tóm tắt đơn hàng
              </h5>
            </div>
            <div class="card-body">
              <!-- Danh sách sản phẩm -->
              <div class="order-items">
                <div v-for="item in cartItems" :key="item.id" class="order-item">
                  <div class="item-image">
                    <img :src="'http://localhost:8080/images/' + item.productImage" alt="Product" />
                    <span class="item-quantity">{{ item.quantity }}</span>
                  </div>
                  <div class="item-info">
                    <h6 class="item-name">{{ item.productName }}</h6>
                    <p class="item-price">{{ formatPrice(item.discountedPrice) }}</p>
                  </div>
                </div>
              </div>

              <hr class="my-4">

              <!-- Mã giảm giá -->
              <!-- <div class="voucher-section mb-3">
                <label class="form-label fw-semibold">
                  <i class="bi bi-tag-fill text-primary me-2"></i>
                  Mã giảm giá
                </label>
                <div class="input-group">
                  <input 
                    type="text" 
                    class="form-control" 
                    v-model="form.voucherCode" 
                    placeholder="Nhập mã giảm giá" 
                    @input="onVoucherInputChange" 
                  />
                  <button class="btn btn-outline-primary" @click="applyVoucher" type="button">
                    <i class="bi bi-tag-fill"></i>
                    Áp dụng
                  </button>
                </div>
                <div v-if="isVoucherValid && voucherDiscountAmount > 0" class="text-success small mt-1">
                  <i class="bi bi-check-circle me-1"></i>
                  Mã giảm giá đã được áp dụng thành công!
                </div>
                <div v-if="errors.voucherCode" class="text-danger small mt-1">
                  <i class="bi bi-exclamation-triangle me-1"></i>
                  {{ errors.voucherCode }}
                </div>
              </div> -->

              <!-- Tổng tiền -->
              <div class="order-summary">
                <div class="summary-item">
                  <span>Tổng tiền sản phẩm</span>
                  <span>{{ formatPrice(totalAmount) }}</span>
                </div>
                <div class="summary-item text-success" v-if="agentDiscountAmount > 0">
                  <span>Giảm giá đại lý
                     <span v-if="agentDiscountPercent">({{ agentDiscountPercent }}%)</span>
                  </span>
                  <span>- {{ formatPrice(agentDiscountAmount) }}</span>
                </div>
                <div class="summary-item text-success" v-if="voucherDiscountAmount > 0">
                  <span>Giảm giá voucher</span>
                  <span>- {{ formatPrice(voucherDiscountAmount) }}</span>
                </div>
                <div class="summary-item">
                  <span>Phí vận chuyển</span>
                  <span>{{ formatPrice(form.shippingFee) }}</span>
                </div>
                <hr class="my-3">
                <div class="summary-item total">
                  <span class="fw-bold">Tổng thanh toán</span>
                  <span class="fw-bold text-primary fs-5">{{ formatPrice(finalTotal) }}</span>
                </div>
              </div>

              <!-- Nút đặt hàng -->
              <button class="btn btn-primary btn-lg w-100 mt-4" @click="submitOrder" :disabled="!form.paymentMethod">
                <i class="bi bi-check-circle me-2"></i>
                Đặt hàng ngay
              </button>

              <div class="text-center mt-3">
                <small class="text-muted">
                  <i class="bi bi-shield-check me-1"></i>
                  Thông tin của bạn được bảo mật
                </small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

const form = reactive({
  agentName: '',
  email: '',
  phone: '',
  addressDetail: '',
  note: '',
  paymentMethod: '',
  paymentStatus: 'Chưa thanh toán',
  shippingFee: 0,
  discountAmount: 0,
  totalAmount: 0,
  voucherCode: ''
})

const errors = reactive({})
const cartItems = ref([])
const totalAmount = ref(0)
const agentDiscountAmount = ref(0)
const voucherDiscountAmount = ref(0)
const finalTotal = ref(0)
const isVoucherValid = ref(false)
const agentDiscountPercent = ref(0)////

const provinces = ref([])
const districts = ref([])
const wards = ref([])

const selectedProvince = ref('')
const selectedDistrictId = ref('')
const selectedWardId = ref('')

const formatPrice = val => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)

const recalculateTotal = () => {
  // Tính tổng giảm giá (đại lý + voucher)
  const totalDiscount = agentDiscountAmount.value + voucherDiscountAmount.value
  form.discountAmount = totalDiscount
  
  // Tính tổng cuối cùng: Tạm tính - Giảm giá + Phí vận chuyển
  finalTotal.value = totalAmount.value - totalDiscount + form.shippingFee
  
  console.log('✅ Tổng tiền tạm tính:', totalAmount.value)
  console.log('✅ Giảm giá đại lý:', agentDiscountAmount.value)
  console.log('✅ Giảm giá voucher:', voucherDiscountAmount.value)
  console.log('✅ Tổng giảm giá:', totalDiscount)
  console.log('✅ Phí vận chuyển:', form.shippingFee)
  console.log('✅ Tổng cộng:', finalTotal.value)
}

const fetchProvinces = async () => {
  const res = await api.get('/api/locations/provinces')
  provinces.value = res.data.map(p => ({ code: p.ProvinceID, name: p.ProvinceName }))
}

const fetchDistricts = async (provinceId) => {
  const res = await api.get(`/api/locations/districts/${provinceId}`)
  districts.value = res.data.map(d => ({ code: d.DistrictID, name: d.DistrictName }))
}

const fetchWards = async (districtId) => {
  const res = await api.get(`/api/locations/wards/${districtId}`)
  wards.value = res.data.map(w => ({ id: String(w.wardId || w.WardCode), name: w.wardName || w.WardName }))
}

const onProvinceChange = async () => {
  if (!selectedProvince.value) return
  await fetchDistricts(selectedProvince.value)
  selectedDistrictId.value = ''
  selectedWardId.value = ''
  wards.value = []
}

const onDistrictChange = async () => {
  if (!selectedDistrictId.value) return
  await fetchWards(selectedDistrictId.value)
  selectedWardId.value = ''
  await calculateShippingFee()
}

const calculateShippingFee = async () => {
  if (!selectedDistrictId.value || !selectedWardId.value) return
  try {
    const weight = cartItems.value.reduce((sum, i) => sum + (i.weight || 500) * i.quantity, 0)
    // Sử dụng tổng tiền gốc (trước giảm giá) để tính insurance value, giống như backend
    const originalTotalAmount = cartItems.value.reduce((sum, i) => {
      // Sử dụng giá gốc nếu có, nếu không thì dùng giá hiện tại
      const originalPrice = i.originalPrice || i.price || i.discountedPrice
      return sum + (originalPrice * i.quantity)
    }, 0)
    const insuranceValue = originalTotalAmount
    
    const res = await api.post('/api/orders/shipping-fee', {
      toDistrictId: Number(selectedDistrictId.value),
      toWardCode: selectedWardId.value,
      weight,
      insuranceValue
    })
    form.shippingFee = res.data.shippingFee || 0
    recalculateTotal()
  } catch {
    form.shippingFee = 0
  }
}


const onVoucherInputChange = () => {
  // Reset voucher khi người dùng thay đổi input
  if (isVoucherValid.value) {
    isVoucherValid.value = false
    voucherDiscountAmount.value = 0
    recalculateTotal()
  }
  
  // Nếu voucher code bị xóa hoàn toàn, reset state
  if (!form.voucherCode.trim()) {
    isVoucherValid.value = false
    voucherDiscountAmount.value = 0
    recalculateTotal()
  }
}

const applyVoucher = async () => {
  try {
    const res = await api.post('/api/orders/voucher/apply', {
      voucherCode: form.voucherCode,
      totalAmount: totalAmount.value
    })

    voucherDiscountAmount.value = res.data.discountAmount
    isVoucherValid.value = true
    
    // Gọi recalculateTotal để tính toán lại tổng tiền
    recalculateTotal()

    Swal.fire({
      icon: 'success',
      title: 'Áp dụng mã thành công!',
      text: `Được giảm ${formatPrice(voucherDiscountAmount.value)}`
    })
  } catch (err) {
    let message = 'Mã giảm giá không hợp lệ!'
    const res = err.response?.data
    if (res?.error) message = res.error
    else if (typeof res === 'string') message = res

    voucherDiscountAmount.value = 0
    isVoucherValid.value = false
    
    // Gọi recalculateTotal để tính toán lại tổng tiền
    recalculateTotal()

    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: message
    })
  }
}

const validateForm = () => {
  Object.keys(errors).forEach(k => delete errors[k])
  if (!form.agentName) errors.agentName = 'Vui lòng nhập tên đại lý'
  if (!form.email) errors.email = 'Vui lòng nhập email'
  if (!form.phone) errors.phone = 'Vui lòng nhập số điện thoại'
  if (!form.addressDetail) errors.addressDetail = 'Vui lòng nhập địa chỉ chi tiết'
  if (!selectedProvince.value) errors.provinceId = 'Chọn tỉnh/thành phố'
  if (!selectedDistrictId.value) errors.districtId = 'Chọn quận/huyện'
  if (!selectedWardId.value) errors.wardId = 'Chọn phường/xã'
  if (!form.paymentMethod) errors.paymentMethod = 'Chọn phương thức thanh toán'
  
  // Kiểm tra voucher code nếu đã được áp dụng
  if (isVoucherValid.value && (!form.voucherCode || !form.voucherCode.trim())) {
    errors.voucherCode = 'Vui lòng nhập mã giảm giá hợp lệ'
  }
  
  return Object.keys(errors).length === 0
}

const submitOrder = async () => {
  if (!validateForm()) {
    Swal.fire({ icon: 'error', title: 'Thông tin không hợp lệ' })
    return
  }

  const fullAddress = `${form.addressDetail}, ${wards.value.find(w => String(w.id) === selectedWardId.value)?.name || ''}, ${districts.value.find(d => String(d.code) === selectedDistrictId.value)?.name || ''}, ${provinces.value.find(p => String(p.code) === selectedProvince.value)?.name || ''}`

  const payload = {
    ...form,
    name: form.agentName,
    address: fullAddress,
    agentOrder: true,
    totalAmount: totalAmount.value, // Gửi tổng tiền từ frontend
    voucherCode: form.voucherCode, // Đảm bảo voucher code được gửi
    items: cartItems.value.map(i => ({
      productVariantId: i.variantId,
      quantity: i.quantity,
      unitPrice: i.discountedPrice
    })),
    toDistrictId: Number(selectedDistrictId.value),
    toWardCode: selectedWardId.value
  }

  try {
    const res = await api.post('/api/agents/checkout', payload)
    if (form.paymentMethod === 'BANK') {
      const paymentRes = await api.post('/api/payment/create', null, {
        params: {
          amount: finalTotal.value,
          orderId: res.data.order.customOrderCode
        }
      })
      window.location.href = paymentRes.data.url
    } else {
      Swal.fire({
        icon: 'success',
        title: 'Đặt hàng thành công!',
        text: 'Đơn hàng của bạn đã được tạo thành công!',
        confirmButtonText: 'Xem đơn hàng'
      }).then((result) => {
        if (result.isConfirmed) {
          // Chuyển hướng về trang đơn hàng
          window.location.href = '/donHang'
        }
      })
    }



  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Đặt hàng thất bại' })
  }
}

onMounted(async () => {
  try {
    await fetchProvinces()
    const cartRes = await api.get('/api/cart')
    cartItems.value = cartRes.data
    totalAmount.value = cartItems.value.reduce((sum, i) => sum + i.discountedPrice * i.quantity, 0)

    const infoRes = await api.get('/api/agents/info')
    const info = infoRes.data

    form.agentName = info.agentName
    form.email = info.email
    form.phone = info.phone
    form.addressDetail = info.addressDetail

    agentDiscountPercent.value = info.discountAmount || 0////////


    agentDiscountAmount.value = totalAmount.value * (info.discountAmount / 100)

    selectedProvince.value = String(info.provinceId)
    await fetchDistricts(selectedProvince.value)
    selectedDistrictId.value = String(info.districtId)
    await fetchWards(selectedDistrictId.value)
    selectedWardId.value = String(info.wardId)

    await calculateShippingFee()
    recalculateTotal()
    
    // Khởi tạo voucher state
    isVoucherValid.value = false
    voucherDiscountAmount.value = 0
  } catch { }
})
</script>



<!-- <script setup>
import { ref, reactive, onMounted } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

const form = reactive({
  email: '',
  receiverName: '',
  phone: '',
  addressDetail: '',
  note: '',
  paymentMethod: '',
  paymentStatus: 'Chưa thanh toán',
  shippingFee: 0,
  discountAmount: 0,
  totalAmount: 0,
  voucherCode: ''
})

const errors = reactive({})
const cartItems = ref([])
const totalAmount = ref(0)
const agentDiscountAmount = ref(0)
const voucherDiscountAmount = ref(0)
const finalTotal = ref(0)

const provinces = ref([])
const districts = ref([])
const wards = ref([])

const selectedProvince = ref('')
const selectedDistrictId = ref('')
const selectedWardId = ref('')

const formatPrice = val => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)

const recalculateTotal = () => {
  // Tính tổng giảm giá (đại lý + voucher)
  const totalDiscount = agentDiscountAmount.value + voucherDiscountAmount.value
  form.discountAmount = totalDiscount
  
  // Tính tổng cuối cùng: Tạm tính - Giảm giá + Phí vận chuyển
  finalTotal.value = totalAmount.value - totalDiscount + form.shippingFee
  
  console.log('✅ Tổng tiền tạm tính:', totalAmount.value)
  console.log('✅ Giảm giá đại lý:', agentDiscountAmount.value)
  console.log('✅ Giảm giá voucher:', voucherDiscountAmount.value)
  console.log('✅ Tổng giảm giá:', totalDiscount)
  console.log('✅ Phí vận chuyển:', form.shippingFee)
  console.log('✅ Tổng cộng:', finalTotal.value)
}

const fetchProvinces = async () => {
  const res = await api.get('/api/locations/provinces')
  provinces.value = res.data.map(p => ({ code: p.ProvinceID, name: p.ProvinceName }))
  console.log('✅ Provinces loaded:', provinces.value)
}

const fetchDistricts = async (provinceId) => {
  const res = await api.get(`/api/locations/districts/${provinceId}`)
  districts.value = res.data.map(d => ({ code: d.DistrictID, name: d.DistrictName }))
  console.log('✅ Districts loaded:', districts.value)
}

const fetchWards = async (districtId) => {
  const res = await api.get(`/api/locations/wards/${districtId}`)
  wards.value = res.data.map(w => ({
     id: String(w.wardId || w.WardCode),
    name: w.wardName || w.WardName

  }))
  console.log('✅ Wards loaded:', wards.value)
  console.log('✅ Wards response raw:', res.data)


}

const onProvinceChange = async () => {
  if (!selectedProvince.value) return
  console.log('📍 Chọn tỉnh:', selectedProvince.value)
  await fetchDistricts(selectedProvince.value)
  selectedDistrictId.value = ''
  selectedWardId.value = ''
  wards.value = []
}

const onDistrictChange = async () => {
  if (!selectedDistrictId.value) return
  console.log('📍 Chọn quận:', selectedDistrictId.value)
  await fetchWards(selectedDistrictId.value)
  selectedWardId.value = ''
  await calculateShippingFee()
}

const calculateShippingFee = async () => {
  if (!selectedDistrictId.value || !selectedWardId.value) return
  try {
    const weight = cartItems.value.reduce((sum, i) => sum + (i.weight || 500) * i.quantity, 0)
    const insuranceValue = totalAmount.value
    const res = await api.post('/api/orders/shipping-fee', {
      toDistrictId: Number(selectedDistrictId.value),
      toWardCode: selectedWardId.value,
      weight,
      insuranceValue
    })
    form.shippingFee = res.data.shippingFee || 0
    console.log('📦 Shipping fee response:', res.data)
    recalculateTotal()
    
    console.log('📦 Shipping fee calculation:')
    console.log('  - Weight:', weight)
    console.log('  - Insurance value:', insuranceValue)
    console.log('  - Shipping fee:', form.shippingFee)
  } catch (err) {
    console.error('❌ Lỗi khi tính phí vận chuyển:', err)
    form.shippingFee = 0
  }
}

const applyVoucher = async () => {
  try {
    const res = await api.post('/api/orders/voucher/apply', {
      voucherCode: form.voucherCode,
      totalAmount: totalAmount.value
    })
    voucherDiscountAmount.value = res.data.discountAmount
    console.log('✅ Mã giảm giá áp dụng:', voucherDiscountAmount.value)
    recalculateTotal()
    Swal.fire({ 
      icon: 'success', 
      title: 'Mã áp dụng thành công',
      text: `Được giảm ${formatPrice(voucherDiscountAmount.value)}`
    })
  } catch (err) {
    voucherDiscountAmount.value = 0
    recalculateTotal()
    let message = 'Mã giảm giá không hợp lệ!'
    const res = err.response?.data
    if (res?.error) message = res.error
    else if (typeof res === 'string') message = res
    
    Swal.fire({ 
      icon: 'error', 
      title: 'Lỗi',
      text: message
    })
  }
}

const validateForm = () => {
  Object.keys(errors).forEach(k => delete errors[k])
  if (!form.receiverName) errors.receiverName = 'Vui lòng nhập họ tên'
  if (!form.email) errors.email = 'Vui lòng nhập email'
  if (!form.phone) errors.phone = 'Vui lòng nhập số điện thoại'
  if (!form.addressDetail) errors.addressDetail = 'Vui lòng nhập địa chỉ chi tiết'
  if (!selectedProvince.value) errors.provinceId = 'Chọn tỉnh/thành phố'
  if (!selectedDistrictId.value) errors.districtId = 'Chọn quận/huyện'
  if (!selectedWardId.value) errors.wardId = 'Chọn phường/xã'
  if (!form.paymentMethod) errors.paymentMethod = 'Chọn phương thức thanh toán'
  console.log('✅ Errors:', errors)
  return Object.keys(errors).length === 0
}

const submitOrder = async () => {
  if (!validateForm()) {
    console.log('❌ Validation errors:', errors)
    Swal.fire({ icon: 'error', title: 'Thông tin không hợp lệ' })
    return
  }

  const fullAddress = `${form.addressDetail}, ${
    wards.value.find(w => String(w.id) === selectedWardId.value)?.name || ''
  }, ${
    districts.value.find(d => String(d.code) === selectedDistrictId.value)?.name || ''
  }, ${
    provinces.value.find(p => String(p.code) === selectedProvince.value)?.name || ''
  }`

  console.log('📌 Full address:', fullAddress)

  const payload = {
    ...form,
    name: form.receiverName, 
    address: fullAddress,
    agentOrder: true,
    items: cartItems.value.map(i => ({
      productVariantId: i.variantId, // ✅ Đúng key để Java map được
  quantity: i.quantity,
  unitPrice: i.discountedPrice
    })),
    toDistrictId: Number(selectedDistrictId.value),
    toWardCode: selectedWardId.value
  }

  console.log('📤 Payload:', payload)

  try {
    const res = await api.post('/api/agents/checkout', payload)
    console.log('✅ Checkout response:', res.data)

    if (form.paymentMethod === 'BANK') {
      const paymentRes = await api.post('/api/payment/create', null, {
        params: {
          amount: finalTotal.value,
          orderId: res.data.order.customOrderCode
        }
      })
      window.location.href = paymentRes.data.url
    } else {
      Swal.fire({
        icon: 'success',
        title: 'Đặt hàng thành công!',
        text: 'Đơn hàng của bạn đã được tạo thành công!',
        confirmButtonText: 'Xem đơn hàng'
      }).then((result) => {
        if (result.isConfirmed) {
          // Chuyển hướng về trang đơn hàng
          window.location.href = '/donHang'
        }
      })
    }
  } catch (err) {
    console.error('❌ Lỗi khi đặt hàng:', err)
    if (err.response) {
      console.log('📦 Status:', err.response.status)
      console.log('📦 Data:', err.response.data)
    }
    Swal.fire({ icon: 'error', title: 'Đặt hàng thất bại' })
  }
}

onMounted(async () => {
  try {
    await fetchProvinces()
    console.log('🏙 Provinces:', provinces.value)

    const cartRes = await api.get('/api/cart')
    cartItems.value = cartRes.data
    totalAmount.value = cartItems.value.reduce((sum, i) => sum + i.discountedPrice * i.quantity, 0)
    console.log('🛒 Cart items:', cartItems.value)
    console.log('💰 Tổng tiền:', totalAmount.value)

    const infoRes = await api.get('/api/agents/info')
    const info = infoRes.data
    console.log('🙋 Agent info:', info)

    form.receiverName = info.receiverName
    form.email = info.email
    form.phone = info.phone
    form.addressDetail = info.addressDetail
    
    // Tính chiết khấu đại lý
    if (info.discountAmount && info.discountAmount > 0) {
      agentDiscountAmount.value = totalAmount.value * (info.discountAmount / 100)
      console.log('✅ Agent discount percentage:', info.discountAmount + '%')
      console.log('✅ Agent discount amount:', agentDiscountAmount.value)
    } else {
      agentDiscountAmount.value = 0
      console.log('✅ No agent discount available')
    }

    selectedProvince.value = String(info.provinceId)
    console.log('📍 Province selected:', selectedProvince.value)

    await fetchDistricts(selectedProvince.value)
    console.log('🏘 Districts:', districts.value)

    selectedDistrictId.value = String(info.districtId)
    console.log('📍 District selected:', selectedDistrictId.value)

    await fetchWards(selectedDistrictId.value)
    console.log('🏡 Wards:', wards.value)

    selectedWardId.value = String(info.wardId)
    console.log('📍 Ward selected:', selectedWardId.value)

    await calculateShippingFee()
    recalculateTotal()
  } catch (err) {
    console.error('❌ Error loading mounted:', err)
  }
})
</script> -->



<style scoped>
.checkout-container {
  background-color: #f8f9fa;
  width: 100%;
  min-height: 100vh;
}

.checkout-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
}

.checkout-title h2 {
  font-size: 24px;
  color: #333;
}

.checkout-title p {
  font-size: 14px;
  color: #666;
}

.checkout-steps {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.step {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.step-number {
  width: 30px;
  height: 30px;
  background-color: #007bff;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  margin-right: 8px;
}

.step.active .step-number {
  background-color: #28a745;
}

.step-line {
  height: 2px;
  background-color: #007bff;
  flex-grow: 1;
  margin: 0 10px;
}

.checkout-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
}

.card-header h5 {
  margin-bottom: 0;
  color: #333;
}

.card-body {
  padding: 20px;
}

.form-label {
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}

.form-control {
  border-radius: 6px;
  padding: 10px 15px;
  font-size: 16px;
  border: 1px solid #ccc;
}

.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.form-select {
  border-radius: 6px;
  padding: 10px 15px;
  font-size: 16px;
  border: 1px solid #ccc;
}

.form-select:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.text-danger {
  color: #dc3545;
}

.small {
  font-size: 0.875em;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-method:hover {
  border-color: #007bff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.payment-method.active {
  border-color: #007bff;
  background-color: #e9f5ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.payment-icon {
  font-size: 24px;
  color: #007bff;
  margin-right: 15px;
}

.payment-info h6 {
  margin-bottom: 5px;
  color: #333;
}

.payment-info p {
  font-size: 13px;
  color: #666;
}

.order-items {
  max-height: 250px; /* Adjust as needed */
  overflow-y: auto;
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.item-image {
  position: relative;
  margin-right: 15px;
}

.item-image img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.item-quantity {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #007bff;
  color: #fff;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.item-info {
  flex-grow: 1;
}

.item-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.item-price {
  font-size: 16px;
  font-weight: bold;
  color: #dc3545;
}

.order-summary {
  margin-top: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
  color: #555;
}

.summary-item.total {
  margin-top: 20px;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.summary-item.total .fw-bold {
  color: #007bff;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #004085;
}

.btn-primary:focus {
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.5);
}

.btn-primary:disabled {
  background-color: #6c757d;
  border-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.back-btn {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #007bff;
  font-weight: 600;
  font-size: 15px;
}

.back-btn i {
  margin-right: 8px;
  font-size: 18px;
}

.text-muted {
  color: #6c757d;
}

.fw-semibold {
  font-weight: 600;
}

.form-control-lg {
  height: calc(1.5em + 1rem + 2px);
}

.form-select-lg {
  height: calc(1.5em + 1rem + 2px);
}

.sticky-top {
  position: sticky;
  top: 100px; /* Adjust based on header height */
  z-index: 900;
}

.text-center {
  text-align: center;
}

.text-primary {
  color: #007bff;
}

.text-danger {
  color: #dc3545;
}

.text-success {
  color: #28a745;
}

.text-muted {
  color: #6c757d;
}

.fw-bold {
  font-weight: 700;
}

.fs-5 {
  font-size: 1.25em;
}

.bi {
  font-size: 1.25em;
}

.bi-arrow-left {
  margin-right: 8px;
}

.bi-person-badge-fill, .bi-credit-card-fill, .bi-cart-check-fill {
  color: #007bff;
}

.bi-envelope, .bi-person, .bi-telephone, .bi-house, .bi-chat-text, .bi-bank2, .bi-truck, .bi-check-circle, .bi-shield-check {
  color: #555;
}

.bi-cart-check-fill {
  color: #28a745;
}

.bi-shield-check {
  color: #6c757d;
}

select.readonly {
  pointer-events: none; /* chặn click chuột */
  background-color: #e9ecef; /* giống màu disabled để người dùng biết là không thao tác được */
}

/* Container chính */
.checkout-container {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 100vh;
}

/* Header */
.checkout-header {
  background: white;
  border-bottom: 1px solid #e9ecef;
  padding: 1.5rem 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.back-btn {
  display: flex;
  align-items: center;
  color: #6c757d;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.back-btn:hover {
  color: #0d6efd;
}

.checkout-title h2 {
  color: #2c3e50;
  font-weight: 700;
}

.checkout-steps {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background: #0d6efd;
  color: white;
}

.step span {
  font-size: 0.875rem;
  color: #6c757d;
  font-weight: 500;
}

.step.active span {
  color: #0d6efd;
  font-weight: 600;
}

.step-line {
  width: 60px;
  height: 2px;
  background: #e9ecef;
  margin: 0 0.5rem;
}

/* Cards */
.checkout-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f4;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.checkout-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.card-header h5 {
  color: #2c3e50;
  font-weight: 700;
  font-size: 1.25rem;
}

.card-body {
  padding: 2rem;
}

/* Form controls */
.form-control, .form-select {
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #fafbfc;
}

.form-control:focus, .form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.15);
  background: white;
}

.form-control-lg, .form-select-lg {
  padding: 1rem 1.25rem;
  font-size: 1.1rem;
}

.form-label {
  color: #495057;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

/* Payment methods */
.payment-method {
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafbfc;
  position: relative;
}

.payment-method:hover {
  border-color: #0d6efd;
  background: #f8f9ff;
}

.payment-method.active {
  border-color: #0d6efd;
  background: linear-gradient(135deg, #f8f9ff 0%, #e3f2fd 100%);
  box-shadow: 0 4px 15px rgba(13, 110, 253, 0.15);
}

.payment-method input[type="radio"] {
  position: absolute;
  opacity: 0;
}

.payment-method label {
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  margin: 0;
  width: 100%;
}

.payment-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0d6efd 0%, #0056b3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
}

.payment-info h6 {
  color: #2c3e50;
  font-weight: 700;
  margin: 0;
}

.payment-info p {
  color: #6c757d;
  margin: 0;
  font-size: 0.9rem;
}

/* Order items */
.order-items {
  max-height: 300px;
  overflow-y: auto;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-quantity {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #0d6efd;
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  color: #2c3e50;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  font-size: 0.95rem;
  line-height: 1.3;
}

.item-price {
  color: #0d6efd;
  font-weight: 700;
  margin: 0;
  font-size: 1rem;
}

/* Voucher section */
.voucher-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.5rem;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.voucher-section:hover {
  border-color: #0d6efd;
  box-shadow: 0 2px 8px rgba(13, 110, 253, 0.1);
}

.voucher-section .form-label {
  color: #2c3e50;
  margin-bottom: 0.75rem;
}

.voucher-section .input-group {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.voucher-section .form-control {
  border: 2px solid #e9ecef;
  border-right: none;
  border-radius: 8px 0 0 8px;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  transition: all 0.3s ease;
}

.voucher-section .form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.voucher-section .btn-outline-primary {
  border: 2px solid #0d6efd;
  border-left: none;
  border-radius: 0 8px 8px 0;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.voucher-section .btn-outline-primary:hover {
  background-color: #0d6efd;
  color: white;
  transform: translateY(-1px);
}

/* Order summary */
.order-summary {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.5rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  font-size: 0.95rem;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item.total {
  font-size: 1.1rem;
  padding-top: 1rem;
  border-top: 2px solid #e9ecef;
  margin-top: 1rem;
}

/* Buttons */
.btn-primary {
  background: linear-gradient(135deg, #0d6efd 0%, #0056b3 100%);
  border: none;
  border-radius: 12px;
  padding: 1rem 2rem;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(13, 110, 253, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4);
  background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
}

.btn-primary:disabled {
  background: #6c757d;
  transform: none;
  box-shadow: none;
}

/* Responsive */
@media (max-width: 768px) {
  .checkout-header .d-flex {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .checkout-steps {
    justify-content: center;
  }
  
  .card-body {
    padding: 1.5rem;
  }
  
  .payment-method {
    padding: 1rem;
  }
  
  .payment-icon {
    width: 50px;
    height: 50px;
    font-size: 1.25rem;
  }
}

@media (max-width: 576px) {
  .checkout-steps {
    gap: 0.5rem;
  }
  
  .step-line {
    width: 30px;
  }
  
  .step span {
    font-size: 0.75rem;
  }
  
  .card-body {
    padding: 1rem;
  }
}

/* Scrollbar styling */
.order-items::-webkit-scrollbar {
  width: 6px;
}

.order-items::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.order-items::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.order-items::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.form-select-xl {
  height: 60px;        /* Chiều cao dropdown lớn hơn */
  font-size: 1.1rem;   /* Chữ to hơn */
  padding: 0.5rem 1rem; /* Khoảng cách bên trong rộng hơn */
}
</style>
