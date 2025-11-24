
<template>
  <section class="content">
    <h2>Địa chỉ của bạn</h2>
    <div class="form-group">
      <button @click="openCreateForm" class="submit-btn">Thêm địa chỉ</button>
    </div>

    <div v-if="addresses.length" class="address-list">
      <div
        class="address-item border rounded p-3 mb-3"
        v-for="(addr, index) in addresses"
        :key="addr.id"
        style="width: 1050px;"
      >
        <p><strong>Họ tên:</strong> {{ addr.name }}</p>
        <div class="d-flex justify-content-between w-100">
          <p class="mb-0">
            <strong>Địa chỉ:</strong>
            {{ addr.address }}, {{ addr.commune }}, {{ addr.district }}, {{ addr.province }}
          </p>
          <div class="text-end" style="min-width: 160px;">
            <a href="#" @click.prevent="editAddress(addr)" class="text-primary">Chỉnh sửa</a>
            <span v-if="addresses.length > 1">
              | <a href="#" @click.prevent="deleteAddress(addr.id)" class="text-danger">Xoá</a>
            </span>
          </div>
        </div>
        <p><strong>Số điện thoại:</strong> {{ addr.phone }}</p>
      </div>
    </div>
  </section>

  <div v-if="showForm" class="modal-overlay">
    <div class="modal-box">
      <div class="modal-header">
        <h3>{{ editingAddressId ? 'CẬP NHẬT ĐỊA CHỈ' : 'THÊM ĐỊA CHỈ MỚI' }}</h3>
        <button class="close-btn" @click="closeForm">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="submitForm">
        <label for="">Họ tên:</label>
          <input type="text" v-model="form.name" placeholder="Họ tên" />
            <label for="">Số điện thoại:</label>
          <input type="text" v-model="form.phone" placeholder="Số điện thoại" />
            <label for="">Địa chỉ cụ thể:</label>
          <input type="text" v-model="form.address" placeholder="Địa chỉ cụ thể" />

          <div class="row-select">
            
            <select v-model="selectedProvinceCode" @change="onProvinceChange" required>
              <option disabled value="">Tỉnh thành</option>
              <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
            </select>

            <select v-model="selectedDistrictCode" @change="onDistrictChange" :disabled="!districts.length" required>
              <option disabled value="">Quận huyện</option>
              <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
            </select>

            <select v-model="selectedWardCode" :disabled="!wards.length" required>
              <option disabled value="">Phường xã</option>
              <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
            </select>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn cancel" @click="closeForm">Hủy</button>
            <button type="submit" class="btn submit" :disabled="isSubmitting">
              <span v-if="isSubmitting">Đang gửi...</span>
              <span v-else>{{ editingAddressId ? 'Cập nhật' : 'Thêm địa chỉ' }}</span>
            </button>
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

// Trạng thái
const showForm = ref(false)
const isSubmitting = ref(false)
const isEditing = ref(false)
const editingAddressId = ref(null)
const addresses = ref([])

// Form dữ liệu
const form = ref({
  name: '',
  phone: '',
  address: '',
  province: '',
  district: '',
  commune: '',
  idProvince: null,
  idDistrict: null,
  idCommune: null
})

// Tỉnh, huyện, xã
const provinces = ref([])
const districts = ref([])
const wards = ref([])
const selectedProvinceCode = ref('')
const selectedDistrictCode = ref('')
const selectedWardCode = ref('')

// === API LẤY ĐỊA CHỈ ===
const fetchProvinces = async () => {
  try {
    const res = await api.get('/api/locations/provinces')
    provinces.value = res.data.map(p => ({ code: p.ProvinceID, name: p.ProvinceName }))
  } catch (err) {
    console.error('Lỗi lấy tỉnh:', err)
    provinces.value = []
  }
}

const fetchDistricts = async (provinceId) => {
  try {
    const res = await api.get(`/api/locations/districts/${provinceId}`)
    districts.value = res.data.map(d => ({ code: d.DistrictID, name: d.DistrictName }))
  } catch (err) {
    console.error('Lỗi lấy quận:', err)
    districts.value = []
  }
}

const fetchWards = async (districtId) => {
  try {
    const res = await api.get(`/api/locations/wards/${districtId}`)
    wards.value = res.data.map(w => ({ code: w.WardCode, name: w.WardName }))
  } catch (err) {
    console.error('Lỗi lấy phường:', err)
    wards.value = []
  }
}

// Load danh sách địa chỉ
const loadAddresses = async () => {
  try {
    const res = await api.get('/api/addresses') // ✅ Không cần withCredentials
    addresses.value = res.data
  } catch (err) {
    console.error('Lỗi khi tải danh sách địa chỉ:', err)
  }
}

onMounted(async () => {
  await fetchProvinces()
  await loadAddresses()
})

// Đóng form modal
const closeForm = () => {
  showForm.value = false
  resetForm()
}

// Reset form
const resetForm = () => {
  form.value = {
    name: '',
    phone: '',
    address: '',
    province: '',
    district: '',
    commune: '',
    idProvince: null,
    idDistrict: null,
    idCommune: null
  }
  selectedProvinceCode.value = ''
  selectedDistrictCode.value = ''
  selectedWardCode.value = ''
  districts.value = []
  wards.value = []
  isEditing.value = false
  editingAddressId.value = null
}

// Mở form thêm
const openCreateForm = () => {
  resetForm()
  showForm.value = true
}

// Mở form cập nhật
const editAddress = async (addr) => {
  isEditing.value = true
  editingAddressId.value = addr.id
  form.value = {
    name: addr.name,
    phone: addr.phone,
    address: addr.address,
    province: addr.province,
    district: addr.district,
    commune: addr.commune,
    idProvince: addr.idProvince,
    idDistrict: addr.idDistrict,
    idCommune: addr.idCommune
  }
  selectedProvinceCode.value = addr.idProvince
  selectedDistrictCode.value = addr.idDistrict
  selectedWardCode.value = addr.idCommune

  await fetchDistricts(addr.idProvince)
  await fetchWards(addr.idDistrict)

  showForm.value = true
}

// Chọn tỉnh
const onProvinceChange = async () => {
  const p = provinces.value.find(p => p.code == selectedProvinceCode.value)
  form.value.province = p?.name || ''
  form.value.idProvince = p?.code || null

  selectedDistrictCode.value = ''
  selectedWardCode.value = ''
  form.value.district = ''
  form.value.commune = ''
  form.value.idDistrict = null
  form.value.idCommune = null

  districts.value = []
  wards.value = []

  if (selectedProvinceCode.value) {
    await fetchDistricts(selectedProvinceCode.value)
  }
}

// Chọn huyện
const onDistrictChange = async () => {
  const d = districts.value.find(d => d.code == selectedDistrictCode.value)
  form.value.district = d?.name || ''
  form.value.idDistrict = d?.code || null

  selectedWardCode.value = ''
  form.value.commune = ''
  form.value.idCommune = null

  wards.value = []

  if (selectedDistrictCode.value) {
    await fetchWards(selectedDistrictCode.value)
  }
}

// Gửi form
const submitForm = async () => {
  const w = wards.value.find(w => w.code == selectedWardCode.value)
  form.value.commune = w?.name || ''
  form.value.idCommune = w?.code || null

  if (!form.value.name || form.value.name.length > 100) {
    Swal.fire({ icon: 'warning', title: 'Họ tên không được rỗng và tối đa 100 ký tự!' })
    return
  }
  if (!form.value.phone || !/^\d{10}$/.test(form.value.phone)) {
    Swal.fire({ icon: 'warning', title: 'Số điện thoại phải đúng 10 chữ số!' })
    return
  }
  if (!form.value.address || form.value.address.length > 100) {
    Swal.fire({ icon: 'warning', title: 'Địa chỉ không được rỗng và tối đa 100 ký tự!' })
    return
  }

  try {
    isSubmitting.value = true
    if (isEditing.value) {
      await api.put(`/api/addresses/${editingAddressId.value}`, form.value)
      Swal.fire({ icon: 'success', title: 'Cập nhật địa chỉ thành công!' })
    } else {
      await api.post(`/api/addresses`, form.value)
      Swal.fire({ icon: 'success', title: 'Thêm địa chỉ thành công!' })
    }
    showForm.value = false
    await loadAddresses()
    resetForm()
  } catch (err) {
    console.error(err)
    Swal.fire({ icon: 'error', title: isEditing.value ? 'Cập nhật thất bại!' : 'Thêm thất bại!' })
  } finally {
    isSubmitting.value = false
  }
}

// Xoá địa chỉ
const deleteAddress = async (id) => {
  const confirm = await Swal.fire({
    icon: 'warning',
    title: 'Bạn có chắc muốn xoá địa chỉ này?',
    showCancelButton: true,
    confirmButtonText: 'Xoá',
    cancelButtonText: 'Hủy'
  })

  if (confirm.isConfirmed) {
    try {
      await api.delete(`/api/addresses/${id}`)
      Swal.fire({ icon: 'success', title: 'Xoá thành công!' })
      await loadAddresses()
    } catch (err) {
      console.error(err)
      Swal.fire({ icon: 'error', title: 'Xoá địa chỉ thất bại!' })
    }
  }
}
</script>

<style scoped>
.page-dia-chi {
  font-family: 'Quicksand', sans-serif;
  ;
  background-color: #fff;
  overflow-x: hidden;
}

header.header-transparent {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  background-color: transparent;
  padding: 5px 0;
  transition: background-color 0.3s ease;
}

header.header-transparent .navbar {
  padding: 0;
}

header.header-transparent .navbar-brand img {
  max-height: 60px;
}

header.header-transparent .navbar-nav .nav-link {
  color: #fff;
  font-weight: 500;
  padding: 10px 15px;
  text-transform: none;
  font-size: 18px;
  position: relative;
  background-color: transparent !important;
  border-bottom: none !important;
  border-radius: 0 !important;
}

header.header-transparent .navbar-nav .nav-link.active {
  color: #cd9b32;
  font-weight: 700;
}

header.header-transparent .navbar-nav .nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15px;
  right: 15px;
  height: 2px;
  background-color: #cd9b32;
}

header.header-transparent .navbar-nav .nav-link:hover {
  color: #cd9b32;
}

header.header-transparent .navbar-icons .nav-link {
  color: #fff;
  font-size: 23px;
  padding: 10px 8px;
}

header.header-transparent .navbar-icons .nav-link:hover {
  color: #cd9b32;
}

.navbar-toggler {
  border-color: rgba(255, 255, 255, 0.5);
}

.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%28255, 255, 255, 0.8%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}


.full-width-banner {

  width: 100vw;
  position: relative;
  left: 50%;
  transform: translateX(-50%);


  min-height: 300px;
  background-size: cover;
  background-position: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 0 50px;
  text-align: center;
  margin-top: 0;
  margin-bottom: 0;
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
  font-family: 'Forte', cursive;
}

.full-width-banner .breadcrumb {
  font-size: 18px;
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
  font-size: 14px;
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

.contact-section {
  padding: 40px 0;

}

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
  color: #cd9b32;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
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

.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #cd9b32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-wrapper i {
  color: #fff;
  font-size: 20px;
}

.text-wrapper p {
  margin: 0;
  font-size: 18px;
  color: #555;
  line-height: 1.5;
}

.text-wrapper .label {
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
}

.form-description {
  font-size: 18px;
  color: #555;
  margin-bottom: 20px;
}

.contact-form-box .form-control {
  border-radius: 5px;
  border: 1px solid #ddd;
  padding: 10px 15px;
  font-size: 18px;
}

.contact-form-box .form-control:focus {
  border-color: #cd9b32;
  box-shadow: 0 0 0 0.2rem rgba(205, 155, 50, 0.25);
}

.contact-form-box .btn-submit {
  background-color: #cd9b32;
  color: #fff;
  border: none;
  padding: 10px 25px;
  border-radius: 5px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.contact-form-box .btn-submit:hover {
  background-color: #a88a2a;
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

.navbar-nav-menu {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
  white-space: nowrap;
}

.navbar-nav-menu .nav-item {
  white-space: nowrap;
}

.account-container {
  display: flex;
  padding: 20px;
}

.account-sidebar {
  width: 250px;
}

.account-sidebar .nav-item {
  margin-bottom: 10px;
}

.content {
  flex: 1;
  padding: 0 20px;
}

.submit-btn {
  background-color: #d8a540;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  /* cao cực để đảm bảo nằm trên hết */
}

.modal-box {
  background: white;
  width: 720px;
  border-radius: 6px;
  padding: 24px;
  position: relative;
  z-index: 10000;
}


.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-weight: bold;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.modal-body input,
.modal-body select {
  width: 100%;
  margin-top: 12px;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.label {
  margin-top: 20px;
  font-weight: bold;
  display: block;
}

.row-select {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.row-select select {
  flex: 1;
}


.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.cancel {
  background-color: #f1f1f1;
  border: 1px solid #ccc;
}

.submit {
  background-color: #d8a540;
  color: white;
  border: none;
}




.account-container {
  display: flex;
  gap: 40px;
  padding: 40px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
  box-sizing: border-box;
}


.account-sidebar {
  width: 260px;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.account-sidebar p {
  margin-bottom: 25px;
  font-size: 17px;
}

.highlight {
  color: #e74c3c;
  font-weight: bold;
}

.account-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.account-sidebar li {
  margin: 12px 0;
  padding: 10px 15px;
  color: #333;
  border-radius: 8px;
  transition: background 0.3s, color 0.3s;
  cursor: pointer;
}

.account-sidebar li:hover {
  background-color: #f0f0f0;
}

.account-sidebar li.active {
  background-color: #ffd8a8;
  color: #b3541e;
  font-weight: bold;
}

.account-info {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.account-info h2 {
  font-size: 22px;
  margin-bottom: 25px;
  color: #2c3e50;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.account-info p {
  margin: 12px 0;
  font-size: 16px;
  color: #444;
}

.account-page {
  display: flex;
  font-family: Arial, sans-serif;
  margin: 40px;
}

.sidebar {
  width: 250px;
  margin-right: 40px;
}

.sidebar h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

.sidebar .highlight {
  color: #d19a00;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.sidebar li {
  padding: 8px 0;
  color: #333;
  cursor: pointer;
}

.sidebar .active {
  color: #d19a00;
  font-weight: bold;
}

.content {
  flex: 1;
  max-width: 500px;
}

.content h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.content p {
  font-size: 14px;
  color: #555;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d3a53c;
  border-radius: 4px;
}

.submit-btn {
  background-color: #d3a53c;
  color: white;
  border: none;
  padding: 12px 20px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 4px;
}

.modal-box input,
.modal-box select,
.modal-box textarea {
  pointer-events: auto !important;
  z-index: 10001;
  background: white;
  color: black;
  opacity: 1;
}
.address-item {
  background-color: #fff;
}

.address-list .text-primary {
  white-space: nowrap;
}

</style>