<template>
  <div class="page-lien-he">
    <main>
      <section class="bread-crumb full-width-banner" style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">
            Liên hệ
          </div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr">&nbsp;<svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" class="svg-inline--fa fa-chevron-right fa-w-10"><path fill="currentColor" d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z" class=""></path></svg>&nbsp;</span>
            </li>
            <li><strong><span>Liên hệ</span></strong></li>
          </ul>
        </div>
      </section>

      <section class="contact-section">
        <div class="container">
          <div class="row">
            <div class="col-lg-6 col-md-12">
              <div class="left-column">
                <div class="info-box">
                  <h2 class="box-title">Cửa hàng Dola Bakery</h2>
                  <div class="info-content">
                    <div class="info-item">
                      <div class="icon-wrapper">
                        <i class="bi bi-geo-alt-fill"></i>
                      </div>
                      <div class="text-wrapper">
                        <p class="label">Địa chỉ:</p>
                        <p>70 Lữ Gia, Phường 15, Quận 11, TP.HCM</p>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="icon-wrapper">
                        <i class="bi bi-clock-fill"></i>
                      </div>
                      <div class="text-wrapper">
                        <p class="label">Thời gian làm việc:</p>
                        <p>8h - 22h,<br> Từ thứ 2 đến chủ nhật</p>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="icon-wrapper">
                        <i class="bi bi-telephone-fill"></i>
                      </div>
                      <div class="text-wrapper">
                        <p class="label">Hotline:</p>
                        <p>1900 6750</p>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="icon-wrapper">
                        <i class="bi bi-envelope-fill"></i>
                      </div>
                      <div class="text-wrapper">
                        <p class="label">Email:</p>
                        <p>support@sapo.vn</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="contact-form-box">
                  <h2 class="box-title">Liên hệ với chúng tôi</h2>
                  <p class="form-description">Nếu bạn có thắc mắc gì, có thể gửi yêu cầu cho chúng tôi, và chúng tôi sẽ liên lạc lại với bạn sớm nhất có thể.</p>

                  <form @submit.prevent="submitForm">
                    <div class="mb-3">
                      <input
                        type="text"
                        class="form-control"
                        placeholder="Họ và tên*"
                        v-model="formData.fullName"
                        @blur="validateName"
                      >
                    </div>
                    <div class="mb-3">
                      <input
                        type="email"
                        class="form-control"
                        placeholder="Email*"
                        v-model="formData.email"
                        @blur="validateEmail"
                      >
                    </div>
                    <div class="mb-3">
                      <input
                        type="tel"
                        class="form-control"
                        placeholder="Điện thoại*"
                        v-model="formData.phone"
                        @blur="validatePhone"
                      >
                    </div>
                    <div class="mb-3">
                      <textarea
                        class="form-control"
                        rows="5"
                        placeholder="Nội dung*"
                        v-model="formData.content"
                        @blur="validateContent"
                      ></textarea>
                    </div>
                    <button
                      type="submit"
                      class="btn btn-submit"
                      :disabled="isLoading"
                    >
                      <span v-if="!isLoading">Gửi thông tin</span>
                      <span v-else>Đang gửi...</span>
                    </button>
                  </form>
                </div>
              </div>
            </div>
            <div class="col-lg-6 col-md-12">
              <div class="map-wrapper">
                <iframe
                  src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3724.840440405246!2d105.82044911542594!3d21.002876093939135!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab0bfa2cd51b%3A0x6868b59b4ff118c4!2zUGjhuqFtIG3hu5l0IFLDtCBD4bqnbiwgU2FwbyBQT1M!5e0!3m2!1sen!2s!4v1674139412966!5m2!1sen!2s"
                  width="100%" height="100%" style="border:0;" allowfullscreen="" loading="lazy">
                </iframe>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';

const formData = ref({
  fullName: '',
  email: '',
  phone: '',
  content: ''
});

const isLoading = ref(false);

const validateName = () => {
  if (!formData.value.fullName.trim()) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Vui lòng nhập họ và tên',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  const nameRegex = /^[a-zA-Z\sÀ-ỹ]+$/;
  if (!nameRegex.test(formData.value.fullName)) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Họ tên không được chứa số hoặc ký tự đặc biệt',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  return true;
};

const validateEmail = () => {
  if (!formData.value.email.trim()) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Vui lòng nhập email',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  if (!formData.value.email.includes('@') ||
      formData.value.email.endsWith('@') ||
      formData.value.email.split('@')[1].length === 0) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi Email',
      text: 'Email phải có phần sau ký tự @. Ví dụ: example@domain.com',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(formData.value.email)) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi Email',
      text: 'Email không hợp lệ. Ví dụ hợp lệ: example@domain.com',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  return true;
};

const validatePhone = () => {
  if (!formData.value.phone.trim()) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Vui lòng nhập số điện thoại',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  const phoneRegex = /^(0|\+84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$/;
  if (!phoneRegex.test(formData.value.phone)) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-12 số',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  return true;
};

const validateContent = () => {
  if (!formData.value.content.trim()) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Vui lòng nhập nội dung',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  const contentRegex = /^[a-zA-Z0-9\sÀ-ỹ.,!?\-]+$/;
  if (!contentRegex.test(formData.value.content)) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Nội dung không được chứa ký tự đặc biệt',
      confirmButtonColor: '#cd9b32'
    });
    return false;
  }

  return true;
};

const submitForm = async () => {
  if (!validateName() || !validateEmail() || !validatePhone() || !validateContent()) {
    return;
  }

  isLoading.value = true;

  try {
    const response = await axios.post('http://localhost:8080/api/contact-inquiries', {
      fullName: formData.value.fullName,
      email: formData.value.email,
      phone: formData.value.phone,
      content: formData.value.content
    });

    await Swal.fire({
      icon: 'success',
      title: 'Thành công!',
      text: 'Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể.',
      confirmButtonColor: '#cd9b32',
      timer: 3000
    });

    // Reset form
    formData.value = {
      fullName: '',
      email: '',
      phone: '',
      content: ''
    };

  } catch (error) {
    console.error('Error:', error);
    await Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: 'Có lỗi xảy ra khi gửi thông tin. Vui lòng thử lại sau.',
      confirmButtonColor: '#cd9b32'
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.page-lien-he {
  font-family: Roboto;
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

.contact-section {
  padding: 40px 0;
}

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

.btn-submit:disabled {
  background-color: #a88a2a;
  opacity: 0.7;
  cursor: not-allowed;
}

.swal2-popup {
  font-family: 'Roboto', sans-serif;
}

.swal2-title {
  font-size: 24px;
}

.swal2-confirm {
  background-color: #cd9b32 !important;
}

.swal2-confirm:focus {
  box-shadow: 0 0 0 3px rgba(205, 155, 50, 0.5) !important;
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
.full-width-banner .title-bread-crumb[data-v-71689a56] {
    font-size: 55px;
    color: #cd9b32;
    font-weight: normal;
    margin-bottom: 5px;
      font-family: 'Playball';
}
</style>
