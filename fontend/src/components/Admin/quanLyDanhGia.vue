<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý đánh giá</h2>
    </div>

    <!-- Bộ lọc -->
    <div class="d-flex mb-3 flex-wrap gap-2">
      <div class="form-seach">
        <div class="input-group">
          <select v-model="statusFilter" @change="fetchRatings" class="form-select">
            <option value="all">Tất cả đánh giá</option>
            <option value="visible">Hiện</option>
            <option value="hidden">Ẩn</option>
          </select>
          <button class="btn btn-gradient-primary" type="button">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <div class="form-seach">
        <div class="input-group">
          <select v-model="userFilter" @change="fetchRatings" class="form-select">
            <option value="">Tất cả người dùng</option>
            <option v-for="user in userList" :key="user" :value="user">{{ user }}</option>
          </select>
          <button class="btn btn-gradient-primary" type="button">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <div class="form-seach">
        <div class="input-group">
          <select v-model="starFilter" @change="fetchRatings" class="form-select">
            <option value="">Tất cả số sao</option>
            <option value="1">1 sao ⭐</option>
            <option value="2">2 sao ⭐⭐</option>
            <option value="3">3 sao ⭐⭐⭐</option>
            <option value="4">4 sao ⭐⭐⭐⭐</option>
            <option value="5">5 sao ⭐⭐⭐⭐⭐</option>
          </select>
          <button class="btn btn-gradient-primary" type="button">
            <i class="bi bi-funnel"></i>
          </button>
        </div>
      </div>

      <button @click="clearFilters" class="btn btn-outline-secondary">
        <i class="bi bi-x-lg me-1"></i> Xóa lọc
      </button>
    </div>

    <!-- Thông tin bộ lọc -->
    <div v-if="hasActiveFilters" class="mb-3">
      <small class="text-muted">
        <i class="bi bi-funnel me-1"></i>
        Đang lọc:
        <span v-if="statusFilter !== 'all'" class="badge bg-primary me-1">{{ getStatusText(statusFilter) }}</span>
        <span v-if="userFilter" class="badge bg-info me-1">{{ userFilter }}</span>
        <span v-if="starFilter" class="badge bg-warning me-1">{{ starFilter }} sao</span>
        <span class="badge bg-secondary">{{ filteredCount }} kết quả</span>
      </small>
    </div>

    <!-- Bảng dữ liệu -->
    <div class="table-responsive">
      <table class="table table-bordered table-hover table-striped align-middle">
        <thead class="" style="color: black; background-color: #f0f0f0">
          <tr class="table-dark ">
            <th class="text-center">STT</th>
            <th class="text-center">Sản phẩm</th>
            <th class="text-center">Người đánh giá</th>
            <th class="text-center">Số sao</th>
            <th class="text-center">Ngày đánh giá</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Đang tải...</span>
              </div>
            </td>
          </tr>

          <tr v-else-if="error">
            <td colspan="7" class="text-center text-danger py-3">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              {{ error }}
            </td>
          </tr>

          <tr v-else-if="ratings.length === 0">
            <td colspan="7" class="text-center py-3">
              <div class="alert alert-info mb-0">
                <i class="bi bi-info-circle-fill me-2"></i>
                <span v-if="hasActiveFilters">Không tìm thấy đánh giá nào phù hợp</span>
                <span v-else>Không có dữ liệu đánh giá</span>
              </div>
            </td>
          </tr>

          <tr v-for="(rating, index) in paginatedRatings" :key="rating.id" v-else>
            <td class="text-center">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
            <td class="text-center">
              <p>{{ rating.productName }}</p>
            </td>
            <td class="text-center">{{ rating.userFullName }}</td>
            <td class="text-center">
              <div class="rating-stars">
                {{ renderStars(rating.ratings) }}
                <small class="text-muted ms-1">({{ rating.ratings }}/5)</small>
              </div>
            </td>
            <td class="text-center">{{ formatDate(rating.ratingDate) }}</td>
            <td class="text-center">
              <span class="badge" :class="getStatusClass(rating.status)">
                {{ rating.status }}
              </span>
            </td>
            <td class="d-flex justify-content-center">
              <router-link
                :to="`/chiTietDanhGia/${rating.id}`"
                class="btn btn-warning btn-sm me-2"
              >
                <i class="bi bi-eye me-1"></i> Xem
              </router-link>

              <button
                class="btn btn-sm me-2"
                :class="rating.status === 'Ẩn' ? 'btn-info' : 'btn-secondary'"
                @click="updateStatus(rating.id, rating.status === 'Ẩn' ? 'Hiện' : 'Ẩn')"
                :disabled="processing"
              >
                <i :class="rating.status === 'Ẩn' ? 'bi bi-eye' : 'bi bi-eye-slash'"></i>
                {{ rating.status === 'Ẩn' ? 'Hiện' : 'Ẩn' }}
              </button>

              <button
                class="btn btn-danger btn-sm"
                @click="deleteRating(rating.id)"
                :disabled="processing"
              >
                <i class="bi bi-trash me-1"></i> Xóa
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click.prevent="prevPage">Trước</a>
        </li>
        <li
          v-for="page in displayedPages"
          :key="page"
          class="page-item"
          :class="{ active: page === currentPage, disabled: page === '...' }"
        >
          <a class="page-link" @click.prevent="typeof page === 'number' ? goToPage(page) : null">
            {{ page }}
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click.prevent="nextPage">Sau</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import api from '@/axios' // ✅ Dùng axios đã cấu hình JWT
import Swal from 'sweetalert2'



export default {
  name: 'QuanLyDanhGia',
  data() {
    return {
      ratings: [],
      userList: [],
      loading: true,
      processing: false,
      error: null,
      successMessage: null,
      statusFilter: 'all',
      userFilter: '',
      starFilter: '',
      filteredCount: 0,
      // Phân trang
      currentPage: 1,
      itemsPerPage: 5,
      maxVisiblePages: 5
    };
  },
  computed: {
    hasActiveFilters() {
      return this.statusFilter !== 'all' || this.userFilter !== '' || this.starFilter !== '';
    },
    // Phân trang
    totalPages() {
      return Math.ceil(this.ratings.length / this.itemsPerPage);
    },
    displayedPages() {
      const pages = [];
      const total = this.totalPages;
      const current = this.currentPage;
      const half = Math.floor(this.maxVisiblePages / 2);

      let start = Math.max(1, current - half);
      let end = Math.min(total, start + this.maxVisiblePages - 1);

      if (end - start + 1 < this.maxVisiblePages) {
        start = Math.max(1, end - this.maxVisiblePages + 1);
      }

      if (start > 1) {
        pages.push(1);
        if (start > 2) pages.push('...');
      }

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (end < total) {
        if (end < total - 1) pages.push('...');
        pages.push(total);
      }

      return pages;
    },
    paginatedRatings() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.ratings.slice(start, start + this.itemsPerPage);
    }
  },
  mounted() {
    this.initializeData();
  },
  methods: {
    async initializeData() {
      try {
        await Promise.all([
          this.loadUserList(),
          this.fetchRatings()
        ]);
      } catch (error) {
        console.error('Error initializing data:', error);
        this.error = 'Có lỗi xảy ra khi tải dữ liệu ban đầu';
      }
    },

    async loadUserList() {
      try {
        const response = await api.get('/api/ratings/users');
        if (response.data && response.data.success) {
          this.userList = response.data.data || [];
        } else if (Array.isArray(response.data)) {
          this.userList = response.data;
        } else {
          this.userList = [];
        }
      } catch (error) {
        console.error('Error loading user list:', error);
        this.userList = [];
      }
    },

    async fetchRatings() {
      this.loading = true;
      this.error = null;

      try {
        const params = this.buildFilterParams();
        console.log('Fetching ratings with params:', params);

        const response = await api.get('/api/ratings/filter', {
          params,
          timeout: 10000
        });

        console.log('API response:', response.data);

        if (response.data && response.data.success) {
          this.ratings = response.data.data || [];
          this.filteredCount = this.ratings.length;
        } else if (Array.isArray(response.data)) {
          this.ratings = response.data;
          this.filteredCount = this.ratings.length;
        } else {
          this.ratings = [];
          this.filteredCount = 0;
          this.error = response.data?.message || 'Không thể tải dữ liệu đánh giá';
        }

        // Reset về trang 1 nếu trang hiện tại vượt quá tổng số trang
        if (this.currentPage > this.totalPages && this.totalPages > 0) {
          this.currentPage = 1;
        }
      } catch (error) {
        console.error('Error fetching ratings:', error);
        this.ratings = [];
        this.filteredCount = 0;

        if (error.code === 'ECONNABORTED') {
          this.error = 'Timeout: Không thể kết nối đến server';
        } else if (error.response) {
          this.error = `Lỗi server: ${error.response.status} - ${error.response.data?.message || error.response.statusText}`;
        } else if (error.request) {
          this.error = 'Không thể kết nối đến server';
        } else {
          this.error = 'Có lỗi xảy ra khi tải dữ liệu';
        }
      } finally {
        this.loading = false;
      }
    },

    buildFilterParams() {
      const params = {};
      if (this.statusFilter && this.statusFilter !== 'all') {
        params.status = this.statusFilter;
      }
      if (this.userFilter && this.userFilter.trim() !== '') {
        params.userName = this.userFilter.trim();
      }
      if (this.starFilter && this.starFilter !== '') {
        params.stars = parseInt(this.starFilter);
      }
      return params;
    },

    async updateStatus(id, newStatus) {
      const actionText = newStatus === 'Đã duyệt' ? 'duyệt' : 'ẩn';

      const result = await Swal.fire({
        title: `Xác nhận ${actionText}`,
        text: `Bạn có chắc chắn muốn ${actionText} đánh giá này không?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: newStatus === 'Đã duyệt' ? '#28a745' : '#ffc107',
        cancelButtonColor: '#6c757d',
        confirmButtonText: `Có, ${actionText}!`,
        cancelButtonText: 'Hủy'
      });

      if (!result.isConfirmed) return;

      this.processing = true;
      try {
        const response = await api.put(`/api/ratings/${id}/status`, {
          status: newStatus
        });

        if (response.data && response.data.success) {
          this.successMessage = `${actionText.charAt(0).toUpperCase() + actionText.slice(1)} đánh giá thành công!`;
          await this.fetchRatings();
          setTimeout(() => {
            this.successMessage = null;
          }, 3000);
        } else {
          throw new Error(response.data?.message || 'Không thể cập nhật trạng thái');
        }
      } catch (error) {
        console.error('Error updating status:', error);
        Swal.fire({
          title: 'Lỗi!',
          text: error.response?.data?.message || error.message || `Không thể ${actionText} đánh giá`,
          icon: 'error'
        });
      } finally {
        this.processing = false;
      }
    },

    async deleteRating(id) {
      const result = await Swal.fire({
        title: 'Xác nhận xóa',
        text: 'Bạn có chắc chắn muốn xóa đánh giá này không? Hành động này không thể hoàn tác.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Có, xóa!',
        cancelButtonText: 'Hủy'
      });

      if (!result.isConfirmed) return;

      this.processing = true;
      try {
        const response = await api.delete(`/api/ratings/${id}`);
        if (response.data && response.data.success) {
          this.successMessage = 'Xóa đánh giá thành công!';
          await this.fetchRatings();
          setTimeout(() => {
            this.successMessage = null;
          }, 3000);
        } else {
          throw new Error(response.data?.message || 'Không thể xóa đánh giá');
        }
      } catch (error) {
        console.error('Error deleting rating:', error);
        Swal.fire({
          title: 'Lỗi!',
          text: error.response?.data?.message || error.message || 'Không thể xóa đánh giá',
          icon: 'error'
        });
      } finally {
        this.processing = false;
      }
    },

    async refreshData() {
      await this.fetchRatings();
    },

    clearFilters() {
      this.statusFilter = 'all';
      this.userFilter = '';
      this.starFilter = '';
      this.currentPage = 1; // Reset về trang đầu khi xóa filter
      this.fetchRatings();
    },

    // Phương thức phân trang
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    goToPage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },

    // Reset trang khi filter thay đổi
    resetPagination() {
      this.currentPage = 1;
    },

    getStatusText(status) {
      const statusMap = {
        'all': 'Tất cả',
        'visible': 'Hiện',
        'hidden': 'Ẩn'
      };
      return statusMap[status] || status;
    },

    renderStars(rating) {
      const validRating = typeof rating === 'number' ? Math.max(0, Math.min(rating, 5)) : 0;
      return '⭐'.repeat(validRating) + '☆'.repeat(5 - validRating);
    },

    formatDate(date) {
      if (!date) return 'N/A';
      try {
        const d = new Date(date);
        if (isNaN(d.getTime())) return 'N/A';
        return d.toLocaleString('vi-VN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        });
      } catch (error) {
        console.error('Error formatting date:', error);
        return 'N/A';
      }
    },

    getStatusClass(status) {
  const classMap = {
    'Đã duyệt': 'badge bg-success text-white',
    'Chờ duyệt': 'badge bg-warning text-dark',
    'Hiện': 'badge bg-success text-white',  // ✅ xanh lá
    'Ẩn': 'badge bg-warning text-dark'      // ✅ vàng
  };
  return classMap[status] || 'badge bg-light text-dark';
}
  }
};
</script>


<style scoped>
.pagination .page-link {
  background: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  margin: 0 4px;
  transition:
    background 0.3s ease,
    color 0.3s ease;
  border-radius: 8px;
}

.pagination .page-link:hover {
  background: linear-gradient(-135deg, #1de9b6 0%, #1dc4e9 100%);
  color: black;
}

.pagination .page-item.active .page-link {
  background: linear-gradient(-135deg, #899fd4 0%, #a389d4 100%);
  color: white;
  border: none;
}

.pagination .page-item.disabled .page-link {
  background: #ccc;
  color: #666;
}
/* Thêm các style từ component quản lý liên hệ */
.form-seach {
  width: 250px;
}

.input-group {
  border-radius: 8px;
}

.input-group .form-select {
  border-radius: 8px 0 0 8px;
}

.input-group .btn {
  border-radius: 0 8px 8px 0;
}

.btn-gradient-primary {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
}

.btn-gradient-primary:hover {
  background-image: linear-gradient(-135deg, #3a8ef9 0%, #5ee7df 100%);
}

.btn-outline-secondary {
  border: 1px solid #6c757d;
  color: #6c757d;
  background-color: transparent;
}

.btn-outline-secondary:hover {
  background-color: #6c757d;
  color: white;
}

/* Nút Xem */
.btn-view {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: #fff;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(23, 162, 184, 0.25);
}

.btn-view:hover {
  background: linear-gradient(135deg, #138496, #117a8b);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(19, 132, 150, 0.3);
}

/* Nút Ẩn */
.btn-hide {
  background: linear-gradient(135deg, #ffc107, #e0a800);
  color: #212529;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(255, 193, 7, 0.25);
}

.btn-hide:hover {
  background: linear-gradient(135deg, #e0a800, #d39e00);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(224, 168, 0, 0.3);
}

/* Nút Bỏ ẩn */
.btn-mo-khoa {
  background: linear-gradient(135deg, #28a745, #218838);
  color: #fff;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(40, 167, 69, 0.25);
}

.btn-mo-khoa:hover {
  background: linear-gradient(135deg, #218838, #1e7e34);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(33, 136, 56, 0.3);
}

/* Nút Xóa */



/* Badge */


.bg-success {
  background-color: #28a745 !important;
}

.bg-secondary {
  background-color: #6c757d !important;
}

.bg-primary {
  background-color: #007bff !important;
}

.bg-info {
  background-color: #17a2b8 !important;
}

.bg-warning {
  background-color: #ffc107 !important;
  color: #212529 !important;
}

/* Breadcrumb */
.breadcrumb {
  font-size: 0.85rem;
  background-color: transparent;
  padding: 0;
  margin: 0;
}

.breadcrumb-item a {
  color: #007bff;
  text-decoration: none;
}

.breadcrumb-item a:hover {
  text-decoration: underline;
}

.breadcrumb-item.active {
  color: #6c757d;
}

/* Rating stars */
.rating-stars {
  font-size: 1rem;
  line-height: 1;
}

/* Spinner */
.spinner-border {
  width: 2rem;
  height: 2rem;
  border-width: 0.2em;
}

/* Alert */
.alert {
  border: none;
}

.alert-info {
  background-color: #d1ecf1;
  color: #0c5460;
}

/* Responsive */
@media (max-width: 768px) {
  .form-seach {
    width: 100%;
  }

  .d-flex.flex-wrap {
    gap: 0.5rem !important;
  }

  td {
    white-space: nowrap;
  }
}
</style>

