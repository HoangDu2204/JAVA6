<template>
  <div class="lien-he">
    <main>
      <div>
        <section class="bread-crumb"
          style="background: linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)), url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436) center no-repeat;">
          <div class="container">
            <div class="title-bread-crumb" style="padding-left: 100px;">
              Giỏ hàng
            </div>
            <ul class="breadcrumb">
              <li class="home">
                <a href="/"><span>Trang chủ ></span></a>
                <a href="/Cart"><span>Giỏ hàng</span></a>
              </li>
            </ul>
          </div>
        </section>
      </div>
    </main>

    <div class="container">
      <div class="row">
        <div class="col-md-8">
          <h2>Giỏ hàng</h2>
          <table class="table cart-table">
            <thead>
              <tr>
                <th class="product-info">Thông tin sản phẩm</th>
                <th class="price">Đơn giá</th>
                <th class="quantity">Số lượng</th>
                <th class="total">Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cartItems" :key="item.id">
                <td class="product-info">
                  <div class="d-flex align-items-center">
                    <div class="product-image">
                      <img v-if="item.productImage" :src="'http://localhost:8080/images/' + item.productImage"
                        style="width: 80px; height: 80px; object-fit: cover;" alt="Ảnh sản phẩm" />
                    </div>
                    <div class="product-details ms-2">
                      <h5 class="product-name">{{ item.productName }}</h5>
                      <p class="text-muted small">
                        <span v-for="(line, index) in item.variantLines" :key="index">{{ line }}<br /></span>
                      </p>
                      <small class="text-warning fw-semibold delete-link" @click="deleteCartItem(item.id)">
                        Xóa
                      </small>
                    </div>
                  </div>
                </td>
                <td class="price">
                  <h5 class="price-value">{{ item.discountedPrice.toLocaleString() || 0 }}₫</h5>
                </td>
                <td class="">
                  <!-- <div class="quantity-wrapper mt-1">
                    <button class="quantity-btn" @click="decreaseQuantity(item)">−</button>
                    <input type="text" class="quantity-input" :value="item.quantity" readonly />
                    <button class="quantity-btn" @click="increaseQuantity(item)"
                      :disabled="invalidItemIds.includes(item.id)">
                      +
                    </button>


                  </div> -->
                  <div class="quantity-wrapper mt-1">
                    <button class="quantity-btn" @click="decreaseQuantity(item)">−</button>
                    <input type="number" class="quantity-input text-center" v-model.number="item.quantity" min="1"
                      @change="updateQuantity(item)" />
                    <button class="quantity-btn" @click="increaseQuantity(item)"
                      :disabled="invalidItemIds.includes(item.id)">
                      +
                    </button>
                  </div>


                </td>
                <td class="total">
                  <h5 class="total-value" v-if="!invalidItemIds.includes(item.id)">
                    {{ (item.discountedPrice * item.quantity).toLocaleString() }}₫
                  </h5>
                  <h5 class="total-value text-danger" v-else title="Không thể tính do vượt quá tồn kho">
                    0₫
                  </h5>
                </td>

              </tr>
            </tbody>
          </table>

          <div class="text-end fw-bold fs-5">
            Tổng tiền: {{ filteredTotalPrice.toLocaleString() }}₫

          </div>

          <div class="text-end mt-3 mb-3">
            <button class="btn btn-primary-custom px-4 py-2" @click="handleCheckout"
              :disabled="cartItems.length === 0 || filteredTotalPrice === 0">
              Thanh toán
            </button>

            <!-- <button class="btn btn-primary-custom px-4 py-2" @click="handleCheckout">Thanh toán</button> -->
          </div>
        </div>

        <!-- Thanh toán (Bên phải) -->
        <div class="col-md-4">
          <div class="voucher-box mb-4">
            <div class="cart-tinhtrang">
              <div class="title_voucher">
                <img src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/voucher.png?1735117293436"
                  alt="voucher" />
                <span>Nhận voucher ngay !!!</span>
              </div>
              <div class="thump-check">
                <div class="check-bar"></div>
                <div class="check-bar1" :style="{ width: progressWidth }"></div>
                <div class="dot dot1" style="left: calc(30% - 10px);"></div>
                <div class="dot dot2" style="left: calc(70% - 10px);"></div>
                <div class="dot dot3" style="left: calc(100% - 10px);"></div>
              </div>
              <!-- <ul class="coupon1">
                <li class="item-coupon1" v-for="(voucher, index) in eligibleVouchers" :key="voucher.code">
                  <div class="coupon-content1">
                    <b class="conlai">Còn {{ voucher.missingAmount.toLocaleString() }}₫</b>
                    để được nhận mã <strong>{{ voucher.code }}</strong>
                  </div>
                  <div class="coupon-code1 js-copy"
                    :class="{ disabled: voucher.missingAmount > 0 || copiedCodes.includes(voucher.code) }"
                    :data-copy="voucher.code"
                    @click="voucher.missingAmount <= 0 && !copiedCodes.includes(voucher.code) ? copyToClipboard(voucher.code) : null">
                    {{ copiedCodes.includes(voucher.code) ? 'Đã sao chép' : 'Sao chép' }}
                  </div>
                </li>
              </ul> -->

              <ul class="coupon1">
                <li class="item-coupon1" v-for="(voucher, index) in eligibleVouchers" :key="voucher.code">
                  <div class="coupon-content1">
                    <b class="conlai">Còn {{ voucher.missingAmount.toLocaleString() }}₫</b>
                    để được nhận mã
                    <strong>{{ voucher.code }}</strong>
                    <span class="text-dark fw-bold">(Giảm {{ voucher.discountPercent }}%)</span>
                  </div>
                  <div class="coupon-code1 js-copy"
                    :class="{ disabled: voucher.missingAmount > 0 || copiedCodes.includes(voucher.code) }"
                    :data-copy="voucher.code"
                    @click="voucher.missingAmount <= 0 && !copiedCodes.includes(voucher.code) ? copyToClipboard(voucher.code) : null">
                    {{ copiedCodes.includes(voucher.code) ? 'Đã sao chép' : 'Sao chép' }}
                  </div>
                </li>
              </ul>


            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>



<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/axios'
import Swal from 'sweetalert2'

const router = useRouter()
const cartItems = ref([])
const totalPrice = ref(0)
const isAgentApproved = ref(false)
const isAgentPending = ref(false)
const eligibleVouchers = ref([])
const copiedCodes = ref([])
const invalidItemIds = ref([])

const loadCart = async () => {
  try {
    const res = await api.get('/api/cart')
    cartItems.value = res.data
    await loadTotalPrice()
  } catch (err) {
    console.error('Lỗi khi load giỏ hàng:', err)
  }
}

const loadTotalPrice = async () => {
  try {
    const res = await api.get('/api/cart/total')
    totalPrice.value = res.data
  } catch (err) {
    console.error('Lỗi khi load tổng tiền:', err)
  }
}

const updateQuantity = async (item) => {
  const oldQuantity = item.quantity
  if (item.quantity < 1) item.quantity = 1

  try {
    await api.put('/api/cart/update', {
      cartItemId: item.id,
      quantity: item.quantity
    })

    invalidItemIds.value = invalidItemIds.value.filter(id => id !== item.id)
    await loadTotalPrice()
  } catch (err) {
    item.quantity = oldQuantity

    const errorMsg =
      typeof err?.response?.data === 'string'
        ? err.response.data
        : err?.response?.data?.error || 'Không thể cập nhật sản phẩm trong giỏ hàng.'

    if (!invalidItemIds.value.includes(item.id)) {
      invalidItemIds.value.push(item.id)
    }

    Swal.fire({
      icon: 'warning',
      title: 'Lỗi cập nhật số lượng',
      text: errorMsg,
      timer: 3000,
      showConfirmButton: false,
      position: 'center'
    })
  }
}

const increaseQuantity = async (item) => {
  item.quantity++
  await updateQuantity(item)
  await loadCart()
}

const decreaseQuantity = async (item) => {
  if (item.quantity > 1) {
    item.quantity--
    await updateQuantity(item)
    await loadCart()
  }
}

const deleteCartItem = async (id) => {
  try {
    await api.delete('/api/cart/delete', { data: { cartItemId: id } })
    await loadCart()
  } catch (err) {
    console.error('Lỗi khi xoá sản phẩm:', err)
  }
}

const checkUserRole = async () => {
  try {
    const res = await api.get('/api/agents/info')
    const agent = res.data

    if (agent.isApproved === true) {
      isAgentApproved.value = true
      isAgentPending.value = false
    } else {
      isAgentApproved.value = false
      isAgentPending.value = true
    }
  } catch (err) {
    isAgentApproved.value = false
    isAgentPending.value = false
  }
}

const handleCheckout = async () => {
  await checkUserRole()

  if (isAgentApproved.value) {
    Swal.fire({
      title: 'Chọn phương thức thanh toán',
      text: 'Bạn muốn đặt hàng như đại lý hay người dùng thường?',
      showDenyButton: true,
      confirmButtonText: 'Người dùng thường',
      denyButtonText: 'Đại lý'
    }).then(result => {
      if (result.isConfirmed) router.push('/ThanhToan')
      else if (result.isDenied) router.push('/ThanhToanDaiLy')
    })
  } else if (isAgentPending.value) {
    Swal.fire({
      icon: 'info',
      title: 'Tài khoản đại lý chưa được duyệt',
      text: 'Bạn đã đăng ký làm đại lý nhưng đang chờ admin duyệt. Vui lòng đặt hàng như người dùng thường.',
      confirmButtonText: 'OK'
    }).then(() => {
      router.push('/ThanhToan')
    })
  } else {
    router.push('/ThanhToan')
  }
}

const filteredTotalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    if (!invalidItemIds.value.includes(item.id)) {
      return sum + item.quantity * item.discountedPrice
    }
    return sum
  }, 0)
})

const fetchVouchers = async () => {
  try {
    const res = await api.get('/api/vouchers/eligible', {
      params: { cartTotal: filteredTotalPrice.value } // ✅ SỬA CHỖ NÀY
    })
    eligibleVouchers.value = res.data
  } catch (err) {
    console.error('Lỗi khi lấy voucher:', err)
  }
}

// ✅ Gọi lại fetchVouchers mỗi khi tổng tiền thực tế thay đổi
watch(filteredTotalPrice, fetchVouchers, { immediate: true })

const progressWidth = computed(() => {
  if (!eligibleVouchers.value.length) return '0%'
  const min = Math.min(...eligibleVouchers.value.map(v => v.missingAmount))
  const reached = filteredTotalPrice.value / (filteredTotalPrice.value + min)
  return `${Math.min(reached * 100, 100)}%`
})

const copyToClipboard = (code) => {
  if (copiedCodes.value.includes(code)) return
  navigator.clipboard.writeText(code).then(() => {
    copiedCodes.value.push(code)
    Swal.fire({
      icon: 'success',
      title: 'Đã sao chép!',
      text: `Mã "${code}" đã được sao chép.`,
      timer: 2000,
      showConfirmButton: false,
      toast: true,
      position: 'top-end'
    })
  })
}

onMounted(() => {
  loadCart()
  checkUserRole()
})
</script>

<style scoped>
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
  padding-left: 610px;
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
  /* pointer-events: none;
  cursor: default; */
  cursor: pointer;
  position: relative !important;
  z-index: 9999 !important;
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

.quantity-wrapper {
  display: flex;
  align-items: center;
  border: 1.5px solid #d5a84b;
  border-radius: 6px;
  padding: 3px;
  width: fit-content;
  background-color: white;
  gap: 4px;
  /* Giảm khoảng cách giữa nút và số */
}

.quantity-btn {
  background-color: #d5a84b;
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.quantity-btn:hover {
  background-color: #c8992e;
}

.quantity-input {
  width: 40px;
  height: 36px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  background-color: transparent;
  color: #000;
  outline: none;
  border: none;
  pointer-events: none;
}
.quantity-wrapper input {
  width: 60px; /* tăng chiều rộng input */
  text-align: center;
}

.cart-tinhtrang .coupon1 li {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  opacity: 1 !important;
}

.cart-tinhtrang .coupon1 li .coupon-content1 {
  font-size: 18px;
  opacity: 0.5;
}

.cart-tinhtrang .coupon1 li .coupon-code1 {
  opacity: 1 !important;
  pointer-events: auto !important;
  background: #cd9b32;
  color: white;
  padding: 2px 15px;
  border-radius: 5px;
  margin-top: 5px;
  cursor: pointer;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.bread-crumb .title-bread-crumb[data-v-6c050207] {
  text-align: center;
  font-size: 55px;
  color: #cd9b32;
  font-weight: 600;
  font-family: 'Playball';
}
</style>
