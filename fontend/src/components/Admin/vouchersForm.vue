<template>
    <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center gap-2 mb-4">
      <h2 style="color: #343a40">{{ isEditMode ? 'Cập nhật voucher' : 'Thêm voucher' }}</h2>
    </div>

      <div class="card">
        <div class="card-body p-4">
          <form @submit.prevent="saveVoucher" class="add-discount-form" v-if="formLoaded">
            <div class="mb-3">
              <label class="form-label">Mã voucher</label>
              <input v-model="form.code" type="text" class="form-control" placeholder="Nhập mã voucher (không dấu, không tiếng Việt)" :class="{ 'is-invalid': errors.code }" />
              <div v-if="errors.code" class="invalid-feedback">{{ errors.code }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">% Giảm</label>
              <input v-model.number="form.discountPercent" type="number" class="form-control" placeholder="Nhập phần trăm giảm" min="0" max="100" :class="{ 'is-invalid': errors.discountPercent }" />
              <div v-if="errors.discountPercent" class="invalid-feedback">{{ errors.discountPercent }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Giảm tối đa</label>
              <input v-model.number="form.maxDiscountAmount" type="number" class="form-control" placeholder="Nhập giảm tối đa (VND)" :class="{ 'is-invalid': errors.maxDiscountAmount }" />
              <div v-if="errors.maxDiscountAmount" class="invalid-feedback">{{ errors.maxDiscountAmount }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Đơn tối thiểu</label>
              <input v-model.number="form.minOrderAmount" type="number" class="form-control" placeholder="Nhập đơn tối thiểu (VND)" :class="{ 'is-invalid': errors.minOrderAmount }" />
              <div v-if="errors.minOrderAmount" class="invalid-feedback">{{ errors.minOrderAmount }}</div>
            </div>
            <!-- <div class="mb-3">
              <label class="form-label">Ngày tạo</label>
              <input v-model="form.createdDate" type="date" class="form-control" readonly :class="{ 'is-invalid': errors.createdDate }" />
              <div v-if="errors.createdDate" class="invalid-feedback">{{ errors.createdDate }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Ngày kết thúc</label>
              <input v-model="form.endDate" type="date" class="form-control" :class="{ 'is-invalid': errors.endDate }" />
              <div v-if="errors.endDate" class="invalid-feedback">{{ errors.endDate }}</div>
            </div> -->
            <div class="mb-3">
              <label class="form-label">Thời gian bắt đầu</label>
              <input v-model="form.createdDate" type="datetime-local" class="form-control" :class="{ 'is-invalid': errors.createdDate }" />
              <div v-if="errors.createdDate" class="invalid-feedback">{{ errors.createdDate }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Thời gian kết thúc</label>
              <input v-model="form.endDate" type="datetime-local" class="form-control" :class="{ 'is-invalid': errors.endDate }" />
              <div v-if="errors.endDate" class="invalid-feedback">{{ errors.endDate }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select v-model="form.isActive" class="form-control" :class="{ 'is-invalid': errors.isActive }">
                <option value="true">Hoạt động</option>
                <option value="false">Không hoạt động</option>
              </select>
              <div v-if="errors.isActive" class="invalid-feedback">{{ errors.isActive }}</div>
            </div>
            <div class="mb-3">
              <label class="form-label">Số lượng</label>
              <input v-model.number="form.quantity" type="number" class="form-control" placeholder="Nhập số lượng" min="0" :class="{ 'is-invalid': errors.quantity }" />
              <div v-if="errors.quantity" class="invalid-feedback">{{ errors.quantity }}</div>
            </div>
            <div class="d-flex justify-content-end">
              <button type="submit" class="btn btn-gradient-approve me-2" :disabled="isSaving">
                <i v-if="isSaving" class="bi bi-arrow-clockwise bi-spin me-1"></i>
                <i v-else class="bi bi-check-lg me-1"></i> Lưu
              </button>
              <router-link to="/quanLyVouchers" class="btn btn-gradient-reject">
                <i class="bi bi-x-lg me-1"></i> Hủy
              </router-link>
            </div>
          </form>
          <div v-if="!formLoaded && isEditMode" class="alert alert-warning mt-3">
            Đang tải dữ liệu voucher...
          </div>
          <div v-if="errorMessage" class="alert alert-danger mt-3">
            {{ errorMessage }}
          </div>
          <div v-if="successMessage" class="alert alert-success mt-3">
            {{ successMessage }}
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/axios'; // ✅ Thay axios bằng api đã cấu hình JWT
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2';

const router = useRouter();
const route = useRoute();
const isEditMode = ref(false);
const voucherId = ref(null);
const formLoaded = ref(false);
// Tính min datetime cho input (hiện tại, format 'YYYY-MM-DDTHH:mm')
const minStartDateTime = ref(new Date().toISOString().slice(0, 16));

const form = ref({
  code: '',
  discountPercent: 0,
  maxDiscountAmount: 0,
  minOrderAmount: 0,
  createdDate: '',
  endDate: '',
  // startTime: '',
  // endTime: '',
  isActive: 'true',
  quantity: 0,
});
const errors = ref({
  code: '',
  discountPercent: '',
  maxDiscountAmount: '',
  minOrderAmount: '',
  createdDate: '',
  endDate: '',
  // startTime: '',
  // endTime: '',
  isActive: '',
  quantity: '',
});
const isSaving = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

onMounted(async () => {
  // Cập nhật minStartDateTime động nếu cần (ví dụ: set mặc định cho form nếu thêm mới)
  if (!isEditMode.value) {
    form.value.createdDate = minStartDateTime.value;
  }
  if (route.query.id) {
    isEditMode.value = true;
    voucherId.value = route.query.id;
    try {
      const response = await api.get(`/api/vouchers/${voucherId.value}`);
      const voucher = response.data;
      if (voucher) {
        form.value = {
          code: voucher.code || '',
          discountPercent: voucher.discountPercent || 0,
          maxDiscountAmount: voucher.maxDiscountAmount || 0,
          minOrderAmount: voucher.minOrderAmount || 0,
          createdDate: voucher.createdDate ? voucher.createdDate.substring(0, 16) : '',
          endDate: voucher.endDate ? voucher.endDate.substring(0, 16) : '',
          // startTime: voucher.startTime ? voucher.startTime.substring(0, 16) : '',
          // endTime: voucher.endTime ? voucher.endTime.substring(0, 16) : '',
          isActive: voucher.isActive?.toString() || 'true',
          quantity: voucher.quantity || 0,
        };
      } else {
        throw new Error('Voucher not found');
      }
      formLoaded.value = true;
    } catch (error) {
      errorMessage.value = `Không thể tải dữ liệu voucher: ${error.response?.data?.message || error.message}`;
      await Swal.fire({ title: 'Lỗi!', text: errorMessage.value, icon: 'error', confirmButtonText: 'OK' });
    }
  } else {
    formLoaded.value = true;
  }
});

// Hàm kiểm tra chuỗi có chứa ký tự tiếng Việt
const containsVietnamese = (str) => {
  const vietnameseRegex = /[àáảãạăắằẳẵặâấầẩẫậđèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵÀÁẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬĐÈÉẺẼẸÊẾỀỂỄỆÌÍỈĨỊÒÓỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÙÚỦŨỤƯỨỪỬỮỰỲÝỶỸỴ]/;
  return vietnameseRegex.test(str);
};

const saveVoucher = async () => {
  isSaving.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  // Reset errors
  errors.value = {
    code: '',
    discountPercent: '',
    maxDiscountAmount: '',
    minOrderAmount: '',
    createdDate: '',
    endDate: '',
    // startTime: '',
    // endTime: '',
    isActive: '',
    quantity: '',
  };

  let hasError = false;

  if (!form.value.code?.trim()) {
    errors.value.code = 'Mã voucher không được để trống!';
    hasError = true;
  } else if (form.value.code.trim().length < 3 || form.value.code.trim().length > 15) {
    errors.value.code = 'Mã voucher từ 3 đến 15 ký tự!';
    hasError = true;
  } else if (containsVietnamese(form.value.code)) {
    errors.value.code = 'Mã voucher không được chứa ký tự tiếng Việt!';
    hasError = true;
  }

  if (form.value.discountPercent === null || form.value.discountPercent === undefined || form.value.discountPercent === 0) {
    errors.value.discountPercent = 'Phần trăm giảm không được để trống hoặc bằng 0!';
    hasError = true;
  }
  if (form.value.maxDiscountAmount === null || form.value.maxDiscountAmount === undefined || form.value.maxDiscountAmount === 0) {
    errors.value.maxDiscountAmount = 'Giảm tối đa không được để trống hoặc bằng 0!';
    hasError = true;
  }
  if (form.value.minOrderAmount === null || form.value.minOrderAmount === undefined || form.value.minOrderAmount === 0) {
    errors.value.minOrderAmount = 'Đơn tối thiểu không được để trống hoặc bằng 0!';
    hasError = true;
  }
  // if (!form.value.createdDate) {
  //   errors.value.createdDate = 'Ngày tạo không được để trống!';
  //   hasError = true;
  // }
  // if (!form.value.endDate) {
  //   errors.value.endDate = 'Ngày kết thúc không được để trống!';
  //   hasError = true;
  // }
  if (!form.value.createdDate) {
    errors.value.createdDate = 'Thời gian bắt đầu không được để trống!';
    hasError = true;
  }
  if (!form.value.endDate) {
    errors.value.endDate = 'Thời gian kết thúc không được để trống!';
    hasError = true;
  }
  if (form.value.quantity === null || form.value.quantity === undefined || form.value.quantity === 0) {
    errors.value.quantity = 'Số lượng không được để trống hoặc bằng 0!';
    hasError = true;
  }

  if (form.value.discountPercent < 0) {
    errors.value.discountPercent = 'Phần trăm giảm không được âm!';
    hasError = true;
  }
  if (form.value.maxDiscountAmount < 0) {
    errors.value.maxDiscountAmount = 'Giảm tối đa không được âm!';
    hasError = true;
  }
  if (form.value.minOrderAmount < 50000) {
    errors.value.minOrderAmount = 'Đơn tối thiểu từ 50.000Đ!';
    hasError = true;
  }
  if (form.value.quantity < 0) {
    errors.value.quantity = 'Số lượng không được âm!';
    hasError = true;
  }
  if (form.value.discountPercent < 5 || form.value.discountPercent > 50) {
    errors.value.discountPercent = 'Phần trăm giảm từ 5% - 50%!';
    hasError = true;
  }
  
  // Kiểm tra ngày kết thúc phải sau ngày tạo
  if (form.value.endDate && form.value.createdDate) {
    const startDate = new Date(form.value.createdDate);
    const endDate = new Date(form.value.endDate);
    
    if (endDate < startDate) {
      errors.value.endDate = 'Ngày kết thúc phải sau ngày tạo!';
      hasError = true;
    } else if (endDate.getTime() === startDate.getTime()) {
      errors.value.endDate = 'Ngày kết thúc không được trùng với ngày tạo!';
      hasError = true;
    }
  }

    // Kiểm tra thời gian không ở quá khứ
  const now = new Date();
  if (form.value.createdDate) {
    const startDateTime = new Date(form.value.createdDate);
    if (startDateTime < now) {
      errors.value.createdDate = 'Thời gian bắt đầu không được ở quá khứ!';
      hasError = true;
    }
  }


  // Kiểm tra startTime và endTime
  if (form.value.createdDate && form.value.endDate) {
    const startDateTime = new Date(form.value.createdDate);
    const endDateTime = new Date(form.value.endDate);

    if (endDateTime <= startDateTime) {
      errors.value.endDate = 'Thời gian kết thúc phải sau thời gian bắt đầu!';
      hasError = true;
    }
  }

  if (hasError) {
    isSaving.value = false;
    return;
  }

  try {
    const payload = {
      ...form.value,
      // createdDate: form.value.createdDate + 'T00:00:00',
      // endDate: form.value.endDate + 'T00:00:00',
       createdDate: form.value.createdDate + ':00',
      endDate: form.value.endDate + ':00',
      isActive: form.value.isActive === 'true',
    };

    let response;
    if (isEditMode.value) {
      response = await api.put(`/api/vouchers/${voucherId.value}`, payload);
      successMessage.value = 'Cập nhật voucher thành công!';
    } else {
      // Kiểm tra mã code tồn tại
      let isCodeExists = false;
      try {
        const checkResponse = await api.get(`/api/vouchers/code/${form.value.code}`);
        if (checkResponse.data) {
          isCodeExists = true;
        }
      } catch (checkError) {
        if (checkError.response && checkError.response.status === 404) {
          isCodeExists = false;
        } else {
          throw checkError;
        }
      }

      if (isCodeExists) {
        errors.value.code = 'Mã voucher đã tồn tại!';
        isSaving.value = false;
        return;
      }

      response = await api.post('/api/vouchers', payload);
      form.value.id = response.data.id; // Lưu ID mới tạo vào form
      successMessage.value = 'Thêm voucher thành công!';
    }

    await Swal.fire({
      title: 'Thành công!',
      text: successMessage.value,
      icon: 'success',
      confirmButtonText: 'OK',
      timer: 2000,
      timerProgressBar: true,
    }).then(() => router.push('/quanLyVouchers'));
  } catch (error) {
    console.error('Error saving voucher:', error.response?.data || error.message);
    errorMessage.value = `Lỗi khi lưu voucher: ${error.response?.data?.message || error.message}`;
  } finally {
    isSaving.value = false;
  }
};
</script>

<style scoped>
/* Giữ nguyên phần CSS gốc */
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
  border-radius: 1rem;
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
  background-image: linear-gradient(to right, #0099ff, #0099ff);
}

.btn-gradient-reject {
  background-image: linear-gradient(to right, #ee3030, #ee3030);
}

.btn-sm {
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
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
.justify-content-between { justify-content: space-between !important; }
.align-items-center { align-items: center !important; }
.mb-4 { margin-bottom: 1.5rem !important; }
.mb-3 { margin-bottom: 1rem !important; }
.mb-1 { margin-bottom: 0.25rem !important; }
.p-0 { padding: 0 !important; }
.m-0 { margin: 0 !important; }
.bg-transparent { background-color: transparent !important; }
.position-relative { position: relative !important; }
.position-absolute { position: absolute !important; }
.top-0 { top: 0 !important; }
.start-100 { left: 100% !important; }
.translate-middle { transform: translate(-50%, -50%) !important; }
.badge { display: inline-block; padding: .35em .65em; font-size: .75em; font-weight: 700; line-height: 1; color: #fff; text-align: center; white-space: nowrap; vertical-align: baseline; border-radius: .25rem; }
.rounded-pill { border-radius: 50rem !important; }
.bg-info { background-color: #17a2b8 !important; }
.visually-hidden { position: absolute !important; width: 1px !important; height: 1px !important; padding: 0 !important; margin: -1px !important; overflow: hidden !important; clip: rect(0, 0, 0, 0) !important; white-space: nowrap !important; border: 0 !important; }

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

.add-discount-form .is-invalid {
  border-color: #dc3545;
}

.add-discount-form .invalid-feedback {
  color: #dc3545;
  font-size: 0.875rem;
}

.settings-dropdown-wrapper:hover .settings-dropdown {
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
}

.alert {
  padding: 0.75rem 1.25rem;
  margin-bottom: 0;
  border-radius: 0.25rem;
}

.alert-success {
  color: #155724;
  background-color: #d4edda;
  border-color: #c3e6cb;
}

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
</style>


