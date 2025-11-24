<template>
  <div class="container py-5">
  <div class="col-xl-12 p-4">
    <h1 class="fs-4 fw-bold mb-4">Quản lý người dùng</h1>

    <div class="card shadow-sm rounded-4">
      <div class="card-body p-4">
        <h5 class="mb-4 fw-bold text-primary">
          <i class="bi bi-person-plus-fill me-2"></i>{{ isEditMode ? 'Cập nhật trạng thái người dùng' : 'Thêm người dùng' }}
        </h5>

        <form @submit.prevent="submitForm">
          <div class="mb-3">
            <label class="form-label fw-semibold" for="username">Tên tài khoản</label>
            <input
              type="text"
              id="username"
              v-model="form.username"
              class="form-control"
              placeholder="Nhập tên tài khoản"
              :disabled="isEditMode"
              required
            />
          </div>

          <div class="mb-3">
            <label class="form-label fw-semibold" for="email">Email</label>
            <input
              type="email"
              id="email"
              v-model="form.email"
              class="form-control"
              placeholder="Nhập email"
              :disabled="isEditMode"
              required
            />
          </div>

          <div class="mb-3">
            <label class="form-label fw-semibold" for="fullName">Họ tên</label>
            <input
              type="text"
              id="fullName"
              v-model="form.fullName"
              class="form-control"
              placeholder="Nhập họ tên"
              :disabled="isEditMode"
              required
            />
          </div>

          <div class="mb-3">
            <label class="form-label fw-semibold" for="phone">Số điện thoại</label>
            <input
              type="text"
              id="phone"
              v-model="form.phone"
              class="form-control"
              placeholder="Nhập số điện thoại"
              :disabled="isEditMode"
              required
            />
          </div>

          <div class="mb-3">
            <label class="form-label fw-semibold" for="role">Vai trò</label>
            <select class="form-select" id="role" v-model="form.role" :disabled="isEditMode" required>
              <option value="1">Admin</option>
              <option value="2">User</option>
              <option value="3">Staff</option>
            </select>
          </div>

          <!-- Chỉ hiện khi chỉnh sửa -->
          <div class="mb-4" v-if="isEditMode">
            <label class="form-label fw-semibold" for="isActive">Trạng thái hoạt động</label>
            <select class="form-select" id="isActive" v-model="form.isActive">
              <option :value="true">Hoạt động</option>
              <option :value="false">Không hoạt động</option>
            </select>
          </div>

          <!-- Mật khẩu chỉ hiện khi thêm mới -->
          <div class="mb-3" v-if="!isEditMode">
            <label class="form-label fw-semibold" for="password">Mật khẩu</label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              class="form-control"
              placeholder="Nhập mật khẩu"
              required
            />
          </div>

          <div class="d-flex justify-content-end gap-2">
            <button type="submit" class="btn btn-warning px-4">
              <i :class="isEditMode ? 'bi bi-pencil-square' : 'bi bi-plus-lg'"></i>
              {{ isEditMode ? 'Cập nhật' : 'Thêm' }}
            </button>

            <RouterLink to="/quanLyNguoiDung" class="btn btn-danger px-4">
              <i class="bi bi-x-circle"></i> Hủy
            </RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios' // ✅ import api thay vì axios

const router = useRouter()
const route = useRoute()

const isEditMode = route.params.id !== undefined

const form = ref({
  username: '',
  email: '',
  password: '',
  fullName: '',
  phone: '',
  role: 1,
  isActive: true,
  createdAt: '',
})

const fetchUser = async (id) => {
  try {
    const response = await api.get(`/api/user/${id}`)
    const data = response.data
    form.value = {
      username: data.username,
      email: data.email,
      fullName: data.fullName,
      phone: data.phone,
      role: data.role,
      isActive: data.isActive,
      createdAt: data.createdAt,
      password: data.password || '',
    }
  } catch (err) {
    console.error('Không tìm thấy người dùng:', err)
    alert('Người dùng không tồn tại.')
    router.push('/quanLyNguoiDung')
  }
}

const submitForm = async () => {
  try {
    if (isEditMode) {
      const originalResponse = await api.get(`/api/user/${route.params.id}`)
      const original = originalResponse.data

      const fieldsToCheck = ['username', 'email', 'phone', 'fullName', 'role', 'password']
      const hasInvalidChange = fieldsToCheck.some(field =>
        String(form.value[field] ?? '') !== String(original[field] ?? '')
      )

      if (hasInvalidChange) {
        console.warn('Dữ liệu khác nhau:', fieldsToCheck.map(field => ({
          field,
          form: form.value[field],
          original: original[field]
        })))

        await Swal.fire({
          icon: 'warning',
          title: 'Không được chỉnh sửa thông tin khác ngoài trạng thái hoạt động!',
        })
        return
      }

      const payload = {
        isActive: form.value.isActive === true || form.value.isActive === 'true',
      }

      await api.put(`/api/user/${route.params.id}`, payload, {
        headers: { 'Content-Type': 'application/json' }
      })

      await Swal.fire({
        icon: 'success',
        title: 'Thành công',
        text: 'Cập nhật trạng thái người dùng thành công!',
      })
    } else {
      const payload = {
        username: form.value.username,
        email: form.value.email,
        fullName: form.value.fullName,
        phone: form.value.phone,
        role: form.value.role,
        isActive: true,
        password: form.value.password,
      }

      await api.post('/api/user', payload, {
        headers: { 'Content-Type': 'application/json' }
      })

      await Swal.fire({
        icon: 'success',
        title: 'Thành công',
        text: 'Thêm người dùng thành công!',
      })
    }

    router.push('/quanLyNguoiDung')
  } catch (err) {
    console.error('Lỗi khi gửi dữ liệu người dùng:', err.response?.data || err)
    await Swal.fire({
      icon: 'error',
      title: 'Lỗi',
      text: isEditMode ? 'Cập nhật thất bại!' : 'Thêm thất bại!',
    })
  }
}

onMounted(() => {
  if (isEditMode) {
    fetchUser(route.params.id)
  }
})
</script>



<style scoped>
/* Giữ nguyên các style từ trước */
</style>
