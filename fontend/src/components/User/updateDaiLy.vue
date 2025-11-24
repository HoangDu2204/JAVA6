<template>
  <div class="container my-5">
    <h4 class="mb-3">Thông tin đại lý của bạn</h4>

    <table v-if="agent" class="table table-bordered">
      <tbody>
        <tr>
          <th>Tên đại lý</th>
          <td>{{ agent.agentName }}</td>
        </tr>
        <tr>
          <th>Người nhận</th>
          <td>{{ agent.receiverName }}</td>
        </tr>
        <tr>
          <th>Điện thoại</th>
          <td>{{ agent.phone }}</td>
        </tr>
        <tr>
          <th>Email</th>
          <td>{{ agent.email }}</td>
        </tr>
        <tr>
          <th>Địa chỉ</th>
          <td>{{ agent.addressDetail }}, {{ agent.wardName }}, {{ agent.districtName }}, {{ agent.provinceName }}</td>
        </tr>
      </tbody>
    </table>

    <button class="btn btn-warning" @click="openModal">Cập nhật</button>

    <!-- Modal Tự Làm -->
    <div v-if="modalVisible" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-content-custom">
        <form @submit.prevent="submit">
          <div class="modal-header-custom">
            <h5 class="modal-title-custom">Cập nhật đại lý</h5>
            <button type="button" class="btn-close-custom" @click="closeModal">&times;</button>
          </div>

          <div class="modal-body-custom row g-3">
            <div class="col-md-6">
              <label class="form-label">Tên đại lý</label>
              <input v-model="form.agentName" class="form-control" required />
            </div>
            <div class="col-md-6">
              <label class="form-label">Số điện thoại</label>
              <input v-model="form.phone" class="form-control" required />
              <div v-if="errors.phone" class="text-danger small mt-1">{{ errors.phone }}</div>
            </div>

            <div class="col-md-6">
              <label class="form-label">Người nhận</label>
              <input v-model="form.receiverName" class="form-control" required />
            </div>
            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input v-model="form.email" type="email" class="form-control" required />
              <div v-if="errors.email" class="text-danger small mt-1">{{ errors.email }}</div>
            </div>


            <div class="col-md-4">
              <label class="form-label">Tỉnh/Thành</label>
              <select v-model="selectedProvince" @change="onProvinceChange" class="form-select">
                <option value="">Chọn tỉnh</option>
                <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Quận/Huyện</label>
              <select v-model="selectedDistrict" @change="onDistrictChange" class="form-select">
                <option value="">Chọn huyện</option>
                <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Phường/Xã</label>
              <select v-model="selectedWard" class="form-select">
                <option value="">Chọn xã</option>
                <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
              </select>
            </div>
            <div class="col-12">
              <label class="form-label">Địa chỉ chi tiết</label>
              <input v-model="form.addressDetail" class="form-control" required />
            </div>
          </div>

          <div class="modal-footer-custom">
            <button type="button" class="btn btn-secondary" @click="closeModal">Đóng</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">Cập nhật</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Swal from 'sweetalert2'
import api from '@/axios'

const agent = ref(null)
const form = ref({
  agentName: '',
  phone: '',
  receiverName: '',
  email: '',
  addressDetail: '',
  provinceId: null,
  provinceName: '',
  districtId: null,
  districtName: '',
  wardId: null,
  wardName: ''
})
const errors = ref({
  email: '',
  phone: ''
})
const provinces = ref([])
const districts = ref([])
const wards = ref([])

const selectedProvince = ref('')
const selectedDistrict = ref('')
const selectedWard = ref('')

const modalVisible = ref(false)
const loading = ref(false)

// Fetch danh sách tỉnh
const fetchProvinces = async () => {
  try {
    const res = await api.get('/api/locations/provinces')
    provinces.value = res.data.map(p => ({
      code: String(p.ProvinceID),
      name: p.ProvinceName
    }))
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tải danh sách tỉnh', 'error')
  }
}

// Khi chọn tỉnh
const onProvinceChange = async (fromSelect = true) => {
  const p = provinces.value.find(p => p.code === selectedProvince.value)
  form.value.provinceId = p?.code || null
  form.value.provinceName = p?.name || ''

  if (fromSelect) {
    selectedDistrict.value = ''
    selectedWard.value = ''
    form.value.districtId = null
    form.value.districtName = ''
    form.value.wardId = null
    form.value.wardName = ''
  }

  districts.value = []
  wards.value = []

  if (!selectedProvince.value) return

  try {
    const res = await api.get(`/api/locations/districts/${selectedProvince.value}`)
    districts.value = res.data.map(d => ({
      code: String(d.DistrictID),
      name: d.DistrictName
    }))
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tải quận/huyện', 'error')
  }
}

// Khi chọn huyện
const onDistrictChange = async () => {
  const d = districts.value.find(d => d.code === selectedDistrict.value)
  form.value.districtId = d?.code || null
  form.value.districtName = d?.name || ''
  selectedWard.value = ''
  wards.value = []

  if (!selectedDistrict.value) return

  try {
    const res = await api.get(`/api/locations/wards/${selectedDistrict.value}`)
    wards.value = res.data.map(w => ({
      code: String(w.WardCode),
      name: w.WardName
    }))

    // Nếu wardId đã tồn tại thì gán lại
    if (form.value.wardId) {
      const existed = wards.value.find(w => w.code === String(form.value.wardId))
      if (existed) {
        selectedWard.value = existed.code
      }
    }
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tải xã/phường', 'error')
  }
}

// Lấy thông tin đại lý hiện tại
const fetchAgent = async () => {
  try {
    const res = await api.get('/api/agents/update/me')
    agent.value = res.data
    form.value = { ...res.data }

    selectedProvince.value = String(res.data.provinceId || '')
    selectedDistrict.value = String(res.data.districtId || '')
    selectedWard.value = String(res.data.wardId || '')

    if (selectedProvince.value) {
      await onProvinceChange(false)
      if (selectedDistrict.value) {
        await onDistrictChange()
      }
    }
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tải thông tin đại lý', 'error')
  }
}

// Mở modal cập nhật
const openModal = async () => {
  modalVisible.value = true
  if (!provinces.value.length) {
    await fetchProvinces()
  }
  await fetchAgent()
}

// Đóng modal
const closeModal = () => {
  modalVisible.value = false
}

// Submit cập nhật đại lý
// const submit = async () => {
//   try {
//     if (!wards.value.length && selectedDistrict.value) {
//       await onDistrictChange()
//     }

//     const ward = wards.value.find(w => w.code === selectedWard.value)
//     form.value.wardId = ward?.code || null
//     form.value.wardName = ward?.name || ''

//     if (!form.value.wardId) {
//       console.warn('⚠️ wardId null - có thể gây lỗi 500!')
//     }

//     // 🟨 Nếu email thay đổi -> gửi OTP xác minh
//     if (form.value.email !== agent.value.email) {
//       loading.value = true
//       await api.post('/api/otp/send', { email: form.value.email })
//       loading.value = false
//       console.log('✅ Đã gửi OTP đến:', form.value.email)

//       const { value: userOtp } = await Swal.fire({
//         title: 'Nhập mã OTP',
//         input: 'text',
//         inputLabel: 'Mã OTP đã gửi đến email mới',
//         inputPlaceholder: 'Nhập mã OTP',
//         showCancelButton: true,
//         confirmButtonText: 'Xác nhận'
//       })

//       if (!userOtp) {
//         console.warn('🚫 Người dùng không nhập mã OTP.')
//         return Swal.fire('Hủy', 'Bạn chưa nhập mã OTP.', 'info')
//       }

//       await api.post('/api/otp/verify', null, {
//         params: { otpCode: userOtp }
//       })
//       console.log('✅ OTP hợp lệ, tiến hành cập nhật')
//     }

//     // ✅ Cập nhật thông tin đại lý
//     loading.value = true
//     await api.put('/api/agents/update', form.value)
//     Swal.fire('✅ Thành công', 'Cập nhật thông tin đại lý thành công', 'success')
//     await fetchAgent()
//     closeModal()
//   } catch (err) {
//     loading.value = false
//     console.error(err)
//     Swal.fire('❌ Lỗi', err.response?.data?.message || 'Không thể cập nhật đại lý', 'error')
//   } finally {
//     loading.value = false
//   }
// }
const submit = async () => {
  try {
    // Reset lỗi
    errors.value.email = ''
    errors.value.phone = ''

    // Kiểm tra email
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailPattern.test(form.value.email)) {
      errors.value.email = 'Email không hợp lệ'
    }

    // Kiểm tra số điện thoại (10 số, bắt đầu bằng 0)
    const phonePattern = /^0\d{9}$/
    if (!phonePattern.test(form.value.phone)) {
      errors.value.phone = 'Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0'
    }

    // Nếu có lỗi -> dừng lại
    if (errors.value.email || errors.value.phone) {
      return
    }

    // Load lại xã nếu cần
    if (!wards.value.length && selectedDistrict.value) {
      await onDistrictChange()
    }

    const ward = wards.value.find(w => w.code === selectedWard.value)
    form.value.wardId = ward?.code || null
    form.value.wardName = ward?.name || ''

    if (!form.value.wardId) {
      console.warn('⚠️ wardId null - có thể gây lỗi 500!')
    }

    // Nếu email thay đổi thì gửi OTP
    if (form.value.email !== agent.value.email) {
      loading.value = true
      await api.post('/api/otp/send', { email: form.value.email })
      loading.value = false

      const { value: userOtp } = await Swal.fire({
        title: 'Nhập mã OTP',
        input: 'text',
        inputLabel: 'Mã OTP đã gửi đến email mới',
        inputPlaceholder: 'Nhập mã OTP',
        showCancelButton: true,
        confirmButtonText: 'Xác nhận'
      })

      if (!userOtp) {
        return Swal.fire('Hủy', 'Bạn chưa nhập mã OTP.', 'info')
      }

      await api.post('/api/otp/verify', null, {
        params: { otpCode: userOtp }
      })
    }

    loading.value = true
    await api.put('/api/agents/update', form.value)
    Swal.fire('✅ Thành công', 'Cập nhật thông tin đại lý thành công', 'success')
    await fetchAgent()
    closeModal()
  } catch (err) {
    console.error(err)
    Swal.fire('❌ Lỗi', err.response?.data?.message || 'Không thể cập nhật đại lý', 'error')
  } finally {
    loading.value = false
  }
}

// Gọi khi trang tải
onMounted(async () => {
  await fetchProvinces()
  await fetchAgent()
})
</script>









<style scoped>
.container {
  max-width: 900px;
}

/* Modal styles */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}

.modal-content-custom {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 1.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.modal-header-custom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.modal-title-custom {
  margin: 0;
  font-size: 1.25rem;
  font-weight: bold;
}

.btn-close-custom {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body-custom {
  padding-bottom: 1rem;
}

.modal-footer-custom {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding-top: 1rem;
  border-top: 1px solid #ddd;
}
</style>
