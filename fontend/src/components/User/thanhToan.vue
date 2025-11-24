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
            <h2 class="mb-0">Thanh toán đơn hàng</h2>
            <p class="text-muted mb-0">Hoàn tất thông tin để đặt hàng</p>
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
                <i class="bi bi-geo-alt-fill text-primary me-2"></i>
                Thông tin giao hàng
              </h5>
            </div>
            <div class="card-body">
              <!-- Chọn địa chỉ đã lưu -->
              <div class="mb-4">
                <label class="form-label fw-semibold">Địa chỉ đã lưu</label>
                <select class="form-select form-select-lg" v-model="selectedAddressId" @change="onAddressSelect">
                  <option value="">{{ displayAddressInSelect }}</option>
                  <option v-for="addr in savedAddresses" :key="addr.id" :value="addr.id">
                    {{ addr.address }}, {{ addr.wardName }}, {{ addr.districtName }}, {{ addr.provinceName }}
                  </option>
                </select>
              </div>

              <div class="row g-3">
                <!-- Email -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-envelope text-muted me-1"></i>
                    Email
                  </label>
                  <input type="email" class="form-control form-control-lg" placeholder="Nhập email" v-model="form.email" />
                  <div class="text-danger small mt-1" v-if="errors.email">{{ errors.email }}</div>
                </div>

                <!-- Họ và tên -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-person text-muted me-1"></i>
                    Họ và tên
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Nhập họ và tên" v-model="form.name" />
                  <div class="text-danger small mt-1" v-if="errors.name">{{ errors.name }}</div>
                </div>

                <!-- Số điện thoại -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-telephone text-muted me-1"></i>
                    Số điện thoại
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Nhập số điện thoại" v-model="form.phone" />
                  <div class="text-danger small mt-1" v-if="errors.phone">{{ errors.phone }}</div>
                </div>

                <!-- Địa chỉ chi tiết -->
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-house text-muted me-1"></i>
                    Địa chỉ chi tiết
                  </label>
                  <input type="text" class="form-control form-control-lg" placeholder="Số nhà, đường..." v-model="form.address" />
                  <div class="text-danger small mt-1" v-if="errors.address">{{ errors.address }}</div>
                </div>

                <!-- Tỉnh/Thành phố -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Tỉnh/Thành phố</label>
                  <select class="form-select form-select-lg" v-model="selectedProvince" @change="onProvinceChange">
                    <option disabled value="">Chọn Tỉnh/Thành phố</option>
                    <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
                  </select>
                  <div class="text-danger small mt-1" v-if="errors.province">{{ errors.province }}</div>
                </div>

                <!-- Quận/Huyện -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Quận/Huyện</label>
                  <select class="form-select form-select-lg" v-model="selectedDistrictCode" :disabled="!districts.length" @change="onDistrictChange">
                    <option disabled value="">Chọn Quận/Huyện</option>
                    <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
                  </select>
                  <div class="text-danger small mt-1" v-if="errors.district">{{ errors.district }}</div>
                </div>

                <!-- Phường/Xã -->
                <div class="col-md-4">
                  <label class="form-label fw-semibold">Phường/Xã</label>
                  <select class="form-select form-select-lg" v-model="selectedWardCode" :disabled="!wards.length" @change="onWardChange">
                    <option disabled value="">Chọn Phường/Xã</option>
                    <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
                  </select>
                  <div class="text-danger small mt-1" v-if="errors.ward">{{ errors.ward }}</div>
                </div>

                <!-- Ghi chú -->
                <div class="col-12">
                  <label class="form-label fw-semibold">
                    <i class="bi bi-chat-text text-muted me-1"></i>
                    Ghi chú (tùy chọn)
                  </label>
                  <textarea class="form-control" rows="3" placeholder="Ghi chú về đơn hàng..." v-model="form.note"></textarea>
                  <div class="text-danger small mt-1" v-if="errors.note">{{ errors.note }}</div>
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
              <div class="voucher-section mb-3">
                <label class="form-label fw-semibold">Mã giảm giá</label>
                <div class="input-group">
                  <input type="text" class="form-control" v-model="form.voucherCode" placeholder="Nhập mã giảm giá" @input="isVoucherValid = true" />
                  <button class="btn btn-outline-primary" @click="applyVoucher">
                    <i class="bi bi-tag-fill"></i>
                  </button>
                </div>
              </div>

              <!-- Tổng tiền -->
              <div class="order-summary">
                <div class="summary-item">
                  <span>Tổng tiền sản phẩm</span>
                  <span>{{ formatPrice(totalAmount) }}</span>
                </div>
                <div class="summary-item text-success" v-if="isVoucherValid && voucherDiscountAmount > 0">
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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

// ===== FORM STATE =====
const form = reactive({
  email: '',
  name: '',
  phone: '',
  address: '',
  note: '',
  paymentMethod: '',
  paymentStatus: 'Chưa thanh toán',
  shippingFee: 0,
  discountAmount: 0,
  totalAmount: 0,
  voucherCode: ''
})

const errors = reactive({})
const provinces = ref([])
const districts = ref([])
const wards = ref([])

const selectedProvince = ref('')
const selectedDistrictCode = ref('')
const selectedWardCode = ref('')
// const selectedPrefix = ref('+84')

// const phonePrefixes = ref([
//   { code: '+84', country: 'Việt Nam' },
//   { code: '+1', country: 'Mỹ' },
//   { code: '+44', country: 'Anh' },
//   { code: '+81', country: 'Nhật Bản' },
//   { code: '+61', country: 'Úc' }
// ])

const savedAddresses = ref([])
const selectedAddressId = ref('')
const cartItems = ref([])
const totalAmount = ref(0)
const voucherDiscountAmount = ref(0)
const finalTotal = ref(0)
const isVoucherValid = ref(true)
const initialSelectedAddressId = ref('')

// ===== FORMAT PRICE =====
const formatPrice = val => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND'
}).format(val)

// ===== LOAD LOCATION =====
const fetchProvinces = async () => {
  try {
    const res = await api.get('/api/locations/provinces')
    provinces.value = res.data.map(p => ({ code: p.ProvinceID, name: p.ProvinceName }))
  } catch (err) {
    console.error('Lỗi lấy tỉnh:', err)
    provinces.value = []
  }
}

const fetchDistricts = async (provinceId) => {
  try {
    const res = await api.get(`/api/locations/districts/${provinceId}`)
    districts.value = res.data.map(d => ({ code: d.DistrictID, name: d.DistrictName }))
  } catch (err) {
    console.error('Lỗi lấy quận:', err)
    districts.value = []
  }
}

const fetchWards = async (districtId) => {
  try {
    const res = await api.get(`/api/locations/wards/${districtId}`)
    wards.value = res.data.map(w => ({ code: w.WardCode, name: w.WardName }))
  } catch (err) {
    console.error('Lỗi lấy phường:', err)
    wards.value = []
  }
}

const onProvinceChange = async () => {
  if (!selectedProvince.value) return
  await fetchDistricts(selectedProvince.value)
  districts.value.length && (selectedDistrictCode.value = '')
  wards.value = []
  selectedWardCode.value = ''
}

const onDistrictChange = async () => {
  if (!selectedDistrictCode.value) return
  await fetchWards(selectedDistrictCode.value)
  selectedWardCode.value = ''
  await calculateShippingFee()
}

const onWardChange = async () => {
  await calculateShippingFee()
}

// ===== ON SELECT SAVED ADDRESS =====
// const onAddressSelect = async () => {
//   const addr = savedAddresses.value.find(a => a.id === selectedAddressId.value)
//   if (!addr) return

//   form.name = addr.name
//   form.phone = addr.phone
//   form.address = addr.address

//   selectedProvince.value = addr.idProvince
//   await onProvinceChange()

//   selectedDistrictCode.value = addr.idDistrict
//   await onDistrictChange()

//   selectedWardCode.value = addr.idCommune
//   await calculateShippingFee()
// }
const onAddressSelect = async () => {
  const addr = savedAddresses.value.find(a => a.id === selectedAddressId.value)
  if (!addr) return
  form.email = addr.email
  form.name = addr.name
  form.phone = addr.phone
  form.address = addr.address

  selectedProvince.value = addr.idProvince
  await onProvinceChange()

  selectedDistrictCode.value = addr.idDistrict
  await onDistrictChange()

  selectedWardCode.value = addr.idCommune
  await calculateShippingFee()

  initialSelectedAddressId.value = selectedAddressId.value  // Lưu lại ID khi chọn

  console.log('Đã chọn địa chỉ lưu:', addr)
  console.log('initialSelectedAddressId set thành:', initialSelectedAddressId.value)
}

watch([selectedProvince, selectedDistrictCode, selectedWardCode], ([newProvince, newDistrict, newWard]) => {
  console.log('Watcher triggered:')
  console.log('  selectedProvince:', newProvince)
  console.log('  selectedDistrictCode:', newDistrict)
  console.log('  selectedWardCode:', newWard)
  console.log('  initialSelectedAddressId:', initialSelectedAddressId.value)

  if (!initialSelectedAddressId.value) return // Chưa chọn địa chỉ lưu thì không làm gì

  const addr = savedAddresses.value.find(a => a.id === initialSelectedAddressId.value)
  if (!addr) return

  if (addr.idProvince !== newProvince || addr.idDistrict !== newDistrict || addr.idCommune !== newWard) {
    console.log('Phát hiện thay đổi địa chỉ không khớp địa chỉ lưu ban đầu, reset selectedAddressId')
    selectedAddressId.value = ''
    initialSelectedAddressId.value = ''
  }
})

watch([selectedProvince, selectedDistrictCode, selectedWardCode], ([newProvince, newDistrict, newWard]) => {
  if (!initialSelectedAddressId.value) return // Chưa chọn địa chỉ lưu thì không làm gì

  const addr = savedAddresses.value.find(a => a.id === initialSelectedAddressId.value)
  if (!addr) return

  // Nếu tỉnh, huyện hoặc xã khác với địa chỉ lưu ban đầu thì reset selectedAddressId
  if (addr.idProvince !== newProvince || addr.idDistrict !== newDistrict || addr.idCommune !== newWard) {
    selectedAddressId.value = ''
    initialSelectedAddressId.value = ''
  }
})

// ===== SHIPPING FEE =====
const calculateShippingFee = async () => {
  if (!selectedDistrictCode.value || !selectedWardCode.value) return
  try {
    const weight = cartItems.value.reduce((sum, i) => sum + ((i.weight || 500) * i.quantity), 0)
    // Sử dụng tổng tiền gốc (trước giảm giá) để tính insurance value, giống như backend
    const originalTotalAmount = cartItems.value.reduce((sum, i) => {
      // Sử dụng giá gốc nếu có, nếu không thì dùng giá hiện tại
      const originalPrice = i.originalPrice || i.price || i.discountedPrice
      return sum + (originalPrice * i.quantity)
    }, 0)
    const insuranceValue = originalTotalAmount

    const res = await api.post('/api/orders/shipping-fee', {
      toDistrictId: Number(selectedDistrictCode.value),
      toWardCode: selectedWardCode.value,
      weight,
      insuranceValue
    })

    form.shippingFee = res.data.shippingFee || 0
    // Tính tổng cuối cùng: Tạm tính - Giảm giá voucher + Phí vận chuyển
    finalTotal.value = totalAmount.value - voucherDiscountAmount.value + form.shippingFee
    
    console.log('📦 Shipping fee calculation:')
    console.log('  - Weight:', weight)
    console.log('  - Insurance value:', insuranceValue)
    console.log('  - Shipping fee:', form.shippingFee)
    console.log('  - Final total:', finalTotal.value)
  } catch (err) {
    console.error('Lỗi tính phí ship:', err)
    form.shippingFee = 0
  }
}

// ===== APPLY VOUCHER =====
const applyVoucher = async () => {
  try {
    const res = await api.post('/api/orders/voucher/apply', {
      voucherCode: form.voucherCode,
      totalAmount: totalAmount.value
    })

    voucherDiscountAmount.value = res.data.discountAmount
    form.discountAmount = voucherDiscountAmount.value
    // Tính tổng cuối cùng: Tạm tính - Giảm giá voucher + Phí vận chuyển
    finalTotal.value = totalAmount.value - voucherDiscountAmount.value + form.shippingFee
    isVoucherValid.value = true

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

    isVoucherValid.value = false

    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: message
    })
  }
}

// ===== VALIDATE FORM =====
const validateForm = () => {
  Object.keys(errors).forEach(k => delete errors[k])

  if (!form.email) errors.email = 'Vui lòng nhập email'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = 'Email không đúng định dạng'

  if (!form.name) errors.name = 'Vui lòng nhập họ tên'
  else if (form.name.length > 50) errors.name = 'Họ tên không quá 50 ký tự'
  else if (/[^a-zA-ZÀ-ỹ\s]/.test(form.name)) errors.name = 'Không được chứa ký tự đặc biệt hoặc số'

  if (!form.phone) errors.phone = 'Vui lòng nhập số điện thoại'
  else if (!/^\d{10}$/.test(form.phone)) errors.phone = 'Số điện thoại phải đúng 10 số'

  if (!form.address) errors.address = 'Vui lòng nhập địa chỉ'
  else if (form.address.length > 50) errors.address = 'Địa chỉ không quá 50 ký tự'

  if (form.note.length > 200) errors.note = 'Ghi chú không quá 200 ký tự'

  if (!selectedProvince.value) errors.province = 'Vui lòng chọn tỉnh/thành phố'
  if (!selectedDistrictCode.value) errors.district = 'Vui lòng chọn quận/huyện'
  if (!selectedWardCode.value) errors.ward = 'Vui lòng chọn phường/xã'
  if (!form.paymentMethod) errors.paymentMethod = 'Vui lòng chọn phương thức thanh toán'

  return Object.keys(errors).length === 0
}

// ===== SUBMIT ORDER =====
const submitOrder = async () => {
  if (!validateForm()) {
    Swal.fire({
      icon: 'error',
      title: 'Thông tin không hợp lệ',
      text: 'Vui lòng kiểm tra lại thông tin!'
    })
    return
  }

  try {
    const cartRes = await api.get('/api/cart')
    cartItems.value = cartRes.data
    const total = cartItems.value.reduce((sum, i) => sum + i.discountedPrice * i.quantity, 0)
    totalAmount.value = total
    form.totalAmount = total

    const province = provinces.value.find(p => p.code === selectedProvince.value)?.name || ''
    const district = districts.value.find(d => d.code === selectedDistrictCode.value)?.name || ''
    const ward = wards.value.find(w => w.code === selectedWardCode.value)?.name || ''
    const fullAddress = `${form.address}, ${ward}, ${district}, ${province}`

    const payload = {
      ...form,
      address: fullAddress,
      // phone: selectedPrefix.value + form.phone,
      phone: form.phone,
      totalAmount: totalAmount.value, // Gửi tổng tiền từ frontend
      items: cartItems.value.map(i => ({
        variantId: i.variantId,
        quantity: i.quantity,
        unitPrice: i.discountedPrice
      })),
      toDistrictId: selectedDistrictCode.value,
      toWardCode: selectedWardCode.value
    }

    const orderRes = await api.post('/api/orders/checkout', payload)
    finalTotal.value = total - voucherDiscountAmount.value + form.shippingFee
    form.discountAmount = voucherDiscountAmount.value

    if (form.paymentMethod === 'BANK') {
      const vnpayRes = await api.post('/api/payment/create', null, {
        params: {
          amount: finalTotal.value,
          orderId: orderRes.data.order.customOrderCode
        }
      })
      window.location.href = vnpayRes.data.url
      return
    }

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
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: 'Đặt hàng thất bại!',
      text: 'Vui lòng thử lại sau.'
    })
  }
}

// ===== INIT DATA =====
onMounted(async () => {
  try {
    await fetchProvinces()

    const cartRes = await api.get('/api/cart')
    cartItems.value = cartRes.data
    totalAmount.value = cartItems.value.reduce((sum, i) => sum + i.discountedPrice * i.quantity, 0)

    const addressRes = await api.get('/api/addresses')
    savedAddresses.value = addressRes.data
  } catch (err) {
    console.error('Lỗi khi khởi tạo dữ liệu:', err)
  }
})

// ===== AUTO TÍNH PHÍ SHIP =====
watch([selectedDistrictCode, selectedWardCode], () => {
  if (selectedDistrictCode.value && selectedWardCode.value) {
    calculateShippingFee()
  }
})



const displayAddressInSelect = computed(() => {
  if (!selectedAddressId.value) return 'Địa chỉ khác'  // Khi không chọn địa chỉ lưu thì hiển thị chữ này

  const addr = savedAddresses.value.find(a => a.id === selectedAddressId.value)
  if (!addr) return 'Địa chỉ khác'

  return `${addr.address}, ${addr.wardName}, ${addr.districtName}, ${addr.provinceName}`
})

</script>







<style scoped>
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
.voucher-section .input-group {
  border-radius: 12px;
  overflow: hidden;
}

.voucher-section .form-control {
  border-right: none;
  border-radius: 12px 0 0 12px;
}

.voucher-section .btn {
  border-radius: 0 12px 12px 0;
  border-left: none;
  padding: 0.75rem 1rem;
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

.btn-outline-primary {
  border: 2px solid #0d6efd;
  color: #0d6efd;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-outline-primary:hover {
  background: #0d6efd;
  color: white;
  transform: translateY(-1px);
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
</style>
