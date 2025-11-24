<template>
  <div class="page-thong-tin">
    <main>
      <section class="bread-crumb full-width-banner"
        style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">Trang khách hàng</div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr">&nbsp;
                <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img"
                  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"
                  class="svg-inline--fa fa-chevron-right fa-w-10">
                  <path fill="currentColor"
                    d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z">
                  </path>
                </svg>&nbsp;
              </span>
            </li>
            <li><strong><span>Trang khách hàng</span></strong></li>
          </ul>
        </div>
      </section>

      <div class="account-container">
        <div class="account-sidebar">
          <p>
            <strong>Xin chào,
              <span class="highlight">
                {{ user?.fullName || '...' }}
              </span>!
            </strong>
          </p>
          <ul>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/thongTinCaNhan">Thông tin tài khoản</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/donHang">Đơn hàng của bạn</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/doiMatKhau">Đổi mật khẩu</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/soDiaChi">
                Sổ địa chỉ ({{ addresses.length }})
              </RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" :to="isAgent ? '/updateAgent' : '/dangKyDaiLy'">
                {{ isAgent ? 'Thông tin đại lý' : 'Đăng ký đại lý' }}
              </RouterLink>
            </li>

          </ul>
        </div>

        <slot></slot>
      </div>
    </main>
  </div>
</template>


<script setup>

import { ref, onMounted } from 'vue'
import api from '@/axios'
import Swal from 'sweetalert2'
import { useRouter } from 'vue-router'

const user = ref(null)
const addresses = ref([])
const isAgent = ref(false)

const router = useRouter()

onMounted(async () => {
  try {
    // Lấy thông tin người dùng
    const res = await api.get('/api/users/profile')
    user.value = res.data

    // Lấy địa chỉ
    const addrRes = await api.get('/api/addresses')
    addresses.value = addrRes.data

    // Kiểm tra xem có phải đại lý không
    const agentRes = await api.get('/api/agents/update/check-agent')
    isAgent.value = agentRes.data === true
  } catch (error) {
    console.error('Lỗi khi lấy thông tin người dùng hoặc địa chỉ:', error)
    Swal.fire({
      icon: 'error',
      title: 'Bạn chưa đăng nhập',
      text: 'Vui lòng đăng nhập để xem thông tin tài khoản',
    }).then(() => {
      router.push('/DangNhap')
    })
  }
})
</script>




<style scoped>
.page-thong-tin {
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

.contact-section {
  padding: 40px 0;

}

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

.account-container {
  display: flex;
  gap: 40px;
  padding: 40px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
  box-sizing: border-box;
}


.account-sidebar {
  width: 260px;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.account-sidebar p {
  margin-bottom: 25px;
  font-size: 17px;
}

.highlight {
  color: #e74c3c;
  font-weight: bold;
}

.account-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.account-sidebar li {
  margin: 12px 0;
  padding: 10px 15px;
  color: #333;
  border-radius: 8px;
  transition: background 0.3s, color 0.3s;
  cursor: pointer;
}

.account-sidebar li:hover {
  background-color: #f0f0f0;
}

.account-sidebar li.active {
  background-color: #ffd8a8;
  color: #b3541e;
  font-weight: bold;
}

.account-info {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.account-info h2 {
  font-size: 22px;
  margin-bottom: 25px;
  color: #2c3e50;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.account-info p {
  margin: 12px 0;
  font-size: 16px;
  color: #444;
}
.full-width-banner .title-bread-crumb[data-v-e3f53b4b] {
    font-size: 55px;
    color: #cd9b32;
    font-weight: normal;
    margin-bottom: 5px;
    font-family: 'Playball';
}
</style>
