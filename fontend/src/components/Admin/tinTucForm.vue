<template>
      <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">{{ isEdit ? 'Cập nhật tin tức' : 'Thêm tin tức' }}</h2>
    </div>

    <div class="card">
      <div class="card-body p-4">
        <form @submit.prevent="submitForm" enctype="multipart/form-data" class="add-discount-form">
          <div class="mb-3">
            <label class="form-label">Tiêu đề <span class="text-danger">*</span></label>
            <input
              v-model="form.title"
              type="text"
              class="form-control"
              placeholder="Nhập tiêu đề tin tức"
              :class="{ 'is-invalid': errors.title }"
            />
            <p class="text-danger" v-if="errors.title">{{ errors.title }}</p>
          </div>

          <div class="mb-3">
            <label class="form-label">Mô tả <span class="text-danger">*</span></label>
            <Editor
              v-model="form.content"
              api-key="adyllgpu90ybl4d19n01s7a585o3es1t1qpzejj0plw8wgcr" :init="{
                plugins: 'advlist autolink lists link image charmap preview anchor searchreplace visualblocks code fullscreen insertdatetime media table help wordcount',
                toolbar: 'undo redo | formatselect | bold italic backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat | help',
                height: 300,
                menubar: false,
                statusbar: false,
                paste_data_images: true, // Cho phép dán ảnh trực tiếp
              }"
              :class="{ 'is-invalid': errors.content }"
            />
            <p class="text-danger" v-if="errors.content">{{ errors.content }}</p>
          </div>

          <div class="mb-3">
            <label class="form-label">Hình ảnh</label>
            <input
              type="file"
              @change="handleImageUpload"
              class="form-control"
              accept="image/*"
            />
            <p class="text-danger" v-if="errors.image">{{ errors.image }}</p>
            <img
              v-if="previewImage"
              :src="previewImage"
              alt="Preview"
              style="max-width: 200px; margin-top: 10px;"
            />
          </div>

          <div class="mb-3">
            <label class="form-label">Ngày đăng <span class="text-danger">*</span></label>
            <input
              v-model="form.createdAt"
              type="datetime-local"
              class="form-control" readonly
              :class="{ 'is-invalid': errors.createdAt }"
            />
            <p class="text-danger" v-if="errors.createdAt">{{ errors.createdAt }}</p>
          </div>

          <div class="mb-3 form-check">
            <input v-model="form.isVisible" type="checkbox" class="form-check-input" id="isVisible" />
            <label class="form-check-label" for="isVisible">Hiển thị</label>
          </div>
          <div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-warning me-2">
              <i class="bi bi-pencil-square"></i> {{ isEdit ? 'Cập nhật' : 'Lưu' }}
            </button>
            <router-link to="/quanLyTinTuc" class="btn btn-danger">
              <i class="bi bi-x-circle"></i> Hủy
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/axios' // ✅ dùng axios đã có JWT
import { useRouter, useRoute } from 'vue-router'
import Swal from 'sweetalert2'
import Editor from '@tinymce/tinymce-vue'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const router = useRouter()
const route = useRoute()

const form = ref({
  newsId: null,
  title: '',
  content: '',
  image: null,
  createdAt: new Date().toISOString().slice(0, 16),
  isVisible: true,
})

const imageFile = ref(null)
const errors = ref({
  title: '',
  content: '',
  image: '',
  createdAt: '',
})

const previewImage = ref(null)
const isEdit = ref(false)

const getImageUrl = (imagePath) => {
  if (!imagePath) return '/images/fallback-image.jpg'
  return imagePath.startsWith('http') ? imagePath : `${API_URL}/images/${imagePath.replace('/images/', '')}`
}

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (!file.type.startsWith('image/')) {
      errors.value.image = 'Chỉ chấp nhận file hình ảnh!'
      imageFile.value = null
      previewImage.value = null
    } else if (file.size > 5 * 1024 * 1024) {
      errors.value.image = 'Kích thước file không được vượt quá 5MB!'
      imageFile.value = null
      previewImage.value = null
    } else {
      errors.value.image = ''
      imageFile.value = file
      previewImage.value = URL.createObjectURL(file)
    }
  } else {
    imageFile.value = null
    previewImage.value = isEdit.value && form.value.image ? getImageUrl(form.value.image) : null
  }
}

const validateForm = () => {
  errors.value = { title: '', content: '', image: '', createdAt: '' }
  let isValid = true

  const trimmedTitle = form.value.title.trim()
  const trimmedContent = form.value.content.trim()

  if (!trimmedTitle) {
    errors.value.title = 'Tiêu đề không được để trống!'
    isValid = false
  } else if (trimmedTitle.split(/\s+/).length > 250) {
    errors.value.title = 'Tiêu đề không được vượt quá 250 kí tự!'
    isValid = false
  }

  if (!trimmedContent || trimmedContent === '<p></p>') {
    errors.value.content = 'Mô tả không được để trống!'
    isValid = false
  }

  if (!form.value.createdAt) {
    errors.value.createdAt = 'Ngày đăng là bắt buộc!'
    isValid = false
  }

  if (!isEdit.value && !imageFile.value) {
    errors.value.image = 'Vui lòng chọn hình ảnh!'
    isValid = false
  }

  return isValid
}

const submitForm = async () => {
  if (!validateForm()) return

  const formData = new FormData()
  formData.append('title', form.value.title.trim())
  formData.append('content', form.value.content.trim())
  formData.append('createdAt', form.value.createdAt)
  formData.append('isVisible', String(form.value.isVisible))

  if (imageFile.value) {
    formData.append('image', imageFile.value)
  } else if (isEdit.value && form.value.image) {
    formData.append('imagePath', form.value.image)
  }

  try {
    let response
    if (isEdit.value) {
      response = await api.put(`/api/news/${form.value.newsId}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 10000,
      })
      await Swal.fire({
        title: 'Thành công!',
        text: 'Cập nhật tin tức thành công!',
        icon: 'success',
        confirmButtonText: 'OK',
        timer: 2000,
        timerProgressBar: true,
      })
    } else {
      response = await api.post('/api/news', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 10000,
      })
      await Swal.fire({
        title: 'Thành công!',
        text: 'Thêm tin tức thành công!',
        icon: 'success',
        confirmButtonText: 'OK',
        timer: 2000,
        timerProgressBar: true,
      })
    }

    router.push({
      path: '/quanLyTinTuc',
      state: { newNews: response.data.data },
    })
  } catch (error) {
    await Swal.fire({
      title: 'Lỗi!',
      text: `Không thể ${isEdit.value ? 'cập nhật' : 'thêm'} tin tức. Chi tiết: ${error.response?.data?.error || error.message}`,
      icon: 'error',
      confirmButtonText: 'OK',
    })
  }
}

const loadNewsData = async (id) => {
  try {
    const response = await api.get(`/api/news/${id}`, { timeout: 10000 })
    if (response.data.error) throw new Error(response.data.error)

    const news = response.data
    form.value = {
      newsId: news.newsId,
      title: news.title || '',
      content: news.content || '',
      image: news.image || null,
      createdAt: new Date(news.createdAt).toISOString().slice(0, 16),
      isVisible: news.isVisible !== false,
    }
    isEdit.value = true
    previewImage.value = news.image ? getImageUrl(news.image) : null
  } catch (err) {
    await Swal.fire({
      title: 'Lỗi!',
      text: `Không thể tải tin tức: ${err.response?.data?.error || err.message}`,
      icon: 'error',
      confirmButtonText: 'OK',
    })
  }
}

onMounted(() => {
  const id = route.query.id
  if (id) {
    loadNewsData(id)
  }
})
</script>


<style scoped>
/* Giữ nguyên các style hiện có */
.page-header {
  padding: 1rem 1.5rem;
}

.btn-icon {
  background: none;
  border: none;
  color: #495057;
  font-size: 1.1rem;
  padding: 0.25rem 0.5rem;
}

.btn-icon:hover {
  color: #212529;
}

.header-right-icons .badge {
  font-size: 0.65em;
  padding: 0.25em 0.4em;
  background-color: #17a2b8 !important;
}

.page-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: #343a40;
}

.breadcrumb {
  font-size: 0.85rem;
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

.container {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.card-header {
  padding: 1rem 1.25rem;
  min-height: 48px;
}

.card-header h5 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #343a40;
}

.card-body {
  padding: 1.5rem;
}

.btn {
  border: none;
  color: rgb(61, 58, 58);
  padding: 0.35rem 0.8rem;
  font-weight: 500;
  transition: opacity 0.2s ease;
  cursor: pointer;
}

.btn:hover {
  opacity: 0.9;
  color: rgb(61, 58, 58);
}

.btn-gradient-approve {
  background-image: linear-gradient(to right, #efc69a, #f3b275);
}

.btn-gradient-reject {
  background-image: linear-gradient(to right, #fa8b8b, #ef5951);
}

.btn-sm {
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
}

.text-danger {
  font-size: 0.875rem;
}

.text-muted {
  color: #6c757d !important;
}

.ms-2 {
  margin-left: 0.5rem !important;
}

.me-1 {
  margin-right: 0.25rem !important;
}

.me-2 {
  margin-right: 0.5rem !important;
}

.p-4 {
  padding: 1.5rem !important;
}

.btn i {
  vertical-align: middle;
  line-height: 1;
}

.d-flex { display: flex !important; }
.justify-content-end { justify-content: flex-end !important; }
.mb-4 { margin-bottom: 1.5rem !important; }
.mb-3 { margin-bottom: 1rem !important; }
.mb-1 { margin-bottom: 0.25rem !important; }
.p-0 { padding: 0 !important; }
.m-0 { margin: 0 !important; }
.bg-transparent { background-color: transparent !important; }

.add-discount-form {
  background-color: #ffffff;
}

.add-discount-form .form-label {
  font-weight: 500;
  color: #495057;
}

.add-discount-form .form-control {
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
}

.add-discount-form .form-control:focus {
  box-shadow: none;
  border-color: #007bff;
}
</style>