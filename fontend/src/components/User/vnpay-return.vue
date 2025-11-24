<template>
  <div class="container text-center mt-5">
    <h3 class="mb-3">{{ message }}</h3>
    <router-link to="/donHang" class="btn btn-success">Xem đơn hàng</router-link>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const route = useRoute()
const message = ref(' Đang xử lý kết quả thanh toán...')

onMounted(async () => {
  try {
    // Gửi request lên backend để xử lý kết quả thanh toán
    const res = await axios.get('http://localhost:8080/api/payment/return', {
      params: route.query
    })

    // Gán thông báo trả về từ backend
    message.value = res.data

    //  Hiện Swal thành công nếu có chữ "thành công"
    if (res.data.includes('thành công')) {
      Swal.fire({
        icon: 'success',
        title: 'Thanh toán thành công!',
        text: res.data,
        confirmButtonText: 'Xem đơn hàng'
      }).then((result) => {
        if (result.isConfirmed) {
          // Chuyển hướng về trang đơn hàng
          window.location.href = '/donHang'
        }
      })
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Thanh toán thất bại!',
        text: res.data,
        confirmButtonText: 'Quay lại'
      }).then((result) => {
        if (result.isConfirmed) {
          // Chuyển hướng về trang giỏ hàng
          window.location.href = '/gioHang'
        }
      })
    }

  } catch (err) {
    message.value = '❌ Có lỗi xảy ra trong quá trình xử lý thanh toán!'
    Swal.fire({
      icon: 'error',
      title: 'Lỗi hệ thống!',
      text: 'Không thể xác nhận kết quả thanh toán.',
      confirmButtonText: 'Quay lại'
    })
  }
})
</script>
