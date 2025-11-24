<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">Quản lý người dùng</h2>
    </div>

    <div class="tab-content" id="userTabsContent">
      <div class="tab-pane fade show active" id="list" role="tabpanel">
        <div class="d-flex mb-3">
          <div class="form-seach me-3">
            <div class="input-group">
              <input
                v-model="searchTerm"
                @input="handleSearchInput"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm theo tên, email hoặc họ tên..."
              />
              <button @click="handleSearch" class="btn btn-gradient-primary" type="button">
                <i class="bi bi-search"></i>
              </button>
            </div>
          </div>
          <div class="form-fillter">
            <div class="input-group">
              <select v-model="statusFilter" @change="handleFilterChange" class="form-select">
                <option value="">Mặc định</option>
                <option value="active">Đang hoạt động</option>
                <option value="inactive">Ngừng hoạt động</option>
              </select>
              <button class="btn btn-gradient-primary" @click="handleFilterChange">
                <i class="bi bi-funnel"></i>
              </button>
            </div>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-bordered table-hover table-striped align-middle">
            <thead style="color: black; background-color: #f0f0f0">
              <tr class="table-dark">
                <th class="text-center">STT</th>
                <th>Tên tài khoản</th>
                <th>Email</th>
                <th>Họ tên</th>
                <th class="text-center">Trạng thái</th>
                <th class="text-center">Vai trò</th>
                <th class="text-center">Hành động</th>
              </tr>
            </thead>
            <tbody>
            <tr v-for="item in users.filter((u) => u.role === 2)" :key="item.id">
                <td class="text-center">{{ item.id }}</td>
                <td>{{ item.username }}</td>
                <td>{{ item.email }}</td>
                <td>{{ item.fullName }}</td>
                <td class="text-center">
                  <span class="badge" :class="item.isActive ? 'bg-success' : 'bg-danger'">
                    {{ item.isActive ? 'Hoạt động' : 'Đã khóa' }}
                  </span>
                </td>
                <td class="text-center">Người dùng</td>
                <td class="d-flex justify-content-center">
                  <RouterLink
                    :to="`/nguoiDungForm/${item.id}`"
                    class="btn btn-warning btn-sm me-2"
                  >
                    <i class="bi bi-pencil-square me-1"></i> Sửa
                  </RouterLink>
                  <button
                    class="btn btn-sm"
                    :class="item.isActive ? 'btn-danger' : 'btn-success'"
                    @click="toggleActive(item.id)"
                  >
                    <i :class="item.isActive ? 'bi bi-lock' : 'bi bi-unlock'"></i>
                    {{ item.isActive ? 'Khóa' : 'Mở khóa' }}
                  </button>
                </td>
              </tr>
              <tr v-if="users.length === 0">
                <td colspan="7" class="text-center">Không có người dùng nào</td>
              </tr>
            </tbody>
          </table>
          <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 0 }">
                <a class="page-link" @click="handlePageChange(currentPage - 1)">Trước</a>
              </li>
              <li
                v-for="(page, index) in displayedPages"
                :key="index"
                class="page-item"
                :class="{ active: page === currentPage + 1, disabled: page === '...' }"
              >
                <a
                  class="page-link"
                  @click="typeof page === 'number' ? handlePageChange(page - 1) : null"
                >
                  {{ page }}
                </a>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
                <a class="page-link" @click="handlePageChange(currentPage + 1)">Sau</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router';
import { ref, onMounted } from 'vue';
import Swal from 'sweetalert2';
import api from '@/axios';

const users = ref([]);
const searchTerm = ref('');
const statusFilter = ref('');
const currentPage = ref(0);
const pageSize = ref(5);
const totalPages = ref(1);
const displayedPages = ref([]);

const fetchUsers = async () => {
  try {
    const params = {
      search: searchTerm.value,
      status: statusFilter.value,
      page: currentPage.value,
      size: pageSize.value,
    };
    const response = await api.get('http://localhost:8080/api/user', { params });
    users.value = response.data.content; // Dữ liệu người dùng từ API
    totalPages.value = response.data.totalPages; // Tổng số trang
    updateDisplayedPages();
  } catch (error) {
    console.error('Lỗi khi tải danh sách người dùng:', error);
    Swal.fire({
      icon: 'error',
      title: 'Lỗi tải dữ liệu',
      text: 'Không thể tải dữ liệu người dùng.',
      confirmButtonColor: '#3a8ef9',
    });
  }
};

const toggleActive = async (id) => {
  const user = users.value.find((u) => u.id === id);
  if (!user) {
    Swal.fire({
      icon: 'error',
      title: 'Không tìm thấy người dùng',
      text: 'Người dùng không tồn tại trong danh sách.',
      confirmButtonColor: '#3a8ef9',
    });
    return;
  }

  const action = user.isActive ? 'khóa' : 'mở khóa';

  const result = await Swal.fire({
    title: `Xác nhận ${action}`,
    text: `Bạn có chắc chắn muốn ${action} tài khoản này không?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3a8ef9',
    cancelButtonColor: '#dc3545',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy',
  });

  if (result.isConfirmed) {
    try {
      const response = await api.put(`http://localhost:8080/api/user/${id}`, {
        isActive: !user.isActive,
      });
      if (response.status === 200) {
        await fetchUsers();
        Swal.fire({
          icon: 'success',
          title: 'Thành công',
          text: `${user.isActive ? 'Đã khóa' : 'Đã mở khóa'} tài khoản thành công!`,
          confirmButtonColor: '#3a8ef9',
        });
      }
    } catch (error) {
      console.error(`Lỗi khi ${action} tài khoản:`, error);
      Swal.fire({
        icon: 'error',
        title: 'Thao tác thất bại',
        text: error.response?.data?.error || 'Có lỗi xảy ra khi cập nhật trạng thái.',
        confirmButtonColor: '#3a8ef9',
      });
    }
  }
};

const handleSearchInput = () => {
  currentPage.value = 0; // Reset về trang đầu khi tìm kiếm
  fetchUsers();
};

const handleSearch = () => {
  currentPage.value = 0; // Reset về trang đầu khi tìm kiếm
  fetchUsers();
};

const handleFilterChange = () => {
  currentPage.value = 0; // Reset về trang đầu khi lọc
  fetchUsers();
};

const handlePageChange = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    fetchUsers();
  }
};

const updateDisplayedPages = () => {
  const maxPagesToShow = 5;
  const pages = [];
  let startPage = Math.max(0, currentPage.value - Math.floor(maxPagesToShow / 2));
  let endPage = Math.min(totalPages.value, startPage + maxPagesToShow);

  if (endPage - startPage < maxPagesToShow) {
    startPage = Math.max(0, endPage - maxPagesToShow);
  }

  for (let i = startPage; i < endPage; i++) {
    pages.push(i + 1);
  }

  if (startPage > 0) {
    pages.unshift('...');
    pages.unshift(1);
  }
  if (endPage < totalPages.value) {
    pages.push('...');
    pages.push(totalPages.value);
  }

  displayedPages.value = pages;
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
/* Thêm các style từ component quản lý liên hệ */
.form-seach {
  width: 350px;
  margin-right: 10px;
}

.input-group {
  border-radius: 8px;
}

.input-group .form-control {
  border-radius: 8px 0 0 8px;
}

.input-group .btn {
  border-radius: 0;
}

.input-group .btn:last-child {
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

/* Nút Sửa - Xóa */
.btn-sua {
  background: linear-gradient(135deg, #80cbc4, #a5d6a7);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(128, 203, 196, 0.25);
}

.btn-sua:hover {
  background: linear-gradient(135deg, #4db6ac, #81c784);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(77, 182, 172, 0.3);
}

.btn-xoa {
  background: linear-gradient(135deg, #f48fb1, #ffab91);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(244, 143, 177, 0.25);
}

.btn-xoa:hover {
  background: linear-gradient(135deg, #ec407a, #ff8a65);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(236, 64, 122, 0.3);
}

.btn-mo-khoa {
  background: linear-gradient(135deg, #a5d6a7, #80cbc4);
  color: #fff;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(165, 214, 167, 0.25);
}

.btn-mo-khoa:hover {
  background: linear-gradient(135deg, #81c784, #4db6ac);
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(129, 199, 132, 0.3);
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

/* Tab navigation */
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
  color: #3a8ef9;
  background-color: #f8f9fa;
}

.user-tab.active {
  color: #3a8ef9;
  background-color: #fff;
  border-color: #dee2e6 #dee2e6 #fff;
  font-weight: bold;
}

.user-tab i {
  margin-right: 6px;
}

/* Badge */
.badge {
  padding: 0.35em 0.65em;
  font-size: 0.75em;
  font-weight: 600;
  border-radius: 0.25rem;
}

.bg-success {
  background-color: #28a745 !important;
}

.bg-danger {
  background-color: #dc3545 !important;
}
</style>
