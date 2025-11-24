<template>
  <div class="container py-4">
    <h2 class="mb-4">Quản lý sản phẩm</h2>

    <button class="btn btn-primary mb-3" @click="openModal(false)">Thêm sản phẩm</button>

    <!-- Modal thêm/sửa -->
    <div v-if="modalVisible" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-content-custom">
        <form @submit.prevent="submitProduct" enctype="multipart/form-data" novalidate>
          <div class="modal-header-custom">
            <h5 class="modal-title-custom">{{ isEditing ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}</h5>
            <button type="button" class="btn-close-custom" @click="closeModal" aria-label="Đóng">&times;</button>
          </div>

          <div class="modal-body-custom">
            <div class="mb-3">
              <label class="form-label">Tên sản phẩm</label>
              <input v-model="form.name" type="text" class="form-control" />
              <small v-if="errors.name" class="text-danger">{{ errors.name }}</small>
            </div>

            <div class="mb-3">
              <label class="form-label">Mô tả</label>
              <textarea v-model="form.description" class="form-control" rows="3"></textarea>
              <small v-if="errors.description" class="text-danger">{{ errors.description }}</small>
            </div>

            <div class="mb-3">
              <label class="form-label">Danh mục</label>
              <select v-model="form.categoryId" class="form-select">
                <option disabled value="">-- Chọn danh mục --</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              </select>
              <small v-if="errors.categoryId" class="text-danger">{{ errors.categoryId }}</small>
            </div>

            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select v-model="form.isActive" class="form-select">
                <option disabled value="">-- Chọn trạng thái --</option>
                <option :value="true">Hoạt động</option>
                <option :value="false">Ngừng hoạt động</option>
              </select>
              <small v-if="errors.isActive" class="text-danger">{{ errors.isActive }}</small>
            </div>

            <div class="mb-3">
              <label class="form-label">Hình ảnh</label>
              <input type="file" multiple @change="handleFileUpload" class="form-control" />
              <small v-if="errors.images" class="text-danger">{{ errors.images }}</small>
            </div>
          </div>

          <div class="modal-footer-custom">
            <button type="button" class="btn btn-secondary" @click="closeModal">Đóng</button>
            <button type="submit" class="btn" :class="isEditing ? 'btn-warning' : 'btn-primary'">
              {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
            </button>
          </div>
        </form>
      </div>
    </div>


    <!-- Bộ lọc sản phẩm -->
    <div class="d-flex gap-3 mb-3">
      <select v-model="filters.categoryId" class="form-select w-25">
        <option value="">-- Tất cả danh mục --</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
      </select>

      <select v-model="filters.isActive" class="form-select w-25">
        <option value="">-- Tất cả trạng thái --</option>
        <option :value="true">Hoạt động</option>
        <option :value="false">Ngừng hoạt động</option>
      </select>

      <input v-model="filters.keyword" type="text" placeholder="Tìm theo tên..." class="form-control w-auto"
        style="min-width: 300px;">

      <!-- <button class="btn btn-primary" @click="searchProducts">Tìm kiếm</button> -->
      <!-- Nút tìm kiếm -->
      <button class="btn btn-primary" @click="searchProducts" title="Tìm kiếm">
        <i class="bi bi-search"></i>
      </button>

      <!-- Nút lọc -->
      <button class="btn btn-info" @click="applyFilters" title="Lọc">
        <i class="bi bi-funnel-fill text-white"></i>
      </button>

      <!-- Nút reset -->
      <button class="btn btn-secondary" @click="resetFilters" title="Đặt lại bộ lọc">
        <i class="bi bi-arrow-counterclockwise"></i>
      </button>

    </div>


    <!-- Danh sách sản phẩm -->
    <div class="card p-4">
      <h5 class="mb-3">Danh sách sản phẩm</h5>
      <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>STT</th>
            <th>Tên</th>
            <th>Danh mục</th>
            <th>Trạng thái</th>
            <th>Ảnh</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <!-- <tr v-for="p in products" :key="p.id"> -->
            <tr v-for="(p, index) in products" :key="p.id">
            <!-- <td>{{ p.id }}</td> -->
             <td>{{ index + 1 }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.categoryName }}</td>
            <td>{{ p.isActive ? 'Hoạt động' : 'Ngừng hoạt động' }}</td>
            <td>
              <img v-if="p.imageUrls?.length" :src="imageBaseUrl + p.imageUrls[0]" alt="SP" width="80"
                style="cursor:pointer" @click="openImagePopup(p.imageUrls)" />
            </td>

              <!-- <button class="btn btn-sm btn-warning me-2" @click="editProduct(p)">Sửa</button>
              <button class="btn btn-sm btn-danger me-2" @click="deactivateProduct(p.id)">Ẩn</button>
              <button class="btn btn-sm btn-info"  @click="goToVariant(p)">
                Biến thể
              </button> -->
              <!-- <button class="btn btn-sm btn-warning me-2" @click="editProduct(p)" title="Sửa">
                <i class="bi bi-pencil-square"></i>
              </button>

              <button class="btn btn-sm" :class="p.isActive ? 'btn-danger' : 'btn-success'"
                @click="toggleProductStatus(p.id)" :title="p.isActive ? 'Ngừng hoạt động' : 'Kích hoạt'">
                <i :class="p.isActive ? 'bi bi-x-circle' : 'bi bi-check-circle'"></i>
              </button>


              <button class="btn btn-sm btn-info" @click="goToVariant(p)" title="Biến thể">
                <i class="bi bi-box-seam"></i>
              </button> -->
<td class="text-center">
  <div class="d-flex justify-content-center gap-2">
    <button class="btn btn-warning btn-sm text-white" @click="editProduct(p)" title="Sửa">
     Sửa
    </button>

    <button
  class="btn btn-sm text-white"
  :class="p.isActive ? 'btn-danger' : 'btn-success'"
  @click="toggleProductStatus(p.id)"
  :title="p.isActive ? 'Ngừng hoạt động' : 'Kích hoạt'" style="width: 200px;"
>
  {{ p.isActive ? 'Ngừng hoạt động' : 'Kích hoạt' }}
</button>


    <button class="btn btn-info btn-sm text-white" @click="goToVariant(p)" title="Biến thể">
      Biến thể
    </button>
  </div>




            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="6" class="text-center text-danger">Chưa có sản phẩm nào.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <nav class="mt-3">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: pagination.page === 0 }" @click="changePage(pagination.page - 1)">
          <a class="page-link" href="#">Trước</a>
        </li>

        <li v-for="page in pagination.totalPages" :key="page" class="page-item"
          :class="{ active: page - 1 === pagination.page }" @click="changePage(page - 1)">
          <a class="page-link" href="#">{{ page }}</a>
        </li>

        <li class="page-item" :class="{ disabled: pagination.page === pagination.totalPages - 1 }"
          @click="changePage(pagination.page + 1)">
          <a class="page-link" href="#">Sau</a>
        </li>
      </ul>
    </nav>



    <!-- Popup hiển thị ảnh -->
    <div v-if="imagePopup.length" class="popup-overlay" @click.self="closeImagePopup">
      <div class="popup-box-medium">
        <div class="d-flex justify-content-end mb-2">
          <button class="btn btn-close" @click="closeImagePopup">&times;</button>
        </div>
        <div class="d-flex flex-wrap justify-content-center gap-3">
          <div v-for="url in imagePopup" :key="url" class="position-relative" style="width: 180px;">
            <img :src="imageBaseUrl + url" class="rounded border shadow-sm"
              style="width: 100%; height: 140px; object-fit: cover;" alt="Ảnh sản phẩm" />
            <button class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1" @click.stop="deleteImage(url)"
              title="Xóa ảnh">×</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

const router = useRouter()
const imageBaseUrl = 'http://localhost:8080/images/'

// ================= STATE =================
const modalVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const form = ref({ name: '', description: '', categoryId: '', isActive: '', images: [] })
const errors = ref({ name: '', description: '', categoryId: '', isActive: '', images: '' })

const filters = ref({ categoryId: '', isActive: '', keyword: '' })
const products = ref([])
const categories = ref([])
const imagePopup = ref([])

const pagination = ref({ page: 0, size: 5, totalPages: 0 }) // Chuẩn bị phân trang

// ================= HELPER =================
const clearErrors = () => Object.keys(errors.value).forEach(k => errors.value[k] = '')

const resetForm = () => {
  form.value = { name: '', description: '', categoryId: '', isActive: '', images: [] }
  isEditing.value = false
  editingId.value = null
  clearErrors()
}

const validateForm = () => {
  clearErrors()
  let valid = true
  if (!form.value.name.trim()) {
    errors.value.name = 'Tên sản phẩm không được để trống.'
    valid = false
  } else if (form.value.name.length > 100) {
    errors.value.name = 'Tên sản phẩm không được quá 100 ký tự.'
    valid = false
  }
  if (!form.value.description.trim()) {
    errors.value.description = 'Mô tả không được để trống.'
    valid = false
  } else if (form.value.description.length > 500) {
    errors.value.description = 'Mô tả không được quá 500 ký tự.'
    valid = false
  }
  if (!form.value.categoryId) {
    errors.value.categoryId = 'Vui lòng chọn danh mục.'
    valid = false
  }
  if (form.value.isActive === '' || form.value.isActive === null) {
    errors.value.isActive = 'Vui lòng chọn trạng thái.'
    valid = false
  }
  if (!isEditing.value && form.value.images.length === 0) {
    errors.value.images = 'Vui lòng chọn ít nhất một ảnh.'
    valid = false
  }
  return valid
}

// ================= MODAL =================
const openModal = (edit = false) => {
  modalVisible.value = true
  if (!edit) resetForm()
}

const closeModal = () => {
  modalVisible.value = false
  resetForm()
}

// ================= IMAGE POPUP =================
const openImagePopup = (urls) => { imagePopup.value = urls }
const closeImagePopup = () => { imagePopup.value = [] }
const handleFileUpload = (e) => { form.value.images = Array.from(e.target.files) }

// ================= CRUD =================
const submitProduct = async () => {
  if (!validateForm()) return
  const fd = new FormData()
  Object.entries(form.value).forEach(([key, value]) => {
    if (key === 'images') value.forEach(file => fd.append('images', file))
    else fd.append(key, value)
  })

  try {
    if (isEditing.value) {
      await api.post(`/api/admin/products/update/${editingId.value}`, fd)
      Swal.fire('Thành công', 'Cập nhật sản phẩm thành công!', 'success')
    } else {
      await api.post('/api/admin/products/create', fd)
      Swal.fire('Thành công', 'Thêm sản phẩm mới thành công!', 'success')
    }
    await loadProducts()
    closeModal()
  } catch (err) {
     const message = err.response?.data?.error || err.response?.data || 'Gửi form sản phẩm thất bại!'
  Swal.fire('Lỗi', message, 'error')
  }
}

//const deactivateProduct = async (id) => {
//  try {
//    await api.put(`/api/admin/products/deactivate/${id}`)
//    Swal.fire('Đã ẩn', 'Sản phẩm đã được ẩn thành công!', 'success')
//    await loadProducts()
//  } catch (err) {
//    Swal.fire('Lỗi', err.response?.data || 'Ẩn sản phẩm thất bại', 'error')
//  }
//}
const toggleProductStatus = async (id) => {
  try {
    // Tìm sản phẩm theo id để lấy thông tin
    const product = products.value.find(p => p.id === id)
    if (!product) return

    const result = await Swal.fire({
      title: 'Xác nhận',
      text: `Bạn có chắc muốn ${product.isActive ? 'ngừng hoạt động' : 'kích hoạt'} sản phẩm "${product.name}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Có',
      cancelButtonText: 'Hủy'
    })

    if (!result.isConfirmed) return

    // Gọi API cập nhật trạng thái
    const res = await api.put(`/api/admin/products/toggle-active/${id}`)

    // Cập nhật trực tiếp trong mảng products để UI đổi ngay
    product.isActive = res.data.isActive

    Swal.fire(
      'Thành công',
      `Sản phẩm "${res.data.name}" đã được ${res.data.isActive ? 'kích hoạt' : 'ngừng hoạt động'}!`,
      'success'
    )
  } catch (err) {
    Swal.fire(
      'Lỗi',
      err.response?.data || 'Cập nhật trạng thái sản phẩm thất bại',
      'error'
    )
  }
}


const deleteImage = async (url) => {
  try {
    await api.delete('/api/admin/products/delete-image', { params: { url } })
    imagePopup.value = imagePopup.value.filter(i => i !== url)
    await loadProducts()
  } catch {
    Swal.fire('Lỗi', 'Không thể xóa ảnh', 'error')
  }
}

const editProduct = (p) => {
  isEditing.value = true
  editingId.value = p.id
  form.value = {
    name: p.name,
    description: p.description,
    categoryId: categories.value.find(c => c.name === p.categoryName)?.id || '',
    isActive: p.isActive,
    images: []
  }
  openModal(true)
}

const goToVariant = (p) => {
  if (!p.isActive) {
    Swal.fire('Không thể truy cập', 'Chỉ sản phẩm đang hoạt động mới được thêm biến thể.', 'warning')
    return
  }
  router.push(`/admin/product/${p.id}/variants`)
}

// ================= API =================
// const loadProducts = async (params = {}) => {
//   try {
//     const res = await api.get('/api/admin/products/search', {
//       params: {
//         categoryId: filters.value.categoryId || null,
//         isActive: filters.value.isActive === '' ? null : filters.value.isActive,
//         keyword: filters.value.keyword || null,
//         page: pagination.value.page,
//         size: pagination.value.size,
//         ...params
//       }
//     })
//     products.value = res.data.content || res.data
//     pagination.value.totalPages = res.data.totalPages || 1
//   } catch {
//     Swal.fire('Lỗi', 'Không thể tải danh sách sản phẩm', 'error')
//   }
// }
const loadProducts = async (params = {}) => {
  try {
    const res = await api.get('/api/admin/products/search', {
      params: {
        categoryId: filters.value.categoryId || null,
        isActive: filters.value.isActive === '' ? null : filters.value.isActive,
        keyword: filters.value.keyword || null,
        page: pagination.value.page,
        size: pagination.value.size,
        ...params
      }
    })

    // Gắn log để kiểm tra dữ liệu trả về từ backend
    console.log("📦 API Response:", res.data)

    products.value = res.data.content || []
    pagination.value.totalPages = res.data.totalPages || 1
    pagination.value.page = res.data.number || 0

    // Gắn log để kiểm tra state phân trang sau khi set
    console.log("📄 Pagination State:", pagination.value)

  } catch (err) {
    console.error("❌ API Error:", err)
    Swal.fire('Lỗi', 'Không thể tải danh sách sản phẩm', 'error')
  }
}



const loadCategories = async () => {
  try {
    const res = await api.get('/api/categories')
    categories.value = res.data
  } catch {
    Swal.fire('Lỗi', 'Không thể tải danh mục', 'error')
  }
}

// const searchProducts = () => {
//   pagination.value.page = 0
//   loadProducts()
// }
const searchProducts = () => {
  applyFilters()
}

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < pagination.value.totalPages) {
    pagination.value.page = newPage
    loadProducts()
  }
}

const applyFilters = () => {
  // Chỉ gọi loadProducts với các filters hiện tại (có thể tương tự searchProducts)
  pagination.value.page = 0
  loadProducts()
}

const resetFilters = () => {
  filters.value = { categoryId: '', isActive: '', keyword: '' }
  pagination.value.page = 0
  loadProducts()
}

// ================= INIT =================
onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
/* Modal form sản phẩm */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}

.modal-content-custom {
  background-color: #fff;
  border-radius: 6px;
  width: 600px;
  max-width: 95vw;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 0 12px rgb(0 0 0 / 0.3);
  display: flex;
  flex-direction: column;
}

.modal-header-custom,
.modal-footer-custom {
  padding: 12px 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-footer-custom {
  border-top: none;
  border-bottom: none;
  margin-top: auto;
}

.modal-title-custom {
  margin: 0;
  font-weight: 600;
  font-size: 1.25rem;
}

.btn-close-custom {
  font-size: 1.5rem;
  line-height: 1;
  background: none;
  border: none;
  cursor: pointer;
  color: #000;
  font-weight: 700;
  padding: 0;
}

.modal-body-custom {
  padding: 15px 20px;
  overflow-y: auto;
}

/* Popup ảnh */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  /* cao hơn modal */
}

.popup-box-medium {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 700px;
  max-height: 80vh;
  overflow-y: auto;
  width: 100%;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
}

.position-relative {
  position: relative;
}
</style>
