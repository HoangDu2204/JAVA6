<template>


	<div class="page-lien-he">

   <main>
	 <section class="bread-crumb full-width-banner" style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
       <div class="banner-overlay"></div>
	   <div class="container banner-content">
		 <div class="title-bread-crumb">
		   Hệ thống cửa hàng
		 </div>
		 <ul class="breadcrumb">
		   <li class="home">
			 <a href="/"><span>Trang chủ</span></a>
			 <span class="mr_lr">&nbsp;<svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" class="svg-inline--fa fa-chevron-right fa-w-10"><path fill="currentColor" d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z" class=""></path></svg>&nbsp;</span>
		   </li>
		   <li><strong><span>Hệ thống cửa hàng</span></strong></li>
		 </ul>
	   </div>
	 </section>


	<section class="section_store_list">
     <div class="container">
         <div class="hethong-info-wrapper">
             <div class="hethong-info">
                <div class="row">
                    <div class="col-lg-4 col-md-4 col-sm-12">
                        <div class="item">
                            <div class="thump">
                                <img src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/icon_hethong1.png?1735117293436" alt="Hệ thống 8 cửa hàng Trên toàn quốc" title="Hệ thống 8 cửa hàng Trên toàn quốc">
                            </div>
                            <span class="info-text">Hệ thống cửa hàng<br>{{ storeInfo.storeName || 'Dola Bakery' }}</span>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-12">
                        <div class="item">
                            <div class="thump">
                                <img src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/icon_hethong2.png?1735117293436" alt="Hơn 100 nhân viên Để phục vụ quý khách" title="Hơn 100 nhân viên Để phục vụ quý khách">
                            </div>
                            <span class="info-text">Hơn 100 nhân viên<br>Để phục vụ quý khách</span>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-12">
                        <div class="item">
                            <div class="thump">
                                <img src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/icon_hethong3.png?1735117293436" alt="Mở cửa 8-22h cả CN & Lễ tết" title="Mở cửa 8-22h cả CN & Lễ tết">
                            </div>
                            <span class="info-text">Mở cửa {{ storeInfo.openTime || '8:00' }}-{{ storeInfo.closeTime || '22:00' }}<br>cả CN &amp; Lễ tết</span>
                        </div>
                    </div>
                </div>
            </div>
         </div>

        <!-- Thông tin liên hệ bổ sung -->
        <div class="row mb-4" v-if="storeInfo.email || storeInfo.facebookUrl || storeInfo.zaloUrl || storeInfo.instagramUrl">
          <div class="col-12">
            <div class="contact-info-wrapper">
              <h4 class="contact-info-title">Thông tin liên hệ</h4>
              <div class="contact-info-content">
                <div class="row">
                  <div class="col-md-3 col-sm-6" v-if="storeInfo.email">
                    <div class="contact-item">
                      <i class="bi bi-envelope-fill"></i>
                      <span>{{ storeInfo.email }}</span>
                    </div>
                  </div>
                  <div class="col-md-3 col-sm-6" v-if="storeInfo.facebookUrl">
                    <div class="contact-item">
                      <i class="bi bi-facebook"></i>
                      <a :href="storeInfo.facebookUrl" target="_blank">Facebook</a>
                    </div>
                  </div>
                  <div class="col-md-3 col-sm-6" v-if="storeInfo.zaloUrl">
                    <div class="contact-item">
                      <i class="bi bi-chat-dots-fill"></i>
                      <a :href="storeInfo.zaloUrl" target="_blank">Zalo</a>
                    </div>
                  </div>
                  <div class="col-md-3 col-sm-6" v-if="storeInfo.instagramUrl">
                    <div class="contact-item">
                      <i class="bi bi-instagram"></i>
                      <a :href="storeInfo.instagramUrl" target="_blank">Instagram</a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row">
            <div class="col-md-5 col-lg-4 col-sm-12 col-12 col-left">
                <div class="option-chos">
                    <div class="group-option">
                        <div class="group-city group">
                            <select id="city" class="select form-select">
                                <option value="ALL" >Chọn tỉnh thành</option>
                                <option value="Hà Nội">Hà Nội</option>
                                <option value="Hồ Chí Minh">Hồ Chí Minh</option>
                                <option value="Đà Nẵng">Đà Nẵng</option>
                                <option value="Bình Dương">Bình Dương</option>
                                <option value="Cần Thơ">Cần Thơ</option>
                            </select>
                        </div>
                        <div class="group-district group">
                            <select id="district" class="select form-select" >
                                <option value="ALL">Chọn quận/huyện</option>
                                <!-- Options will be populated by JS -->
                            </select>
                        </div>
                    </div>
                    <div class="info-store info-block">

                        <div data-local="Hồ Chí Minh" data-district="Quận 11" class="store-list">
                            <span class="name-cuahang">{{ storeInfo.storeName || 'Dola Sài Gòn' }}</span>
                            <span class="store-address"><b>Địa chỉ:</b> {{ storeInfo.address || 'Tầng 3, 70 Lữ Gia, Phường 15, Quận 11, Thành phố Hồ Chí Minh' }}</span>
                            <span class="store-phone"><b>Hotline:</b> <a class="phone-url" href="tel:{{ storeInfo.phone || '19006750' }}">{{ storeInfo.phone || '1900 6750' }}</a></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-7 col-lg-8 col-sm-12 col-12 col-right">
                <div class="map-store">
                    <!-- Default Map URL or dynamically updated by JS -->
                    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.518261716127!2d106.65095971411642!3d10.771561562230547!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f4964c382dd%3A0xb61978d7651a271f!2zUGjhuqduIG3hu4FtIHF14bqjbiBsw70gYsOhbiBow6BuZyBTYXBvIC0gSOG7kyBDaMOtIE1pbmg!5e0!3m2!1svi!2s!4v1649778540165!5m2!1svi!2s" width="100%" height="600" style="border:0; border-radius: 10px;" allowfullscreen="" loading="lazy"></iframe>
                </div>
            </div>
        </div>
     </div>
 </section>


 </main>




 </div>

 </template>

 <script setup>
import { ref, onMounted } from 'vue'
import axios from '@/axios' // ✅ Đã đổi sang axios có JWT interceptor

const storeInfo = ref({
  storeName: 'Dola Bakery',
  phone: '1900 6750',
  openTime: '8:00',
  closeTime: '22:00',
  description: 'Hệ thống bánh ngọt chất lượng cao',
  email: 'info@dolabakery.com',
  address: 'Tầng 3, 70 Lữ Gia, Phường 15, Quận 11, Thành phố Hồ Chí Minh',
  facebookUrl: '',
  zaloUrl: '',
  instagramUrl: ''
})

const fetchStoreInfo = async () => {
  try {
    const res = await axios.get('/api/store-info')
    if (res.data) {
      storeInfo.value = res.data
    }
  } catch (err) {
    console.error('Không thể tải thông tin cửa hàng:', err)
  }
}

onMounted(() => {
  fetchStoreInfo()
})
</script>


 <style scoped>
.page-he-thong {
    font-family: roboto;
    background-color: #fff;
 }

 .section_store_list {
     padding: 40px 0;
 }

 /* Top Info Boxes */
 .hethong-info-wrapper {
    padding: 3px;
    border-radius: 10px;
    margin-bottom: 30px;
    border: 2px solid #cd9b32; /* Golden border */
    background: #fff;
 }

 .hethong-info .item {
    display: flex;
    align-items: center;
    justify-content: center; /* Center items on smaller screens if they stack */
    padding: 10px 0;
 }

 .hethong-info .item .thump {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: #cd9b32; /* Golden background */
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    flex-shrink: 0;
 }

 .hethong-info .item .thump img {
    max-height: 30px;
    max-width: 30px;
 }

 .hethong-info .item .info-text {
    font-size: 18px;
    color: #333;
    line-height: 1.4;
 }

 /* Contact Info Section */
 .contact-info-wrapper {
    background: #fdf8ec;
    border-radius: 10px;
    padding: 20px;
    border: 2px solid #cd9b32;
 }

 .contact-info-title {
    color: #333;
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 20px;
    text-align: center;
    position: relative;
 }

 .contact-info-title::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background-color: #cd9b32;
 }

 .contact-info-content {
    margin-top: 20px;
 }

 .contact-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #cd9b32;
    margin-bottom: 10px;
    transition: all 0.3s ease;
 }

 .contact-item:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
 }

 .contact-item i {
    font-size: 20px;
    color: #cd9b32;
    width: 24px;
    text-align: center;
 }

 .contact-item span,
 .contact-item a {
    font-size: 16px;
    color: #333;
    text-decoration: none;
    font-weight: 500;
 }

 .contact-item a:hover {
    color: #cd9b32;
    text-decoration: underline;
 }



 .option-chos {
    border-radius: 10px;
    height: 100%;
    padding: 20px;
    background: #fdf8ec; /* Lighter beige background */
    margin-bottom: 20px; /* Add margin for smaller screens */
 }

 .option-chos .group-option {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 20px; /* Space below dropdowns */
    gap: 10px; /* Gap between dropdowns */
 }

 .option-chos .group-option .group {
    flex: 1; /* Make dropdowns share space */
    min-width: 150px; /* Minimum width before wrapping */
 }

 .option-chos .select.form-select {
    width: 100%;
    border-radius: 5px;
    padding: 10px 15px;
    font-size: 18px;
    line-height: 1.5;
    appearance: none; /* Remove default arrow */
    background-repeat: no-repeat;
    background-position: right 0.75rem center;
    background-size: 16px 12px;
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='%23343a40' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M2 5l6 6 6-6'/%3e%3c/svg%3e"); /* Custom arrow */
 }

 #city.form-select {
    background-color: #cd9b32; /* Golden background */
    color: #fff; /* White text */
    border: 1px solid #cd9b32;
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='%23ffffff' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M2 5l6 6 6-6'/%3e%3c/svg%3e"); /* White arrow */
 }
 #city.form-select option {
     background-color: #fff;
     color: #333;
 }

 #district.form-select {
    background-color: #fff; /* White background */
    color: #555; /* Grey text */
    border: 1px solid #ced4da;
 }

 .info-store {
    max-height: 500px; /* Limit height and enable scroll */
    overflow-y: auto;
    padding-right: 5px; /* Space for scrollbar */
 }

 /* Custom Scrollbar for Webkit browsers */
 .info-store::-webkit-scrollbar {
    width: 6px;
 }
 .info-store::-webkit-scrollbar-track {
    background: #eee;
    border-radius: 3px;
 }
 .info-store::-webkit-scrollbar-thumb {
    background: #cd9b32;
    border-radius: 3px;
 }
 .info-store::-webkit-scrollbar-thumb:hover {
    background: #a88a2a;
 }

 .store-list {
    background: #fff; /* White background for each store */
    border: 1px solid #cd9b32; /* Golden border */
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
    cursor: pointer;
    transition: box-shadow 0.3s ease;
 }
 .store-list:hover, .store-list.active {
     box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
     border-color: #ebb94c; /* Darker border on hover/active */
     background-color: #ebb94c;
 }

 .store-list span {
     display: block;
     margin-bottom: 5px;
     font-size: 18px;
     color: #555;
     line-height: 1.5;
 }
 .store-list span:last-child {
     margin-bottom: 0;
 }

 .store-list .name-cuahang {
    font-weight: 700; /* Bold store name */
    color: #333;
    font-size: 20px;
 }

 .store-list .store-address b,
 .store-list .store-phone b {
    font-weight: 600; /* Semi-bold labels */
    color: #333;
 }

 .store-list .store-phone .phone-url {
    color: #cd9b32; /* Golden phone number */
    font-weight: 600;
    text-decoration: none;
 }
 .store-list .store-phone .phone-url:hover {
     text-decoration: underline;
 }



 .map-store iframe {
    width: 100%;
    height: 600px; /* Match height or adjust as needed */
    border: none; /* Remove iframe border */
    border-radius: 10px; /* Rounded corners for map */
 }



 /* General Page Styles */
 .page-lien-he {
    font-family: roboto;
    background-color: #fff;
    overflow-x: hidden; /* Prevent horizontal scroll caused by full-width element */
 }

 /* Header */
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

 /* Banner - Full Width CSS Fix */
 .full-width-banner {
    /* --- CSS Technique to break out of container --- */
    width: 100vw; /* Force width to viewport width */
    position: relative; /* Needed for left positioning */
    left: 50%;
    transform: translateX(-50%);
    /* --- End Technique --- */

    min-height: 300px;
    background-size: cover;
    background-position: center center;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 80px 0 50px;
    text-align: center;
    margin-top: 0; /* Ensure no top margin */
    margin-bottom: 0; /* Ensure no bottom margin */
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
    font-size: 18px;
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
     color: #333;
     font-size: 20px;
     font-weight: 700;
     margin-bottom: 20px;
     position: relative;
     padding-bottom: 10px;
 }
 .box-title::after {
     content: '';
     position: absolute;
     bottom: 0;
     left: 0;
     width: 50px;
     height: 3px;
     background-color: #cd9b32;
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
    .contact-info-content .row {
        margin: 0;
    }
    .contact-item {
        margin-bottom: 8px;
    }
 }

 @media (max-width: 576px) {
    .full-width-banner .title-bread-crumb {
        font-size: 40px;
    }
    .hethong-info .item .info-text {
        font-size: 16px;
    }
    .contact-info-title {
        font-size: 20px;
    }
    .contact-item {
        padding: 8px;
        font-size: 14px;
    }
    .contact-item i {
        font-size: 18px;
    }
 }
.full-width-banner .title-bread-crumb[data-v-d99b55fc] {
    font-size: 55px;
    color: #cd9b32;
    font-weight: normal;
    margin-bottom: 5px;
    font-family: 'Playball';
}

 </style>

