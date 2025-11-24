<template>
  <!-- <div class="page-doi-mk">
    <main>
      <section
        class="bread-crumb full-width-banner"
        style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);"
      >
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">Thay đổi mật khẩu</div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr">&nbsp;›&nbsp;</span>
            </li>
            <li><strong><span>Thay đổi mật khẩu</span></strong></li>
          </ul>
        </div>
      </section>

      <div class="account-container">
        <div class="account-sidebar">
          <p><strong>Xin chào, <span class="highlight">tuyết anh</span>!</strong></p>
          <ul>
            <li class="nav-item">
              <RouterLink to="/thongTinCaNhan">Thông tin tài khoản</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/donHang">Đơn hàng của bạn</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/doiMatKhau">Đổi mật khẩu</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/soDiaChi">Sổ địa chỉ (0)</RouterLink>
            </li>
          </ul>
        </div> -->

        <section class="content">
          <h2>ĐỔI MẬT KHẨU</h2>
          <p>Để đảm bảo tính bảo mật bạn vui lòng đặt lại mật khẩu với ít nhất 8 kí tự</p>

          <form @submit.prevent="changePassword">
            <div class="form-group">
              <label>Mật khẩu cũ *</label>
              <input type="password" v-model="form.currentPassword" placeholder="Nhập mật khẩu cũ"  />
            </div>

            <div class="form-group">
              <label>Mật khẩu mới *</label>
              <input
                type="password"
                v-model="form.newPassword"
                placeholder="Nhập mật khẩu mới"

                minlength="8"
              />
            </div>

            <div class="form-group">
              <label>Xác nhận lại mật khẩu *</label>
              <input
                type="password"
                v-model="form.confirmPassword"
                placeholder="Nhập lại mật khẩu"
                required
              />
            </div>

            <button type="submit" class="submit-btn" :disabled="isSubmitting">
              <span v-if="isSubmitting">Đang xử lý...</span>
              <span v-else>ĐẶT LẠI MẬT KHẨU</span>
            </button>
          </form>
        </section>
      <!-- </div>
    </main>
  </div> -->
</template>

<script setup>
import { ref } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios' // ✅ axios custom interceptor (tự gắn JWT)

const form = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const isSubmitting = ref(false)

// ✅ Đổi mật khẩu
const changePassword = async () => {
  const { currentPassword, newPassword, confirmPassword } = form.value

  // ✅ Kiểm tra độ mạnh mật khẩu
  const hasLetter = /[a-zA-Z]/.test(newPassword)
  const hasNumber = /[0-9]/.test(newPassword)

  if (newPassword.length < 8 || !hasLetter || !hasNumber) {
    Swal.fire({
      icon: 'error',
      title: 'Mật khẩu mới phải có ít nhất 8 ký tự và chứa cả chữ cái và số'
    })
    return
  }

  // ✅ Kiểm tra xác nhận mật khẩu
  if (newPassword !== confirmPassword) {
    Swal.fire({ icon: 'error', title: 'Mật khẩu xác nhận không khớp' })
    return
  }

  try {
    isSubmitting.value = true

    const res = await api.post('/api/users/change-password', form.value) // ✅ Dùng api không cần withCredentials
    Swal.fire({ icon: 'success', title: res.data || 'Đổi mật khẩu thành công!' })

    // ✅ Reset form
    form.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
  } catch (err) {
    const msg = err?.response?.data || 'Đổi mật khẩu thất bại!'
    Swal.fire({ icon: 'error', title: msg })
  } finally {
    isSubmitting.value = false
  }
}
</script>


<style scoped>
.page-doi-mk {
    font-family: 'Quicksand', sans-serif;;
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
     top: 0; left: 0; right: 0; bottom: 0;
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

 .info-box, .contact-form-box {
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
 /* .box-title::after {
     content: '';
     position: absolute;
     bottom: 0;
     left: 0;
     width: 50px;
     height: 3px;
     background-color: #cd9b32;
 } */

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
        background-color: rgba(0,0,0,0.8);
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
.account-page {
  display: flex;
  font-family: Arial, sans-serif;
  margin: 40px;
}

.sidebar {
  width: 250px;
  margin-right: 40px;
}

.sidebar h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

.sidebar .highlight {
  color: #d19a00;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.sidebar li {
  padding: 8px 0;
  color: #333;
  cursor: pointer;
}

.sidebar .active {
  color: #d19a00;
  font-weight: bold;
}

.content {
  flex: 1;
  max-width: 500px;
}

.content h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.content p {
  font-size: 14px;
  color: #555;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d3a53c;
  border-radius: 4px;
}

.submit-btn {
  background-color: #d3a53c;
  color: white;
  border: none;
  padding: 12px 20px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 4px;
}
.submit-btn:hover {

  background-color: #edab0e;

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
:deep(input) {
  pointer-events: auto !important;
  opacity: 1 !important;
  background-color: white !important;
  color: black !important;
  z-index: 1 !important;
}

</style>
