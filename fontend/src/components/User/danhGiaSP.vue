<template>
  <div class="page-dang-nhap">
    <main>
      <section class="bread-crumb full-width-banner" style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">Đánh giá</div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr"> <svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" class="svg-inline--fa fa-chevron-right fa-w-10"><path fill="currentColor" d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z" class=""></path></svg> </span>
            </li>
            <li><strong><span>Đánh giá</span></strong></li>
          </ul>
        </div>
      </section>

      <div class="review-wrapper">
        <h2 class="review-heading">CẢM ƠN BẠN ĐÃ MUA HÀNG!</h2>

        <!-- IMPROVED: Enhanced loading state -->
        <div v-if="loading" class="text-center py-4">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
          <p class="mt-2">Đang tải thông tin sản phẩm...</p>
        </div>

        <!-- IMPROVED: Enhanced error state with retry option -->
        <div v-if="error" class="alert alert-danger">
          <div class="d-flex align-items-center">
            <i class="fas fa-exclamation-triangle me-2"></i>
            <div class="flex-grow-1">
              <strong>Có lỗi xảy ra:</strong>
              <p class="mb-0">{{ error }}</p>
            </div>
          </div>
          <div class="mt-3 d-flex gap-2">
            <button class="btn btn-outline-primary btn-sm" @click="retryLoadData">
              <i class="fas fa-redo me-1"></i> Thử lại
            </button>
            <button class="btn btn-secondary btn-sm" @click="goBack">
              <i class="fas fa-arrow-left me-1"></i> Quay lại
            </button>
          </div>
        </div>

        <!-- IMPROVED: Enhanced rating form -->
        <div v-if="!loading && !error && ratingData" class="review-content">
          <div class="review-image-section">
            <!-- ENHANCED: Better image handling with multiple fallback options -->
            <div class="image-container">
              <img
                v-if="currentImageUrl"
                :src="currentImageUrl"
                :alt="ratingData.productName"
                class="review-image"
                @error="handleImageError"
                @load="handleImageLoad"
                :style="{ display: imageLoaded ? 'block' : 'none' }"
              />
              <div
                v-if="!imageLoaded || !currentImageUrl"
                class="review-image-placeholder"
                :class="{ 'loading': !imageErrorOccurred }"
              >
                <div v-if="!imageErrorOccurred" class="loading-spinner">
                  <div class="spinner-border text-muted" role="status">
                    <span class="visually-hidden">Đang tải ảnh...</span>
                  </div>
                </div>
                <div v-else class="no-image-content">
                  <i class="fas fa-image"></i>
                  <p>Không có ảnh</p>
                </div>
              </div>
            </div>
            <p class="review-description">{{ getProductDescription() }}</p>
          </div>

          <div class="review-info-section">
            <h3 class="review-product-name">{{ ratingData.productName }}</h3>

            <!-- IMPROVED: Enhanced product variant info -->
            <div v-if="hasVariantInfo()" class="product-variant-info mb-3">
              <div class="variant-details">
                <span v-if="ratingData.size" class="variant-item">
                  <i class="fas fa-ruler-combined me-1"></i>
                  <strong>Kích cỡ:</strong> {{ ratingData.size }}
                </span>
                <span v-if="ratingData.flavor" class="variant-item">
                  <i class="fas fa-cookie-bite me-1"></i>
                  <strong>Hương vị:</strong> {{ ratingData.flavor }}
                </span>
                <span v-if="ratingData.shape" class="variant-item">
                  <i class="fas fa-shapes me-1"></i>
                  <strong>Hình dạng:</strong> {{ ratingData.shape }}
                </span>
                <span v-if="ratingData.origin" class="variant-item">
                  <i class="fas fa-map-marker-alt me-1"></i>
                  <strong>Xuất xứ:</strong> {{ ratingData.origin }}
                </span>
              </div>
            </div>

            <!-- IMPROVED: Enhanced rating status display -->
            <div v-if="ratingData && !ratingData.canRate" class="alert alert-warning">
              <div class="d-flex align-items-center">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <div>
                  <strong>Không thể đánh giá:</strong>
                  <p class="mb-0">{{ ratingData.message || 'Không thể đánh giá sản phẩm này' }}</p>
                </div>
              </div>
            </div>

            <!-- IMPROVED: Enhanced rating form -->
            <div v-if="ratingData && ratingData.canRate">
              <div class="rating-section">
                <label class="review-rating-label">
                  <i class="fas fa-star me-2"></i>Đánh giá sản phẩm:
                </label>

                <div class="review-stars">
                  <span
                    v-for="star in 5"
                    :key="star"
                    class="review-star"
                    :class="{
                      active: star <= selectedRating,
                      hover: star <= hoverRating && star > selectedRating
                    }"
                    @click="setRating(star)"
                    @mouseover="hoverRating = star"
                    @mouseleave="hoverRating = 0"
                    :title="`${star} sao - ${getRatingText(star)}`"
                  >
                    ★
                  </span>
                </div>

                <div class="rating-text" v-if="selectedRating > 0">
                  <i class="fas fa-quote-left me-1"></i>
                  {{ getRatingText(selectedRating) }}
                </div>

                <!-- IMPROVED: Enhanced comment section -->
                <div class="comment-section">
                  <label class="review-comment-label">
                    <i class="fas fa-comment me-2"></i>Nhận xét:
                  </label>
                  <textarea
                    v-model="comment"
                    class="review-comment-box"
                    rows="4"
                    placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                    maxlength="500"
                    :class="{ 'is-invalid': commentError }"
                    @input="handleCommentInput"
                  ></textarea>
                  <div class="d-flex justify-content-between align-items-center">
                    <div class="character-count">
                      {{ comment.length }}/500 ký tự
                    </div>
                    <small v-if="commentError" class="text-danger">{{ commentError }}</small>
                  </div>
                </div>

                <!-- Image upload section -->
                <div class="image-upload-section">
                  <label class="image-upload-label">
                    <i class="fas fa-camera me-2"></i>Thêm ảnh đánh giá (tối đa 5 ảnh):
                  </label>

                  <div class="upload-button-container">
                    <input
                      ref="fileInput"
                      type="file"
                      multiple
                      accept="image/*"
                      @change="handleFileSelect"
                      class="file-input"
                      :disabled="uploading"
                    />
                    <button
                      @click="triggerFileInput"
                      class="upload-btn"
                      :disabled="uploading || selectedImages.length >= 5"
                    >
                      <span v-if="uploading">
                        <i class="fas fa-spinner fa-spin me-1"></i>Đang upload...
                      </span>
                      <span v-else>
                        <i class="fas fa-camera me-1"></i>Chọn ảnh
                      </span>
                    </button>
                    <span class="upload-hint">Mỗi ảnh không quá 5MB</span>
                  </div>

                  <!-- Image preview -->
                  <div v-if="selectedImages.length > 0" class="image-preview-container">
                    <div class="image-preview-grid">
                      <div
                        v-for="(image, index) in selectedImages"
                        :key="index"
                        class="image-preview-item"
                      >
                        <img :src="image.preview" :alt="`Preview ${index + 1}`" class="preview-image" />
                        <button
                          @click="removeImage(index)"
                          class="remove-image-btn"
                          type="button"
                          title="Xóa ảnh"
                        >
                          <i class="fas fa-times"></i>
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- Upload progress -->
                  <div v-if="uploading" class="upload-progress">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                    </div>
                    <p class="progress-text">Đang upload ảnh... {{ uploadProgress }}%</p>
                  </div>
                </div>

                <!-- IMPROVED: Enhanced action buttons -->
                <div class="button-group">
                  <button
                    class="review-submit-btn"
                    @click="submitRating"
                    :disabled="submitting || selectedRating === 0 || commentError"
                    :title="getSubmitButtonTitle()"
                  >
                    <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-paper-plane me-2"></i>
                    {{ submitting ? 'Đang gửi...' : 'Gửi đánh giá' }}
                  </button>
                  <button class="review-cancel-btn" @click="goBack" :disabled="submitting">
                    <i class="fas fa-times me-2"></i>Hủy
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- IMPROVED: Enhanced success modal -->
        <div v-if="submitted" class="modal-overlay" @click.self="closeSuccessModal" role="dialog" aria-modal="true" aria-labelledby="success-modal-title">
          <div class="success-message" @click.stop tabindex="-1">
            <div class="success-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <h3 id="success-modal-title">Cảm ơn bạn đã đánh giá!</h3>
            <p>Đánh giá của bạn đã được gửi thành công.</p>
            <div class="rating-summary">
              <div class="submitted-rating">
                <span class="rating-stars">
                  {{ '★'.repeat(selectedRating) }}{{ '☆'.repeat(5 - selectedRating) }}
                </span>
                <span class="rating-text">{{ getRatingText(selectedRating) }}</span>
              </div>
              <div v-if="comment.trim()" class="submitted-comment">
                <strong>Nhận xét:</strong> "{{ comment.trim() }}"
              </div>
            </div>
            <div class="button-group">
              <button class="btn btn-primary" @click="goToOrders">
                <i class="fas fa-list me-2"></i>Xem đơn hàng
              </button>
              <button class="btn btn-outline-secondary" @click="closeSuccessModal">
                <i class="fas fa-times me-2"></i>Đóng
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>



<script setup>
import { ref, onMounted, computed, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '@/axios'
import Swal from 'sweetalert2'

const router = useRouter()
const route = useRoute()

const ratingData = ref(null)
const selectedRating = ref(0)
const hoverRating = ref(0)
const comment = ref('')
const loading = ref(true)
const error = ref('')
const submitting = ref(false)
const submitted = ref(false)

const currentImageUrl = ref('')
const imageLoaded = ref(false)
const imageErrorOccurred = ref(false)
const imageAttempts = ref(0)
// Thêm vào phần script setup
const selectedImages = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const fileInput = ref(null)

const triggerFileInput = () => fileInput.value.click()

const handleFileSelect = async (event) => {
  const files = Array.from(event.target.files)

   if (selectedImages.value.length + files.length > 5) {
    await Swal.fire({
      icon: 'warning',
      title: 'Quá giới hạn ảnh',
      text: 'Chỉ được chọn tối đa 5 ảnh',
      confirmButtonText: 'OK',
      confirmButtonColor: '#cd9b32'
    })
    return
  }

  files.forEach(file => {
  if (file.size > 5 * 1024 * 1024) {
    Swal.fire({
      icon: 'error',
      title: 'File quá lớn',
      html: `File <strong>${file.name}</strong> vượt quá giới hạn 5MB`,
      confirmButtonText: 'OK',
      confirmButtonColor: '#cd9b32'
    })
    return
  }


    if (!file.type.startsWith('image/')) {
  Swal.fire({
    icon: 'error',
    title: 'Loại file không hợp lệ',
    html: `File <strong>${file.name}</strong> không phải là ảnh`,
    confirmButtonText: 'OK',
    confirmButtonColor: '#cd9b32'
  })
  return
}

    const reader = new FileReader()
    reader.onload = (e) => {
      selectedImages.value.push({
        file: file,
        preview: e.target.result,
        name: file.name
      })
    }
    reader.readAsDataURL(file)
  })

  event.target.value = ''
}

const removeImage = (index) => {
  selectedImages.value.splice(index, 1)
}

const uploadImages = async () => {
  if (selectedImages.value.length === 0) return []

  try {
    uploading.value = true
    uploadProgress.value = 0

    const formData = new FormData()
    selectedImages.value.forEach(image => {
      formData.append('images', image.file)
    })

    const response = await axios.post('/api/ratings/upload-images', formData, {
      withCredentials: true,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        uploadProgress.value = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        )
      }
    })

    if (response.data.success) {
      return response.data.data.imagePaths
    } else {
      throw new Error(response.data.message || 'Upload ảnh thất bại')
    }
  } catch (error) {
    console.error('Error uploading images:', error)
    throw new Error(error.response?.data?.message || 'Không thể upload ảnh')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

const commentError = computed(() => {
  if (comment.value.length > 500) {
    console.warn('[Validation] Comment vượt quá 500 ký tự')
    return 'Nhận xét không được vượt quá 500 ký tự'
  }
  return null
})

const generateImageUrls = (imagePath) => {
  console.log('[Image] Generate URLs from', imagePath)
  if (!imagePath) return []

  const urls = []

  if (imagePath.startsWith('http')) {
    urls.push(imagePath)
    return urls
  }

  // Ưu tiên đường dẫn localhost:8080/images/ trước
  let cleanPath = imagePath
  if (cleanPath.startsWith("/")) cleanPath = cleanPath.substring(1)
  
  // Thêm đường dẫn localhost:8080/images/ đầu tiên
  if (cleanPath) {
    urls.push(`http://localhost:8080/images/${cleanPath}`)
  }
  
  if (ratingData.value?.productImage) {
    urls.push(`http://localhost:8080/images/${ratingData.value.productImage}`)
  }

  if (!cleanPath.startsWith("images/")) {
    urls.push(`/images/${cleanPath}`)
  } else {
    urls.push(`/${cleanPath}`)
  }

  if (cleanPath.startsWith("images/")) {
    const withoutPrefix = cleanPath.substring(7)
    urls.push(`/images/${withoutPrefix}`)
  }

  urls.push(`/${imagePath}`)

  const filename = cleanPath.split("/").pop()
  if (filename) {
    urls.push(`http://localhost:8080/images/${filename}`)
    urls.push(`/images/${filename}`)
  }

  const unique = [...new Set(urls)]
  console.log("[Image] Generated URLs:", unique)
  return unique
}

const loadProductImage = () => {
  console.log('[Image] Load product image...')
  if (!ratingData.value) return

  const imagePath = ratingData.value.productImage || ratingData.value.imageUrl
  if (!imagePath) {
    imageErrorOccurred.value = true
    console.warn('[Image] Không có đường dẫn ảnh')
    return
  }

  const imageUrls = generateImageUrls(imagePath)
  imageAttempts.value = 0
  imageLoaded.value = false
  imageErrorOccurred.value = false

  tryLoadImage(imageUrls, 0)
}

const tryLoadImage = (urls, index) => {
  console.log(`[Image] Try loading URL index ${index}:`, urls[index])
  if (index >= urls.length) {
    console.warn('[Image] All attempts failed')
    imageErrorOccurred.value = true
    currentImageUrl.value = ''
    return
  }

  currentImageUrl.value = urls[index]
  imageAttempts.value = index + 1
}

const handleImageLoad = () => {
  console.log('[Image] Loaded successfully')
  imageLoaded.value = true
  imageErrorOccurred.value = false
}

const handleImageError = (event) => {
  console.warn('[Image] Load failed:', currentImageUrl.value)
  event.target.src = ''
  const imagePath = ratingData.value?.productImage || ratingData.value?.imageUrl
  const urls = generateImageUrls(imagePath)

  if (imageAttempts.value < urls.length) {
    tryLoadImage(urls, imageAttempts.value)
  } else {
    console.error('[Image] Exhausted all fallback URLs')
    imageErrorOccurred.value = true
    imageLoaded.value = false
    currentImageUrl.value = ''
  }
}

const loadRatingData = async () => {
  console.log('[Data] Loading rating data...')
  try {
    loading.value = true
    error.value = ''

    const storedData = sessionStorage.getItem('ratingData')
    console.log('[Session] Found ratingData:', storedData)

    if (storedData) {
      try {
        ratingData.value = JSON.parse(storedData)
        console.log('[Session] Parsed ratingData:', ratingData.value)
        setTimeout(loadProductImage, 100)
        loading.value = false
        return
      } catch (e) {
        console.error('[Session] Parse error:', e)
      }
    }

    const orderItemId = route.query.orderItemId
    console.log('[Route] orderItemId:', orderItemId)

    if (!orderItemId) {
      error.value = 'Không tìm thấy thông tin sản phẩm để đánh giá'
      console.warn('[Route] Thiếu orderItemId')
      return
    }

    const res = await axios.get(`/api/ratings/order-item/${orderItemId}`, {
      withCredentials: true,
      timeout: 10000
    })

    console.log('[API] Response:', res)

      if (res.data?.success) {
        ratingData.value = res.data.data
        // Ensure product_image is used if available
        if (res.data.data.product_image) {
          ratingData.value.productImage = res.data.data.product_image
        }
        console.log("[API] ratingData:", ratingData.value)
        setTimeout(loadProductImage, 100)

      if (!ratingData.value.canRate) {
        error.value = ratingData.value.message || 'Không thể đánh giá sản phẩm này'
        console.warn('[Permission] Không thể đánh giá:', error.value)
        console.warn('[Permission] Order status:', ratingData.value.orderStatus)
        console.warn('[Permission] Can rate:', ratingData.value.canRate)
      }
    } else {
      error.value = res.data?.message || 'Không thể tải thông tin sản phẩm'
      console.warn('[API] Lỗi từ server:', error.value)
    }

  } catch (err) {
    console.error('[Axios Error]', err)

    if (err.code === 'ECONNABORTED') {
      error.value = 'Timeout: Không thể kết nối đến server'
    } else if (err.response?.status === 401) {
      error.value = 'Bạn chưa đăng nhập. Vui lòng đăng nhập để đánh giá sản phẩm.'
    } else if (err.response?.status === 404) {
      error.value = 'Không tìm thấy sản phẩm hoặc đường dẫn API sai'
    } else {
      error.value = err.response?.data?.message || 'Có lỗi xảy ra khi tải thông tin sản phẩm'
    }
  } finally {
    loading.value = false
    console.log('[Data] Done loading rating data')
  }
}

const retryLoadData = () => {
  console.log('[Action] Retry loading rating data')
  loadRatingData()
}

const setRating = (r) => {
  console.log('[Action] Set rating:', r)
  selectedRating.value = r
  console.log('[State] selectedRating updated to:', selectedRating.value)
}

const handleCommentInput = (event) => {
  console.log('[Input] Comment input:', event.target.value)
  console.log('[State] comment length:', comment.value.length)
  console.log('[State] commentError:', commentError.value)
}

const getRatingText = (r) => {
  const text = ({
    1: 'Rất không hài lòng',
    2: 'Không hài lòng',
    3: 'Bình thường',
    4: 'Hài lòng',
    5: 'Rất hài lòng'
  }[r] || '')
  console.log('[Text] Rating text:', text)
  return text
}

const getProductDescription = () => {
  const text = ratingData.value ? `Đánh giá cho sản phẩm từ đơn hàng #${ratingData.value.orderId}` : ''
  console.log('[Text] Product description:', text)
  return text
}

const hasVariantInfo = () => {
  const result = !!ratingData.value && (
    ratingData.value.size ||
    ratingData.value.flavor ||
    ratingData.value.shape ||
    ratingData.value.origin
  )
  console.log('[Check] hasVariantInfo:', result)
  return result
}

const getSubmitButtonTitle = () => {
  let title = ''
  if (selectedRating.value === 0) title = 'Vui lòng chọn số sao đánh giá'
  else if (commentError.value) title = commentError.value
  else if (submitting.value) title = 'Đang gửi đánh giá...'
  else title = 'Gửi đánh giá của bạn'
  console.log('[Button Title]', title)
  return title
}

const submitRating = async () => {
  if (selectedRating.value === 0) {
    alert('Vui lòng chọn số sao đánh giá')
    return
  }
   // Kiểm tra orderItemId có tồn tại không
  if (!ratingData.value?.orderItemId) {
    alert('Không tìm thấy thông tin sản phẩm để đánh giá');
    return;
  }

  if (commentError.value) {
    alert(commentError.value)
    return
  }

  submitting.value = true
  console.log('[Submit] Bắt đầu gửi đánh giá')

  try {
// Upload images first if any
    let imagePaths = []
    if (selectedImages.value.length > 0) {
      imagePaths = await uploadImages()
    }

    const payload = {
      orderItemId: ratingData.value.orderItemId,
      rating: selectedRating.value,
      comment: comment.value.trim(),
      imagePaths: imagePaths
    }

    console.log('[Submit Payload]', payload)

    const res = await axios.post('/api/ratings/submit', payload, {
      withCredentials: true,
      timeout: 10000
    })

    console.log('[Submit Response]', res)

    if (res.data?.success) {
      submitted.value = true
      sessionStorage.removeItem('ratingData')
      console.log('[Submit] Thành công')
    } else {
      throw new Error(res.data?.message || 'Lỗi khi gửi đánh giá')
    }

  } catch (err) {
    let msg = 'Có lỗi xảy ra khi gửi đánh giá'
    console.error('[Submit Error]', err)

    if (err.code === 'ECONNABORTED') {
      msg = 'Timeout: Không thể kết nối đến server'
    } else if (err.response?.status === 401) {
      msg = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'
      router.push('/login')
      return
    } else {
      msg = err.response?.data?.message || msg
    }

    alert(msg)
  } finally {
    submitting.value = false
    console.log('[Submit] Hoàn tất')
  }
}

const goBack = () => {
  console.log('[Nav] goBack')
  router.go(-1)
}

const goToOrders = () => {
  console.log('[Nav] to /donHang')
  router.push('/donHang')
}

const closeSuccessModal = () => {
  submitted.value = false
  goToOrders()
}

// Add keyboard event listener for Escape key
const handleKeydown = (event) => {
  if (event.key === 'Escape' && submitted.value) {
    closeSuccessModal()
  }
}

// Focus management for modal
const focusModal = () => {
  if (submitted.value) {
    setTimeout(() => {
      const modalElement = document.querySelector('.success-message')
      if (modalElement) {
        modalElement.focus()
      }
    }, 100)
  }
}

// Body scroll lock
const lockBodyScroll = () => {
  document.body.style.overflow = 'hidden'
}

const unlockBodyScroll = () => {
  document.body.style.overflow = ''
}

// Add and remove event listeners
onMounted(() => {
  console.log('[Mounted] Start')
  loadRatingData()
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})

// Watch for modal state changes
watch(submitted, (newValue) => {
  if (newValue) {
    focusModal()
  }
})
</script>


<style scoped>
/* Fix scrolling issue */
.page-dang-nhap {
  overflow-y: auto !important;
  height: auto !important;
  min-height: 100vh;
}

.page-dang-nhap main {
  overflow-y: visible !important;
}

.review-wrapper {
  overflow-y: visible !important;
}

/* Ensure body can scroll */
:global(body) {
  overflow-y: auto !important;
}

:global(html) {
  overflow-y: auto !important;
}

:global(body.modal-open) {
  overflow-y: auto !important;
}

/* Thêm vào phần style */
.image-upload-section {
  margin: 20px 0;
}

.upload-button-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.file-input {
  display: none;
}

.upload-btn {
  padding: 8px 16px;
  background-color: #4b5563;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.upload-btn:hover:not(:disabled) {
  background-color: #374151;
}

.upload-btn:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.upload-hint {
  font-size: 12px;
  color: #6b7280;
}

.image-preview-container {
  margin-top: 15px;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
}

.image-preview-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.preview-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  display: block;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  background-color: rgba(220, 38, 38, 0.8);
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
}

.remove-image-btn:hover {
  background-color: rgba(220, 38, 38, 1);
}

.upload-progress {
  margin-top: 15px;
}

.progress-bar {
  height: 6px;
  background-color: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #3b82f6;
  transition: width 0.3s ease;
}

.progress-text {
  margin-top: 5px;
  font-size: 12px;
  color: #6b7280;
}

/* Enhanced styling for better UX */
.page-dang-nhap {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f8fafc;
  overflow-x: hidden;
}

.full-width-banner {
  width: 100vw;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  min-height: 280px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  text-align: center;
  margin: 0;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.4));
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
}

.title-bread-crumb {
  font-size: 48px;
  color: #cd9b32;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.breadcrumb {
  display: flex;
  justify-content: center;
  align-items: center;
  background: none;
  margin: 0;
  padding: 0;
}

.breadcrumb li {
  display: flex;
  align-items: center;
  color: #ffffff;
  font-size: 16px;
}

.breadcrumb li a {
  color: #e2e8f0;
  text-decoration: none;
  transition: color 0.3s ease;
}

.breadcrumb li a:hover {
  color: #facc15;
}

.breadcrumb li .mr_lr svg {
  width: 8px;
  height: 8px;
  margin: 0 8px;
  fill: #e2e8f0;
}

.review-wrapper {
  max-width: 900px;
  margin: 48px auto;
  padding: 32px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
}

.review-wrapper:hover {
  transform: translateY(-4px);
}

.review-heading {
  text-align: center;
  color: #15803d;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 32px;
  letter-spacing: -0.5px;
}

.review-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
  align-items: start;
}

.review-image-section {
  text-align: center;
}

/* ENHANCED: Better image container with loading states */
.image-container {
  position: relative;
  width: 220px;
  height: 220px;
  margin: 0 auto;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.review-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.review-image:hover {
  transform: scale(1.05);
}

.review-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  color: #9ca3af;
  position: absolute;
  top: 0;
  left: 0;
}

.review-image-placeholder.loading {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.no-image-content i {
  font-size: 48px;
  margin-bottom: 8px;
  color: #d1d5db;
}

.no-image-content p {
  margin: 0;
  font-size: 14px;
  color: #9ca3af;
}

.review-description {
  font-size: 14px;
  color: #6b7280;
  margin-top: 12px;
  line-height: 1.5;
}

.review-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-product-name {
  font-size: 24px;
  color: #1f2937;
  font-weight: 600;
  margin-bottom: 8px;
}

/* IMPROVED: Enhanced variant info styling */
.product-variant-info {
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.variant-details {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.variant-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #495057;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.variant-item i {
  color: #007bff;
}

/* IMPROVED: Enhanced rating section */
.rating-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-rating-label {
  font-size: 16px;
  color: #4b5563;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.review-rating-label i {
  color: #facc15;
}

.review-stars {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.review-star {
  font-size: 32px;
  color: #d1d5db;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.review-star:hover,
.review-star.active {
  color: #facc15;
  transform: scale(1.1);
}

.review-star.hover {
  color: #fef08a;
}

.rating-text {
  font-size: 16px;
  color: #6b7280;
  font-style: italic;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
}

.rating-text i {
  color: #6c757d;
}

/* IMPROVED: Enhanced comment section */
.comment-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.review-comment-label {
  font-size: 16px;
  color: #374151;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.review-comment-label i {
  color: #6c757d;
}

.review-comment-box {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 12px;
  resize: vertical;
  font-size: 14px;
  line-height: 1.5;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  font-family: inherit;
}

.review-comment-box:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.review-comment-box.is-invalid {
  border-color: #dc3545;
}

.character-count {
  font-size: 12px;
  color: #6b7280;
}

.button-group {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 8px;
}

.review-submit-btn,
.review-cancel-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.review-submit-btn {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.review-submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.review-submit-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.review-cancel-btn {
  background: #6b7280;
  color: white;
}

.review-cancel-btn:hover:not(:disabled) {
  background: #4b5563;
  transform: translateY(-2px);
}

/* IMPROVED: Enhanced modal styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeInBackdrop 0.3s ease-in-out;
  backdrop-filter: blur(4px);
}

.success-message {
  background: #ffffff;
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  animation: slideIn 0.3s ease-in-out;
  position: relative;
  z-index: 10000;
  pointer-events: auto;
}

.success-icon {
  font-size: 64px;
  color: #15803d;
  margin-bottom: 20px;
  animation: bounceIn 0.5s ease;
}

.success-message h3 {
  color: #15803d;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  pointer-events: auto;
}

.success-message p {
  color: #4b5563;
  font-size: 16px;
  margin-bottom: 24px;
  pointer-events: auto;
}

/* NEW: Rating summary in success modal */
.rating-summary {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  text-align: left;
  pointer-events: auto;
}

.submitted-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.submitted-rating .rating-stars {
  font-size: 18px;
  color: #facc15;
}

.submitted-rating .rating-text {
  font-weight: 500;
  color: #374151;
}

.submitted-comment {
  font-size: 14px;
  color: #6b7280;
  font-style: italic;
}

/* Button styling */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  position: relative;
  z-index: 1;
  pointer-events: auto;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  position: relative;
  z-index: 2;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-outline-secondary {
  border: 1px solid #6c757d;
  color: #6c757d;
  background: transparent;
}

.btn-outline-secondary:hover {
  background: #6c757d;
  color: white;
}

.btn-outline-primary {
  border: 1px solid #3b82f6;
  color: #3b82f6;
  background: transparent;
}

.btn-outline-primary:hover {
  background: #3b82f6;
  color: white;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

/* Alert styling */
.alert {
  border: none;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.alert-warning {
  background: linear-gradient(135deg, #fef3cd, #ffeaa7);
  border-left: 4px solid #ffc107;
  color: #856404;
}

.alert-danger {
  background: linear-gradient(135deg, #f8d7da, #f5c6cb);
  border-left: 4px solid #dc3545;
  color: #721c24;
}

/* Loading spinner */
.spinner-border {
  width: 1.5rem;
  height: 1.5rem;
  border-width: 0.2em;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  border-width: 0.15em;
}

/* Animations */
@keyframes fadeInBackdrop {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateY(50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes bounceIn {
  0% { transform: scale(0.3); opacity: 0; }
  50% { transform: scale(1.2); opacity: 1; }
  100% { transform: scale(1); }
}

/* Responsive design */
@media (max-width: 768px) {
  .review-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .image-container {
    width: 180px;
    height: 180px;
  }

  .button-group {
    justify-content: center;
    flex-direction: column;
  }

  .title-bread-crumb {
    font-size: 36px;
  }

  .variant-details {
    flex-direction: column;
    gap: 8px;
  }

  .variant-item {
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .review-wrapper {
    padding: 24px 16px;
    margin: 24px;
  }

  .review-heading {
    font-size: 24px;
  }

  .review-product-name {
    font-size: 20px;
  }

  .success-message {
    padding: 24px;
    width: 95%;
  }

  .review-stars {
    justify-content: center;
  }

  .review-star {
    font-size: 28px;
  }

  .image-container {
    width: 160px;
    height: 160px;
  }
}
</style>



