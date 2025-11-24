<template>
  <div class="container my-5">
    <div class="card shadow">
      <div class="card-body">
        <h4 class="card-title mb-4">Đăng ký làm đại lý</h4>

        <form @submit.prevent="submit">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">Tên đại lý</label>
              <input
                v-model.trim="form.agentName"
                class="form-control"
                :class="{ 'is-invalid': errors.agentName }"
              />
              <div class="invalid-feedback">{{ errors.agentName }}</div>
            </div>

            <div class="col-md-6">
              <label class="form-label">Số điện thoại</label>
              <input
                v-model.trim="form.phone"
                class="form-control"
                :class="{ 'is-invalid': errors.phone }"
              />
              <div class="invalid-feedback">{{ errors.phone }}</div>
            </div>

            <div class="col-md-6">
              <label class="form-label">Người nhận hàng</label>
              <input
                v-model.trim="form.receiverName"
                class="form-control"
                :class="{ 'is-invalid': errors.receiverName }"
              />
              <div class="invalid-feedback">{{ errors.receiverName }}</div>
            </div>

            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input
                v-model.trim="form.email"
                type="email"
                class="form-control"
                :class="{ 'is-invalid': errors.email }"
              />
              <div class="invalid-feedback">{{ errors.email }}</div>
            </div>

            <div class="col-md-4">
              <label class="form-label">Tỉnh/Thành</label>
              <select
                class="form-select"
                v-model="selectedProvince"
                @change="onProvinceChange"
                :class="{ 'is-invalid': errors.provinceId }"
              >
                <option disabled value="">Chọn tỉnh</option>
                <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
              </select>
              <div class="invalid-feedback">{{ errors.provinceId }}</div>
            </div>

            <div class="col-md-4">
              <label class="form-label">Quận/Huyện</label>
              <select
                class="form-select"
                v-model="selectedDistrict"
                @change="onDistrictChange"
                :class="{ 'is-invalid': errors.districtId }"
              >
                <option disabled value="">Chọn huyện</option>
                <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
              </select>
              <div class="invalid-feedback">{{ errors.districtId }}</div>
            </div>

            <div class="col-md-4">
              <label class="form-label">Phường/Xã</label>
              <select
                class="form-select"
                v-model="selectedWard"
                :class="{ 'is-invalid': errors.wardId }"
              >
                <option disabled value="">Chọn xã</option>
                <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
              </select>
              <div class="invalid-feedback">{{ errors.wardId }}</div>
            </div>

            <div class="col-12">
              <label class="form-label">Địa chỉ cụ thể</label>
              <input
                v-model.trim="form.addressDetail"
                class="form-control"
                :class="{ 'is-invalid': errors.addressDetail }"
              />
              <div class="invalid-feedback">{{ errors.addressDetail }}</div>
            </div>
<div class="col-12">
  <input v-model.trim="form.otpCode" type="hidden" />
</div>



            <div class="col-12 text-end">
              <button class="btn btn-success" type="submit" :disabled="isSubmitting || isSending">
                <span v-if="isSubmitting">Đang gửi...</span>
                <span v-else>Đăng ký đại lý</span>
              </button>
            </div>
          </div>
        </form>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/axios'
import Swal from 'sweetalert2'

const form = ref({
  agentName: '',
  phone: '',
  receiverName: '',
  email: '',
  addressDetail: '',
  otpCode: '',
  provinceId: null,
  provinceName: '',
  districtId: null,
  districtName: '',
  wardId: null,
  wardName: ''
})

const errors = ref({
  agentName: '',
  phone: '',
  receiverName: '',
  email: '',
  addressDetail: '',
  provinceId: '',
  districtId: '',
  wardId: ''
})

const clearErrors = () => {
  for (const key in errors.value) errors.value[key] = ''
}

const provinces = ref([])
const districts = ref([])
const wards = ref([])
const selectedProvince = ref('')
const selectedDistrict = ref('')
const selectedWard = ref('')

const isSubmitting = ref(false)
const isSending = ref(false)

const fetchProvinces = async () => {
  const res = await api.get('/api/locations/provinces')
  provinces.value = res.data.map(p => ({ code: p.ProvinceID, name: p.ProvinceName }))
}

const onProvinceChange = async () => {
  const selected = provinces.value.find(p => p.code === selectedProvince.value)
  form.value.provinceId = selected?.code || null
  form.value.provinceName = selected?.name || ''
  form.value.districtId = null
  form.value.districtName = ''
  form.value.wardId = null
  form.value.wardName = ''
  selectedDistrict.value = ''
  selectedWard.value = ''
  wards.value = []
  const res = await api.get(`/api/locations/districts/${selectedProvince.value}`)
  districts.value = res.data.map(d => ({ code: d.DistrictID, name: d.DistrictName }))
}

const onDistrictChange = async () => {
  const selected = districts.value.find(d => d.code === selectedDistrict.value)
  form.value.districtId = selected?.code || null
  form.value.districtName = selected?.name || ''
  form.value.wardId = null
  form.value.wardName = ''
  selectedWard.value = ''
  const res = await api.get(`/api/locations/wards/${selectedDistrict.value}`)
  wards.value = res.data.map(w => ({ code: w.WardCode, name: w.WardName }))
}

const resetForm = () => {
  form.value = {
    agentName: '',
    phone: '',
    receiverName: '',
    email: '',
    addressDetail: '',
    otpCode: '',
    provinceId: null,
    provinceName: '',
    districtId: null,
    districtName: '',
    wardId: null,
    wardName: ''
  }
  selectedProvince.value = ''
  selectedDistrict.value = ''
  selectedWard.value = ''
  districts.value = []
  wards.value = []
  clearErrors()
}

const submit = async () => {
  // ✅ Gán đầy đủ ward trước khi gửi
  const selectedProvinceObj = provinces.value.find(p => p.code === selectedProvince.value)
  form.value.provinceId = selectedProvinceObj?.code || null
  form.value.provinceName = selectedProvinceObj?.name || ''

  const selectedDistrictObj = districts.value.find(d => d.code === selectedDistrict.value)
  form.value.districtId = selectedDistrictObj?.code || null
  form.value.districtName = selectedDistrictObj?.name || ''

  const selectedWardObj = wards.value.find(w => w.code === selectedWard.value)
  form.value.wardId = selectedWardObj?.code || null
  form.value.wardName = selectedWardObj?.name || ''

  clearErrors()
  let hasError = false

  if (!form.value.agentName) {
    errors.value.agentName = 'Tên đại lý không được để trống'
    hasError = true
  }

  if (!/^0[0-9]{9}$/.test(form.value.phone)) {
    errors.value.phone = 'Số điện thoại không hợp lệ (bắt đầu bằng 0 và đủ 10 số)'
    hasError = true
  }

  if (!form.value.receiverName) {
    errors.value.receiverName = 'Người nhận không được để trống'
    hasError = true
  }

  if (!form.value.email) {
    errors.value.email = 'Email không được để trống'
    hasError = true
  }

  if (!form.value.addressDetail) {
    errors.value.addressDetail = 'Địa chỉ cụ thể không được để trống'
    hasError = true
  }

  if (!form.value.provinceId) {
    errors.value.provinceId = 'Vui lòng chọn tỉnh/thành'
    hasError = true
  }

  if (!form.value.districtId) {
    errors.value.districtId = 'Vui lòng chọn quận/huyện'
    hasError = true
  }

  if (!form.value.wardId) {
    errors.value.wardId = 'Vui lòng chọn phường/xã'
    hasError = true
  }

  if (hasError) {
    console.warn('⚠️ Form validation failed:', errors.value)
    return
  }

  try {
    console.log('📨 Gửi yêu cầu gửi OTP...')
    isSending.value = true
    await api.post('/api/otp/send')
    isSending.value = false
    console.log('✅ Đã gửi OTP')

    const { value: userOtp } = await Swal.fire({
      title: 'Nhập mã OTP',
      input: 'text',
      inputLabel: 'Mã OTP đã gửi đến email đại lý của bạn',
      inputPlaceholder: 'Nhập mã OTP',
      showCancelButton: true,
      confirmButtonText: 'Xác nhận'
    })

    if (!userOtp) {
      console.warn('🚫 Người dùng không nhập mã OTP.')
      return Swal.fire('Hủy', 'Bạn chưa nhập mã OTP.', 'info')
    }

    form.value.otpCode = userOtp
    console.log('🔐 Xác minh OTP:', userOtp)
    await api.post('/api/otp/verify', null, { params: { otpCode: userOtp } })
    console.log('✅ OTP hợp lệ')

    isSubmitting.value = true
    console.log('📦 Dữ liệu gửi lên:', form.value)

    await api.post('/api/agents/register', form.value)
    console.log('✅ Đăng ký thành công')

    Swal.fire('Thành công', '✅ Đăng ký thành công. Vui lòng chờ xét duyệt.', 'success')
    resetForm()
  } catch (err) {
    console.error('❌ Đăng ký thất bại:', err)
    Swal.fire('Lỗi', err.response?.data?.message || '❌ Đăng ký thất bại.', 'error')
  } finally {
    isSending.value = false
    isSubmitting.value = false
  }
}



onMounted(fetchProvinces)
</script>

