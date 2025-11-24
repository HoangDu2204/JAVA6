<template>
  <div class="search-results-page">
    <!-- Breadcrumb Section -->
    <section class="bread-crumb"
      style="background: linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)), url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436) center no-repeat;">
      <div class="container">
        <div class="title-bread-crumb">
          Tìm kiếm
        </div>
        <ul class="breadcrumb">
          <li class="home">
            <RouterLink to="/"><span>Trang chủ ></span></RouterLink>
            <span>Tìm kiếm</span>
          </li>
        </ul>
      </div>
    </section>

    <!-- Search Results Content -->
    <div class="container-fluid my-5">
      <div class="container">
        <!-- Search Info -->
        <div class="search-info">
          <h2 class="search-title">
            Kết quả tìm kiếm cho: "<span class="search-query">{{ searchQuery }}</span>"
          </h2>
          <p class="search-count" v-if="!isLoading">
            Tìm thấy {{ totalResults }} kết quả
          </p>
        </div>

        <!-- Search Tabs -->
        <!-- <div class="search-tabs-container">
          <div class="search-tabs">
            <button
              :class="[
                'search-tab',
                { active: activeTab === 'products' }
              ]"
              @click="switchTab('products')"
            >
              Sản phẩm ({{ productResults.length }})
            </button>
            <button
              :class="[
                'search-tab',
                { active: activeTab === 'news' }
              ]"
              @click="switchTab('news')"
            >
              Tin tức ({{ newsResults.length }})
            </button>
          </div>
        </div> -->

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>Đang tìm kiếm...</p>
        </div>

        <!-- Search Results -->
        <div v-else class="search-results-content">
          <!-- Products Tab -->
          <div v-if="activeTab === 'products'" class="products-results">
            <div v-if="productResults.length > 0" class="row">
              <div
                v-for="product in paginatedProducts"
                :key="product.id"
                class="col-xl-3 col-lg-4 col-md-6 col-sm-6 col-12 mb-4"
              >
                <div class="product-card">
                  <div class="product-image-container">
                    <img
                      :src="getProductImage(product)"
                      :alt="product.name"
                      class="product-image"
                      @error="handleImageError"
                    />
                    <!-- <div v-if="product.discountPercentage > 0" class="discount-badge">
                      -{{ product.discountPercentage }}%
                    </div> -->
                    <div class="product-overlay">
                      <button
                        class="btn-view-product"
                        @click="goToProduct(product.id)"
                      >
                        <i class="bi bi-eye"></i>
                        Xem chi tiết
                      </button>
                    </div>
                  </div>
                  <div class="product-info">
                    <h4 class="product-name" @click="goToProduct(product.id)">
                      {{ product.name }}
                    </h4>
                    <div class="current-price">
                      <span v-if="product.discountPercentage > 0" >
                        {{ formatPrice(product.price) }}đ
                      </span>
                      <!-- <span class="current-price">
                        {{ formatPrice(getFinalPrice(product)) }}đ
                      </span> -->
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- No Products Found -->
            <div v-else class="no-results">
              <i class="bi bi-search"></i>
              <h3>Không tìm thấy sản phẩm nào</h3>
              <p>Hãy thử tìm kiếm với từ khóa khác hoặc xem tất cả sản phẩm của chúng tôi</p>
              <RouterLink to="/sanPham" class="btn btn-primary">
                Xem tất cả sản phẩm
              </RouterLink>
            </div>

            <!-- Pagination for Products -->
            <div v-if="productResults.length > productsPerPage" class="pagination-container">
              <nav aria-label="Product pagination">
                <ul class="pagination">
                  <li class="page-item" :class="{ disabled: currentProductPage === 1 }">
                    <button
                      class="page-link"
                      @click="changeProductPage(currentProductPage - 1)"
                      :disabled="currentProductPage === 1"
                    >
                      <i class="bi bi-chevron-left"></i>
                    </button>
                  </li>

                  <li
                    v-for="page in productPageNumbers"
                    :key="page"
                    class="page-item"
                    :class="{ active: page === currentProductPage }"
                  >
                    <button
                      class="page-link"
                      @click="changeProductPage(page)"
                    >
                      {{ page }}
                    </button>
                  </li>

                  <li class="page-item" :class="{ disabled: currentProductPage === totalProductPages }">
                    <button
                      class="page-link"
                      @click="changeProductPage(currentProductPage + 1)"
                      :disabled="currentProductPage === totalProductPages"
                    >
                      <i class="bi bi-chevron-right"></i>
                    </button>
                  </li>
                </ul>
              </nav>
            </div>
          </div>

          <!-- News Tab -->
          <div v-else-if="activeTab === 'news'" class="news-results">
            <div v-if="newsResults.length > 0" class="row">
              <div
                v-for="article in paginatedNews"
                :key="article.id"
                class="col-lg-6 col-12 mb-4"
              >
                <div class="news-card">
                  <div class="news-image-container">
                    <img
                      :src="getNewsImage(article)"
                      :alt="article.title"
                      class="news-image"
                      @error="handleImageError"
                    />
                  </div>
                  <div class="news-content">
                    <div class="news-meta">
                      <span class="news-date">{{ formatDate(article.createdAt) }}</span>
                      <span class="news-category">{{ article.category || 'Tin tức' }}</span>
                    </div>
                    <h3 class="news-title" @click="goToArticle(article.id)">
                      {{ article.title }}
                    </h3>
                    <p class="news-excerpt">{{ getExcerpt(article.content) }}</p>
                    <button
                      class="btn-read-more"
                      @click="goToArticle(article.id)"
                    >
                      Đọc thêm
                      <i class="bi bi-arrow-right"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- No News Found -->
            <div v-else class="no-results">
              <i class="bi bi-newspaper"></i>
              <h3>Không tìm thấy tin tức nào</h3>
              <p>Hãy thử tìm kiếm với từ khóa khác hoặc xem tất cả tin tức của chúng tôi</p>
              <RouterLink to="/tinTuc" class="btn btn-primary">
                Xem tất cả tin tức
              </RouterLink>
            </div>

            <!-- Pagination for News -->
            <div v-if="newsResults.length > newsPerPage" class="pagination-container">
              <nav aria-label="News pagination">
                <ul class="pagination">
                  <li class="page-item" :class="{ disabled: currentNewsPage === 1 }">
                    <button
                      class="page-link"
                      @click="changeNewsPage(currentNewsPage - 1)"
                      :disabled="currentNewsPage === 1"
                    >
                      <i class="bi bi-chevron-left"></i>
                    </button>
                  </li>

                  <li
                    v-for="page in newsPageNumbers"
                    :key="page"
                    class="page-item"
                    :class="{ active: page === currentNewsPage }"
                  >
                    <button
                      class="page-link"
                      @click="changeNewsPage(page)"
                    >
                      {{ page }}
                    </button>
                  </li>

                  <li class="page-item" :class="{ disabled: currentNewsPage === totalNewsPages }">
                    <button
                      class="page-link"
                      @click="changeNewsPage(currentNewsPage + 1)"
                      :disabled="currentNewsPage === totalNewsPages"
                    >
                      <i class="bi bi-chevron-right"></i>
                    </button>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// Reactive data
const searchQuery = ref('')
const activeTab = ref('products')
const isLoading = ref(false)
const productResults = ref([])
const newsResults = ref([])

// Pagination
const currentProductPage = ref(1)
const currentNewsPage = ref(1)
const productsPerPage = 12
const newsPerPage = 6

// Computed properties
const totalResults = computed(() => {
  return productResults.value.length //+ newsResults.value.length
})

const totalProductPages = computed(() => {
  return Math.ceil(productResults.value.length / productsPerPage)
})

const totalNewsPages = computed(() => {
  return Math.ceil(newsResults.value.length / newsPerPage)
})

const paginatedProducts = computed(() => {
  const start = (currentProductPage.value - 1) * productsPerPage
  const end = start + productsPerPage
  return productResults.value.slice(start, end)
})

const paginatedNews = computed(() => {
  const start = (currentNewsPage.value - 1) * newsPerPage
  const end = start + newsPerPage
  return newsResults.value.slice(start, end)
})

const productPageNumbers = computed(() => {
  const pages = []
  const total = totalProductPages.value
  const current = currentProductPage.value

  // Show max 5 page numbers
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)

  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const newsPageNumbers = computed(() => {
  const pages = []
  const total = totalNewsPages.value
  const current = currentNewsPage.value

  // Show max 5 page numbers
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)

  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// Methods
const performSearch = async () => {
  if (!searchQuery.value.trim()) return

  isLoading.value = true

  try {
    // Search products using full search endpoint
    const productResponse = await fetch(`http://localhost:8080/api/search/products?name=${encodeURIComponent(searchQuery.value)}`)
    if (productResponse.ok) {
      productResults.value = await productResponse.json()
    } else {
      console.error('Failed to fetch products:', productResponse.status)
      productResults.value = []
    }

    // Search news
    const newsResponse = await fetch(`http://localhost:8080/api/search/news?query=${encodeURIComponent(searchQuery.value)}`)
    if (newsResponse.ok) {
      newsResults.value = await newsResponse.json()
    } else {
      console.error('Failed to fetch news:', newsResponse.status)
      newsResults.value = []
    }

  } catch (error) {
    console.error('Search error:', error)
    productResults.value = []
    newsResults.value = []
  } finally {
    isLoading.value = false
  }
}


const switchTab = (tab) => {
  activeTab.value = tab
  // Reset pagination when switching tabs
  if (tab === 'products') {
    currentProductPage.value = 1
  } else {
    currentNewsPage.value = 1
  }

  // Update URL
  router.replace({
    query: {
      ...route.query,
      type: tab
    }
  })
}

const changeProductPage = (page) => {
  if (page >= 1 && page <= totalProductPages.value) {
    currentProductPage.value = page
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const changeNewsPage = (page) => {
  if (page >= 1 && page <= totalNewsPages.value) {
    currentNewsPage.value = page
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const goToArticle = (articleId) => {
  router.push(`/binhLuanTinTuc/${articleId}`)
}

// Helper functions
const getProductImage = (product) => {
  if (product.imageUrls && product.imageUrls.length > 0) {
    return `http://localhost:8080/images/${product.imageUrls[0]}`
  }
  return '/default-product.jpg'
}

const getNewsImage = (news) => {
  if (news.imageUrl) {
    return `http://localhost:8080/images/${news.imageUrl}`
  }
  return '/default-news.jpg'
}

const getFinalPrice = (product) => {
  if (product.discountPercentage > 0) {
    return product.price * (1 - product.discountPercentage / 100)
  }
  return product.price
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN').format(price)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const getExcerpt = (content) => {
  if (!content) return '';
  const strippedContent = content.replace(/<[^>]*>/g, ''); // Remove HTML tags
  return strippedContent.length > 100 ? strippedContent.substring(0, 100) + '...' : strippedContent;
}

const handleImageError = (event) => {
  event.target.src = '/default-product.jpg'
}

// Lifecycle
onMounted(() => {
  // Get search query from URL
  searchQuery.value = route.query.q || ''
  activeTab.value = route.query.type || 'products'

  if (searchQuery.value) {
    performSearch()
  }
})

// Watch for route changes
watch(() => route.query, (newQuery) => {
  if (newQuery.q !== searchQuery.value) {
    searchQuery.value = newQuery.q || ''
    if (searchQuery.value) {
      performSearch()
    }
  }

  if (newQuery.type !== activeTab.value) {
    activeTab.value = newQuery.type || 'products'
  }
})
</script>

<style scoped>
.search-results-page {
  font-family: 'Quicksand', sans-serif;
}

/* Breadcrumb */
.bread-crumb {
  background-size: cover !important;
  background-position: center !important;
  padding: 100px 0 50px;
  position: relative;
}

.title-bread-crumb {
  font-size: 55px;
  color: #cd9b32;
  font-weight: normal;
  margin-bottom: 10px;
  font-family: 'Forte';
  text-align: center;
}

.breadcrumb {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
.breadcrumb li {
  display: inline;
  color: #eee;
  font-size: 18px;
}

.breadcrumb a {
  color: #eee;
  text-decoration: none;
}

.breadcrumb a:hover {
  color: #cd9b32;
}

/* Search Info */
.search-info {
  text-align: center;
  margin-bottom: 40px;
}

.search-title {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.search-query {
  color: #cd9b32;
  font-weight: 600;
}

.search-count {
  color: #666;
  font-size: 16px;
}

/* Search Tabs */
.search-tabs-container {
  margin-bottom: 40px;
}

.search-tabs {
  display: flex;
  justify-content: center;
  border-bottom: 2px solid #e0e0e0;
}

.search-tab {
  padding: 15px 30px;
  background: none;
  border: none;
  font-size: 18px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
  margin: 0 10px;
}

.search-tab.active {
  color: #cd9b32;
  border-bottom-color: #cd9b32;
}

.search-tab:hover {
  color: #cd9b32;
}

/* Loading State */
.loading-container {
  text-align: center;
  padding: 50px 0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #cd9b32;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Product Card */
.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transform: translateY(-3px);
}

.product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect Ratio */
  overflow: hidden;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.discount-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #ff4757;
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: 600;
  z-index: 1;
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.btn-view-product {
  background: #cd9b32;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s ease;
}

.btn-view-product:hover {
  background: #b8862d;
}

.product-info {
  padding: 15px;
  text-align: center;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.product-name:hover {
  color: #cd9b32;
}

.product-price {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 15px;
}

.current-price {
  color: #cd9b32;
  font-weight: 700;
  font-size: 18px;
}

/* News Card */
.news-card {
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  display: flex;
  height: 100%;
}

.news-card:hover {
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transform: translateY(-3px);
}

.news-image-container {
  width: 40%;
  flex-shrink: 0;
}

.news-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.news-meta {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
}

.news-meta span {
  margin-right: 10px;
}

.news-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.news-title:hover {
  color: #cd9b32;
}

.news-excerpt {
  font-size: 15px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
}

.btn-read-more {
  background: none;
  border: none;
  color: #cd9b32;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: gap 0.3s ease;
}

.btn-read-more:hover {
  gap: 10px;
}

/* No Results */
.no-results {
  text-align: center;
  padding: 50px 0;
}

.no-results i {
  font-size: 60px;
  color: #ddd;
  margin-bottom: 20px;
}

.no-results h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.no-results p {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.no-results .btn-primary {
  background-color: #cd9b32;
  border-color: #cd9b32;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.no-results .btn-primary:hover {
  background-color: #b8862d;
  border-color: #b8862d;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.pagination {
  display: flex;
  padding-left: 0;
  list-style: none;
  border-radius: .25rem;
}

.page-item .page-link {
  position: relative;
  display: block;
  padding: .5rem .75rem;
  margin-left: -1px;
  line-height: 1.25;
  color: #cd9b32;
  background-color: #fff;
  border: 1px solid #dee2e6;
  transition: all 0.3s ease;
}

.page-item.active .page-link {
  z-index: 1;
  color: #fff;
  background-color: #cd9b32;
  border-color: #cd9b32;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}

.page-item .page-link:hover {
  color: #b8862d;
  background-color: #e9ecef;
  border-color: #dee2e6;
}

/* Responsive */
@media (max-width: 991px) {
  .news-card {
    flex-direction: column;
  }
  .news-image-container {
    width: 100%;
    height: 200px;
  }
}

@media (max-width: 767px) {
  .title-bread-crumb {
    font-size: 40px;
  }
  .search-title {
    font-size: 24px;
  }
  .search-tab {
    padding: 10px 15px;
    font-size: 16px;
  }
  .product-card, .news-card {
    margin-bottom: 20px;
  }
}
</style>


