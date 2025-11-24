<script setup>
import { ref, reactive } from 'vue'
import Swal from 'sweetalert2'
import { onMounted } from 'vue'

// Reactive data for form
const formData = reactive({
  size: '',
  flavor: '',
  shape: '',
  origin: '',
  price: '',
  quantity: ''
})

// Error states
const errors = reactive({
  size: '',
  flavor: '',
  shape: '',
  origin: '',
  price: '',
  quantity: ''
})

// Validation functions
const validatePrice = (price) => {
  console.log('🔍 Validating price:', price, 'type:', typeof price)
  
  // Check if empty
  if (!price || price.toString().trim() === '') {
    console.log('❌ Price is empty')
    return 'Giá bán không được để trống'
  }
  
  // Remove any non-numeric characters except decimal point
  const cleanPrice = price.toString().replace(/[^\d.]/g, '')
  console.log('🔍 Cleaned price:', cleanPrice)
  
  // Convert to number and check if valid
  const numPrice = Number(cleanPrice)
  console.log('🔍 Converted price:', numPrice, 'isNaN:', isNaN(numPrice))
  
  if (isNaN(numPrice)) {
    console.log('❌ Price is not a number')
    return 'Giá bán phải là số hợp lệ'
  }
  
  if (numPrice <= 0) {
    console.log('❌ Price is not positive:', numPrice)
    return 'Giá bán phải là số dương'
  }
  
  // Strict minimum price check
  if (numPrice < 10000) {
    console.log('❌ Price is too low:', numPrice, '< 10000')
    return 'Giá bán phải từ 10,000 VND trở lên'
  }
  
  // Additional check for unreasonably low prices
  if (numPrice < 1000) {
    console.log('❌ Price is unreasonably low:', numPrice)
    return 'Giá bán không hợp lệ (quá thấp)'
  }
  
  console.log('✅ Price validation passed:', numPrice)
  return ''
}

const validateQuantity = (quantity) => {
  console.log('🔍 Validating quantity:', quantity, 'type:', typeof quantity)
  
  if (!quantity || quantity.toString().trim() === '') {
    console.log('❌ Quantity is empty')
    return 'Số lượng không được để trống'
  }
  
  const numQuantity = Number(quantity)
  console.log('🔍 Converted quantity:', numQuantity, 'isNaN:', isNaN(numQuantity))
  
  if (isNaN(numQuantity)) {
    console.log('❌ Quantity is not a number')
    return 'Số lượng phải là số hợp lệ'
  }
  
  if (numQuantity <= 0) {
    console.log('❌ Quantity is not positive:', numQuantity)
    return 'Số lượng phải là số nguyên dương'
  }
  
  if (!Number.isInteger(numQuantity)) {
    console.log('❌ Quantity is not integer:', numQuantity)
    return 'Số lượng phải là số nguyên'
  }
  
  console.log('✅ Quantity validation passed:', numQuantity)
  return ''
}

const validateRequired = (value, fieldName) => {
  console.log(`🔍 Validating required field ${fieldName}:`, value)
  
  if (!value || value.toString().trim() === '') {
    console.log(`❌ ${fieldName} is empty`)
    return `${fieldName} không được để trống`
  }
  
  console.log(`✅ ${fieldName} validation passed`)
  return ''
}

// Handle input changes
const handleInputChange = (field, value) => {
  console.log(`🔍 Input change for ${field}:`, value)
  formData[field] = value
  
  // Clear error when user starts typing
  if (errors[field]) {
    errors[field] = ''
  }
}

// Handle price input specifically
const handlePriceChange = (value) => {
  console.log('🔍 Price input change:', value)
  
  // Remove any non-numeric characters except decimal point
  const cleanValue = value.toString().replace(/[^\d.]/g, '')
  console.log('🔍 Cleaned input value:', cleanValue)
  
  formData.price = cleanValue
  
  // Validate immediately
  const error = validatePrice(cleanValue)
  errors.price = error
  
  if (error) {
    console.log('❌ Price validation error:', error)
  } else {
    console.log('✅ Price validation passed')
  }
}

// Handle quantity input specifically
const handleQuantityChange = (value) => {
  console.log('🔍 Quantity input change:', value)
  formData.quantity = value
  
  // Validate immediately
  const error = validateQuantity(value)
  errors.quantity = error
  
  if (error) {
    console.log('❌ Quantity validation error:', error)
  } else {
    console.log('✅ Quantity validation passed')
  }
}

// Form submission
const handleSubmit = (e) => {
  e.preventDefault()
  console.log('🚀 Form submission started...')
  console.log('📊 Form data:', formData)
  
  // Validate all fields
  let hasErrors = false
  const errorMessages = []
  
  // Validate required fields
  errors.size = validateRequired(formData.size, 'Size')
  errors.flavor = validateRequired(formData.flavor, 'Hương vị')
  errors.shape = validateRequired(formData.shape, 'Hình dạng')
  errors.origin = validateRequired(formData.origin, 'Xuất xứ')
  
  // Validate price and quantity with extra checks
  errors.price = validatePrice(formData.price)
  errors.quantity = validateQuantity(formData.quantity)
  
  // Double-check price validation
  const priceNum = Number(formData.price)
  if (!errors.price && (priceNum < 10000 || isNaN(priceNum))) {
    console.log('❌ Double-check failed for price:', priceNum)
    errors.price = 'Giá bán không hợp lệ (kiểm tra lại)'
    hasErrors = true
  }
  
  // Check if any errors exist and collect error messages
  Object.entries(errors).forEach(([field, error]) => {
    if (error) {
      hasErrors = true
      errorMessages.push(error)
      console.log(`❌ ${field} error:`, error)
    }
  })
  
  console.log('📊 Validation result:', { hasErrors, errorMessages })
  
  if (hasErrors) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi validation',
      html: `
        <div class="text-start">
          <p><strong>Vui lòng sửa các lỗi sau:</strong></p>
          <ul class="text-start">
            ${errorMessages.map(msg => `<li>${msg}</li>`).join('')}
          </ul>
        </div>
      `,
      confirmButtonText: 'Đóng'
    })
    return
  }
  
  // Final validation check before submission
  if (priceNum < 10000) {
    console.log('❌ Final price check failed:', priceNum)
    Swal.fire({
      icon: 'error',
      title: 'Lỗi giá bán',
      text: 'Giá bán phải từ 10,000 VND trở lên',
      confirmButtonText: 'Đóng'
    })
    return
  }
  
  // If validation passes, proceed with form submission
  console.log('✅ All validations passed, proceeding with submission...')
  Swal.fire({
    icon: 'success',
    title: 'Thành công!',
    text: 'Biến thể đã được thêm thành công',
    confirmButtonText: 'OK'
  }).then(() => {
    // Reset form
    resetForm()
    console.log('🔄 Form reset completed')
  })
}

// Reset form
const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = ''
  })
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })
}

// Test validation function
const testValidation = () => {
  console.log('🧪 Testing validation...')
  
  // Test cases
  const testCases = [
    { price: '', expected: 'Giá bán không được để trống' },
    { price: 'abc', expected: 'Giá bán phải là số hợp lệ' },
    { price: '0', expected: 'Giá bán phải là số dương' },
    { price: '-1000', expected: 'Giá bán phải là số dương' },
    { price: '1', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '2', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '100', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '1000', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '9999', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '10000', expected: '' },
    { price: '15000', expected: '' },
    { price: '1đ', expected: 'Giá bán phải từ 10,000 VND trở lên' },
    { price: '2đ', expected: 'Giá bán phải từ 10,000 VND trở lên' }
  ]
  
  testCases.forEach((testCase, index) => {
    const result = validatePrice(testCase.price)
    const passed = result === testCase.expected
    console.log(`Test ${index + 1}: Price "${testCase.price}" - ${passed ? '✅ PASS' : '❌ FAIL'}`)
    if (!passed) {
      console.log(`  Expected: "${testCase.expected}"`)
      console.log(`  Got: "${result}"`)
    }
  })
}

// Call test on component mount
onMounted(() => {
  testValidation()
})
</script>

<template>
<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Thêm biến thể mới cho sản phẩm - <span class="text-primary">Bánh kem</span></h3>
      <button class="btn btn-sm btn-gradient-blue">Quay lại danh sách</button>
    </div>

    <form class="card p-4 shadow-sm rounded-4" @submit="handleSubmit">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Size <span class="text-danger">*</span></label>
          <input 
            type="text" 
            class="form-control" 
            :class="{ 'is-invalid': errors.size }"
            placeholder="VD: S, M, L..." 
            v-model="formData.size"
            @input="handleInputChange('size', $event.target.value)"
          />
          <div v-if="errors.size" class="invalid-feedback">
            {{ errors.size }}
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label">Hương vị <span class="text-danger">*</span></label>
          <input 
            type="text" 
            class="form-control" 
            :class="{ 'is-invalid': errors.flavor }"
            placeholder="VD: Dâu, Sô cô la..." 
            v-model="formData.flavor"
            @input="handleInputChange('flavor', $event.target.value)"
          />
          <div v-if="errors.flavor" class="invalid-feedback">
            {{ errors.flavor }}
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label">Hình dạng <span class="text-danger">*</span></label>
          <input 
            type="text" 
            class="form-control" 
            :class="{ 'is-invalid': errors.shape }"
            placeholder="VD: Tròn, Vuông..." 
            v-model="formData.shape"
            @input="handleInputChange('shape', $event.target.value)"
          />
          <div v-if="errors.shape" class="invalid-feedback">
            {{ errors.shape }}
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label">Xuất xứ <span class="text-danger">*</span></label>
          <input 
            type="text" 
            class="form-control" 
            :class="{ 'is-invalid': errors.origin }"
            placeholder="VD: Việt Nam, Hàn Quốc..." 
            v-model="formData.origin"
            @input="handleInputChange('origin', $event.target.value)"
          />
          <div v-if="errors.origin" class="invalid-feedback">
            {{ errors.origin }}
          </div>
        </div>
        <div class="col-md-6">
          <label class="form-label">Giá bán (VND) <span class="text-danger">*</span></label>
          <div class="input-group">
            <input 
              type="number" 
              class="form-control" 
              :class="{ 'is-invalid': errors.price }"
              placeholder="VD: 250000" 
              min="10000"
              step="1000"
              required
              pattern="[0-9]{5,}"
              title="Giá bán phải từ 10,000 VND trở lên"
              v-model="formData.price"
              @input="handlePriceChange($event.target.value)"
              @blur="handlePriceChange($event.target.value)"
            />
            <span class="input-group-text">VND</span>
          </div>
          <div v-if="errors.price" class="invalid-feedback d-block">
            {{ errors.price }}
          </div>
          <small class="form-text text-muted">Giá tối thiểu: 10,000 VND</small>
        </div>
        <div class="col-md-6">
          <label class="form-label">Số lượng <span class="text-danger">*</span></label>
          <input 
            type="number" 
            class="form-control" 
            :class="{ 'is-invalid': errors.quantity }"
            placeholder="VD: 100" 
            min="1"
            step="1"
            v-model="formData.quantity"
            @input="handleQuantityChange($event.target.value)"
          />
          <div v-if="errors.quantity" class="invalid-feedback">
            {{ errors.quantity }}
          </div>
        </div>
      </div>

      <div class="mt-4 text-end">
        <button type="submit" class="btn btn-gradient-blue px-4 me-2">Thêm biến thể</button>
        <button type="button" class="btn btn-secondary" @click="resetForm">Hủy</button>
      </div>
    </form>
  </div>

  <!-- <div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Sửa biến thể cho sản phẩm - <span class="text-primary">Bánh kem</span></h3>
      <button class="btn btn-sm btn-gradient-blue">Quay lại danh sách</button>
    </div>

    <form class="card p-4 shadow-sm rounded-4">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Size</label>
          <input type="text" class="form-control" placeholder="VD: S, M, L..." />
        </div>
        <div class="col-md-6">
          <label class="form-label">Hương vị</label>
          <input type="text" class="form-control" placeholder="VD: Dâu, Sô cô la..." />
        </div>
        <div class="col-md-6">
          <label class="form-label">Hình dạng</label>
          <input type="text" class="form-control" placeholder="VD: Tròn, Vuông..." />
        </div>
        <div class="col-md-6">
          <label class="form-label">Xuất xứ</label>
          <input type="text" class="form-control" placeholder="VD: Việt Nam, Hàn Quốc..." />
        </div>
        <div class="col-md-6">
          <label class="form-label">Giá bán (VND)</label>
          <input type="number" class="form-control" placeholder="VD: 250000" />
        </div>
        <div class="col-md-6">
          <label class="form-label">Số lượng</label>
          <input type="number" class="form-control" placeholder="VD: 100" />
        </div>
      </div>

      <div class="mt-4 text-end">
        <button type="submit" class="btn btn-gradient-blue px-4 me-2">Lưu biến thể</button>
        <button type="button" class="btn btn-secondary">Hủy</button>
      </div>
    </form>
  </div> -->
</template>

<style>
/* Responsive phần header */
@media (max-width: 576px) {
  .d-flex.justify-content-between.align-items-center {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  h3 {
    text-align: center;
    font-size: 1.4rem;
  }

  .btn-gradient-blue {
    width: 100%;
    text-align: center;
  }
}

/* Input style */
.form-control {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58, 142, 249, 0.3);
}

/* Button gradient */
.btn-gradient-blue {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: color 0.3s ease, box-shadow 0.3s ease;
}

.btn-gradient-blue:hover {
  color: black;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

/* Validation styles */
.form-control.is-invalid {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

.form-control.is-invalid:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

.invalid-feedback {
  display: block;
  width: 100%;
  margin-top: 0.25rem;
  font-size: 0.875em;
  color: #dc3545;
}

/* Input group styling */
.input-group-text {
  background-color: #f8f9fa;
  border: 1px solid #ced4da;
  color: #495057;
  font-weight: 500;
}

/* Required field indicator */
.text-danger {
  color: #dc3545 !important;
}

/* Form text styling */
.form-text {
  font-size: 0.875em;
  color: #6c757d;
  margin-top: 0.25rem;
}

/* Card styling improvements */
.card {
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  transition: box-shadow 0.3s ease;
}

/* Animation for error messages */
.invalid-feedback {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Success state styling */
.form-control.is-valid {
  border-color: #198754;
  box-shadow: 0 0 0 0.2rem rgba(25, 135, 84, 0.25);
}

.form-control.is-valid:focus {
  border-color: #198754;
  box-shadow: 0 0 0 0.2rem rgba(25, 135, 84, 0.25);
}
</style>