<template>
  <div class="page-dang-ky">
    <main>
      <section class="bread-crumb full-width-banner" style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">Đăng nhập</div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr">&nbsp;
                <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" class="svg-inline--fa fa-chevron-right fa-w-10">
                  <path fill="currentColor" d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z"></path>
                </svg>&nbsp;
              </span>
            </li>
            <li><strong><span>Đăng nhập</span></strong></li>
          </ul>
        </div>
      </section>

      <div class="dangky container">
        <div class="tabs">
          <RouterLink class="tab active" to="/DangNhap">ĐĂNG NHẬP</RouterLink>
          <RouterLink class="tab" to="/DangKy">ĐĂNG KÝ</RouterLink>
        </div>

        <h2 class="dangky-title">ĐĂNG NHẬP</h2>

        <form @submit.prevent="login">
          <div class="form-group">
            <input v-model="username" type="text" id="username" name="username" class="form-control" placeholder="Vui lòng nhập vào username" />
            <small v-if="errorUsername" class="text-danger">{{ errorUsername }}</small>
          </div>

          <div class="form-group">
            <input v-model="password" type="password" id="password" name="password" class="form-control" placeholder="Vui lòng nhập vào password" />
            <small v-if="errorPassword" class="text-danger">{{ errorPassword }}</small>
          </div>



          <button type="submit" class="dangky-button">Đăng nhập</button>

        </form>
<div class="form-group text-center">
  <RouterLink
    to="/forgot-password"
    class="forgot-link"
  >
    Quên mật khẩu
  </RouterLink>
</div>
        <p class="or-text">Hoặc đăng nhập bằng</p>

        <div class="social-login">
          <button class="fb-button">
            <i class="fab fa-facebook-f"></i> Facebook
          </button>
          <button class="google-button">
            <i class="fab fa-google-plus-g"></i> Google
          </button>
        </div>
      </div>
    </main>
  </div>
</template>


<!-- <script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from '@/axios' // dùng axios có interceptor

const username = ref('')
const password = ref('')
const errorUsername = ref('')
const errorPassword = ref('')
const router = useRouter()
const auth = useAuthStore()

const login = async () => {
  errorUsername.value = ''
  errorPassword.value = ''

  if (!username.value) {
    errorUsername.value = 'Username không được để trống'
  }
  if (!password.value) {
    errorPassword.value = 'Password không được để trống'
  }

  if (errorUsername.value || errorPassword.value) return

  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (res.data && res.data.token) {
      auth.setToken(res.data.token)

      // Tùy theo role chuyển trang
      if (auth.user && auth.user.role === 1) {
        router.push('/dashboard')
      } else {
        router.push('/')
      }
    } else {
      errorPassword.value = res.data.message || 'Tài khoản hoặc mật khẩu sai'
    }
  } catch (err) {
    errorPassword.value = 'Lỗi kết nối đến máy chủ'
    console.error(err)
  }
}
</script> -->

<!-- <script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from '@/axios'
import Swal from 'sweetalert2' // Thêm dòng này để dùng alert

const username = ref('')
const password = ref('')
const errorUsername = ref('')
const errorPassword = ref('')
const router = useRouter()
const auth = useAuthStore()

const login = async () => {
  errorUsername.value = ''
  errorPassword.value = ''

  if (!username.value) {
    errorUsername.value = 'Username không được để trống'
  }
  if (!password.value) {
    errorPassword.value = 'Password không được để trống'
  }

  if (errorUsername.value || errorPassword.value) return

  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (res.data && res.data.token) {
      // Lưu token và role
      auth.setToken(res.data.token)
      localStorage.setItem('role', res.data.role.toString())

      if (res.data.role === 1) {
        Swal.fire({
          icon: 'success',
          title: 'Chào mừng Admin!',
          text: 'Bạn đã đăng nhập với quyền ADMIN.',
          timer: 2000,
          showConfirmButton: false
        })
        router.push('/dasboard')
      } else {
        router.push('/')
      }
    } else {
      errorPassword.value = res.data.message || 'Tài khoản hoặc mật khẩu sai'
    }
  } catch (err) {
    errorPassword.value = 'Lỗi kết nối đến máy chủ'
    console.error(err)
  }
}
</script> -->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from '@/axios'
import Swal from 'sweetalert2'

const username = ref('')
const password = ref('')
const errorUsername = ref('')
const errorPassword = ref('')
const router = useRouter()
const auth = useAuthStore()

const login = async () => {
  // Reset lỗi mỗi lần submit
  errorUsername.value = ''
  errorPassword.value = ''

  // Kiểm tra đầu vào rỗng
  if (!username.value) {
    errorUsername.value = 'Username không được để trống'
  }
  if (!password.value) {
    errorPassword.value = 'Password không được để trống'
  }

  // Nếu có lỗi đầu vào, không gửi request
  if (errorUsername.value || errorPassword.value) return

  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (res.data && res.data.success === false) {
      const message = res.data.message?.toLowerCase() || ''

      if (message.includes('không tồn tại')) {
        Swal.fire({
          icon: 'error',
          title: 'Tên đăng nhập không tồn tại',
          text: 'Vui lòng kiểm tra lại username.'
        })
      } else if (message.includes('mật khẩu')) {
        Swal.fire({
          icon: 'warning',
          title: 'Sai mật khẩu',
          text: 'Mật khẩu bạn nhập không chính xác. Vui lòng thử lại.'
        })
      } else if (message.includes('bị khóa')) {
        Swal.fire({
          icon: 'error',
          title: 'Tài khoản bị khóa',
          text: 'Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.'
        })
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Đăng nhập thất bại',
          text: res.data.message || 'Đã xảy ra lỗi. Vui lòng thử lại.'
        })
      }
      return
    }

    // Đăng nhập thành công
    if (res.data && res.data.token) {
      auth.setToken(res.data.token)
      localStorage.setItem('role', res.data.role.toString())
      localStorage.setItem('username', res.data.username)

      const isAdmin = res.data.role === 1

      Swal.fire({
        icon: 'success',
        title: isAdmin ? 'Chào mừng Admin!' : 'Đăng nhập thành công',
        text: isAdmin
          ? 'Bạn đã đăng nhập với quyền ADMIN.'
          : 'Bạn đã đăng nhập với vai trò USER.',
        timer: 2000,
        showConfirmButton: false
      })

      router.push(isAdmin ? '/dasboard' : '/')
    }
  } catch (err) {
    console.error('Chi tiết lỗi:', err)

    // Có phản hồi từ server nhưng là lỗi (401, 403, ...)
    if (err.response && err.response.data && err.response.data.message) {
      const msg = err.response.data.message.toLowerCase()
      if (msg.includes('không tồn tại')) {
        Swal.fire({
          icon: 'error',
          title: 'Tên đăng nhập không tồn tại',
          text: 'Vui lòng kiểm tra lại username.'
        })
      } else if (msg.includes('mật khẩu')) {
        Swal.fire({
          icon: 'warning',
          title: 'Sai mật khẩu',
          text: 'Mật khẩu không chính xác. Vui lòng thử lại.'
        })
      } else if (msg.includes('bị khóa')) {
        Swal.fire({
          icon: 'error',
          title: 'Tài khoản bị khóa',
          text: 'Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.'
        })
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Đăng nhập thất bại',
          text: err.response.data.message
        })
      }
    }
    // Request gửi nhưng không có phản hồi (server không chạy, lỗi mạng)
    else if (err.request) {
      Swal.fire({
        icon: 'error',
        title: 'Lỗi máy chủ',
        text: 'Không thể kết nối đến máy chủ. Vui lòng thử lại sau.'
      })
    }
    // Lỗi khi cấu hình axios, hoặc lỗi không xác định
    else {
      Swal.fire({
        icon: 'error',
        title: 'Lỗi không xác định',
        text: err.message || 'Đã xảy ra lỗi. Vui lòng thử lại.'
      })
    }
  }
}
</script>



<style>
.forgot-link {
  color: #000; /* màu đen */
  text-decoration: none;
  transition: color 0.3s ease;
  margin-top: 8px;
   display: inline-block; /* để margin-top hoạt động */
}

.forgot-link:hover {
  color: #ffa500; /* màu vàng cam */
}

.page-doi-mk {
  font-family: 'Quicksand', sans-serif;
  ;
  background-color: #fff;
  overflow-x: hidden;
}

header.header-transparent {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  background-color: transparent;
  padding: 5px 0;
  transition: background-color 0.3s ease;
}

header.header-transparent .navbar {
  padding: 0;
}

header.header-transparent .navbar-brand img {
  max-height: 60px;
}

header.header-transparent .navbar-nav .nav-link {
  color: #fff;
  font-weight: 500;
  padding: 10px 15px;
  text-transform: none;
  font-size: 18px;
  position: relative;
  background-color: transparent !important;
  border-bottom: none !important;
  border-radius: 0 !important;
}

header.header-transparent .navbar-nav .nav-link.active {
  color: #cd9b32;
  font-weight: 700;
}

header.header-transparent .navbar-nav .nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15px;
  right: 15px;
  height: 2px;
  background-color: #cd9b32;
}

header.header-transparent .navbar-nav .nav-link:hover {
  color: #cd9b32;
}

header.header-transparent .navbar-icons .nav-link {
  color: #fff;
  font-size: 23px;
  padding: 10px 8px;
}

header.header-transparent .navbar-icons .nav-link:hover {
  color: #cd9b32;
}

.navbar-toggler {
  border-color: rgba(255, 255, 255, 0.5);
}

.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%28255, 255, 255, 0.8%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}


.full-width-banner {

  width: 100vw;
  position: relative;
  left: 50%;
  transform: translateX(-50%);


  min-height: 300px;
  background-size: cover;
  background-position: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 0 50px;
  text-align: center;
  margin-top: 0;
  margin-bottom: 0;
  box-sizing: border-box;
}

.banner-overlay {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
}

.full-width-banner .title-bread-crumb {
  font-size: 55px;
  color: #cd9b32;
  font-weight: normal;
  margin-bottom: 5px;
  font-family: 'Forte', cursive;
}

.full-width-banner .breadcrumb {
  font-size: 18px;
  padding: 0;
  width: auto;
  text-align: center;
  display: inline-block;
  background: none;
  margin: 0;
}

.full-width-banner .breadcrumb li {
  display: inline-block;
  margin: 0 3px;
}

.full-width-banner .breadcrumb li a {
  font-size: 14px;
  color: #eee;
  text-decoration: none;
}

.full-width-banner .breadcrumb li span {
  font-size: 18px;
  color: #eee;
}

.full-width-banner .breadcrumb li .mr_lr svg {
  width: 7px;
  height: 7px;
  margin: 0 5px;
  fill: #eee;
}

.full-width-banner .breadcrumb li strong span {
  color: #cd9b32;
  font-weight: 600;
}

.full-width-banner .breadcrumb li a:hover span {
  color: #fff;
}

/* Contact Section */
.contact-section {
  padding: 40px 0;
  /* Add margin-top if banner overlaps due to CSS fix */
  /* margin-top: -XXpx; Adjust XX based on overlap */
}

/* Rest of the styles remain the same */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.info-box,
.contact-form-box {
  background-color: #fdf8ec;
  padding: 25px;
  border-radius: 10px;
}

.box-title {
  color: #cd9b32;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}


.info-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #cd9b32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-wrapper i {
  color: #fff;
  font-size: 20px;
}

.text-wrapper p {
  margin: 0;
  font-size: 18px;
  color: #555;
  line-height: 1.5;
}

.text-wrapper .label {
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
}

.form-description {
  font-size: 18px;
  color: #555;
  margin-bottom: 20px;
}

.contact-form-box .form-control {
  border-radius: 5px;
  border: 1px solid #ddd;
  padding: 10px 15px;
  font-size: 18px;
}

.contact-form-box .form-control:focus {
  border-color: #cd9b32;
  box-shadow: 0 0 0 0.2rem rgba(205, 155, 50, 0.25);
}

.contact-form-box .btn-submit {
  background-color: #cd9b32;
  color: #fff;
  border: none;
  padding: 10px 25px;
  border-radius: 5px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.contact-form-box .btn-submit:hover {
  background-color: #a88a2a;
}

.map-wrapper {
  height: 100%;
  min-height: 500px;
  border-radius: 10px;
  overflow: hidden;
}

@media (max-width: 991px) {
  header.header-transparent .navbar-nav {
    background-color: rgba(0, 0, 0, 0.8);
    padding: 10px;
    border-radius: 5px;
  }

  header.header-transparent .navbar-nav .nav-link.active::after {
    display: none;
  }

  .info-content {
    grid-template-columns: 1fr;
  }

  .map-wrapper {
    margin-top: 30px;
    height: 400px;
    min-height: 400px;
  }
}

.navbar-nav-menu {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
  white-space: nowrap;
}

.navbar-nav-menu .nav-item {
  white-space: nowrap;
}

.dangky.container {

  margin: 30px auto;
  padding: 30px;
  background-color: #fff8e1;
  border-radius: 10px;
  font-family: 'Segoe UI', sans-serif;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);


  margin: 30px auto;
  padding: 20px;
  background-color: #f7e7c5;
  border-radius: 8px;
  font-family: Arial, sans-serif;
  text-align: center;

}

.tabs {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 30px;
}

.tab {
  font-weight: bold;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  color: #c79b2f;
  text-decoration: none;
  font-size: 18px;
}

.tab:hover {
  color: #c79b2f;
  text-decoration: none;
  border-bottom: 2px solid #c79b2f;
}

.tab.active {
  color: black;
  border-color: #c79b2f;
}

.tab.active:hover {
  color: #c79b2f;
  text-decoration: none;
}

.dangky {
  background: #f5e7c9;
  padding: 30px;
  max-width: 500px;
  margin: 40px auto;
  border-radius: 12px;
}

.tabs {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #c69c6d;
  margin-bottom: 15px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  font-weight: bold;
  color: #c69c6d;
  border-bottom: 2px solid transparent;
}

.tab.active {
  color: black;
  border-bottom: 2px solid #c69c6d;
}

.dangky-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin: 15px 0;
}

.form-group {
  margin-bottom: 12px;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9a84e;
  border-radius: 6px;
}

.dangky-button {
  background: #d9a84e;
  color: white;
  width: 100%;
  padding: 12px;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  margin-top: 10px;
}

.or-text {
  text-align: center;
  margin: 15px 0;
  font-weight: 500;
}

.social-login {
  display: flex;
  gap: 10px;
}

.fb-button,
.google-button {
  flex: 1;
  padding: 10px;
  border: none;
  color: white;
  font-weight: bold;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.fb-button {
  background: #3b5998;
}

.google-button {
  background: #db4437;
}

.user-dropdown .dropdown-menu {
  display: block;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.3s ease;
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 1000;
  min-width: 180px;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
}

.user-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.user-dropdown .dropdown-menu .dropdown-item {
  padding: 10px 20px;
  font-size: 15px;
  color: #333;
  transition: background-color 0.2s ease;
}

.user-dropdown .dropdown-menu .dropdown-item:hover {
  background-color: #f0f0f0;
  color: #000;
}

.forgot {
  display: block;
  margin-top: 10px;
  color: #444;
  text-decoration: none;
  font-size: 13px;
  font-weight: bold;
}

.forgot:hover {
  color: #c79b2f;
  text-decoration: underline;
  text-decoration: none;
}

.or {
  margin: 15px 0 10px;
  font-size: 13px;
  color: #333;
  font-weight: bold;
}



.bread-crumb .title-bread-crumb {
  text-align: center;
  font-size: 35px;
  color: #cd9b32;
  font-weight: 600;
  font-size: 50px;
  font-family: 'Forte';
}

.breadcrumb {
  margin: 0;
  font-size: 14px;
  padding: 15px 10px;
  border-radius: 0;
  font-weight: 400;
  line-height: 24px;
  width: 100%;
  text-align: center;
  padding-left: 540px;
}

.breadcrumb li {
  display: inline-block;
  margin: 0 10px;
}

.breadcrumb li:first-child {
  margin-left: 0;
}

.breadcrumb li:last-child {
  margin-right: 0;
}

.breadcrumb li .mr_lr svg {
  width: 10px;
  height: 10px;
  margin: 0 5px;
  vertical-align: middle;
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

.row {
  margin-left: auto;
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

.ab {
  background-color: #f6e7c8;
  padding: 15px;
  border-radius: 5px;
  width: 400px;
  margin-bottom: 20px;
  margin-left: 600px;
}

.ab h2 {
  text-align: center;
  color: #000000;
  margin-bottom: 15px;
}

.tabs {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  border-bottom: 2px solid #d69f4d;
}

.tabs div {
  padding: 10px 0;
  cursor: pointer;
  flex: 1;
  text-align: center;
  color: #000000;
}

form {
  display: flex;
  flex-direction: column;
}

form input {
  color: white;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
}

form input:focus {
  outline: none;
  border-color: #d69f4d;
  box-shadow: 0 2px 8px rgba(214, 159, 77, 0.3);
}


form button:hover {
  background-color: #e39e13;
}

.social-login {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 10px;
}

.fb-button,
.google-button {
  border: none;
  color: white;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  border-radius: 3px;
}

.fb-button {
  background-color: #3b5998;
  width: 200px;
}

.google-button {
  background-color: #dd4b39;
  width: 200px;
}

.fb-button i,
.google-button i {
  font-size: 16px;
}
</style>
