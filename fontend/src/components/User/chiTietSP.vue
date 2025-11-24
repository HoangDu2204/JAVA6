<template>
  <div class="lien-he">
    <main>
      <div>

        <section class="bread-crumb"
          style="background: linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)), url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg) center no-repeat;">
          <div class="container">
            <div class="title-bread-crumb" style="padding-left: 100px;">Chi Tiết Sản Phẩm</div>
            <ul class="breadcrumb">
              <li class="home">
                <a href="/">Trang chủ ></a>
                <a href="/">{{ product.name }}</a>
              </li>
            </ul>
          </div>
        </section>

        <div class="abc">
          <div class="product-header">

            <div class="product-image">
              <img :src="selectedImage" alt="Ảnh chính" />
              <div class="product-thumbnails">
                <img v-for="(img, index) in product.imageUrls" :key="index"
                  :src="img.startsWith('http') ? img : 'http://localhost:8080/images/' + img"
                  @click="selectedImage = img.startsWith('http') ? img : 'http://localhost:8080/images/' + img" />
              </div>
            </div>


            <div class="product-details">
              <h1 class="product-title">{{ product.name }}</h1>
              <p class="product-status">
                <span class="label">Loại:</span>
                <span class="value">{{ product.category }}</span>
                <span class="label">Tình trạng:</span>
               <span class="status">{{ selectedVariant && selectedVariant.quantity > 0 ? 'Còn hàng' : 'Hết hàng' }}</span>

              </p>

              <!-- <p class="product-price">
                {{ finalPrice.toLocaleString() }}₫
                <span class="original-price" v-if="selectedVariant">
                  {{ selectedVariant.price.toLocaleString() }}₫
                </span>
                <span class="discount" v-if="product.discountPercentage">
                  Tiết kiệm: {{ product.discountAmount.toLocaleString() }}₫
                </span>
              </p> -->
              <p class="product-price">
                {{ finalPrice.toLocaleString() }}₫
                <span class="original-price">
                  {{ originalPrice.toLocaleString() }}₫
                </span>
              </p>
              <p class="save-amount " v-if="amountSaved > 0">
                Tiết kiệm: {{ amountSaved.toLocaleString() }}₫
              </p>



             
              <div class="variant-section">
                <div class="mb-3">
                  <strong>Size:</strong>
                  <div class="d-flex flex-wrap gap-2 mt-1">
                    <button v-for="s in getUnique('size')" :key="s" @click="handleVariantSelection('size', s)"
                      :class="['btn', selectedSize === s ? 'btn-custom-selected' : isOptionAvailable('size', s) ? 'btn-outline-secondary' : 'btn-disabled']"
                      :disabled="!isOptionAvailable('size', s)">
                      {{ s }}
                    </button>
                  </div>
                </div>

                <div class="mb-3">
                  <strong>Hình dáng:</strong>
                  <div class="d-flex flex-wrap gap-2 mt-1">
                    <button v-for="s in getUnique('shape')" :key="s" @click="handleVariantSelection('shape', s)"
                      :class="['btn', selectedShape === s ? 'btn-custom-selected' : isOptionAvailable('shape', s) ? 'btn-outline-secondary' : 'btn-disabled']"
                      :disabled="!isOptionAvailable('shape', s)">
                      {{ s }}
                    </button>
                  </div>
                </div>

                <div class="mb-3">
                  <strong>Hương vị:</strong>
                  <div class="d-flex flex-wrap gap-2 mt-1">
                    <button v-for="s in getUnique('flavor')" :key="s" @click="handleVariantSelection('flavor', s)"
                      :class="['btn', selectedFlavor === s ? 'btn-custom-selected' : isOptionAvailable('flavor', s) ? 'btn-outline-secondary' : 'btn-disabled']"
                      :disabled="!isOptionAvailable('flavor', s)">
                      {{ s }}
                    </button>
                  </div>
                </div>

                <div class="mb-3">
                  <strong>Xuất xứ:</strong>
                  <div class="d-flex flex-wrap gap-2 mt-1">
                    <button v-for="s in getUnique('origin')" :key="s" @click="handleVariantSelection('origin', s)"
                      :class="['btn', selectedOrigin === s ? 'btn-custom-selected' : isOptionAvailable('origin', s) ? 'btn-outline-secondary' : 'btn-disabled']"
                      :disabled="!isOptionAvailable('origin', s)">
                      {{ s }}
                    </button>
                  </div>
                </div>
              </div>

              <!-- <div class="quantity">
                <label for="quantity">Số lượng:</label>
                <div class="quantity-controls">
                  <button class="decrease" @click="decreaseQuantity">-</button>
                  <input type="number" id="quantity" v-model.number="quantity" min="1" />
                  <button class="increase" @click="increaseQuantity">+</button>
                </div>
              </div> -->
              <div class="mb-3">
                <label class="fw-bold">Số lượng:</label>
                <div class="quantity-wrapper mt-1">
                  <button class="quantity-btn" @click="decreaseQuantity">−</button>
                  <div class="quantity-display">{{ quantity }}</div>
                  <button class="quantity-btn" @click="increaseQuantity">+</button>
                </div>
              </div>

              <div class="d-flex gap-3 my-4">


                <!-- <button class="d-flex align-items-center px-3 py-2"
                  style="border-radius: 8px; background-color: #d4a548; border: none; color: white; min-height: 60px;"
                  @click="handleAddToCart">
                  <div class="me-3 d-flex align-items-center">
                    <i class="bi bi-bag fs-3"></i>
                  </div>
                  <div class="text-start text-white">
                    <div class="fw-bold">THÊM VÀO GIỎ</div>
                    <small>Giao hàng tận nơi miễn phí</small>
                  </div>
                </button> -->
                <button class="d-flex align-items-center px-3 py-2"
                  :class="{ 'disabled-btn': !hasAvailableVariant }"
                  style="border-radius: 8px; background-color: #d4a548; border: none; color: white; min-height: 60px; cursor: pointer;"
                  @click="handleAddToCart" :disabled="!hasAvailableVariant">
                  <div class="me-3 d-flex align-items-center">
                    <i class="bi bi-bag fs-3"></i>
                  </div>
                  <div class="text-start text-white">
                    <div class="fw-bold">THÊM VÀO GIỎ</div>
                    <small>Giao hàng tận nơi miễn phí</small>
                  </div>
                </button>


                <button class="d-flex align-items-center px-3 py-2"
                  style="border-radius: 8px; background-color: #d4a548; border: none; color: white; min-height: 60px;">
                  <div class="me-3 d-flex align-items-center">
                    <i class="bi bi-heart fs-3"></i>
                  </div>
                  <div class="text-start text-white">
                    <div class="fw-bold">YÊU THÍCH</div>
                    <small>Thêm vào yêu thích để lưu lại nhé!</small>
                  </div>
                </button>
              </div>
              <div class="col-md-12">
                <div class="voucher-box mb-4">
                  <div class="cart-tinhtrang">
                    <div class="title_voucher">
                      <img src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/voucher.png?1735117293436"
                        alt="voucher"><span>Nhận voucher ngay !!!</span>
                    </div>
                    <div class="thump-check">
                      <div class="check-bar"></div>
                      <div class="check-bar1" style="width: 3.6%;"></div>
                      <div class="dot dot1" style="left: calc(30% - 10px);"></div>
                      <div class="dot dot2" style="left: calc(70% - 10px);"></div>
                      <div class="dot dot3" style="left: calc(100% - 10px);"></div>
                    </div>
                    <ul class="coupon1">
                      <li class="item-coupon1" v-for="(voucher, index) in eligibleVouchers" :key="voucher.code">
                        <div class="coupon-content1">
                          <b class="conlai">Còn {{ voucher.missingAmount.toLocaleString() }}₫</b>
                          để được nhận mã <strong>{{ voucher.code }}</strong>
                        </div>
                        <div class="coupon-code1 js-copy"
                          :class="{ disabled: voucher.missingAmount > 0 || copiedCodes.includes(voucher.code) }"
                          :data-copy="voucher.code"
                          @click="voucher.missingAmount <= 0 && !copiedCodes.includes(voucher.code) ? copyToClipboard(voucher.code) : null">
                          {{ copiedCodes.includes(voucher.code) ? 'Đã sao chép' : 'Sao chép' }}
                        </div>
                      </li>
                    </ul>
                  </div>

                </div>
              </div>








            </div>
          </div>
          <div class="description">
            <h2><span class="highlight">Mô tả sản phẩm</span></h2>
            <p>{{ product.description }}</p>
          </div>

          <!-- Phần đánh giá sản phẩm -->
          <div class="product-ratings">
            <h2><span class="highlight">Đánh giá sản phẩm</span></h2>
            
            <!-- Test section to verify component is working -->
            <!-- Removed the test section as requested -->
            
            <div class="row mt-4">
              <div class="col-md-3 text-center">
                <div class="overall-rating">
                  <div class="rating-number">{{ averageRating.toFixed(1) }}</div>
                  <div class="rating-stars">
                    <i v-for="star in 5" :key="star" 
                       :class="['bi', star <= averageRating ? 'bi-star-fill' : 'bi-star']"
                       style="color: #ffc107; font-size: 24px;"
                       :title="`${star} sao`"></i>
                    <!-- Fallback stars if Bootstrap Icons don't load -->
                    <div class="fallback-stars" style="display: none;">
                      <span v-for="star in 5" :key="star" 
                            :style="{ color: star <= averageRating ? '#ffc107' : '#ccc', fontSize: '24px', marginRight: '2px' }">
                        {{ star <= averageRating ? '★' : '☆' }}
                      </span>
                    </div>
                  </div>
                  <div class="total-ratings">{{ totalRatings }} đánh giá</div>
                
                 
                </div>
              </div>
              <div class="col-md-9">
                <div class="rating-bars">
                  <div v-for="(count, stars) in ratingDistribution" :key="stars" class="rating-bar-item">
                    <span class="stars-label">{{ stars }} sao</span>
                    <div class="progress" style="height: 8px;">
                      <div class="progress-bar bg-warning" 
                           :style="{ width: totalRatings > 0 ? (count / totalRatings * 100) + '%' : '0%' }"></div>
                    </div>
                    <span class="count-label">{{ count }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Danh sách đánh giá -->
            <div class="ratings-list">
              <!-- Debug info -->
              <div v-if="ratings.length === 0" class="no-ratings text-center py-4">
                <i class="bi bi-chat-dots" style="font-size: 3rem; color: #ccc;"></i>
                <p class="text-muted mt-2">Chưa có đánh giá nào cho sản phẩm này</p>
              </div>
              
              <div v-else>
                <div v-for="rating in ratings" :key="rating.id" class="rating-item mb-3">
                  <div class="d-flex align-items-center mb-1">
                    <!-- Avatar mặc định với icon person -->
                    <div v-if="!rating.userAvatar" class="default-avatar me-2" style="width: 40px; height: 40px; background: #f8f9fa; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 2px solid #e9ecef;">
                      <i class="bi bi-person" style="font-size: 20px; color: #6c757d;"></i>
                    </div>
                    <img v-else :src="rating.userAvatar" alt="User Avatar" class="rounded-circle me-2" style="width: 40px; height: 40px;">
                    <strong class="me-2">{{ rating.userFullName || 'Người dùng ẩn danh' }}</strong>
                    <div class="rating-stars">
                      <i v-for="star in 5" :key="star" 
                         :class="['bi', star <= rating.ratings ? 'bi-star-fill' : 'bi-star']"
                         style="color: #ffc107;"></i>
                      <!-- Debug info for individual rating -->
                      <span style="font-size: 10px; color: #666; margin-left: 5px;">
                        ({{ rating.ratings }}/5)
                      </span>
                    </div>
                  </div>
                  <p class="mb-1">{{ rating.comment }}</p>
                  
                  <!-- Hiển thị ảnh đánh giá nếu có -->
                  <div v-if="rating.reviewImages && rating.reviewImages.length > 0" class="rating-images mt-2">
                   

                    <div class="d-flex flex-wrap gap-2">
                      <img v-for="image in rating.reviewImages" :key="image.reviewImageId || image.id || Math.random()" 
                           :src="getImageUrl(image.images || image.image || image)"
                           :alt="'Review image'" 
                           class="review-image" 
                           style="width: 80px; height: 80px; object-fit: cover; border-radius: 8px; border: 2px solid #e9ecef; cursor: pointer;"
                           @click="openImageModal(getImageUrl(image.images || image.image || image))"
                           :title="'Click để xem ảnh lớn'"
                           @error="handleImageError"
                           @load="handleImageLoad" />
                    </div>
                  </div>
                  
                  <!-- Fallback: hiển thị thông tin ảnh ngay cả khi không load được -->
                  <div v-else-if="rating.reviewImages && Array.isArray(rating.reviewImages) && rating.reviewImages.length > 0" class="rating-images mt-2">
                    <div class="debug-images" style="font-size: 10px; color: #666; margin-bottom: 5px;">
                      Fallback: Found {{ rating.reviewImages.length }} images but they might be in different format
                    </div>
                    <div class="d-flex flex-wrap gap-2">
                      <div v-for="(image, index) in rating.reviewImages" :key="index" 
                           class="review-image-placeholder"
                           style="width: 80px; height: 80px; background: #f8f9fa; border: 2px dashed #e9ecef; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer;"
                           @click="showImageInfo(image)"
                           :title="'Click để xem thông tin ảnh'">
                        <i class="bi bi-image" style="font-size: 24px; color: #6c757d;"></i>
                        <small style="font-size: 8px; color: #666; position: absolute; bottom: 2px;">{{ index + 1 }}</small>
                      </div>
                    </div>
                  </div>
                  
                  <small class="text-muted">{{ new Date(rating.ratingDate).toLocaleDateString('vi-VN') }}</small>
                </div>
                
                <!-- Pagination -->
                <nav v-if="totalRatingPages > 1" aria-label="Page navigation example">
                  <ul class="pagination justify-content-center">
                    <li class="page-item" :class="{ disabled: currentRatingPage === 1 }">
                      <a class="page-link" href="#" @click.prevent="changeRatingPage(currentRatingPage - 1)">Trước</a>
                    </li>
                    <li class="page-item" v-for="page in totalRatingPages" :key="page" :class="{ active: page === currentRatingPage }">
                      <a class="page-link" href="#" @click.prevent="changeRatingPage(page)">{{ page }}</a>
                    </li>
                    <li class="page-item" :class="{ disabled: currentRatingPage === totalRatingPages }">
                      <a class="page-link" href="#" @click.prevent="changeRatingPage(currentRatingPage + 1)">Sau</a>
                    </li>
                  </ul>
                </nav>
              </div>
            </div>
          </div>

          <div class="related-products">
            <h2>Sản phẩm liên quan</h2>
            <img class="related-banner"
              src="https://bizweb.dktcdn.net/100/492/035/themes/919334/assets/title.png?1735117293436"
              alt="Banner liên quan" />

            <!-- Danh sách sản phẩm liên quan -->
            <div class="product-list" v-if="relatedProducts.length > 0">
              <div class="product-item" v-for="relatedProduct in relatedProducts" :key="relatedProduct.id">
                <div class="product-image-container">
                  <img :src="getProductImageUrl(relatedProduct)" :alt="relatedProduct.name" />
                  <div class="product-icons">
                    <button class="cart-icon-btn" @click="addRelatedToCart(relatedProduct)" title="Thêm vào giỏ hàng">
                      <i class="bi bi-bag"></i>
                    </button>
                    <button class="magnifier-icon-btn" @click="viewRelatedProduct(relatedProduct.id)" title="Xem chi tiết">
                      <i class="bi bi-eye"></i>
                    </button>
                  </div>
                </div>
                
                <!-- Badge giảm giá nếu có -->
                <div class="badge-container" v-if="relatedProduct.discountPercentage">
                  <div class="discount-badge">-{{ relatedProduct.discountPercentage }}%</div>
                </div>
                
                <div class="product-info">
                  <h5 class="product-name">{{ relatedProduct.name }}</h5>
                  <div class="product-price">
                    <span class="current-price">{{ formatPrice(getFinalPrice(relatedProduct)) }}₫</span>
                    <span class="old-price" v-if="relatedProduct.discountPercentage">
                      {{ formatPrice(relatedProduct.price) }}₫
                    </span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Thông báo khi không có sản phẩm liên quan -->
            <div v-else class="no-related-products">
              <p>Không có sản phẩm liên quan</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal xem ảnh đánh giá -->
    <div class="modal fade" id="imageModal" tabindex="-1" aria-labelledby="imageModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="imageModalLabel">Xem ảnh đánh giá</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <img :src="selectedReviewImage" alt="Review image" class="img-fluid" style="max-height: 70vh; object-fit: contain;" />
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/axios'
import Swal from 'sweetalert2'
import { Modal } from 'bootstrap'

const route = useRoute()
const router = useRouter()

const product = ref({})
const selectedImage = ref('')
const selectedSize = ref('')
const selectedShape = ref('')
const selectedFlavor = ref('')
const selectedOrigin = ref('')
const selectedVariant = ref(null)
const quantity = ref(1)
const relatedProducts = ref([]) // Thêm biến cho sản phẩm liên quan

// Lấy sản phẩm từ API
// const fetchProduct = async () => {
//   try {
//     const res = await api.get(`/api/products/${route.params.id}`)
//     product.value = res.data
//     selectedImage.value = 'http://localhost:8080/images/' + (res.data.imageUrls[0] || '')
//   } catch (err) {
//     console.error('Lỗi khi tải chi tiết sản phẩm:', err)
//   }
// }
const fetchProduct = async () => {
  try {
    const res = await api.get(`/api/products/${route.params.id}`)
    product.value = res.data
    selectedImage.value = 'http://localhost:8080/images/' + (res.data.imageUrls[0] || '')

    // Khởi tạo giá trị mặc định cho biến thể đầu tiên
    const firstVariant = res.data.productVariants?.[0] || null
    if (firstVariant) {
      // Chỉ chọn size đầu tiên, các biến thể khác sẽ được lọc tự động
      selectedSize.value = firstVariant.size
      selectedShape.value = ''
      selectedFlavor.value = ''
      selectedOrigin.value = ''
      updateVariant()
    }
    
    // Lấy đánh giá sản phẩm sau khi có thông tin sản phẩm
    fetchProductRatings()
    
    // Lấy sản phẩm liên quan
    fetchRelatedProducts()
  } catch (err) {
    console.error('Lỗi khi tải chi tiết sản phẩm:', err)
  }
}
watch([selectedSize, selectedShape, selectedFlavor, selectedOrigin], () => {
  updateVariant()
})



onMounted(() => {
  fetchProduct()
  fetchProductRatings()
  
  // Check if Bootstrap Icons are loaded
  setTimeout(() => {
    const testIcon = document.querySelector('.bi-star-fill')
    if (!testIcon || getComputedStyle(testIcon, '::before').content === 'none') {
      console.log('⚠️ Bootstrap Icons not loaded, showing fallback stars')
      document.querySelectorAll('.fallback-stars').forEach(el => el.style.display = 'block')
      document.querySelectorAll('.rating-stars .bi').forEach(el => el.style.display = 'none')
    }
  }, 1000)
})

// Lấy danh sách thuộc tính không trùng
const getUnique = (field) => {
  return [...new Set(product.value.productVariants?.map(v => v[field]) || [])]
}

// Lấy danh sách thuộc tính có sẵn dựa trên các lựa chọn hiện tại
const getAvailableOptions = (field) => {
  if (!product.value.productVariants) return []
  
  let filteredVariants = product.value.productVariants
  
  // Lọc theo các lựa chọn đã chọn
  if (selectedSize.value && field !== 'size') {
    filteredVariants = filteredVariants.filter(v => v.size === selectedSize.value)
  }
  if (selectedShape.value && field !== 'shape') {
    filteredVariants = filteredVariants.filter(v => v.shape === selectedShape.value)
  }
  if (selectedFlavor.value && field !== 'flavor') {
    filteredVariants = filteredVariants.filter(v => v.flavor === selectedFlavor.value)
  }
  if (selectedOrigin.value && field !== 'origin') {
    filteredVariants = filteredVariants.filter(v => v.origin === selectedOrigin.value)
  }
  
  return [...new Set(filteredVariants.map(v => v[field]))]
}

// Kiểm tra xem một tùy chọn có khả dụng hay không
const isOptionAvailable = (field, value) => {
  if (!product.value.productVariants) return false
  
  let filteredVariants = product.value.productVariants
  
  // Lọc theo các lựa chọn đã chọn
  if (selectedSize.value && field !== 'size') {
    filteredVariants = filteredVariants.filter(v => v.size === selectedSize.value)
  }
  if (selectedShape.value && field !== 'shape') {
    filteredVariants = filteredVariants.filter(v => v.shape === selectedShape.value)
  }
  if (selectedFlavor.value && field !== 'flavor') {
    filteredVariants = filteredVariants.filter(v => v.flavor === selectedFlavor.value)
  }
  if (selectedOrigin.value && field !== 'origin') {
    filteredVariants = filteredVariants.filter(v => v.origin === selectedOrigin.value)
  }
  
  // Kiểm tra xem tùy chọn có tồn tại trong các biến thể đã lọc không
  return filteredVariants.some(v => v[field] === value)
}

// Cập nhật biến thể đã chọn
const updateVariant = () => {
  if (!product.value.productVariants || product.value.productVariants.length === 0) {
    selectedVariant.value = null
    return
  }

  // Tìm biến thể khớp với các lựa chọn đã chọn
  // Chỉ so sánh các thuộc tính đã được chọn (không rỗng)
  const matchedVariant = product.value.productVariants.find(v => {
    const sizeMatch = !selectedSize.value || v.size === selectedSize.value
    const shapeMatch = !selectedShape.value || v.shape === selectedShape.value
    const flavorMatch = !selectedFlavor.value || v.flavor === selectedFlavor.value
    const originMatch = !selectedOrigin.value || v.origin === selectedOrigin.value
    
    return sizeMatch && shapeMatch && flavorMatch && originMatch
  })

  // Nếu tìm thấy biến thể khớp, sử dụng nó
  if (matchedVariant) {
    selectedVariant.value = matchedVariant
  } else {
    // Nếu không tìm thấy, sử dụng biến thể đầu tiên có sẵn
    const availableVariant = product.value.productVariants.find(v => v.quantity > 0) || product.value.productVariants[0]
    selectedVariant.value = availableVariant || null
  }
}

// Xử lý khi chọn một biến thể
const handleVariantSelection = (field, value) => {
  // Nếu đã chọn rồi thì bỏ chọn (toggle)
  if (field === 'size' && selectedSize.value === value) {
    selectedSize.value = ''
  } else if (field === 'shape' && selectedShape.value === value) {
    selectedShape.value = ''
  } else if (field === 'flavor' && selectedFlavor.value === value) {
    selectedFlavor.value = ''
  } else if (field === 'origin' && selectedOrigin.value === value) {
    selectedOrigin.value = ''
  } else {
    // Cập nhật giá trị đã chọn
    if (field === 'size') selectedSize.value = value
    else if (field === 'shape') selectedShape.value = value
    else if (field === 'flavor') selectedFlavor.value = value
    else if (field === 'origin') selectedOrigin.value = value
  }
  
  // Reset các lựa chọn khác nếu không còn khả dụng
  const availableVariants = product.value.productVariants?.filter(v => {
    if (field === 'size') return v.size === value
    else if (field === 'shape') return v.shape === value
    else if (field === 'flavor') return v.flavor === value
    else if (field === 'origin') return v.origin === value
    return true
  }) || []
  
  // Reset các lựa chọn không còn khả dụng
  if (field !== 'size' && selectedSize.value) {
    if (!availableVariants.some(v => v.size === selectedSize.value)) {
      selectedSize.value = ''
    }
  }
  if (field !== 'shape' && selectedShape.value) {
    if (!availableVariants.some(v => v.shape === selectedShape.value)) {
      selectedShape.value = ''
    }
  }
  if (field !== 'flavor' && selectedFlavor.value) {
    if (!availableVariants.some(v => v.flavor === selectedFlavor.value)) {
      selectedFlavor.value = ''
    }
  }
  if (field !== 'origin' && selectedOrigin.value) {
    if (!availableVariants.some(v => v.origin === selectedOrigin.value)) {
      selectedOrigin.value = ''
    }
  }
  
  updateVariant()
}

// Tăng/giảm số lượng
const increaseQuantity = () => {
  const max = selectedVariant.value?.quantity ?? product.value.productVariants?.[0]?.quantity ?? 1
  if (quantity.value < max) {
    quantity.value++
  } else {
    Swal.fire({
      icon: 'warning',
      title: 'Vượt quá số lượng tồn kho',
      text: 'Bạn đã chọn đến số lượng tối đa hiện có trong kho!',
      confirmButtonText: 'OK',
      position: 'center'
    })
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) quantity.value--
}

// Thêm vào giỏ hàng (dùng JWT - api đã gắn token)
const addToCart = async (variantId) => {
  try {
    // Kiểm tra xem user có đăng nhập không
    const token = localStorage.getItem('token')
    if (!token) {
      Swal.fire({
        icon: 'warning',
        title: 'Bạn chưa đăng nhập',
        text: 'Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng',
        confirmButtonText: 'Đăng nhập',
        showCancelButton: true,
        cancelButtonText: 'Hủy'
      }).then((result) => {
        if (result.isConfirmed) {
          router.push('/dangNhap')
        }
      })
      return
    }

    const response = await api.post('/api/cart/add', {
      variantId,
      quantity: quantity.value
    })
    
    Swal.fire({
      icon: 'success',
      title: 'Thêm vào giỏ hàng thành công!',
      text: 'Sản phẩm đã được thêm vào giỏ hàng của bạn',
      timer: 2000,
      showConfirmButton: false,
      toast: true,
      position: 'top-end'
    }).then(() => {
      router.push('/gioHang')
    })
  } catch (err) {
    console.error('❌ Lỗi khi thêm vào giỏ hàng:', err)
    
    let errorMessage = 'Không thể thêm sản phẩm vào giỏ hàng. Vui lòng thử lại.'
    
    if (err.response) {
      // Lỗi từ server
      const status = err.response.status
      const data = err.response.data
      
      if (status === 401 || status === 403) {
        errorMessage = 'Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'
        Swal.fire({
          icon: 'warning',
          title: 'Phiên đăng nhập hết hạn',
          text: errorMessage,
          confirmButtonText: 'Đăng nhập',
          showCancelButton: true,
          cancelButtonText: 'Hủy'
        }).then((result) => {
          if (result.isConfirmed) {
            localStorage.removeItem('token')
            router.push('/dangNhap')
          }
        })
        return
      } else if (status === 400) {
        errorMessage = data?.error || data?.message || 'Thông tin không hợp lệ'
      } else if (status === 404) {
        errorMessage = 'Không tìm thấy sản phẩm'
      } else if (status >= 500) {
        errorMessage = 'Lỗi server. Vui lòng thử lại sau.'
      }
    } else if (err.request) {
      // Không nhận được response từ server
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.'
    }
    
    Swal.fire({
      icon: 'error',
      title: 'Thêm vào giỏ hàng thất bại',
      text: errorMessage,
      confirmButtonText: 'OK'
    })
  }
}

const handleAddToCart = () => {
  // Nếu không có selectedVariant, thử sử dụng biến thể đầu tiên có sẵn
  let variantToAdd = selectedVariant.value
  
  if (!variantToAdd) {
    // Tìm biến thể đầu tiên có số lượng > 0
    variantToAdd = product.value.productVariants?.find(v => v.quantity > 0)
    
    if (!variantToAdd) {
      // Nếu không có biến thể nào có số lượng > 0, sử dụng biến thể đầu tiên
      variantToAdd = product.value.productVariants?.[0]
    }
  }

  if (!variantToAdd) {
    Swal.fire({
      icon: 'info',
      title: 'Không có loại này rồi bạn ơi!',
      text: 'Bạn thử chọn lại size, hương vị hoặc hình dáng khác xem sao nha.',
      confirmButtonText: 'OK'
    })
    return
  }

  if (variantToAdd.quantity === 0) {
    Swal.fire({
      icon: 'warning',
      title: 'Sản phẩm này đã hết hàng!',
      text: 'Bạn vui lòng chọn loại khác.',
      confirmButtonText: 'OK'
    })
    return
  }

  addToCart(variantToAdd.id)
}

// Kiểm tra xem có biến thể khả dụng hay không
const hasAvailableVariant = computed(() => {
  if (!product.value.productVariants || product.value.productVariants.length === 0) {
    return false
  }
  
  // Kiểm tra xem có ít nhất một biến thể có số lượng > 0
  const hasAvailable = product.value.productVariants.some(v => v.quantity > 0)
  
  // Nếu có selectedVariant, kiểm tra xem nó có khả dụng không
  if (selectedVariant.value) {
    return selectedVariant.value.quantity > 0
  }
  
  return hasAvailable
})

// Biến thể đang hiển thị
const displayedVariant = computed(() => {
  return selectedVariant.value || product.value.productVariants?.[0] || null
})

// Giá sau khi giảm
const finalPrice = computed(() => {
  const variant = displayedVariant.value
  if (!variant) return 0
  const discount = product.value.discountPercentage || 0
  return Math.round(variant.price * (1 - discount / 100))
})

// Giá gốc
const originalPrice = computed(() => {
  return displayedVariant.value?.price || 0
})

// Số tiền tiết kiệm
const amountSaved = computed(() => {
  return originalPrice.value - finalPrice.value
})

// ----------------------
// VOUCHER PHẦN TRANG CHI TIẾT SẢN PHẨM
// ----------------------

const productTotal = computed(() => {
  return finalPrice.value * quantity.value
})

const eligibleVouchers = ref([])

// ========== PHẦN ĐÁNH GIÁ SẢN PHẨM ==========
const ratings = ref([])
const totalRatings = ref(0)
const averageRating = ref(0)
const ratingDistribution = ref({})
const currentRatingPage = ref(1)
const ratingsPerPage = 5
const selectedReviewImage = ref('') // Thêm biến để lưu ảnh được chọn

// Lấy đánh giá sản phẩm
const fetchProductRatings = async () => {
  try {
    console.log('🔍 Fetching ratings for product:', route.params.id)
    console.log('🔗 API URL:', `/api/ratings/product/${route.params.id}`)
    
    // First try without authentication to see if that's the issue
    let res
    try {
      res = await api.get(`/api/ratings/product/${route.params.id}`, {
        params: {
          page: currentRatingPage.value - 1,
          size: ratingsPerPage
        }
      })
    } catch (authError) {
      console.log('⚠️ Auth error, trying without credentials...')
      // Try without authentication
      res = await fetch(`http://localhost:8080/api/ratings/product/${route.params.id}?page=${currentRatingPage.value - 1}&size=${ratingsPerPage}`)
      const data = await res.json()
      res = { data, status: res.status, headers: res.headers }
    }
    
    console.log('📊 Ratings API response:', res.data)
    console.log('📊 Response status:', res.status)
    console.log('📊 Response headers:', res.headers)
    
    if (res.data && res.data.content) {
      ratings.value = res.data.content
      totalRatings.value = res.data.totalElements
      
      console.log('✅ Ratings loaded:', ratings.value.length, 'total:', totalRatings.value)
      console.log('�� Sample rating with images:', ratings.value.find(r => r.reviewImages && r.reviewImages.length > 0))
      console.log('🔍 Full sample rating:', JSON.stringify(ratings.value[0], null, 2))
      
      // Tính toán thống kê đánh giá
      calculateRatingStats()
    } else if (res.data && res.data.data && res.data.data.content) {
      // Backend returns data nested under res.data.data
      ratings.value = res.data.data.content
      totalRatings.value = res.data.data.totalElements
      
      console.log('✅ Ratings loaded (nested):', ratings.value.length, 'total:', totalRatings.value)
      console.log('🔍 Sample rating with images:', ratings.value.find(r => r.reviewImages && r.reviewImages.length > 0))
      console.log('🔍 Full sample rating:', JSON.stringify(ratings.value[0], null, 2))
      
      // Tính toán thống kê đánh giá
      calculateRatingStats()
    } else {
      console.log('⚠️ No ratings data in response')
      console.log('⚠️ Response structure:', Object.keys(res.data || {}))
      console.log('⚠️ Full response:', res.data)
      ratings.value = []
      totalRatings.value = 0
      calculateRatingStats()
    }
  } catch (error) {
    console.error('❌ Error loading product ratings:', error)
    console.error('❌ Error response:', error.response?.data)
    console.error('❌ Error status:', error.response?.status)
    ratings.value = []
    totalRatings.value = 0
    calculateRatingStats()
  }
}

// Tính toán thống kê đánh giá
const calculateRatingStats = () => {
  console.log('📊 Calculating rating stats for:', ratings.value.length, 'ratings')
  console.log('📊 Sample rating data:', ratings.value[0])
  
  if (ratings.value.length === 0) {
    averageRating.value = 0
    ratingDistribution.value = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
    console.log('📊 No ratings, stats reset to 0')
    return
  }
  
  // Tính điểm trung bình
  const totalRating = ratings.value.reduce((sum, rating) => {
    console.log('📊 Processing rating:', rating.ratings, 'from rating object:', rating)
    return sum + (rating.ratings || 0)
  }, 0)
  averageRating.value = totalRating / ratings.value.length
  
  console.log('📊 Total rating sum:', totalRating, 'Average:', averageRating.value)
  
  // Tính phân bố số sao
  const distribution = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
  ratings.value.forEach(rating => {
    if (distribution[rating.ratings] !== undefined) {
      distribution[rating.ratings]++
    }
  })
  ratingDistribution.value = distribution
  
  console.log('📊 Rating distribution:', ratingDistribution.value)
}

// Thay đổi trang đánh giá
const changeRatingPage = (page) => {
  if (page >= 1 && page <= totalRatingPages.value) {
    currentRatingPage.value = page
    fetchProductRatings()
  }
}

// Tính tổng số trang đánh giá
const totalRatingPages = computed(() => {
  return Math.ceil(totalRatings.value / ratingsPerPage)
})

// Tính danh sách trang để hiển thị
const ratingPages = computed(() => {
  const pages = []
  const maxPages = 5
  let startPage = Math.max(1, currentRatingPage.value - Math.floor(maxPages / 2))
  let endPage = Math.min(totalRatingPages.value, startPage + maxPages - 1)
  
  if (endPage - startPage + 1 < maxPages) {
    startPage = Math.max(1, endPage - maxPages + 1)
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }
  
  return pages
})

// Format ngày tháng
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Lấy đánh giá khi sản phẩm thay đổi
watch(() => route.params.id, () => {
  if (route.params.id) {
    currentRatingPage.value = 1
    fetchProductRatings()
  }
}, { immediate: true })

const fetchVouchers = async () => {
  try {
    const res = await api.get('/api/vouchers/eligible', {
      params: { cartTotal: productTotal.value }
    })
    eligibleVouchers.value = res.data
  } catch (error) {
    console.error('Lỗi khi tải voucher:', error)
  }
}

watch(productTotal, fetchVouchers, { immediate: true })

const progressWidth = computed(() => {
  if (eligibleVouchers.value.length === 0) return '0%'
  const minAmount = Math.min(...eligibleVouchers.value.map(v => v.missingAmount))
  const reached = productTotal.value / (productTotal.value + minAmount)
  return `${Math.min(reached * 100, 100)}%`
})

const copiedCodes = ref([])

const copyToClipboard = (text) => {
  if (copiedCodes.value.includes(text)) return

  navigator.clipboard.writeText(text)
    .then(() => {
      copiedCodes.value.push(text)
      Swal.fire({
        icon: 'success',
        title: 'Đã sao chép!',
        text: `Mã "${text}" đã được sao chép vào clipboard.`,
        timer: 2000,
        showConfirmButton: false,
        toast: true,
        position: 'top-end'
      })
    })
    .catch(err => {
      console.error('❌ Lỗi sao chép:', err)
      Swal.fire({
        icon: 'error',
        title: 'Lỗi!',
        text: 'Không thể sao chép mã. Vui lòng thử lại.',
        timer: 2000,
        showConfirmButton: false,
        toast: true,
        position: 'top-end'
      })
    })
}

const openImageModal = (imageUrl) => {
  selectedReviewImage.value = imageUrl
  const imageModal = new Modal(document.getElementById('imageModal'))
  imageModal.show()
}

const getImageUrl = (imagePath) => {
  console.log('🔍 getImageUrl called with:', imagePath, 'type:', typeof imagePath)
  
  if (!imagePath) {
    console.warn('⚠️ No image path provided')
    return 'https://via.placeholder.com/80x80?text=No+Image'
  }
  
  if (typeof imagePath === 'string') {
    if (imagePath.startsWith('http')) {
      return imagePath
    }
    if (imagePath.startsWith('/')) {
      return `http://localhost:8080${imagePath}`
    }
    return 'http://localhost:8080/images/' + imagePath
  }
  
  if (typeof imagePath === 'object' && imagePath !== null) {
    console.log('🔍 Image path is object:', imagePath)
    // Try different possible properties
    const possiblePaths = ['images', 'image', 'url', 'src', 'path']
    for (const prop of possiblePaths) {
      if (imagePath[prop]) {
        console.log('🔍 Found image in property:', prop, 'value:', imagePath[prop])
        return getImageUrl(imagePath[prop])
      }
    }
  }
  
  console.warn('⚠️ Could not determine image URL for:', imagePath)
  return 'https://via.placeholder.com/80x80?text=Invalid+Image'
}

const handleImageError = (event) => {
  console.error('Error loading image:', event.target.src)
  event.target.src = 'https://via.placeholder.com/80x80?text=Error+Loading+Image'
}

const handleImageLoad = (event) => {
  console.log('Image loaded successfully:', event.target.src)
}

const showImageInfo = (imageData) => {
  console.log('🔍 Image data clicked:', imageData)
  Swal.fire({
    title: 'Thông tin ảnh đánh giá',
    html: `
      <div class="text-start">
        <p><strong>Loại dữ liệu:</strong> ${typeof imageData}</p>
        <p><strong>Dữ liệu:</strong></p>
        <pre style="text-align: left; font-size: 10px; max-height: 200px; overflow-y: auto;">${JSON.stringify(imageData, null, 2)}</pre>
      </div>
    `,
    icon: 'info',
    confirmButtonText: 'Đóng',
    width: '600px'
  })
}

// ========== PHẦN SẢN PHẨM LIÊN QUAN ==========

// Lấy sản phẩm liên quan
const fetchRelatedProducts = async () => {
  try {
    const res = await api.get(`/api/products/${route.params.id}/related?limit=4`)
    relatedProducts.value = res.data
    console.log('✅ Related products loaded:', relatedProducts.value.length)
  } catch (error) {
    console.error('❌ Error loading related products:', error)
    relatedProducts.value = []
  }
}

// Lấy URL ảnh sản phẩm
const getProductImageUrl = (product) => {
  if (product.imageUrls && product.imageUrls.length > 0) {
    const imageUrl = product.imageUrls[0]
    return imageUrl.startsWith('http') ? imageUrl : `http://localhost:8080/images/${imageUrl}`
  }
  return 'https://via.placeholder.com/200x200?text=No+Image'
}

// Tính giá cuối cùng sau khi giảm giá
const getFinalPrice = (product) => {
  if (product.discountPercentage) {
    return Math.round(product.price * (1 - product.discountPercentage / 100))
  }
  return product.price
}

// Format giá tiền
const formatPrice = (price) => {
  return price ? price.toLocaleString() : '0'
}

// Thêm sản phẩm liên quan vào giỏ hàng
const addRelatedToCart = async (relatedProduct) => {
  try {
    // Lấy biến thể đầu tiên của sản phẩm liên quan
    const firstVariant = relatedProduct.productVariants?.[0]
    if (!firstVariant) {
      Swal.fire({
        icon: 'warning',
        title: 'Không có biến thể khả dụng',
        text: 'Sản phẩm này hiện không có biến thể nào khả dụng.',
        confirmButtonText: 'OK'
      })
      return
    }

    await api.post('/api/cart/add', {
      variantId: firstVariant.id,
      quantity: 1
    })

    Swal.fire({
      icon: 'success',
      title: 'Đã thêm vào giỏ hàng!',
      text: `${relatedProduct.name} đã được thêm vào giỏ hàng.`,
      timer: 2000,
      showConfirmButton: false,
      toast: true,
      position: 'top-end'
    })
  } catch (error) {
    console.error('❌ Error adding related product to cart:', error)
    Swal.fire({
      icon: 'error',
      title: 'Lỗi!',
      text: 'Không thể thêm sản phẩm vào giỏ hàng. Vui lòng thử lại.',
      confirmButtonText: 'OK'
    })
  }
}

// Xem chi tiết sản phẩm liên quan
const viewRelatedProduct = (productId) => {
  router.push(`/chiTietSP/${productId}`)
}
</script>


<style scoped>
.lien-he {
  font-size: 18px;
  font-family: Roboto;
  background: #fff;
  font-weight: 500;
  color: #000;
  width: 100vw;
}

.bread-crumb .title-bread-crumb {
  text-align: center;
  font-size: 55px;
  color: #cd9b32;
  font-weight: 600;
  font-family: 'Forte';
}

.breadcrumb {
  font-size: 14px;
  padding: 15px 10px;
  line-height: 24px;
  width: 100%;
  text-align: center;
  padding-left: 540px;
}

.breadcrumb .home a {
  text-decoration: none !important;
  color: white;
}

.breadcrumb .home a:nth-child(2) span {
  color: #cd9b32 !important;
  font-size: 18px !important;
}

.breadcrumb li {
  display: inline-block;
  margin: 0 10px;
}

.breadcrumb li:first-child {
  margin-left: 0;
}

.breadcrumb li:last-child {
  margin-right: 0;
}

.breadcrumb li a:hover span,
.breadcrumb li.active span,
.breadcrumb li strong span {
  color: #cd9b32;
}

.breadcrumb li>a>span,
.breadcrumb li span {
  color: #fff;
}

.bread-crumb {
  margin-bottom: 40px;
  min-height: 200px;
  background-attachment: fixed;
  background-position: center center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  padding: 150px 0 50px;
}

header.header {
  padding: 10px 0;
  position: absolute;
  top: 0 !important;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 9;
  background-color: transparent;
}

.header .nav-link {
  color: white;
}

.nav-link li a:hover {
  color: #e4a53d;
}

.header .nav-link:hover {
  color: #e4a53d;
}

.abc {
  width: 90%;
  max-width: 1200px;
  margin: 20px auto;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.product-header {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.product-image {
  flex: 1;
  min-width: 500px;
  border: 3px solid #ddd;
  padding: 5px;
}

.product-image img {
  width: 100%;
  border-radius: 8px;
}

.product-thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.product-thumbnails img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid #ddd;
}

.product-thumbnails img:hover {
  border-color: #f0ad4e;
  transform: scale(1.1);
  transition: 0.3s ease;
}

.product-details {
  flex: 2;
  min-width: 300px;
}

.product-title {
  font-size: 40px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.product-status .label {
  color: black;
  margin-right: 10px;
}

.product-status .value,
.product-status .status {
  color: #CD9B32;
  margin-right: 10px;
}

.product-price {
  font-size: 37px;
  font-family: Forte;
  color: #CD9B32;
  margin: 10px 0;
}

.product-price .original-price {
  text-decoration: line-through;
  color: #888;
  margin-left: 10px;
  font-size: 18px;
  font-family: Roboto;
}

.product-price .discount {
  display: block;
  font-size: 20px;
  margin-top: 5px;
  font-family: Roboto;
  color: #000;
}

.quantity {
  text-align: left;
  margin-bottom: 10px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #CD9B32;
  width: 120px;
  border-radius: 5px;
  margin-top: 10px;
}

.quantity-controls button {
  background-color: #CD9B32;
  font-size: 20px;
  padding: 5px 10px;
  border: 1px solid white;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 5px;
  color: white;
}

.quantity-controls input {
  width: 60px;
  text-align: center;
  font-size: 18px;
  border: 1px solid #ddd;
  background-color: #f1f1f1;
}

.buttons {
  display: flex;
  gap: 20px;
}

button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: auto;
  background-color: #d4a548;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #b68e3a;
}

.add-to-cart,
.favorite-btn {
  display: flex;
  align-items: center;
  gap: 10px;
}

.add-to-cart .text,
.favorite-btn .text {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.add-to-cart .text span,
.favorite-btn .text span {
  font-weight: bold;
  font-size: 19px;
}

.add-to-cart .text p,
.favorite-btn .text p {
  font-size: 16px;
  margin: 0;
  color: #fff;
}

.cart-icon,
.heart-icon {
  width: 24px;
  height: 24px;
}

.promotion,
.special-promotion {
  margin: 20px 0;
  border: 1px solid #d4a548;
  padding: 10px;
  background: #fffaf0;
}

.promo-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.promo-list {
  list-style: none;
  padding: 0;
}

.promo-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 5px 0;
}

.voucher-btn {
  background: #d4a548;
  border: none;
  color: white;
  padding: 5px 10px;
  cursor: pointer;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 20px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #fffaf0;
  border: 1px solid #d4a548;
}

.benefit-item .icon {
  font-size: 20px;
}

.description {
  text-align: center;
  margin: 20px 0;
}

.description h2 .highlight {
  color: #CD9B32;
  font-family: Lucida Calligraphy;
  font-size: 35px;
  margin-right: 20px;
  text-decoration: underline;
}

.description h2 .title {
  color: #000;
  font-family: Lucida Calligraphy;
  font-size: 35px;
}

.description p {
  text-align: left;
  margin-left: 50px !important;
}

.related-products {
  text-align: center;
  margin: 20px auto;
  max-width: 1200px;
}

.related-products h2 {
  font-size: 40px;
  font-family: Lucida Calligraphy;
  margin-bottom: 20px;
}

.related-banner {
  width: 200px;
  margin-bottom: 30px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.product-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  text-align: center;
  background-color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.product-image-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
}

.product-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-item:hover .product-image-container img {
  transform: scale(1.1);
}

.product-icons {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.product-item:hover .product-icons {
  opacity: 1;
}

.cart-icon-btn,
.magnifier-icon-btn {
  background-color: #d4a548;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
}

.cart-icon-btn:hover,
.magnifier-icon-btn:hover {
  background-color: #b68e3a;
  transform: scale(1.1);
}

.cart-icon-btn i,
.magnifier-icon-btn i {
  font-size: 16px;
}

.badge-container {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.discount-badge {
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.new-badge {
  background-color: #f1c40f;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  padding: 5px 15px;
  border-radius: 10px 1px 10px 1px;
  text-align: center;
  display: inline-block;
  margin-top: 5px;
}

.wishlist-icon {
  position: absolute;
  top: -3px;
  right: 10px;
  color: #e74c3c;
  font-size: 18px;
  cursor: pointer;
  transform: translateX(130px);
  z-index: 10;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.3;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.current-price {
  color: #d4a548;
  font-weight: bold;
  font-size: 18px;
}

.old-price {
  text-decoration: line-through;
  color: #999;
  font-size: 14px;
}

.no-related-products {
  padding: 40px;
  color: #666;
  font-style: italic;
}

.no-related-products p {
  margin: 0;
  font-size: 16px;
}

/* Responsive cho sản phẩm liên quan */
@media (max-width: 768px) {
  .product-list {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
  }
  
  .product-image-container {
    height: 150px;
  }
  
  .product-name {
    font-size: 14px;
    height: 36px;
  }
  
  .current-price {
    font-size: 16px;
  }
  
  .old-price {
    font-size: 12px;
  }
  
  .related-products h2 {
    font-size: 28px;
  }
  
  .related-products {
    padding: 0 15px;
  }
}

@media (max-width: 480px) {
  .product-list {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 10px;
  }
  
  .product-image-container {
    height: 120px;
  }
  
  .product-info {
    padding: 10px;
  }
  
  .product-name {
    font-size: 12px;
    height: 32px;
  }
  
  .current-price {
    font-size: 14px;
  }
  
  .old-price {
    font-size: 11px;
  }
  
  .related-products h2 {
    font-size: 24px;
  }
  
  .related-banner {
    width: 150px;
  }
  
  .related-products {
    padding: 0 10px;
  }
  
  .cart-icon-btn,
  .magnifier-icon-btn {
    width: 35px;
    height: 35px;
  }
  
  .cart-icon-btn i,
  .magnifier-icon-btn i {
    font-size: 14px;
  }
}

/* Fix cho grid layout */
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
  justify-items: center;
}

/* Fix cho product item */
.product-item {
  width: 100%;
  max-width: 280px;
  min-width: 200px;
}

/* Fix cho product image container */
.product-image-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
  background-color: #f8f9fa;
}

.product-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

/* Fix cho product icons */
.product-icons {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.product-item:hover .product-icons {
  opacity: 1;
}

/* Fix cho buttons */
.cart-icon-btn,
.magnifier-icon-btn {
  background-color: #d4a548;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.cart-icon-btn:hover,
.magnifier-icon-btn:hover {
  background-color: #b68e3a;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.cart-icon-btn i,
.magnifier-icon-btn i {
  font-size: 16px;
}

/* Fix cho badge */
.badge-container {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.discount-badge {
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Fix cho product info */
.product-info {
  padding: 15px;
  background-color: white;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.3;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.current-price {
  color: #d4a548;
  font-weight: bold;
  font-size: 18px;
}

.old-price {
  text-decoration: line-through;
  color: #999;
  font-size: 14px;
}

/* Fix cho no related products */
.no-related-products {
  padding: 40px;
  color: #666;
  font-style: italic;
  text-align: center;
}

.no-related-products p {
  margin: 0;
  font-size: 16px;
}

/* Fix cho related products container */
.related-products {
  text-align: center;
  margin: 40px auto;
  max-width: 1200px;
  padding: 0 20px;
}

.related-products h2 {
  font-size: 40px;
  font-family: Lucida Calligraphy;
  margin-bottom: 20px;
  color: #333;
}

.related-banner {
  width: 200px;
  margin-bottom: 30px;
  opacity: 0.8;
}

/* Fix cho product list container */
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
  justify-items: center;
  align-items: start;
}

/* Fix cho product item */
.product-item {
  width: 100%;
  max-width: 280px;
  min-width: 200px;
  border-radius: 12px;
  overflow: hidden;
  text-align: center;
  background-color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
  position: relative;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* Fix cho product image container */
.product-image-container {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
  background-color: #f8f9fa;
}

.product-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-item:hover .product-image-container img {
  transform: scale(1.1);
}

/* Fix cho product icons */
.product-icons {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.product-item:hover .product-icons {
  opacity: 1;
}

/* Fix cho buttons */
.cart-icon-btn,
.magnifier-icon-btn {
  background-color: #d4a548;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.cart-icon-btn:hover,
.magnifier-icon-btn:hover {
  background-color: #b68e3a;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.cart-icon-btn i,
.magnifier-icon-btn i {
  font-size: 16px;
}

/* Fix cho badge */
.badge-container {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.discount-badge {
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Fix cho product info */
.product-info {
  padding: 15px;
  background-color: white;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.3;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.current-price {
  color: #d4a548;
  font-weight: bold;
  font-size: 18px;
}

.old-price {
  text-decoration: line-through;
  color: #999;
  font-size: 14px;
}

/* CSS cho các phần khác */
.btn-outline-secondary {
  border: 1.5px solid #c8cfd6;
  color: #150e0e;
  background-color: transparent;
  margin-right: 5px;
  margin-bottom: 5px;
  font-weight: 600;
  cursor: pointer;
}

.btn-custom-selected {
  border: 1px solid #d4a548 !important;
  color: white !important;
  background-color: #d4a548 !important;
  margin-right: 5px;
  margin-bottom: 5px;
  font-weight: 600;
  cursor: pointer;
}

.quantity-wrapper {
  display: flex;
  align-items: center;
  border: 1.5px solid #d5a84b;
  border-radius: 6px;
  padding: 3px;
  width: 130px;
  justify-content: space-between;
  background-color: white;
  gap: 4px;
}

.quantity-btn {
  background-color: #d5a84b;
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.quantity-btn:hover {
  background-color: #c8992e;
}

.quantity-display {
  width: 40px;
  height: 36px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  background-color: transparent;
  color: #000;
  outline: none;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-tinhtrang .coupon1 li {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  opacity: 1 !important;
}

.cart-tinhtrang .coupon1 li .coupon-content1 {
  font-size: 18px;
  opacity: 0.5;
}

.cart-tinhtrang .coupon1 li .coupon-code1 {
  opacity: 1 !important;
  pointer-events: auto !important;
  background: #cd9b32;
  color: white;
  padding: 2px 15px;
  border-radius: 5px;
  margin-top: 5px;
  cursor: pointer;
}

.disabled-btn {
  opacity: 0.6;
  cursor: not-allowed !important;
}

.disabled-btn:hover {
  background-color: #d4a548 !important;
}

.save-amount {
  color: #e74c3c;
  font-weight: bold;
  font-size: 16px;
  margin-top: 5px;
}

/* ========== CSS CHO PHẦN ĐÁNH GIÁ SẢN PHẨM ========== */
.product-ratings {
    margin: 40px 0;
    padding: 30px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.product-ratings h2 {
    margin-bottom: 30px;
    color: #333;
    font-size: 28px;
    font-weight: 600;
}

.product-ratings .highlight {
    color: #d4a548;
    position: relative;
}

.product-ratings .highlight::after {
    content: '';
    position: absolute;
    bottom: -5px;
    left: 0;
    width: 100%;
    height: 3px;
    background: linear-gradient(90deg, #d4a548, #f1c40f);
    border-radius: 2px;
}

/* Thống kê đánh giá */
.rating-summary {
    background: #f8f9fa;
    padding: 25px;
    border-radius: 10px;
    border: 1px solid #e9ecef;
}

.overall-rating {
    text-align: center;
}

.rating-number {
    font-size: 48px;
    font-weight: 700;
    color: #d4a548;
    line-height: 1;
    margin-bottom: 10px;
}

.rating-stars {
    margin-bottom: 15px;
}

.total-ratings {
    color: #6c757d;
    font-size: 14px;
    font-weight: 500;
}

/* Thanh phân bố đánh giá */
.rating-bars {
    padding-left: 20px;
}

.rating-bar-item {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    gap: 15px;
}

.stars-label {
    min-width: 60px;
    font-weight: 600;
    color: #495057;
}

.rating-bar-item .progress {
    flex: 1;
    background-color: #e9ecef;
    border-radius: 10px;
    overflow: hidden;
}

.rating-bar-item .progress-bar {
    background: linear-gradient(90deg, #ffc107, #ff8c00);
    border-radius: 10px;
}

.count-label {
    min-width: 40px;
    text-align: right;
    font-weight: 600;
    color: #6c757d;
}

/* Danh sách đánh giá */
.ratings-list {
    margin-top: 30px;
}

.no-ratings {
    color: #6c757d;
}

.no-ratings i {
    color: #dee2e6;
}

.rating-item {
    border-bottom: 1px solid #e9ecef;
    padding: 25px 0;
}

.rating-item:last-child {
    border-bottom: none;
}

.rating-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 15px;
}

.avatar {
    width: 50px;
    height: 50px;
    background: #f8f9fa;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid #e9ecef;
}

.avatar i {
    font-size: 24px;
    color: #6c757d;
}

.user-details {
    display: flex;
    flex-direction: column;
}

.user-name {
    font-weight: 600;
    color: #333;
    margin-bottom: 5px;
}

.rating-date {
    font-size: 14px;
    color: #6c757d;
}

.rating-content {
    margin-left: 65px;
}

.rating-comment {
    color: #495057;
    line-height: 1.6;
    margin-bottom: 15px;
    font-size: 15px;
}

.rating-images {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.review-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
    border: 2px solid #e9ecef;
    cursor: pointer;
    transition: transform 0.2s ease;
}

.review-image:hover {
    transform: scale(1.05);
    border-color: #d4a548;
}

/* Phân trang đánh giá */
.rating-pagination {
    margin-top: 30px;
}

.rating-pagination .pagination {
    margin-bottom: 0;
}

.rating-pagination .page-link {
    color: #d4a548;
    border-color: #e9ecef;
    padding: 10px 15px;
    margin: 0 2px;
    border-radius: 6px;
    font-weight: 500;
}

.rating-pagination .page-link:hover {
    background-color: #d4a548;
    border-color: #d4a548;
    color: white;
}

.rating-pagination .page-item.active .page-link {
    background-color: #d4a548;
    border-color: #d4a548;
    color: white;
}

.rating-pagination .page-item.disabled .page-link {
    color: #6c757d;
    background-color: #fff;
    border-color: #e9ecef;
}

/* Responsive cho phần đánh giá */
@media (max-width: 768px) {
    .product-ratings {
        padding: 20px;
        margin: 20px 0;
    }
    
    .rating-summary {
        padding: 20px;
    }
    
    .rating-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 15px;
    }
    
    .rating-content {
        margin-left: 0;
    }
    
    .rating-bar-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }
    
    .stars-label, .count-label {
        min-width: auto;
    }
    
    .rating-bar-item .progress {
        width: 100%;
    }
}

/* CSS cho modal ảnh đánh giá */
#imageModal .modal-dialog {
    max-width: 90vw;
}

#imageModal .modal-body img {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.rating-images .review-image {
    transition: all 0.3s ease;
}

.rating-images .review-image:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    z-index: 10;
}

/* CSS cho avatar mặc định */
.default-avatar {
    background: #f8f9fa !important;
    border: 2px solid #e9ecef !important;
    transition: all 0.3s ease;
}

.default-avatar:hover {
    background: #e9ecef !important;
    border-color: #d4a548 !important;
}

.default-avatar i {
    color: #6c757d !important;
    transition: color 0.3s ease;
}

.default-avatar:hover i {
    color: #d4a548 !important;
}

/* CSS cho ảnh đánh giá */
.rating-images {
    margin-top: 15px;
}

.rating-images .review-image {
    border: 2px solid #e9ecef;
    transition: all 0.3s ease;
    object-fit: cover;
}

.rating-images .review-image:hover {
    border-color: #d4a548;
    transform: scale(1.05);
}

/* Debug styles */
.rating-item {
    position: relative;
}

.rating-item:has(.rating-images) {
    border-left: 3px solid #d4a548;
    padding-left: 15px;
}

/* CSS cho placeholder ảnh */
.review-image-placeholder {
    position: relative;
    transition: all 0.3s ease;
}

.review-image-placeholder:hover {
    background: #e9ecef !important;
    border-color: #d4a548 !important;
    transform: scale(1.05);
}

.review-image-placeholder i {
    transition: color 0.3s ease;
}

.review-image-placeholder:hover i {
    color: #d4a548 !important;
}

/* CSS cho debug info */
.debug-images {
    background: #f8f9fa;
    padding: 5px;
    border-radius: 4px;
    border: 1px solid #e9ecef;
    font-family: monospace;
}

/* CSS cho thông báo không có biến thể khả dụng */
.text-muted.small {
    color: #6c757d !important;
    font-size: 0.875em;
    font-style: italic;
    padding: 8px 12px;
    background-color: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    margin-top: 8px;
    text-align: center;
    width: 100%;
}

/* CSS cho variant section */
.variant-section .mb-3 {
    transition: all 0.3s ease;
}

.variant-section .mb-3:hover {
    transform: translateY(-2px);
}

/* CSS cho button biến thể */
.btn-outline-secondary {
    transition: all 0.3s ease;
    border-width: 2px;
}

.btn-outline-secondary:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.btn-custom-selected {
    background-color: #d4a548 !important;
    border-color: #d4a548 !important;
    color: white !important;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(212, 165, 72, 0.3);
}

/* CSS cho button bị mờ (không khả dụng) */
.btn-disabled {
    background-color: #f8f9fa !important;
    border-color: #dee2e6 !important;
    color: #6c757d !important;
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

.btn-disabled:hover {
    background-color: #f8f9fa !important;
    border-color: #dee2e6 !important;
    color: #6c757d !important;
    transform: none;
    box-shadow: none;
    opacity: 0.6;
}
</style>