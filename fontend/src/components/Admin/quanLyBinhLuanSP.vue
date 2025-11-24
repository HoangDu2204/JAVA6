<template>
    
          <h1 class="fs-4 fw-bold mb-4">Quản lý bình luận</h1>

     <ul class="nav nav-tabs mb-3" id="userTabs" role="tablist">
  <li class="nav-item" role="presentation">
    <RouterLink
      class="nav-link user-tab"
      :class="{ active: isActive('/QuanLyBinhLuan') }"
      to="/QuanLyBinhLuan"
    >
      <i class="bi bi-chat-dots-fill fs-4 me-1"></i> Bình luận sản phẩm
    </RouterLink>
  </li>

  <!-- <li class="nav-item" role="presentation">
    <RouterLink
      class="nav-link user-tab"
      :class="{ active: isActive('/BinhLuanTinTuc') }"
      to="/BinhLuanTinTuc"
    >
      <i class="bi bi-journal-text fs-4 me-1"></i> Bình luận tin tức
    </RouterLink>
  </li>

  <li class="nav-item" role="presentation">
    <RouterLink
      class="nav-link user-tab"
      :class="{ active: isActive('/BinhLuanDonHang') }"
      to="/BinhLuanDonHang"
    >
      <i class="bi bi-receipt fs-4 me-1"></i> Bình luận đơn hàng
    </RouterLink>
  </li> -->
</ul>
          <div class="table-responsive">
            <table class="table table-hover align-middle shadow rounded">
              <thead class="custom-thead">
                <tr>
                  <th>ID</th>
                  <th>Loại</th>
                  <th>Liên quan</th>
                  <th>Người dùng</th>
                  <th>Nội dung</th>
                  <th>Ngày đăng</th>
                  <th>Trạng thái</th>
                  <th>Hành động</th>
                </tr>
              </thead>

              <tbody>
                <tr>
                  <td>1</td>
                  <td><span class="badge bg-primary">Sản phẩm</span></td>
                  <td>Bánh mì nướng</td>
                  <td>Nguyễn Văn A</td>
                  <td>Bánh rất ngon</td>
                  <td>2025-06-01</td>
                  <td><span class="badge bg-success">Hiển thị</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-success me-2">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-2">
                      <i class="bi bi-eye-slash"></i> Ẩn
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash"></i> Xóa
                    </button>
                  </td>
                </tr>
                 <tr>
                  <td>2</td>
                  <td><span class="badge bg-primary">Sản phẩm</span></td>
                  <td>Bánh mì nướng</td>
                  <td>Nguyễn Văn A</td>
                  <td>Bánh rất ngon</td>
                  <td>2025-06-01</td>
                  <td><span class="badge bg-success">Hiển thị</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-success me-2">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-2">
                      <i class="bi bi-eye-slash"></i> Ẩn
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash"></i> Xóa
                    </button>
                  </td>
                </tr>
               <tr>
                  <td>3</td>
                  <td><span class="badge bg-primary">Sản phẩm</span></td>
                  <td>Bánh mì nướng</td>
                  <td>Nguyễn Văn A</td>
                  <td>Bánh rất ngon</td>
                  <td>2025-06-01</td>
                  <td><span class="badge bg-success">Hiển thị</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-success me-2">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-2">
                      <i class="bi bi-eye-slash"></i> Ẩn
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash"></i> Xóa
                    </button>
                  </td>
                </tr>
               <tr>
                  <td>4</td>
                  <td><span class="badge bg-primary">Sản phẩm</span></td>
                  <td>Bánh mì nướng</td>
                  <td>Nguyễn Văn A</td>
                  <td>Bánh rất ngon</td>
                  <td>2025-06-01</td>
                  <td><span class="badge bg-success">Hiển thị</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-success me-2">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-2">
                      <i class="bi bi-eye-slash"></i> Ẩn
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash"></i> Xóa
                    </button>
                  </td>
                </tr>
                <tr>
                  <td>5</td>
                  <td><span class="badge bg-primary">Sản phẩm</span></td>
                  <td>Bánh mì nướng</td>
                  <td>Nguyễn Văn A</td>
                  <td>Bánh rất ngon</td>
                  <td>2025-06-01</td>
                  <td><span class="badge bg-success">Hiển thị</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-success me-2">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                    <button class="btn btn-sm btn-outline-secondary me-2">
                      <i class="bi bi-eye-slash"></i> Ẩn
                    </button>
                    <button class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash"></i> Xóa
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
     
</template>
<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const isActive = (path) => route.path === path
</script>

<style scoped>


.table-responsive {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.table th,
.table td {
  vertical-align: middle;
}

.badge {
  font-size: 0.85rem;
}
.custom-thead th {
 
  color: black;
  background-color: #f0f0f0; /* xám nhạt */

}
.active-donhang {
  font-weight: bold;
  background-color: #ffe5b4; 
}

.active-tintuc {
  font-weight: bold;
  background-color: #d1ecf1; 
}


.text-dark {
  color: #000 !important;
}
 .text-skyblue {
  color: #1e90ff !important; 
  }
.user-tab {
  min-width: 220px; 
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid #dee2e6;
  border-bottom: none;
  border-radius: 0.375rem 0.375rem 0 0;
  color: #101010;
  background-color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.user-tab:hover {
  color: #00bfff;
  background-color: #f8f9fa;
}


.user-tab.active {
  color: #00bfff;
  background-color: #fff;
  border-color: #dee2e6 #dee2e6 #fff;
  font-weight: bold;
}


.user-tab i {
  margin-right: 6px;
}


</style>