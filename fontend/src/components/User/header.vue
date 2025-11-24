<template>
  <header class="header header-transparent"
    style="position: fixed; top: 0; left: 0; width: 100%; z-index: 999; background-color: rgba(0, 0, 0, 0.1);">
    <div class="container-fluid">
      <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">
            <img src="/src/images/logo.png" class="img-fluid" width="100" height="50"
              alt="Dola Bakery Logo" style="margin-left: 40px;" />
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false"
            aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/' }" to="/">Trang chủ</RouterLink>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/gioiThieu' }" to="/gioiThieu">Giới thiệu</RouterLink>
              </li>
                            <li class="nav-item">
                <div class="d-flex align-items-center">
                  <RouterLink class="nav-link" :class="{ 'active': $route.path === '/sanPham' }" to="/sanPham">
                    Sản phẩm
                  </RouterLink>
                  <div class="dropdown">
                    <button class="btn btn-link dropdown-toggle-arrow" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                      <i class="bi bi-chevron-down"></i>
                    </button>
                    <div class="dropdown-menu mega-menu" style="width: 900px; margin-left: -300px">
                      <div class="row">
                        <div class="col-xl-3">
                          <RouterLink to="/sanPham" class="dropdown-item fw-bold" style="color: #e4a53d">Tất cả sản phẩm</RouterLink>
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh kem</a>
                          <a class="dropdown-item" href="#">Bánh sinh nhật</a>
                          <a class="dropdown-item" href="#">Bánh đặt trước</a>
                          <a class="dropdown-item" href="#">Bánh sự kiện</a>
                          <a class="dropdown-item" href="#">Bánh dành cho trẻ em</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh mì</a>
                          <a class="dropdown-item" href="#">Bánh mì kẹp</a>
                          <a class="dropdown-item" href="#">Bánh cán lớp</a>
                          <a class="dropdown-item" href="#">Bánh mì gối</a>
                          <a class="dropdown-item" href="#">Bánh mì mặn</a>
                          <a class="dropdown-item" href="#">Bánh mì ngọt</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh ngọt</a>
                          <a class="dropdown-item" href="#">Bánh Chiffon</a>
                          <a class="dropdown-item" href="#">Bánh su kem</a>
                          <a class="dropdown-item" href="#">Bánh cuộn</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh tráng miệng</a>
                          <a class="dropdown-item" href="#">Bánh mousse</a>
                          <a class="dropdown-item" href="#">Panna cotta & Pudding</a>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh khô</a>
                          <a class="dropdown-item" href="#">Bánh quy</a>
                          <a class="dropdown-item" href="#">Bánh mì nướng</a>
                          <a class="dropdown-item" href="#">Bánh sừng bò mini</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh đông lạnh</a>
                          <a class="dropdown-item" href="#">Bánh bao</a>
                          <a class="dropdown-item" href="#">Bánh pizza</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Bánh theo mùa</a>
                          <a class="dropdown-item" href="#">Bánh quy tết</a>
                          <a class="dropdown-item" href="#">Bánh trung thu</a>
                          <a class="dropdown-item" href="#">Bánh quy Noel</a>
                          <a class="dropdown-item" href="#">Bánh kem Noel</a>
                        </div>
                        <div class="col-xl-3">
                          <a class="dropdown-item fw-bold" href="#" style="color: #e4a53d">Đồ uống</a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/tinTuc' }" to="/tinTuc">Tin tức</RouterLink>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/lienHe' }" to="/lienHe">Liên hệ</RouterLink>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/heThongCuaHang' }" to="/heThongCuaHang">Hệ thống cửa hàng</RouterLink>
              </li>
              <li class="nav-item">
                <RouterLink class="nav-link" :class="{ 'active': $route.path === '/cauHoiThuongGap' }" to="/cauHoiThuongGap">Câu hỏi thường gặp</RouterLink>
              </li>
            </ul>
            <ul class="navbar-nav navbar-icons mb-2 mb-lg-0">
              <li class="nav-item">
                <button
                  class="nav-link search-btn"
                  @click="openSearch"
                  title="Tìm kiếm"
                >
                  <i class="bi bi-search"></i>
                </button>
              </li>

              <li class="nav-item dropdown position-relative">
                <a
                  class="nav-link"
                  href="#"
                  @click.prevent="toggleUserDropdown"
                  @blur="closeUserDropdown"
                  tabindex="0"
                >
                  <i class="bi bi-person"></i>
                </a>

                <ul v-if="isUserDropdownOpen" class="dropdown-menu dropdown-menu-end custom-dropdown">
                  <div class="arrow-up"></div>

                  <!-- Nếu chưa đăng nhập -->
                  <li v-if="!user">
                    <RouterLink class="dropdown-item custom-hover" to="/dangNhap" @click="closeUserDropdown">Đăng nhập</RouterLink>
                  </li>
                  <li v-if="!user">
                    <hr class="dropdown-divider custom-divider">
                  </li>
                  <li v-if="!user">
                    <RouterLink class="dropdown-item custom-hover" to="/dangKy" @click="closeUserDropdown">Đăng ký</RouterLink>
                  </li>

                  <!-- Nếu đã đăng nhập -->
                  <li v-if="user">
                    <RouterLink class="dropdown-item custom-hover" to="/thongTinCaNhan" @click="closeUserDropdown">
                       Tài khoản
                    </RouterLink>
                  </li>
                  <li v-if="user">
                    <hr class="dropdown-divider custom-divider">
                  </li>
                  <li v-if="user">
                    <a class="dropdown-item text-danger custom-hover" href="#" @click.prevent="logout">Đăng xuất</a>
                  </li>
                </ul>
              </li>

              <li class="nav-item position-relative">
                <RouterLink class="nav-link" to="/trangYeuThich">
                  <i class="bi bi-heart"></i>
                  <span v-if="favoriteCount > 0" class="badge bg-danger position-absolute top-0 start-100 translate-middle rounded-pill favorite-badge">
                    {{ favoriteCount }}
                  </span>
                </RouterLink>
              </li>

              <li class="nav-item">
                <RouterLink class="nav-link" to="/gioHang">
                  <i class="bi bi-bag"></i>
                </RouterLink>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </div>
    <SearchOverlay
  :isVisible="isSearchVisible"
  @close="closeSearch"
/>
  </header>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { nextTick } from 'vue'
import SearchOverlay from './SearchOverlay.vue'

const router = useRouter()
const user = ref(null)
const favoriteCount = ref(0)
const isUserDropdownOpen = ref(false)
import api from '@/axios'

// Hàm cập nhật số lượng sản phẩm yêu thích (DB). Nếu chưa đăng nhập -> 0
const updateFavoriteCount = async () => {
  try {
    const res = await api.get('/api/favorites/me/ids')
    favoriteCount.value = Array.isArray(res.data) ? res.data.length : 0
  } catch (e) {
    favoriteCount.value = 0
  }
}

// Hàm xử lý dropdown user
const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value
}

const closeUserDropdown = () => {
  setTimeout(() => {
    isUserDropdownOpen.value = false
  }, 100)
}

// Lắng nghe sự thay đổi trong localStorage
const handleStorageChange = () => {
  updateFavoriteCount()
}

// Đóng dropdown khi click bên ngoài
const handleClickOutside = (event) => {
  const dropdown = event.target.closest('.nav-item.dropdown')
  if (!dropdown && isUserDropdownOpen.value) {
    isUserDropdownOpen.value = false
  }
}

// Khi mounted, kiểm tra username trong localStorage
onMounted(() => {
  const storedUsername = localStorage.getItem('username')
  if (storedUsername && storedUsername !== 'null') {
    user.value = {
      username: storedUsername
    }
  }

  // Khởi tạo số lượng sản phẩm yêu thích
  updateFavoriteCount()

  // Lắng nghe sự thay đổi trong localStorage
  window.addEventListener('storage', handleStorageChange)

  // Lắng nghe custom event khi có thay đổi sản phẩm yêu thích
  window.addEventListener('favoritesChanged', () => updateFavoriteCount())

  // Lắng nghe click bên ngoài để đóng dropdown
  document.addEventListener('click', handleClickOutside)
})

// Cleanup event listeners khi component unmount
onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  window.removeEventListener('favoritesChanged', () => updateFavoriteCount())
  document.removeEventListener('click', handleClickOutside)
})

// Hàm đăng xuất
const logout = async () => {
  const confirm = await Swal.fire({
    title: 'Bạn có chắc muốn đăng xuất?',
    text: 'Bạn sẽ cần đăng nhập lại để tiếp tục sử dụng!',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có, tôi muốn đăng xuất!',
    cancelButtonText: 'Hủy bỏ',
  })

  if (confirm.isConfirmed) {
    // Xóa thông tin user khỏi localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')

    // Xóa dữ liệu trong biến user
    user.value = null
    favoriteCount.value = 0

    // Thông báo và chuyển hướng
    await Swal.fire('Đã đăng xuất', 'Bạn đã đăng xuất thành công.', 'success')
    router.push('/dangNhap')
  }
}
// Search functions
const isSearchVisible = ref(false)

const openSearch = () => {
  isSearchVisible.value = true
}

const closeSearch = () => {
  isSearchVisible.value = false
}


//const showSearch = ref(false)

//const searchQuery = ref('')

// const toggleSearch = () => {
//   showSearch.value = !showSearch.value
//   if (showSearch.value) {
//     nextTick(() => {
//       document.querySelector('.search-box input')?.focus()
//     })
//   }
// }

// const searchProducts = () => {
//   if (!searchQuery.value.trim()) return
//   router.push({ path: '/sanPham', query: { search: searchQuery.value } })
//   showSearch.value = false
// }
</script>



<style>
/* Header Icons Alignment */
.navbar-icons {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.navbar-icons .nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar-icons .nav-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  padding: 0;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 22px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.navbar-icons .nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.navbar-icons .nav-link:hover::before {
  opacity: 1;
}

.navbar-icons .nav-link:hover {
  color: #cd9b32;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(205, 155, 50, 0.2);
}

/* Search Button Styles */
.search-btn {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.search-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.search-btn:hover::before {
  opacity: 1;
}

.search-btn:hover {
  color: #cd9b32;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(205, 155, 50, 0.2);
}
.search-box input:focus {
  box-shadow: none;
  outline: none;
  background: #fffbe7;
}
.search-box {
  transition: box-shadow 0.2s;
}
.page-faq {
  padding-top: 30px;
  /* Add some space below breadcrumb */
  padding-bottom: 30px;
}

.faq-title,
.page-faq .contact h4 {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 25px;
  /* Increased margin */
  position: relative;
  display: inline-block;
  padding-bottom: 8px;
  color: #333;
}

.faq-title:before,
.page-faq .contact h4:before {
  background: #cd9b32;
  content: '';
  position: absolute;
  bottom: 0;
  /* Position underline at the bottom */
  left: 0;
  height: 3px;
  width: 60px;
}


.product-fpt-with-stick-tab {
  margin-bottom: 25px;
}



.faq {
  margin-bottom: 10px;
  border-radius: 5px;
  overflow: hidden;
  /* Ensure content respects border-radius */
  border: 1px solid #E3E3E3;
}

.faq h4 {
  color: #333;
  padding: 15px 20px;
  font-size: 23px;
  font-weight: 600;
  margin-bottom: 0;
  background: #f7d690;
  cursor: pointer;
  border-radius: 0;
  border-bottom: 1px solid #E3E3E3;
  /* Separator line */
  transition: background-color 0.3s ease;
}

.faq .content {
  /* display: none; */
  border: none;
  padding: 15px 20px;
  background-color: #fff;
  border-top: none;
  border-radius: 0;
  font-size: 18px;
  line-height: 1.6;
  color: #555;
}

.faq .content p {
  margin-bottom: 10px;
}

.faq .content p:last-child {
  margin-bottom: 0;
}

.faq .content ul,
.faq .content ol {
  padding-left: 20px;
  margin-top: 10px;
  margin-bottom: 10px;
}

/* Active State */
.faq.active h4 {
  background: #cd9b32;
  color: #fff;
  border-bottom-color: #cd9b32;
}


.page-faq .contact {
  margin-bottom: 20px;
  border-radius: 5px;
  padding: 25px;
  background: #f3e7cd;
  border: none;
}

.page-faq .contact .content-form {
  padding: 0;
  /* Remove extra padding */
  display: block;
  margin-bottom: 15px;
  font-size: 18px;
  color: #555;
}

.page-faq .contact .group_contact {
  padding: 0;
  /* Remove extra padding */
  border-radius: 0;
}

.page-faq .contact .group_contact .form-control {
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 10px 15px;
  font-size: 18px;
}

.page-faq .contact .group_contact textarea.form-control {
  min-height: 100px;
  /* Adjust textarea height */
}

.page-faq .contact .group_contact .btn-lienhe {
  background: #cd9b32;
  color: #fff;
  border: none;
  padding: 10px 25px;
  line-height: normal;
  font-size: 20px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: auto;
  display: inline-block;
}

.page-faq .contact .group_contact .btn-lienhe:hover {
  background: #a88a2a;
}




.map-store iframe {
  width: 100%;
  height: 600px;
  /* Match height or adjust as needed */
  border: none;
  /* Remove iframe border */
  border-radius: 10px;
  /* Rounded corners for map */
}



/* General Page Styles */
.page-lien-he {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #fff;
  overflow-x: hidden;
  /* Prevent horizontal scroll caused by full-width element */
}

/* Header */
header.header-transparent {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.08));
  padding: 12px 0;
  transition: all 0.3s ease;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

/* Removed header hover effect */

header.header-transparent .navbar {
  padding: 10px 0;
  align-items: center;
}

header.header-transparent .navbar .container-fluid {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

header.header-transparent .navbar-collapse {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

header.header-transparent .navbar-brand {
  cursor: pointer;
  transition: all 0.3s ease;
}

header.header-transparent .navbar-brand img {
  max-height: 55px;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

header.header-transparent .navbar-brand:hover {
  transform: scale(1.02);
}

header.header-transparent .navbar-brand:hover img {
  transform: scale(1.05);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
}

header.header-transparent .navbar-nav .nav-link {
  color: #fff;
  font-weight: 500;
  padding: 12px 18px;
  text-transform: none;
  font-size: 16px;
  position: relative;
  background-color: transparent !important;
  border-bottom: none !important;
  border-radius: 8px !important;
  transition: all 0.3s ease;
  margin: 0 2px;
  cursor: pointer;
}

/* Ensure dropdown toggle has enough space for arrow */
header.header-transparent .navbar-nav .nav-link.dropdown-toggle {
  padding-right: 24px;
}

/* Active link styles */
header.header-transparent .navbar-nav .nav-link.active {
  color: #cd9b32 !important;
  font-weight: 600;
  position: relative;
}

header.header-transparent .navbar-nav .nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  width: 80%;
  height: 3px;
  background: #cd9b32;
  transform: translateX(-50%);
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(205, 155, 50, 0.3);
}

/* Hover effect for all nav links */
header.header-transparent .navbar-nav .nav-link {
  position: relative;
}

header.header-transparent .navbar-nav .nav-link::before {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  width: 0;
  height: 2px;
  background: rgba(255, 255, 255, 0.6);
  transform: translateX(-50%);
  border-radius: 1px;
  transition: width 0.3s ease;
}

header.header-transparent .navbar-nav .nav-link:hover::before {
  width: 60%;
}

/* Hide underline for non-active links */
header.header-transparent .navbar-nav .nav-link:not(.active):not(.dropdown-toggle)::after {
  display: none;
}

header.header-transparent .navbar-nav .nav-link.active {
  color: #fff;
  font-weight: 500;
}

header.header-transparent .navbar-nav .nav-link.active::after {
  display: none;
}

header.header-transparent .navbar-nav .nav-link:hover {
  color: #cd9b32;
  transform: translateY(-1px);
  background-color: rgba(255, 255, 255, 0.05) !important;
}

/* Dropdown toggle arrow button */
.dropdown-toggle-arrow {
  background: none !important;
  border: none !important;
  color: #fff !important;
  padding: 0 8px !important;
  margin-left: 4px !important;
  font-size: 14px !important;
  transition: all 0.3s ease !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.dropdown-toggle-arrow:hover {
  color: #cd9b32 !important;
  transform: scale(1.1);
}

.dropdown-toggle-arrow:focus {
  box-shadow: none !important;
}

.dropdown.show .dropdown-toggle-arrow i {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

/* Removed hover color changes */

header.header-transparent .navbar-icons .nav-link {
  color: #fff;
  font-size: 20px;
  padding: 10px 8px;
  transition: all 0.3s ease;
}

header.header-transparent .navbar-icons .nav-link:hover {
  color: #cd9b32;
  transform: scale(1.1);
}

.navbar-toggler {
  border-color: rgba(255, 255, 255, 0.5);
  padding: 4px 8px;
}

.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%28255, 255, 255, 0.8%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}

/* Mega menu responsive */
.mega-menu {
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  border: none;
  padding: 20px;
  transition: all 0.3s ease;
}

.mega-menu .dropdown-item {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  padding: 8px 15px;
  border-radius: 4px;
}

.mega-menu .dropdown-item:hover {
  background-color: rgba(205, 155, 50, 0.1);
  color: #cd9b32;
  transform: translateX(3px);
}

.mega-menu .dropdown-item.fw-bold {
  color: #cd9b32;
  font-weight: 600;
  margin-bottom: 5px;
}

.mega-menu .dropdown-item.fw-bold:hover {
  background-color: rgba(205, 155, 50, 0.15);
  transform: translateX(3px);
}

/* Responsive improvements */
@media (max-width: 991px) {
  header.header-transparent .navbar-nav {
    background-color: rgba(0, 0, 0, 0.9);
    padding: 15px;
    border-radius: 8px;
    margin-top: 10px;
    backdrop-filter: blur(10px);
  }

  header.header-transparent .navbar-nav .nav-link {
    font-size: 16px;
    padding: 12px 15px;
    border-bottom: 1px solid rgba(255,255,255,0.1);
  }

  header.header-transparent .navbar-nav .nav-link:last-child {
    border-bottom: none;
  }

  header.header-transparent .navbar-nav .nav-link.active::after {
    display: none;
  }

  .mega-menu {
    width: 100% !important;
    margin-left: 0 !important;
    position: static !important;
  }

  .navbar-icons {
    margin-top: 10px;
    justify-content: center;
    gap: 15px;
  }

  .navbar-icons .nav-item {
    margin: 0;
  }

  .navbar-icons .nav-link {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  header.header-transparent .navbar-brand img {
    max-height: 50px;
    margin-left: 20px !important;
  }

  header.header-transparent .navbar-nav .nav-link {
    font-size: 15px;
    padding: 10px 15px;
  }

  header.header-transparent .navbar-icons .nav-link {
    font-size: 18px;
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 576px) {
  header.header-transparent .navbar-brand img {
    max-height: 45px;
    margin-left: 10px !important;
  }

  .container-fluid {
    padding: 0 10px;
  }
}

/* Banner - Full Width CSS Fix */
.full-width-banner {
  /* --- CSS Technique to break out of container --- */
  width: 100vw;
  /* Force width to viewport width */
  position: relative;
  /* Needed for left positioning */
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
  margin-top: 0;
  /* Ensure no top margin */
  margin-bottom: 0;
  /* Ensure no bottom margin */
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
  font-family: 'Playball', cursive;
}

.full-width-banner .breadcrumb {
  font-size: 20px;
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
  font-size: 20px;
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
  color: #333;
  font-size: 25px;
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
.custom-hover:hover {
  background-color:#fff!important;
  color:#fd7e14 !important;
}
.custom-divider {
  border-top: 1px dashed  #cd9b32 !important;
  opacity: 1; /* để thấy rõ màu */
  margin: 0.3rem 0; /* chỉnh khoảng cách trên dưới nếu cần */
}
.arrow-up {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 15px solid transparent;
  border-right: 15px solid transparent;
  border-bottom: 10px solid #cd9b32;
}

.custom-dropdown {
  margin-top: 10px;
  border: none;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  background: #fff;
  min-width: 200px;
  animation: dropdownFadeIn 0.3s ease;
  position: absolute;
  top: 100%;
  right: 0;
  z-index: 1000;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.custom-dropdown .dropdown-item {
  padding: 10px 20px;
  color: #333;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.custom-dropdown .dropdown-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(205, 155, 50, 0.1), transparent);
  transition: left 0.5s ease;
}

.custom-dropdown .dropdown-item:hover::before {
  left: 100%;
}

/* Ensure dropdown container has proper positioning */
.nav-item.dropdown.position-relative {
  position: relative !important;
}

/* Improve dropdown visibility */
.custom-dropdown {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}

/* Favorite Badge Styles */
.favorite-badge {
  font-size: 0.65rem !important;
  min-width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px 6px;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transform: translate(-50%, -50%) !important;
  top: 8px !important;
  left: calc(100% - 8px) !important;
  z-index: 10;
}

.favorite-badge:hover {
  transform: translate(-50%, -50%) scale(1.1) !important;
  transition: transform 0.2s ease;
}

/* Ensure the heart icon container has proper positioning */
.nav-item.position-relative .nav-link {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Responsive adjustments for favorite badge */
@media (max-width: 768px) {
  .favorite-badge {
    font-size: 0.6rem !important;
    min-width: 16px;
    height: 16px;
    padding: 1px 5px;
    top: 6px !important;
    left: calc(100% - 6px) !important;
  }
}

@media (max-width: 576px) {
  .favorite-badge {
    font-size: 0.55rem !important;
    min-width: 14px;
    height: 14px;
    padding: 1px 4px;
    top: 5px !important;
    left: calc(100% - 5px) !important;
  }
  
  .custom-dropdown {
    min-width: 180px;
    right: -10px;
  }
  
  .custom-dropdown .dropdown-item {
    padding: 8px 15px;
    font-size: 14px;
  }
}
</style>
