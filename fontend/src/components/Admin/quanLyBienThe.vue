<template>
  <div class="container py-4">
    <h3 class="mb-4">Biến thể sản phẩm #{{ productId }}</h3>

    <!-- Nút mở modal -->
    <button class="btn btn-primary mb-3" @click="openModal(false)">
      Thêm biến thể
    </button>

    <!-- Modal tự làm -->
    <div v-if="modalVisible" class="modal-backdrop" @click.self="closeModal()">
      <div class="modal-content-custom">
        <form @submit.prevent="handleSubmit" novalidate>
          <div class="modal-header-custom">
            <h5 class="modal-title-custom">{{ form.id ? 'Cập nhật biến thể' : 'Thêm biến thể mới' }}</h5>
            <button type="button" class="btn-close-custom" @click="closeModal()" aria-label="Đóng">&times;</button>
          </div>

          <div class="modal-body-custom">
            <div class="row g-3">
              <!-- Select fields -->
              <div class="col-md-6" v-for="(option, key) in selectOptions" :key="key">
                <label class="form-label">{{ option.label }}</label>
                <select v-model="form[option.model]" class="form-select" :class="{ 'is-invalid': errors[option.model] }"
                  @change="clearError(option.model)">
                  <option disabled value="">-- Chọn {{ option.label.toLowerCase() }} --</option>
                  <option v-for="o in option.data" :key="o.id" :value="o.id">{{ o.name }}</option>
                </select>
                <div class="invalid-feedback" v-if="errors[option.model]">{{ errors[option.model] }}</div>
              </div>

              <!-- Number fields -->
              <div class="col-md-6" v-for="field in numberFields" :key="field.model">
                <label class="form-label">{{ field.label }}</label>
                <input v-model="form[field.model]" type="number" class="form-control"
                  :class="{ 'is-invalid': errors[field.model] }" min="0" step="1" @input="clearError(field.model)" />
                <div class="invalid-feedback" v-if="errors[field.model]">{{ errors[field.model] }}</div>
              </div>

              <!-- Status select -->
              <div class="col-md-6">
                <label class="form-label">Trạng thái</label>
                <select v-model="form.isActive" class="form-select" :class="{ 'is-invalid': errors.isActive }"
                  @change="clearError('isActive')">
                  <option :value="true">Hoạt động</option>
                  <option :value="false">Ngừng</option>
                </select>
                <div class="invalid-feedback" v-if="errors.isActive">{{ errors.isActive }}</div>
              </div>
            </div>
          </div>

          <div class="modal-footer-custom">
            <button type="button" class="btn btn-secondary" @click="closeModal()">Đóng</button>
            <button v-if="form.id" class="btn btn-warning me-2" type="submit">Cập nhật</button>
            <button v-else class="btn btn-primary" type="submit">Thêm mới</button>
          </div>
        </form>
      </div>
    </div>

    <div class="row mb-3 align-items-end">
      <div class="col">
        <label>Trọng lượng</label>
        <input type="number" v-model.number="weightFilter" @change="onFilterChange" class="form-control"
          placeholder="Nhập trọng lượng (gram)" />
      </div>
      <div class="col">
        <label>Trạng thái</label>
        <select v-model="isActiveFilter" @change="onFilterChange" class="form-select">
          <option :value="null">Tất cả</option>
          <option :value="true">Hoạt động</option>
          <option :value="false">Ngừng hoạt động</option>
        </select>
      </div>
      <div class="col">
        <label>Giá từ</label>
        <input type="number" v-model.number="minPriceFilter" @change="onFilterChange" class="form-control"
          placeholder="Giá từ" />
      </div>
      <div class="col">
        <label>Giá đến</label>
        <input type="number" v-model.number="maxPriceFilter" @change="onFilterChange" class="form-control"
          placeholder="Giá đến" />
      </div>
      <div class="col-auto">
        <button class="btn btn-primary" @click="onFilterChange" title="Lọc" type="button" aria-label="Lọc">
          <i class="bi bi-funnel-fill"></i>
        </button>
        <button class="btn btn-secondary ms-2" @click="resetFilters" title="Reset" type="button" aria-label="Reset">
          <i class="bi bi-arrow-clockwise"></i>
        </button>
      </div>
    </div>



    <!-- Danh sách biến thể -->
    <div class="card p-4">
      <h5 class="mb-3">Danh sách biến thể</h5>
      <table class="table table-bordered table-striped">
        <thead class="table-light">
          <tr>
            <th>STT</th>
            <th>Size</th>
            <th>Hương vị</th>
            <th>Hình dạng</th>
            <th>Xuất xứ</th>
            <th>Giá</th>
            <th>SL</th>
            <th>KL</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="variants.length === 0">
            <td colspan="9" class="text-center text-danger">Không có biến thể nào</td>
          </tr>
          <tr v-for="(v, index) in variants" :key="v.id">
            <td>{{ index + 1 }}</td>
            <!-- <td>{{ v.id }}</td> -->
            <td>{{ v.sizeName }}</td>
            <td>{{ v.flavorName }}</td>
            <td>{{ v.shapeName }}</td>
            <td>{{ v.originName }}</td>
            <td>{{ v.price.toLocaleString() }} đ</td>
            <td>{{ v.quantity }}</td>
            <td>{{ v.weight }}g</td>
            <td>{{ v.isActive ? 'Hoạt động' : 'Ngừng hoạt động' }}</td>
            <td>

              <!-- <button class="btn btn-sm btn-warning me-2" @click="editVariant(v)" title="Sửa">
  <i class="bi bi-pencil-square"></i>
</button>

<button
  class="btn btn-sm"
  :class="v.isActive ? 'btn-danger' : 'btn-success'"
  @click="toggleActiveVariant(v)"
  :title="v.isActive ? 'Ngừng hoạt động' : 'Kích hoạt'"
>
  <i :class="v.isActive ? 'bi bi-x-circle' : 'bi bi-check-circle'"></i>
</button> -->
              <button class="btn btn-outline-warning btn-sm me-2" @click="editVariant(v)" 
                :title="`Sửa biến thể ${v.sizeName} - ${v.flavorName}`">
                <i class="bi bi-pencil-square me-1"></i>
                Sửa
              </button>

              <button class="btn btn-sm"
                :class="v.isActive ? 'btn-outline-danger' : 'btn-outline-success'" 
                @click="toggleActiveVariant(v)"
                :title="v.isActive ? `Ngừng hoạt động biến thể ${v.sizeName} - ${v.flavorName}` : `Kích hoạt biến thể ${v.sizeName} - ${v.flavorName}`">
                <i :class="v.isActive ? 'bi bi-x-circle me-1' : 'bi bi-check-circle me-1'"></i>
                {{ v.isActive ? 'Ngừng hoạt động' : 'Kích hoạt' }}
              </button>


               <button class="btn btn-outline-info btn-sm me-2" 
          @click="openDiscountPopup(v)">
    <i class="bi bi-tags"></i>GG
  </button>




            </td>
          </tr>
        </tbody>
      </table>
    </div>


<!-- Modal áp dụng giảm giá -->
<div v-if="discountModalVisible" class="modal-backdrop" @click.self="closeDiscountPopup">
  <div class="modal-content-custom">
    <div class="modal-header-custom">
      <h5 class="modal-title-custom">Áp dụng giảm giá cho biến thể</h5>
      <button type="button" class="btn-close-custom" @click="closeDiscountPopup">&times;</button>
    </div>

    <div class="modal-body-custom">
      <!-- Ô tìm kiếm -->
      <div class="input-group mb-3">
        <input type="text" v-model="discountSearch" class="form-control" placeholder="Nhập tên giảm giá để tìm"
               @input="fetchDiscounts" />
        <button class="btn btn-outline-secondary" type="button" @click="fetchDiscounts">
          <i class="bi bi-search"></i>
        </button>
      </div>

      <!-- Danh sách giảm giá -->
      <ul class="list-group">
        <!-- <li v-for="d in discounts" :key="d.discountId"
            class="list-group-item d-flex justify-content-between align-items-center"
            :class="{active: selectedDiscount === d.discountId}"
            @click="selectedDiscount = d.discountId">
          <div>
            <strong>{{ d.name }}</strong> - {{ d.percentage }}%
            <br>
            <small>{{ formatDate(d.startDate) }} → {{ formatDate(d.endDate) }}</small>
          </div>
          <span class="badge bg-success" v-if="selectedDiscount === d.discountId">Đã chọn</span>
        </li> -->

        <li v-for="d in discounts" :key="d.discountId"
    class="list-group-item d-flex justify-content-between align-items-center"
    :class="{ active: selectedDiscount === d.discountId, 'disabled bg-light text-muted': d.isApplied }"
    @click="!d.isApplied && (selectedDiscount = d.discountId)">
  <div>
    <strong>{{ d.name }}</strong> - {{ d.percentage }}%
    <br>
    <small>{{ formatDate(d.startDate) }} → {{ formatDate(d.endDate) }}</small>
  </div>
  <span class="badge bg-success" v-if="selectedDiscount === d.discountId">Đã chọn</span>
  <span class="badge bg-secondary" v-else-if="d.isApplied">Đã áp dụng</span>
</li>

      </ul>
    </div>

    <div class="modal-footer-custom">
      <button class="btn btn-secondary" @click="closeDiscountPopup">Đóng</button>
      <button class="btn btn-primary" @click="applyDiscount">Áp dụng</button>
    </div>
  </div>
</div>


    <!-- Pagination -->
    <nav class="mt-3">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: page === 0 }" @click.prevent="changePage(page - 1)">
          <a class="page-link" href="#">Trước</a>
        </li>

        <li v-for="p in totalPages" :key="p" class="page-item" :class="{ active: p - 1 === page }"
          @click.prevent="changePage(p - 1)">
          <a class="page-link" href="#">{{ p }}</a>
        </li>

        <li class="page-item" :class="{ disabled: page === totalPages - 1 }" @click.prevent="changePage(page + 1)">
          <a class="page-link" href="#">Sau</a>
        </li>
      </ul>
    </nav>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import Swal from 'sweetalert2'
import api from '@/axios'

const route = useRoute()
const productId = route.params.productId

const variants = ref([])
const form = ref({
  id: null,
  sizeId: '',
  flavorId: '',
  shapeId: '',
  originId: '',
  price: '',
  quantity: '',
  weight: '',
  isActive: true,
})

const errors = ref({})

const sizes = ref([])
const flavors = ref([])
const shapes = ref([])
const origins = ref([])

const modalVisible = ref(false)

const selectOptions = computed(() => [
  { label: 'Size', model: 'sizeId', data: sizes.value },
  { label: 'Hương vị', model: 'flavorId', data: flavors.value },
  { label: 'Hình dạng', model: 'shapeId', data: shapes.value },
  { label: 'Xuất xứ', model: 'originId', data: origins.value },
])

const numberFields = [
  { label: 'Giá', model: 'price' },
  { label: 'Số lượng', model: 'quantity' },
  { label: 'Khối lượng (gram)', model: 'weight' },
]

const openModal = (isEdit = false) => {
  if (!isEdit) resetForm()
  modalVisible.value = true
}

const closeModal = () => {
  modalVisible.value = false
  resetForm()
  errors.value = {}
}

// const fetchVariants = async () => {
//   try {
//     const res = await api.get(`/api/admin/variants/by-product/${productId}`)
//     console.log("🧪 Dữ liệu từ API:", res.data[0])

//     variants.value = res.data
//   } catch (err) {
//     Swal.fire('Lỗi', 'Không thể tải danh sách biến thể', 'error')
//   }
// }
const fetchVariants = async () => {
  try {
    const params = {
      productId,
      page: page.value,
      size: size.value,
    }

    if (weightFilter.value !== null) params.weight = weightFilter.value
    if (isActiveFilter.value !== null) params.isActive = isActiveFilter.value
    if (minPriceFilter.value) params.minPrice = minPriceFilter.value
    if (maxPriceFilter.value) params.maxPrice = maxPriceFilter.value

    console.log('🧪 Tham số gửi lên API:', params)

    const res = await api.get('/api/admin/variants/filter', { params })

    console.log('📦 Dữ liệu trả về từ API:', res.data)

    variants.value = res.data.content.map(variant => ({
      ...variant,
      sizeName: variant.size?.name || '',
      flavorName: variant.flavor?.name || '',
      shapeName: variant.shape?.name || '',
      originName: variant.origin?.name || '',
    }))

    totalPages.value = res.data.totalPages
    totalElements.value = res.data.totalElements

    console.log('📊 Tổng trang:', totalPages.value, '| Tổng phần tử:', totalElements.value)
  } catch (err) {
    console.error('❌ Lỗi khi tải danh sách biến thể:', err)
    Swal.fire('Lỗi', 'Không thể tải danh sách biến thể', 'error')
  }
}




const fetchOptions = async () => {
  try {
    const [sizesRes, flavorsRes, shapesRes, originsRes] = await Promise.all([
      api.get('/api/admin/sizes-new'),
      api.get('/api/admin/flavors-new'),
      api.get('/api/admin/shapes-new'),
      api.get('/api/admin/origins-new'),
    ])
    sizes.value = sizesRes.data
    flavors.value = flavorsRes.data
    shapes.value = shapesRes.data
    origins.value = originsRes.data

    // 🔍 Ghi log kiểm tra dữ liệu select đã có chưa
    console.log(' [fetchOptions] sizes:', sizes.value)
    console.log(' [fetchOptions] flavors:', flavors.value)
    console.log(' [fetchOptions] shapes:', shapes.value)
    console.log(' [fetchOptions] origins:', origins.value)
  } catch (err) {
    Swal.fire('Lỗi', 'Không thể tải dữ liệu tùy chọn', 'error')
  }
}

const clearError = (field) => {
  if (errors.value[field]) {
    errors.value[field] = ''
  }
}

const validateForm = () => {
  const err = {}

  selectOptions.value.forEach((opt) => {
    if (!form.value[opt.model]) {
      err[opt.model] = `Vui lòng chọn ${opt.label.toLowerCase()}`
    }
  })

  numberFields.forEach((field) => {
    const val = form.value[field.model]
    if (val === '' || val === null) {
      err[field.model] = `${field.label} không được để trống`
    } else if (isNaN(val)) {
      err[field.model] = `${field.label} phải là số`
    } else {
      if (field.model === 'price' || field.model === 'weight') {
        if (+val <= 0) err[field.model] = `${field.label} phải lớn hơn 0`
      } else if (field.model === 'quantity') {
        if (+val < 0) err[field.model] = `${field.label} không được âm`
      }
      if (!Number.isInteger(+val)) {
        err[field.model] = `${field.label} phải là số nguyên`
      }
    }
  })

  if (form.value.isActive !== true && form.value.isActive !== false) {
    err.isActive = 'Vui lòng chọn trạng thái'
  }

  const duplicate = variants.value.some(v =>
    v.id !== form.value.id &&
    v.sizeId === form.value.sizeId &&
    v.flavorId === form.value.flavorId &&
    v.shapeId === form.value.shapeId &&
    v.originId === form.value.originId
  )
  if (duplicate) {
    err.duplicate = 'Biến thể với tổ hợp này đã tồn tại'
  }

  errors.value = err
  return Object.keys(err).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    if (errors.value.duplicate) {
      Swal.fire('Lỗi', errors.value.duplicate, 'error')
    }
    return
  }

  const data = { ...form.value, productId: +productId }

  try {
    if (form.value.id) {
      await api.put(`/api/admin/variants/update/${form.value.id}`, data)
      Swal.fire('Thành công', 'Cập nhật thành công!', 'success')
    } else {
      await api.post('/api/admin/variants/create', data)
      Swal.fire('Thành công', 'Thêm mới thành công!', 'success')
    }
    await fetchVariants()
    closeModal()
  } catch (err) {
    const errorMsg = err.response?.data?.error || err.response?.data || 'Có lỗi xảy ra khi gửi dữ liệu'
    Swal.fire('Lỗi', errorMsg, 'error')
  }
}

// ✅ Hàm sửa biến thể, có log kiểm tra dữ liệu
// const editVariant = async (v) => {
//   if (!sizes.value.length || !flavors.value.length || !shapes.value.length || !origins.value.length) {
//     await fetchOptions()
//   }

//   form.value = {
//     id: v.id,
//     sizeId: v.sizeId != null ? +v.sizeId : '',
//     flavorId: v.flavorId != null ? +v.flavorId : '',
//     shapeId: v.shapeId != null ? +v.shapeId : '',
//     originId: v.originId != null ? +v.originId : '',
//     price: v.price ?? '',
//     quantity: v.quantity ?? '',
//     weight: v.weight ?? '',
//     isActive: v.isActive === false ? false : true,
//   }

//   console.log('🟡 [editVariant] form.value:', form.value)

//   openModal(true)
// }
const editVariant = async (v) => {
  if (!sizes.value.length || !flavors.value.length || !shapes.value.length || !origins.value.length) {
    await fetchOptions()
  }

  form.value = {
    id: v.id,
    sizeId: v.size?.id ?? '',
    flavorId: v.flavor?.id ?? '',
    shapeId: v.shape?.id ?? '',
    originId: v.origin?.id ?? '',
    price: v.price ?? '',
    quantity: v.quantity ?? '',
    weight: v.weight ?? '',
    isActive: v.isActive === false ? false : true,
  }

  console.log('🟡 [editVariant] form.value:', form.value)

  openModal(true)
}

// const deleteVariant = async (id) => {
//   const confirm = await Swal.fire({
//     title: 'Bạn chắc chắn muốn ẩn biến thể này?',
//     icon: 'warning',
//     showCancelButton: true,
//     confirmButtonText: 'Ẩn',
//   })
//   if (confirm.isConfirmed) {
//     try {
//       await api.delete(`/api/admin/variants/delete/${id}`)
//       await fetchVariants()
//       Swal.fire('Đã ẩn thành công!', '', 'success')
//     } catch (err) {
//       const errorMsg = err.response?.data?.error || err.response?.data || 'Ẩn biến thể thất bại'
//       Swal.fire('Lỗi', errorMsg, 'error')
//     }
//   }
// }
const toggleActiveVariant = async (variant) => {
  const action = variant.isActive ? 'Ngừng hoạt động' : 'kích hoạt'
  const confirm = await Swal.fire({
    title: `Bạn chắc chắn muốn ${action} biến thể này?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: action.charAt(0).toUpperCase() + action.slice(1),
  })

  if (confirm.isConfirmed) {
    try {
      // Gọi API toggle trạng thái isActive đúng đường dẫn và method
      await api.put(`/api/admin/variants/toggle-active/${variant.id}`)

      await fetchVariants()
      Swal.fire(`${action.charAt(0).toUpperCase() + action.slice(1)} thành công!`, '', 'success')
    } catch (err) {
      const errorMsg = err.response?.data?.error || err.response?.data || `${action.charAt(0).toUpperCase() + action.slice(1)} thất bại`
      Swal.fire('Lỗi', errorMsg, 'error')
    }
  }
}


const resetForm = () => {
  form.value = {
    id: null,
    sizeId: '',
    flavorId: '',
    shapeId: '',
    originId: '',
    price: '',
    quantity: '',
    weight: '',
    isActive: true,
  }
  errors.value = {}
}

const weightFilter = ref(null)
const isActiveFilter = ref(null)
const minPriceFilter = ref(null)
const maxPriceFilter = ref(null)

const page = ref(0)
const size = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)
const changePage = (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return
  page.value = newPage
  fetchVariants()
}

const onFilterChange = () => {
  page.value = 0
  fetchVariants()
}
const resetFilters = () => {
  weightFilter.value = null
  isActiveFilter.value = null
  minPriceFilter.value = null
  maxPriceFilter.value = null
  page.value = 0
  fetchVariants()
}

onMounted(async () => {
  await fetchOptions()
  await fetchVariants()

  console.log('✅ [onMounted] Đã tải options và variants')
})




const discountModalVisible = ref(false)
const discounts = ref([])
const discountSearch = ref("")
const selectedDiscount = ref(null)
const selectedVariantId = ref(null)

const openDiscountPopup = (variant) => {
  selectedVariantId.value = variant.id
  discountModalVisible.value = true
  discountSearch.value = ""
  fetchDiscounts()
}

const closeDiscountPopup = () => {
  discountModalVisible.value = false
  discounts.value = []
  selectedDiscount.value = null
}

// Lấy danh sách giảm giá active có filter theo keyword
// const fetchDiscounts = async () => {
//   try {
//     const res = await api.get('/api/product-discounts/active', {
//       params: { keyword: discountSearch.value }
//     })
//     discounts.value = res.data
//   } catch (err) {
//     console.error("❌ Lỗi khi tải giảm giá:", err)
//     Swal.fire("Lỗi", "Không thể tải danh sách giảm giá", "error")
//   }
// }
const fetchDiscounts = async () => {
  try {
    const res = await api.get(`/api/product-discounts/active-for-variant`, {
      params: { 
        variantId: selectedVariantId.value,
        keyword: discountSearch.value
      }
    })
    discounts.value = res.data
  } catch (err) {
    Swal.fire("Lỗi", "Không thể tải danh sách giảm giá", "error")
  }
}


// Gọi API áp dụng
const applyDiscount = async () => {
  if (!selectedDiscount.value) {
    Swal.fire("Chú ý", "Vui lòng chọn giảm giá", "warning")
    return
  }

  try {
    await api.post("/api/product-discounts/apply", {
      productVariantId: selectedVariantId.value,
      discountId: selectedDiscount.value
    })

    Swal.fire("Thành công", "Áp dụng giảm giá thành công!", "success")
    closeDiscountPopup()
    await fetchVariants()
  } catch (err) {
    Swal.fire("Lỗi", err.response?.data || "Áp dụng thất bại", "error")
  }
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString("vi-VN")
}

</script>





<style scoped>
.table th,
.table td {
  vertical-align: middle;
}

/* Modal background */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

/* Modal content */
.modal-content-custom {
  background: white;
  border-radius: 0.375rem;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 0.5rem 1rem rgb(0 0 0 / 0.15);
  display: flex;
  flex-direction: column;
}

/* Header */
.modal-header-custom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-title-custom {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 500;
}

/* Close button */
.btn-close-custom {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  color: #000;
}

/* Body */
.modal-body-custom {
  padding: 1rem 1.5rem;
  flex-grow: 1;
}

/* Footer */
.modal-footer-custom {
  display: flex;
  justify-content: flex-end;
  padding: 1rem 1.5rem;
  border-top: 1px solid #dee2e6;
  gap: 0.5rem;
}

/* Bootstrap validation style override */
.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  display: block;
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 0.25rem;
}

/* Responsive */
@media (max-width: 576px) {
  .modal-content-custom {
    width: 95%;
  }
}

/* Action buttons styling */
.btn-outline-warning:hover {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #000;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  border-color: #dc3545;
  color: #fff;
}

.btn-outline-success:hover {
  background-color: #198754;
  border-color: #198754;
  color: #fff;
}

/* Table action column */
.table td:last-child {
  white-space: nowrap;
  min-width: 200px;
}
</style>
