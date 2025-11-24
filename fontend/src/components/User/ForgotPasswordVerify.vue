
<template>
  <div class="container mt-5" style="max-width: 500px">
    <h3 class="mb-4 text-center">Xác minh mã OTP</h3>
    <div class="card p-4 shadow">
      <div class="mb-3">
        <label class="form-label">Mã OTP</label>
        <input
          v-model="otp"
          type="text"
          class="form-control"
          placeholder="Nhập mã OTP gửi về email"
          :disabled="loading"
        />
      </div>

      <div class="mb-3">
        <label class="form-label">Mật khẩu mới</label>
        <input
          v-model="newPassword"
          type="password"
          class="form-control"
          placeholder="Nhập mật khẩu mới"
          :disabled="loading"
        />
      </div>

      <button
        class="btn btn-success w-100"
        @click="resetPassword"
        :disabled="loading || !otp || !newPassword"
      >
        <span v-if="loading">
          <span class="spinner-border spinner-border-sm me-2"></span>
          Đang xử lý...
        </span>
        <span v-else>Xác nhận và đổi mật khẩu</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '@/axios'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()

const email = route.query.email || ''
const otp = ref('')
const newPassword = ref('')
const loading = ref(false)

// const resetPassword = async () => {
//   loading.value = true
//   try {
//     const res = await axios.post('/api/forgot-password/verify', {
//       email,
//       otp: otp.value,
//       newPassword: newPassword.value
//     })

//     // ✅ Hiển thị dialog hỏi người dùng có muốn quay lại trang đăng nhập không
//     const result = await Swal.fire({
//       icon: 'success',
//       title: 'Thành công',
//       text: res.data.message,
//       showCancelButton: true,
//       confirmButtonText: 'Về đăng nhập',
//       cancelButtonText: 'Ở lại',
//     })

//     if (result.isConfirmed) {
//       router.push({ name: 'dangNhap' }) // ✅ Đúng route đăng nhập
//     }
//   } catch (err) {
//     Swal.fire({
//       icon: 'error',
//       title: 'Lỗi',
//       text: err.response?.data?.message || 'Xác minh OTP thất bại'
//     })
//   } finally {
//     loading.value = false
//   }
// }
const resetPassword = async () => {
  if (!otp.value || !newPassword.value) {
    return Swal.fire({
      icon: 'warning',
      title: 'Thiếu thông tin',
      text: 'Vui lòng nhập đầy đủ OTP và mật khẩu mới.'
    })
  }

  // Kiểm tra mật khẩu mạnh
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/
  if (!passwordRegex.test(newPassword.value)) {
    return Swal.fire({
      icon: 'warning',
      title: 'Mật khẩu chưa đủ mạnh',
      text: 'Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường và số.'
    })
  }

  loading.value = true
  try {
    const res = await axios.post('/api/forgot-password/verify', {
      email,
      otp: otp.value,
      newPassword: newPassword.value
    })

    const result = await Swal.fire({
      icon: 'success',
      title: 'Thành công',
      text: res.data.message,
      showCancelButton: true,
      confirmButtonText: 'Về đăng nhập',
      cancelButtonText: 'Ở lại',
    })

    if (result.isConfirmed) {
      router.push({ name: 'dangNhap' })
    }
  } catch (err) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: err.response?.data?.message || 'Xác minh OTP thất bại'
    })
  } finally {
    loading.value = false
  }
}

</script>
