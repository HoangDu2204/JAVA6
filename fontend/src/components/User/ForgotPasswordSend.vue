
<template>
  <div class="forgot-password-page">
    <div class="overlay"></div>

    <div class="content-wrapper">
      <div class="card shadow-lg p-4">
        <h3 class="text-center mb-4 text-primary fw-bold">🧁 Quên mật khẩu</h3>

        <div class="mb-3">
          <label for="email" class="form-label fw-semibold">Email đã đăng ký</label>
          <input
            v-model="email"
            type="email"
            id="email"
            class="form-control"
            placeholder="Nhập email của bạn"
            :disabled="loading"
          />
        </div>

        <button
          class="btn btn-primary w-100 fw-semibold"
          @click="sendOtp"
          :disabled="loading || !email"
        >
          <template v-if="loading">
            <span class="spinner-border spinner-border-sm me-2"></span> Đang gửi...
          </template>
          <template v-else>
            📩 Gửi mã OTP
          </template>
        </button>

        <div class="text-center mt-3">
          <RouterLink to="/DangNhap" class="back-link">
            ← Quay lại trang đăng nhập
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '@/axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const email = ref('')
const loading = ref(false)
const router = useRouter()

const sendOtp = async () => {
  loading.value = true
  try {
    const res = await axios.post('/api/forgot-password/send-otp', {
      email: email.value
    })

    await Swal.fire({
      icon: 'success',
      title: 'Thành công',
      text: res.data.message,
      timer: 2000,
      showConfirmButton: false
    })

    router.push({ name: 'ForgotPasswordVerify', query: { email: email.value } })
  } catch (err) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: err.response?.data?.message || 'Gửi mã OTP thất bại'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-page {
  position: relative;
 background: url("https://bizweb.dktcdn.net/100/492/035/themes/919334/assets/2banner_2.jpg?1735117293436") no-repeat center center fixed;
  background-size: cover;
  min-height: 100vh;
  overflow: hidden;
}


.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(4px);
  z-index: 1;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
}

.card {
  width: 100%;
  max-width: 450px;
  border-radius: 1.5rem;
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease-in-out;
}

.card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
}

button.btn-primary {
  background-color: #ff7f50;
  border: none;
  transition: all 0.3s ease;
  border-radius: 0.5rem;
}

button.btn-primary:hover {
  background-color: #e66b3c;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(255, 127, 80, 0.4);
}

.back-link {
  color: #333;
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s ease;
}

.back-link:hover {
  color: #ff9933;
}
</style>
