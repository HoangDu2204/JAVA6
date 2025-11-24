<template>
  <div class="admin-layout" id="admin-layout">
    <!-- Sidebar -->
    <aside
      class="bg-dark text-white p-3 transition-all overflow-auto"
      :class="{ 'd-none': !sidebarVisible }"
      style="width: 300px;"
    >
      <div class="mb-4 fs-4 fw-bold d-flex justify-content-center align-items-center">
        <img src="/src/images/logo.png" alt="Logo" style="max-height: 150px;">
      </div>

      <!-- Navigation -->
      <div class="small text-uppercase text-secondary">Điều hướng</div>
      <ul class="nav flex-column mb-3">
        <li class="nav-item">
          <RouterLink to="/dasboard" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-speedometer2 me-2"></i> Bảng điều khiển
          </RouterLink>
        </li>
      </ul>

      <!-- UI Components -->
      <div class="small text-uppercase text-secondary">Thành phần giao diện</div>
      <ul class="nav flex-column mb-3">
        <li class="nav-item">
          <RouterLink to="/quanLyThongTinCuaHang" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-shop me-2"></i> Quản lý thông tin cửa hàng
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanlyLienHe" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-shop me-2"></i> Quản lý liên hệ
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyTinTuc" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-newspaper me-2"></i> Quản lý tin tức
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyNguoiDung" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-people me-2"></i> Quản lý người dùng
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyDonHang" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-bag-check me-2"></i> Quản lý đơn hàng
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyGiamGia" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-tags me-2"></i> Quản lý giảm giá
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyVouchers" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-ticket-perforated me-2"></i> Quản lý vouchers
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyDanhGia" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-star me-2"></i> Quản lý đánh giá
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyBinhLuanTT" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-list-ul me-2"></i> Bình luận tin tức
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyDaiLy" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-building me-2"></i> Quản lý đại lý
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLyDanhMuc" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-list-columns me-2"></i> Quản lý danh mục
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink to="/quanLySanPham" class="nav-link text-white" active-class="active-link">
            <i class="bi bi-box-seam me-2"></i> Quản lý sản phẩm
          </RouterLink>
        </li>

        <!-- Quản lý biến thể -->
        <li class="nav-item">
          <a
            href="#"
            class="nav-link text-white d-flex justify-content-between align-items-center"
            @click.prevent="toggleVariantMenu"
          >
            <span><i class="bi bi-layers me-2"></i> Quản lý biến thể</span>
            <i :class="variantMenuOpen ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
          </a>
          <ul v-if="variantMenuOpen" class="nav flex-column ms-4">
            <li class="nav-item">
              <RouterLink to="/quanLyHinhDang" class="nav-link text-white" active-class="active-link">Quản lý hình dạng</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/quanLyHuongVi" class="nav-link text-white" active-class="active-link">Quản lý hương vị</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/quanLyNguonGoc" class="nav-link text-white" active-class="active-link">Quản lý nguồn gốc</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/quanLySize" class="nav-link text-white" active-class="active-link">Quản lý kích thước</RouterLink>
            </li>
          </ul>
        </li>

        <!-- Quản lý thống kê -->
        <li class="nav-item">
          <a href="#" class="nav-link text-white d-flex justify-content-between align-items-center"
             @click.prevent="toggleStatsMenu">
            <span><i class="bi bi-bar-chart-line me-2"></i> Quản lý thống kê</span>
            <i :class="statsMenuOpen ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
          </a>
          <ul v-if="statsMenuOpen" class="nav flex-column ms-4">
            <li class="nav-item">
              <RouterLink to="/tkDoanhThuNam" class="nav-link text-white" active-class="active-link">Doanh thu theo năm</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/tkDoanhThuThang" class="nav-link text-white" active-class="active-link">Doanh thu theo tháng</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/tkDoanhThuNgay" class="nav-link text-white" active-class="active-link">Doanh thu theo ngày</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/tkDanhGia" class="nav-link text-white" active-class="active-link">Thống kê sản phẩm</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/tkKhachHang" class="nav-link text-white" active-class="active-link">Thống kê khách hàng vip</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/tkSanPhamYeuThich" class="nav-link text-white" active-class="active-link">Thống kê lượt yêu thích</RouterLink>
            </li>
          </ul>
        </li>
      </ul>
    </aside>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Header -->
      <header class="admin-header">
        <div class="header-left">
          <i class="bi bi-list fs-4 pointer" @click="toggleSidebar"></i>
          <i class="bi bi-search fs-5 pointer"></i>
        </div>
        <div class="header-right">
          <i class="bi bi-gear fs-5 pointer"></i>
          <i class="bi bi-bell fs-5 pointer"></i>
          <i class="bi bi-person-circle fs-5 pointer"></i>
          <i class="bi bi-box-arrow-right fs-5 pointer" @click="logout" title="Đăng xuất"></i>
        </div>
      </header>

      <!-- Main Section -->
      <main class="main-section">
        <slot></slot>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

const router = useRouter()

const user = ref(null)
const sidebarVisible = ref(true)
const statsMenuOpen = ref(false)
const variantMenuOpen = ref(false)

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value
}
const toggleStatsMenu = () => {
  statsMenuOpen.value = !statsMenuOpen.value
}
const toggleVariantMenu = () => {
  variantMenuOpen.value = !variantMenuOpen.value
}

const logout = async () => {
  const result = await Swal.fire({
    title: 'Bạn có chắc muốn đăng xuất?',
    text: 'Bạn sẽ cần đăng nhập lại để tiếp tục sử dụng!',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có, tôi muốn đăng xuất!',
    cancelButtonText: 'Hủy bỏ',
  })

  if (result.isConfirmed) {
    try {
      await api.post('/api/auth/logout')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      user.value = null
      router.push('/dangNhap')
      Swal.fire('Đăng xuất thành công!', '', 'success')
    } catch (error) {
      console.error('Lỗi khi đăng xuất:', error)
      Swal.fire('Lỗi!', 'Không thể đăng xuất. Vui lòng thử lại.', 'error')
    }
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/dangNhap')
  }
})
</script>

<style scoped>
/* Reset CSS để tránh khoảng trắng */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* Layout chính */
.admin-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f8f9fa;
}

/* Đảm bảo layout hoạt động trên tất cả trình duyệt */
@supports not (display: flex) {
  .admin-layout {
    display: block;
  }
  
  aside {
    float: left;
    width: 300px;
  }
  
  .main-content {
    margin-left: 300px;
  }
}

/* Sidebar */
aside {
  background-color: #212529;
  color: white;
  padding: 1rem;
  transition: all 0.3s ease;
  overflow-y: auto;
  width: 300px;
  flex-shrink: 0;
  height: 100vh;
}

aside.d-none {
  display: none !important;
}

/* Main content */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0; /* Quan trọng để tránh overflow */
}

/* Header */
.admin-header {
  background-color: white;
  border-bottom: 1px solid #dee2e6;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  height: 70px;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

/* Main section */
.main-section {
  padding: 1.5rem;
  background-color: #f8f9fa;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  height: calc(100vh - 70px); /* Trừ đi chiều cao header */
}

/* Đảm bảo nội dung trong main section không bị tràn */
.main-section > * {
  max-width: 100%;
  box-sizing: border-box;
}

/* Navigation styles */
.nav {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 0.25rem;
}

.nav-link {
  display: block;
  padding: 0.75rem 1rem;
  color: white;
  text-decoration: none;
  border-radius: 0.375rem;
  transition: all 0.3s ease;
  font-size: 1rem;
  font-weight: 500;
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active-link {
  background-color: #0d6efd;
  color: white !important;
}

/* Submenu styles */
.nav .nav {
  margin-left: 1rem;
  margin-top: 0.5rem;
}

.nav .nav .nav-link {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

/* Text styles */
.small.text-uppercase {
  font-size: 0.85rem;
  letter-spacing: 1px;
  font-weight: 600;
  color: #6c757d;
  margin: 1.5rem 0 0.75rem 0;
  padding: 0 1rem;
}

.fw-bold, .fs-4 {
  font-weight: 700;
}

.fs-4 {
  font-size: 1.5rem;
}

.fs-5 {
  font-size: 1.25rem;
}

/* Logo */
aside img {
  max-height: 150px;
  width: auto;
  display: block;
  margin: 0 auto;
}

/* Icons */
.pointer {
  cursor: pointer;
}

.bi {
  font-size: 1.25rem;
}

/* Responsive */
@media (max-width: 768px) {
  aside {
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    height: 100vh;
  }
  
  .main-content {
    margin-left: 0;
  }
}

/* Scrollbar styling */
aside::-webkit-scrollbar,
.main-section::-webkit-scrollbar {
  width: 6px;
}

aside::-webkit-scrollbar-track,
.main-section::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

aside::-webkit-scrollbar-thumb,
.main-section::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

aside::-webkit-scrollbar-thumb:hover,
.main-section::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Đảm bảo không có khoảng trắng */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

#app {
  height: 100vh;
  overflow: hidden;
}

/* Admin layout specific fixes */
#admin-layout {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

/* Đảm bảo main content không bị tràn */
.main-content {
  position: relative;
  overflow: hidden;
}

/* Fix cho sidebar toggle */
aside.d-none + .main-content {
  margin-left: 0;
  width: 100vw;
}

/* Khi sidebar ẩn, main content chiếm toàn bộ màn hình */
aside.d-none ~ .main-content {
  margin-left: 0;
  width: 100vw;
}

/* Đảm bảo không có scrollbar không cần thiết */
.admin-layout {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

.admin-layout::-webkit-scrollbar {
  display: none;  /* Chrome, Safari and Opera */
}
</style>
