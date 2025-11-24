<template>
  <div class="lien-he">
    <main>
      <div>
        <section class="bread-crumb" style="background: linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)), url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436) center no-repeat;">
          <div class="container">
            <div class="title-bread-crumb" style="padding-left: 100px;">
              Sản Phẩm Yêu Thích
            </div>
            <ul class="breadcrumb">
              <li class="home">
                <a href="/"><span>Trang chủ ></span></a>
                <a href="/"><span>Sản phẩm yêu thích</span></a>
              </li>
            </ul>
          </div>
        </section>
      </div>
    </main>

    <div class="container-fluid my-5" style="margin-left: 10px;">
      <section class="section2">
        <div class="container-fluid pe-0 ps-0" style="margin-left: 30px;">
          <h1 style="text-align:center;">Sản phẩm yêu thích</h1>
          <img src="https://bizweb.dktcdn.net/100/492/035/themes/919334/assets/title.png?1735117293436" alt="" style="padding-left: 720px; padding-bottom: 30px;">

          <!-- Loading state -->
          <div v-if="loading" style="text-align:center; color:#888; font-size:18px; margin-top:40px;">
            Đang tải danh sách yêu thích...
          </div>

          <!-- Error state -->
          <div v-else-if="error" style="text-align:center; color:#dc3545; font-size:18px; margin-top:40px;">
            {{ error }}
          </div>

          <!-- Products list -->
          <div v-else class="row-fix">
            <div class="col-fix" v-for="product in favoriteProducts" :key="product.id">
              <div class="prod1 position-relative">
                <!-- Hình -->
                <div class="prod1img">
                  <img :src="'http://localhost:8080/images/' + product.productImageUrl" :alt="product.productName" style="border-radius: 10px; height: 250px" />
                </div>

                <!-- Giảm giá -->
                <div class="muangay1" v-if="product.discountPercentage ">
                  <div class="btnmuangay1 ps-2 fs-6" style="height: 25px; width: 55px; background-color: rgb(249, 55, 55); color: white; border-top-right-radius: 12px; border-bottom-left-radius: 12px;">
                    -{{ Math.round(product.discountPercentage) }}%
                  </div>
                </div>

                <!-- Icon tim -->
                <div class="muangay2">
                  <div class="btnmuangay2 ps-2 fs-4" @click="removeFavorite(product)" style="cursor:pointer;">
                    <i class="bi bi-heart-fill text-danger" style="font-size: 28px;"></i>
                  </div>
                </div>

                <!-- Tag "New" -->
                <div class="muangay3">
                  <div class="btnmuangay3 ps-2 fs-6" style="height: 25px; width: 55px; background-color:#cd9b32; color: white; border-top-right-radius:12px; border-bottom-left-radius: 12px;">
                    New
                  </div>
                </div>

                <!-- Nút mua và xem chi tiết -->
                <div class="muangay10">
                  <input type="hidden" name="muangay10" />
                  <a href="" class="btnmuangay10 fs-5 text-white pb-3" @click.prevent="openPopup(product)" style="border-radius: 50px; background-color: #cd9b32; width: 58px; height: 56px">
                    <i class="bi bi-basket2-fill"></i>
                  </a>
                  <a href="" class="btnmuangay11 fs-5 text-white pb-3" @click.prevent="openPopup(product)" style="border-radius: 50px; background-color: #cd9b32; width: 58px; height: 56px">
                    <i class="bi bi-search"></i>
                  </a>
                </div>

                <!-- Tên sản phẩm -->
                <p class="fw-bold pt-3 product-name" style="text-align: center;">
                <RouterLink :to="`/product/${product.id}`">  {{ product.productName }}</RouterLink>
                </p>

                <!-- Giá -->
                <p style="text-align: center;">
                  <span class="fw-bold text-warning">
                    {{ product.productPrice ? (product.productPrice * (1 - (product.productDiscountPercentage || 0) / 100)).toLocaleString() : 'Liên hệ' }} đ
                  </span>
                  <span class="ps-3 text-body-secondary" v-if="product.productPrice && product.productDiscountPercentage && product.productDiscountPercentage > 0">
                    <del>{{ product.productPrice.toLocaleString() }}đ</del>
                  </span>
                </p>
              </div>
            </div>
            
            <!-- Empty state -->
            <div v-if="!loading && !error && favoriteProducts.length === 0" style="text-align:center; color:#888; font-size:18px; margin-top:40px; grid-column: 1 / -1;">
              Chưa có sản phẩm yêu thích nào.
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- Popup biến thể -->
    <div v-if="showPopup" class="popup-overlay">
      <div class="popup-content">
        <!-- Nút đóng -->
        <button class="btn-close" @click="closePopup">&times;</button>

        <!-- Hình ảnh & Tên sản phẩm -->
        <div class="product-header">
          <img :src="'http://localhost:8080/images/' + selectedProduct?.productImageUrl" class="main-image" />
          <div class="price-info">
            <h4>{{ selectedProduct?.productName }}</h4>
            <div class="price">
              <span class="new-price">
                {{
                  selectedVariant
                    ? (selectedVariant.price * (1 - (selectedProduct.productDiscountPercentage || 0) / 100)).toLocaleString()
                    : defaultVariant
                      ? (defaultVariant.price * (1 - (selectedProduct.productDiscountPercentage || 0) / 100)).toLocaleString()
                      : selectedProduct?.productPrice 
                        ? (selectedProduct.productPrice * (1 - (selectedProduct.productDiscountPercentage || 0) / 100)).toLocaleString()
                        : 'Liên hệ'
                }} đ
              </span>
              <span v-if="((selectedVariant || defaultVariant) && selectedProduct.productDiscountPercentage) || (selectedProduct?.productPrice && selectedProduct.productDiscountPercentage)" class="old-price">
                {{
                  (selectedVariant?.price || defaultVariant?.price || selectedProduct?.productPrice)?.toLocaleString()
                }} đ
              </span>
            </div>

            <div class="stock">
              Kho: {{ selectedVariant?.quantity ?? defaultVariant?.quantity ?? '---' }}
            </div>
          </div>
        </div>

        <!-- Các biến thể (chỉ hiển thị nếu có productVariants) -->
        <div v-if="selectedProduct?.productVariants && selectedProduct.productVariants.length > 0">
          <div class="option-group">
            <div class="label">Size</div>
            <div class="option-list">
              <button v-for="s in getUnique('size')" :key="'size-' + s" @click="selectedSize = s; updateVariant()"
                :class="['variant-button', selectedSize === s ? 'active' : '']">
                {{ s }}
              </button>
            </div>
          </div>

          <div class="option-group">
            <div class="label">Hình dáng</div>
            <div class="option-list">
              <button v-for="s in getUnique('shape')" :key="'shape-' + s"
                @click="selectedShape = s; updateVariant()"
                :class="['variant-button', selectedShape === s ? 'active' : '']">
                {{ s }}
              </button>
            </div>
          </div>

          <div class="option-group">
            <div class="label">Hương vị</div>
            <div class="option-list">
              <button v-for="s in getUnique('flavor')" :key="'flavor-' + s"
                @click="selectedFlavor = s; updateVariant()"
                :class="['variant-button', selectedFlavor === s ? 'active' : '']">
                {{ s }}
              </button>
            </div>
          </div>

          <div class="option-group">
            <div class="label">Xuất xứ</div>
            <div class="option-list">
              <button v-for="s in getUnique('origin')" :key="'origin-' + s"
                @click="selectedOrigin = s; updateVariant()"
                :class="['variant-button', selectedOrigin === s ? 'active' : '']">
                {{ s }}
              </button>
            </div>
          </div>
        </div>

        <!-- Số lượng -->
        <div class="mb-3">
          <label class="fw-bold">Số lượng:</label>
          <div class="quantity-wrapper mt-1">
            <button class="quantity-btn" @click="decreaseQuantity">−</button>
            <input type="text" class="quantity-input" :value="quantity" readonly />
            <button class="quantity-btn" @click="increaseQuantity">+</button>
          </div>
        </div>

        <!-- Nút thêm vào giỏ hàng -->
        <button class="add-to-cart" @click="addToCart()" 
          :disabled="selectedProduct?.productVariants && selectedProduct.productVariants.length > 0 && !selectedVariant">
          Thêm vào Giỏ hàng
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import api from '@/axios';

const favoriteProducts = ref([]);
const loading = ref(true);
const error = ref('');
const router = useRouter();

// ========== Popup sản phẩm ==========
const showPopup = ref(false)
const selectedProduct = ref(null)
const selectedVariant = ref(null)

const selectedSize = ref('')
const selectedShape = ref('')
const selectedFlavor = ref('')
const selectedOrigin = ref('')

const defaultVariant = computed(() => selectedProduct.value?.productVariants?.[0] || null)

const quantity = ref(1)

const openPopup = async (product) => {
  try {
    // Lấy thông tin chi tiết sản phẩm từ API
    const response = await api.get(`/api/products/${product.productId}`);
    if (response.data) {
      // Kết hợp thông tin từ favorite với thông tin chi tiết từ API
      selectedProduct.value = {
        ...response.data,
        productName: product.productName,
        productImageUrl: product.productImageUrl,
        productPrice: product.productPrice,
        productDiscountPercentage: product.productDiscountPercentage
      };
      resetSelections();
      showPopup.value = true;
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin sản phẩm:', error);
    // Fallback: sử dụng thông tin có sẵn từ favorite
    selectedProduct.value = {
      ...product,
      productVariants: [] // Không có biến thể
    };
    resetSelections();
    showPopup.value = true;
  }
}

const closePopup = () => {
  showPopup.value = false
}

const resetSelections = () => {
  selectedSize.value = ''
  selectedShape.value = ''
  selectedFlavor.value = ''
  selectedOrigin.value = ''
  quantity.value = 1
  selectedVariant.value = null
}

const updateVariant = () => {
  if (!selectedSize.value || !selectedShape.value || !selectedFlavor.value || !selectedOrigin.value) {
    selectedVariant.value = null
    return
  }

  const variants = selectedProduct.value?.productVariants || []
  const found = variants.find(
    (v) =>
      v.size === selectedSize.value &&
      v.shape === selectedShape.value &&
      v.flavor === selectedFlavor.value &&
      v.origin === selectedOrigin.value
  )

  selectedVariant.value = found || null
}

const getUnique = (field) => {
  const variants = selectedProduct.value?.productVariants || []
  return [...new Set(variants.map((v) => v[field]))]
}

const increaseQuantity = () => {
  const max = selectedVariant.value?.quantity ?? defaultVariant.value?.quantity ?? 999
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

// ========== Thêm vào giỏ ==========
const addToCart = async () => {
  try {
    // Nếu sản phẩm có biến thể nhưng chưa chọn biến thể
    if (selectedProduct.value?.productVariants && selectedProduct.value.productVariants.length > 0 && !selectedVariant.value) {
      Swal.fire({
        icon: 'info',
        title: 'Vui lòng chọn biến thể',
        text: 'Bạn cần chọn size, hình dáng, hương vị và xuất xứ trước khi thêm vào giỏ hàng!',
        confirmButtonText: 'OK',
      })
      return
    }

    const requestData = {
      quantity: quantity.value,
    }

    // Nếu có biến thể được chọn
    if (selectedVariant.value) {
      requestData.variantId = selectedVariant.value.id
    } else {
      // Nếu không có biến thể, sử dụng productId
      requestData.productId = selectedProduct.value.productId || selectedProduct.value.id
    }

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

    await api.post('/api/cart/add', requestData)
    
    Swal.fire({
      icon: 'success',
      title: 'Thêm vào giỏ hàng thành công!',
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 2000
    })
    
    closePopup()
  } catch (err) {
    console.error('❌ Lỗi thêm vào giỏ:', err)
    
    let errorMessage = 'Không thể thêm sản phẩm vào giỏ hàng. Vui lòng thử lại.'
    
    if (err.response) {
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
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.'
    }
    
    Swal.fire({
      icon: 'error',
      title: 'Thêm vào giỏ hàng thất bại!',
      text: errorMessage,
      confirmButtonText: 'OK'
    })
  }
}

// ========== Hover sản phẩm ==========
function MouseOver(event) {
  const productElement = event.currentTarget
  const buyNowButton = productElement.querySelector('.btnmuangay10')
  const buyNowButton1 = productElement.querySelector('.btnmuangay11')

  productElement.style.transform = 'scale(1)'
  productElement.style.transition = 'transform 0.3s ease'

  if (buyNowButton) buyNowButton.style.display = 'block'
  if (buyNowButton1) buyNowButton1.style.display = 'block'
}

function MouseOut(event) {
  const productElement = event.currentTarget
  const buyNowButton = productElement.querySelector('.btnmuangay10')
  const buyNowButton1 = productElement.querySelector('.btnmuangay11')

  productElement.style.transform = 'scale(1)'
  if (buyNowButton) buyNowButton.style.display = 'none'
  if (buyNowButton1) buyNowButton1.style.display = 'none'
}

watch(favoriteProducts, async () => {
  await nextTick()
  const productElements = document.querySelectorAll('.prod1')
  productElements.forEach((el) => {
    el.addEventListener('mouseover', MouseOver)
    el.addEventListener('mouseout', MouseOut)
  })
})

// Lấy danh sách sản phẩm yêu thích từ API
const loadFavoriteProducts = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    const response = await api.get('/api/favorites/user');
    
    if (response.data.success) {
      favoriteProducts.value = response.data.data;
    } else {
      error.value = 'Không thể tải danh sách yêu thích';
    }
  } catch (err) {
    console.error('Lỗi khi tải danh sách yêu thích:', err);
    
    if (err.response?.status === 401) {
      error.value = 'Bạn chưa đăng nhập. Vui lòng đăng nhập để xem danh sách yêu thích.';
      
      // Hiển thị thông báo và chuyển hướng đến trang đăng nhập
      Swal.fire({
        icon: 'warning',
        title: 'Bạn chưa đăng nhập',
        text: 'Vui lòng đăng nhập để xem danh sách yêu thích',
        confirmButtonText: 'Đăng nhập',
      }).then((result) => {
        if (result.isConfirmed) {
          router.push('/Login');
        }
      });
    } else {
      error.value = err.response?.data?.message || 'Có lỗi xảy ra khi tải danh sách yêu thích';
    }
  } finally {
    loading.value = false;
  }
};

// Xóa sản phẩm khỏi danh sách yêu thích
const removeFavorite = async (product) => {
  try {
    const result = await Swal.fire({
      title: 'Xác nhận',
      text: `Bạn có chắc chắn muốn xóa "${product.productName}" khỏi danh sách yêu thích?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#cd9b32',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    });

    if (result.isConfirmed) {
      const response = await api.delete('/api/favorites/remove', {
        params: { productId: product.productId }
      });

      if (response.data.success) {
        // Xóa sản phẩm khỏi danh sách hiện tại
        favoriteProducts.value = favoriteProducts.value.filter(p => p.id !== product.id);
        
        Swal.fire({
          icon: 'success',
          title: 'Đã xóa',
          text: response.data.message || 'Đã xóa sản phẩm khỏi danh sách yêu thích',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      }
    }
  } catch (err) {
    console.error('Lỗi khi xóa sản phẩm yêu thích:', err);
    
    Swal.fire({
      icon: 'error',
      title: 'Có lỗi xảy ra',
      text: err.response?.data?.message || 'Không thể xóa sản phẩm khỏi danh sách yêu thích'
    });
  }
};

onMounted(() => {
  loadFavoriteProducts();
});
</script>

<style scoped>
/* Header */
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
  font-family: 'Playball';
}

.breadcrumb {
  font-size: 18px;
  padding: 15px 10px;
  line-height: 24px;
  width: 100%;
  text-align: left;
  padding-left: 560px;
}

.breadcrumb .home {
  display: inline-flex;
  align-items: center;
}

.breadcrumb .home a {
  text-decoration: none !important;
  color: white;
  font-size: 18px !important;
  margin-right: 10px;
}

.breadcrumb .home a:nth-child(2) span {
  color: #cd9b32 !important;
  font-size: 18px !important;
}

.breadcrumb li {
  display: inline-block;
  margin: 0 5px;
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

.prod1img {
  position: relative;
  overflow: hidden;
  width: 250px;
  height: 250px;
  border-radius: 15px;
}

.prod1img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: inherit;
  transition: transform 0.3s ease;
  display: block;
}

.prod1img:hover img {
  transform: scale(1.1);
}

.btnmuangay1 {
  position: absolute;
  top: 7%;
  left: 15%;
  transform: translate(-50%, -50%);
  z-index: 1;
  color: white;
  font-size: 1.2rem;
  text-align: center;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.btnmuangay2 {
  position: absolute;
  top: 7%;
  left: 75%;
  transform: translate(-50%, -50%);
  z-index: 1;
  color: white;
  font-size: 1.2rem;
  text-align: center;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.btnmuangay3 {
  position: absolute;
  top: 17%;
  left: 15%;
  transform: translate(-50%, -50%);
  z-index: 1;
  color: white;
  font-size: 1.2rem;
  text-align: center;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.btnmuangay10 {
  position: absolute;
  top: 50%;
  left: 30%;
  transform: translate(-50%, -50%);
  display: none;
  z-index: 1;
  padding: 10px 20px;
  color: white;
  font-size: 1.2rem;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.btnmuangay11 {
  position: absolute;
  top: 50%;
  left: 55%;
  transform: translate(-50%, -50%);
  display: none;
  z-index: 1;
  padding: 10px 20px;
  color: white;
  font-size: 1.2rem;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.product-name a {
  color: #000;
  text-decoration: none;
  transition: color 0.3s;
}

.product-name a:hover {
  color: #d4a548;
}

/* Hover effects for product items */
.prod1:hover .btnmuangay10,
.prod1:hover .btnmuangay11 {
  display: block;
}

.prod1 {
  transition: transform 0.3s ease;
}

.prod1:hover {
  transform: scale(1.02);
}

/* ========== Grid Layout ========== */
.row-fix {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin: 0 -15px;
}

.col-fix {
  padding: 0 15px;
  margin-bottom: 30px;
  width: 100%;
}

/* Responsive */
@media (max-width: 1200px) {
  .row-fix {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .row-fix {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .row-fix {
    grid-template-columns: 1fr;
  }
}

/* ========== Popup Styles ========== */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.popup-content {
  background: #fff;
  padding: 20px;
  width: 90%;
  max-width: 450px;
  border-radius: 10px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
  border: none;
  background: none;
  cursor: pointer;
}

.product-header {
  display: flex;
  gap: 12px;
  margin-bottom: 15px;
}

.main-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
}

.price-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.price-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.price {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.new-price {
  color: #d9534f;
}

.old-price {
  text-decoration: line-through;
  color: #aaa;
  margin-left: 10px;
  font-size: 14px;
}

.stock {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.option-group {
  margin-top: 15px;
}

.label {
  font-weight: 600;
  margin-bottom: 5px;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.variant-button {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s ease;
}

.variant-button.active {
  border-color: #cd9b32;
  background-color: #cd9b32;
  color: white;
}

.variant-button:hover {
  border-color: #cd9b32;
}

.add-to-cart {
  width: 100%;
  background-color: #cd9b32;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 15px;
  transition: background-color 0.2s ease;
}

.add-to-cart:hover {
  background-color: #b8872d;
}

.add-to-cart:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.quantity-wrapper {
  display: flex;
  align-items: center;
  border: 1.5px solid #d5a84b;
  border-radius: 6px;
  padding: 3px;
  width: fit-content;
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

.quantity-input {
  width: 40px;
  height: 36px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  background-color: transparent;
  color: #000;
  outline: none;
  border: none;
  pointer-events: none;
}

.mb-3 {
  margin-bottom: 1rem;
}

.fw-bold {
  font-weight: bold;
}

.mt-1 {
  margin-top: 0.25rem;
}
</style>