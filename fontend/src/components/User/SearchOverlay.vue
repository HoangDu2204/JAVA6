<template>
  <div>
    <!-- Search Overlay -->
    <div
      v-if="isSearchVisible"
      class="search-overlay"
      @click="closeSearch"
    >
      <div class="search-container" @click.stop>
        <!-- Close Button -->
        <button class="search-close-btn" @click="closeSearch">
          <i class="bi bi-x-lg"></i>
        </button>

        <!-- Search Input -->
        <div class="search-input-wrapper">
          <input
            ref="searchInput"
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm bánh..."
            class="search-input"
            @input="onSearchInput"
            @keydown.enter="viewAllResults"
            @keydown.escape="closeSearch"
          />
          <i class="bi bi-search search-icon"></i>
        </div>

        <!-- Search Tabs -->
        <!-- <div class="search-tabs">
          <button
            :class="['search-tab', { active: activeTab === 'products' }]"
            @click="activeTab = 'products'"
          >
            Sản phẩm
          </button>
          <button
            :class="['search-tab', { active: activeTab === 'news' }]"
            @click="activeTab = 'news'"
          >
            Tin tức
          </button>
        </div> -->

        <!-- Search Results -->
        <div class="search-results" v-if="searchQuery.trim()">
          <!-- Loading State -->
          <div v-if="isLoading" class="search-loading">
            <div class="loading-spinner"></div>
            <p>Đang tìm kiếm...</p>
          </div>

          <!-- Products Tab -->
          <div v-else-if="activeTab === 'products'" class="search-products">
            <div v-if="searchResults.products.length > 0">
              <p class="search-count">Có {{ searchResults.products.length }} sản phẩm</p>

              <div class="product-suggestions">
                <div
                  v-for="product in searchResults.products.slice(0, 4)"
                  :key="product.id"
                  class="product-suggestion-item"
                  @click="goToProduct(product.id)"
                >
                  <div class="product-image">
                    <img
                      :src="getProductImage(product)"
                      :alt="product.name"
                      @error="handleImageError"
                    />
                  </div>
                  <div class="product-info">
                    <h4 class="product-name">{{ product.name }}</h4>
                    <div class="product-price">
                      <!-- <span v-if="product.discountPercentage > 0" class="original-price">
                        {{ formatPrice(product.price) }}đ
                      </span> -->
                      <span class="current-price">
                        {{ formatPrice(product.price) }}đ
                      </span>
                      <!-- <span v-if="product.discountPercentage > 0" class="discount-badge">
                        -{{ product.discountPercentage }}%
                      </span> -->
                    </div>
                  </div>
                </div>
              </div>

              <!-- View All Button -->
              <button
                v-if="searchResults.products.length > 4"
                class="view-all-btn"
                @click="viewAllResults"
              >
                Xem tất cả
              </button>
            </div>

            <!-- No Products Found -->
            <div v-else class="no-results">
              <i class="bi bi-search"></i>
              <p>Không tìm thấy sản phẩm nào</p>
            </div>
          </div>

          <!-- News Tab -->
          <div v-else-if="activeTab === 'news'" class="search-news">
            <div v-if="searchResults.news.length > 0">
              <p class="search-count">Có {{ searchResults.news.length }} tin tức</p>

              <div class="news-suggestions">
                <div
                  v-for="article in searchResults.news.slice(0, 4)"
                  :key="article.id"
                  class="news-suggestion-item"
                  @click="goToArticle(article.id)"
                >
                  <div class="news-image">
                    <img
                      :src="article.imageUrl || '/default-news.jpg'"
                      :alt="article.title"
                      @error="handleImageError"
                    />
                  </div>
                  <div class="news-info">
                    <h4 class="news-title">{{ article.title }}</h4>
                    <p class="news-excerpt">{{ article.excerpt }}</p>
                    <span class="news-date">{{ formatDate(article.createdAt) }}</span>
                  </div>
                </div>
              </div>

              <!-- View All Button -->
              <button
                v-if="searchResults.news.length > 4"
                class="view-all-btn"
                @click="viewAllResults"
              >
                Xem tất cả
              </button>
            </div>

            <!-- No News Found -->
            <div v-else class="no-results">
              <i class="bi bi-newspaper"></i>
              <p>Không tìm thấy tin tức nào</p>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="search-empty">
          <i class="bi bi-search"></i>
          <p>Nhập từ khóa để tìm kiếm</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['close'])

// Reactive data
const isSearchVisible = ref(props.isVisible)
const searchQuery = ref('')
const activeTab = ref('products')
const isLoading = ref(false)
const searchInput = ref(null)

const searchResults = reactive({
  products: [],
  news: []
})

// Debounce timer
let searchTimeout = null

// Watch for visibility changes
watch(() => props.isVisible, (newVal) => {
  isSearchVisible.value = newVal
  if (newVal) {
    nextTick(() => {
      searchInput.value?.focus()
    })
  }
})

// Search input handler with debounce
const onSearchInput = () => {
  clearTimeout(searchTimeout)

  if (searchQuery.value.trim().length < 2) {
    searchResults.products = []
    searchResults.news = []
    return
  }

  searchTimeout = setTimeout(() => {
    performSearch()
  }, 300)
}

// Perform search
const performSearch = async () => {
  if (!searchQuery.value.trim()) return

  isLoading.value = true

  try {
    // Search products using suggestions endpoint for faster response
    if (activeTab.value === 'products') {
      const response = await fetch(`http://localhost:8080/api/search/products/suggestions?name=${encodeURIComponent(searchQuery.value)}&limit=6`)
      if (response.ok) {
        const products = await response.json()
        searchResults.products = products
      } else {
        console.error('Failed to fetch products:', response.status)
        searchResults.products = []
      }
    }

    // Search news
    if (activeTab.value === 'news') {
      const response = await fetch(`http://localhost:8080/api/search/news?query=${encodeURIComponent(searchQuery.value)}`)
      if (response.ok) {
        const news = await response.json()
        searchResults.news = news
      } else {
        // Fallback to mock data if API not implemented
        searchResults.news = mockNewsData.filter(article =>
          article.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          article.excerpt.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }
    }
  } catch (error) {
    console.error('Search error:', error)
    // Set empty results on error
    if (activeTab.value === 'products') {
      searchResults.products = []
    } else {
      searchResults.news = []
    }
  } finally {
    isLoading.value = false
  }
}

// Mock news data for fallback
//

// Close search overlay
const closeSearch = () => {
  isSearchVisible.value = false
  searchQuery.value = ''
  searchResults.products = []
  searchResults.news = []
  emit('close')
}

// Navigate to product detail
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
  closeSearch()
}

// Navigate to article detail
const goToArticle = (articleId) => {
  router.push(`/binhluanTinTuc/${articleId}`)
  closeSearch()
}

// View all search results
const viewAllResults = () => {
  const query = searchQuery.value.trim()
  if (query) {
    router.push({
      path: '/timKiem',
      query: {
        q: query,
        type: activeTab.value
      }
    })
    closeSearch()
  }
}

// Helper functions
const getProductImage = (product) => {
  if (product.imageUrls && product.imageUrls.length > 0) {
    return `http://localhost:8080/images/${product.imageUrls[0]}`
  }
  return '/default-product.jpg'
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

const handleImageError = (event) => {
  event.target.src = '/default-product.jpg'
}

// Watch active tab changes
watch(activeTab, () => {
  if (searchQuery.value.trim()) {
    performSearch()
  }
})
</script>

<style scoped>
.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  z-index: 9999;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 100px;
  backdrop-filter: blur(5px);
}

.search-container {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-close-btn {
  position: absolute;
  /* top: 20px; */

  right: 5px;
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  z-index: 10;
  transition: color 0.2s;
}

.search-close-btn:hover {
  color: #cd9b32;
}

.search-input-wrapper {
  position: relative;
  padding: 30px 30px 20px;
}

.search-input {
  width: 100%;
  padding: 15px 50px 15px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 50px;
  font-size: 18px;
  outline: none;
  transition: border-color 0.3s;
}

.search-input:focus {
  border-color: #cd9b32;
}

.search-icon {
  position: absolute;
  right: 50px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 20px;
}

.search-tabs {
  display: flex;
  padding: 0 30px;
  border-bottom: 1px solid #e0e0e0;
}

.search-tab {
  padding: 15px 25px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
}

.search-tab.active {
  color: #cd9b32;
  border-bottom-color: #cd9b32;
}

.search-tab:hover {
  color: #cd9b32;
}

.search-results {
  padding: 20px 30px 30px;
}

.search-loading {
  text-align: center;
  padding: 40px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #cd9b32;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.search-count {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.product-suggestions,
.news-suggestions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-suggestion-item,
.news-suggestion-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.product-suggestion-item:hover,
.news-suggestion-item:hover {
  background: #f9f9f9;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image,
.news-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 15px;
  flex-shrink: 0;
}

.product-image img,
.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info,
.news-info {
  flex: 1;
}

.product-name,
.news-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
  font-size: 14px;
}

.current-price {
  color: #cd9b32;
  font-weight: 600;
  font-size: 16px;
}

.discount-badge {
  background: #ff4757;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.news-excerpt {
  color: #666;
  font-size: 14px;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.news-date {
  color: #999;
  font-size: 12px;
}

.view-all-btn {
  width: 100%;
  padding: 15px;
  background: #cd9b32;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 20px;
  transition: background 0.3s;
}

.view-all-btn:hover {
  background: #b8862d;
}

.no-results,
.search-empty {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-results i,
.search-empty i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.no-results p,
.search-empty p {
  font-size: 16px;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .search-overlay {
    padding-top: 50px;
  }

  .search-container {
    width: 95%;
    max-height: 90vh;
  }

  .search-input-wrapper {
    padding: 20px 20px 15px;
  }

  .search-results {
    padding: 15px 20px 20px;
  }

  .product-suggestion-item,
  .news-suggestion-item {
    padding: 12px;
  }

  .product-image,
  .news-image {
    width: 60px;
    height: 60px;
    margin-right: 12px;
  }

  .product-name,
  .news-title {
    font-size: 14px;
  }

  .current-price {
    font-size: 14px;
  }
}





/* CSS từ search-styles.css */
/* ===== DOLA BAKERY SEARCH STYLES ===== */

/* Global Search Variables */
:root {
  --dola-primary: #cd9b32;
  --dola-primary-dark: #b8862d;
  --dola-primary-light: #f7d690;
  --dola-secondary: #e4a53d;
  --dola-text-dark: #333;
  --dola-text-light: #666;
  --dola-text-muted: #999;
  --dola-border: #e0e0e0;
  --dola-background: #fff;
  --dola-background-light: #f9f9f9;
  --dola-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  --dola-shadow-hover: 0 10px 30px rgba(0, 0, 0, 0.15);
  --dola-radius: 8px;
  --dola-radius-large: 15px;
  --dola-transition: all 0.3s ease;
}

/* Enhanced Search Overlay Animations */
.search-overlay {
  animation: fadeInBackdrop 0.3s ease-out;
}

@keyframes fadeInBackdrop {
  from {
    opacity: 0;
    backdrop-filter: blur(0px);
  }
  to {
    opacity: 1;
    backdrop-filter: blur(5px);
  }
}

.search-container {
  animation: slideDownBounce 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes slideDownBounce {
  0% {
    opacity: 0;
    transform: translateY(-100px) scale(0.9);
  }
  70% {
    transform: translateY(10px) scale(1.02);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Enhanced Search Input */
.search-input-wrapper {
  position: relative;
}

.search-input-wrapper::before {
  content: \'\';
  position: absolute;
  top: 50%;
  left: 20px;
  transform: translateY(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--dola-primary), var(--dola-secondary));
  transition: width 0.3s ease;
  z-index: 1;
}

.search-input:focus + .search-icon {
  color: var(--dola-primary);
  transform: translateY(-50%) scale(1.1);
}

.search-input:focus ~ .search-input-wrapper::before {
  width: calc(100% - 40px);
}

/* Enhanced Product Suggestion Items */
.product-suggestion-item {
  position: relative;
  overflow: hidden;
}

.product-suggestion-item::before {
  content: \'\';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(205, 155, 50, 0.1), transparent);
  transition: left 0.5s ease;
}

.product-suggestion-item:hover::before {
  left: 100%;
}

.product-suggestion-item:hover .product-image img {
  transform: scale(1.05);
}

.product-suggestion-item:hover .product-name {
  color: var(--dola-primary);
}

/* Enhanced Loading Animation */
.loading-spinner {
  position: relative;
}

.loading-spinner::after {
  content: \'\';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  margin: -30px 0 0 -30px;
  border: 3px solid transparent;
  border-top: 3px solid var(--dola-secondary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Enhanced Search Results Page */
.search-results-page {
  background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
  min-height: 100vh;
}

/* Enhanced Product Cards */
.product-card {
  position: relative;
  overflow: hidden;
  transition: var(--dola-transition);
}

.product-card::before {
  content: \'\';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(205, 155, 50, 0.05) 0%, rgba(228, 165, 61, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
}

.product-card:hover::before {
  opacity: 1;
}

.product-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--dola-shadow-hover);
}

.product-image {
  transition: transform 0.5s ease;
}

.product-card:hover .product-image {
  transform: scale(1.1);
}

/* Enhanced Discount Badge */
.discount-badge {
  position: relative;
  overflow: hidden;
}

.discount-badge::before {
  content: \'\';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

/* Enhanced News Cards */
.news-card {
  position: relative;
  overflow: hidden;
}

.news-card::after {
  content: \'\';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--dola-primary), var(--dola-secondary));
  transition: width 0.3s ease;
}

.news-card:hover::after {
  width: 100%;
}

.news-card:hover .news-image img {
  transform: scale(1.05);
  filter: brightness(1.1);
}

/* Enhanced Pagination */
.pagination .page-link {
  position: relative;
  overflow: hidden;
}

.pagination .page-link::before {
  content: \'\';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(205, 155, 50, 0.2), transparent);
  transition: left 0.3s ease;
}

.pagination .page-link:hover::before {
  left: 100%;
}

/* Enhanced Search Tabs */
.search-tab {
  position: relative;
  overflow: hidden;
}

.search-tab::before {
  content: \'\';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--dola-primary), var(--dola-secondary));
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.search-tab.active::before,
.search-tab:hover::before {
  width: 100%;
}

/* Enhanced View All Button */
.view-all-btn {
  position: relative;
  overflow: hidden;
}

.view-all-btn::before {
  content: \'\';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.view-all-btn:hover::before {
  left: 100%;
}

.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(205, 155, 50, 0.3);
}

/* Enhanced No Results */
.no-results {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.no-results i {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* Enhanced Search Count */
.search-count {
  position: relative;
  padding-left: 20px;
}

.search-count::before {
  content: \'🔍\';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
}

/* Responsive Enhancements */
@media (max-width: 768px) {
  .search-container {
    animation: slideDownBounce 0.3s ease-out;
  }

  .product-card:hover {
    transform: translateY(-4px) scale(1.01);
  }

  .news-card {
    margin-bottom: 20px;
  }
}

@media (max-width: 576px) {
  .search-overlay {
    padding-top: 20px;
  }

  .search-container {
    border-radius: 10px;
    margin: 0 10px;
  }

  .product-suggestion-item,
  .news-suggestion-item {
    flex-direction: column;
    text-align: center;
  }

  .product-image,
  .news-image {
    width: 100%;
    height: 120px;
    margin-right: 0;
    margin-bottom: 10px;
  }
}

/* Dark Mode Support (Optional) */
@media (prefers-color-scheme: dark) {
  :root {
    --dola-background: #1a1a1a;
    --dola-background-light: #2a2a2a;
    --dola-text-dark: #fff;
    --dola-text-light: #ccc;
    --dola-text-muted: #999;
    --dola-border: #444;
  }

  .search-container {
    background: var(--dola-background);
    color: var(--dola-text-dark);
  }

  .search-input {
    background: var(--dola-background-light);
    color: var(--dola-text-dark);
    border-color: var(--dola-border);
  }

  .product-card,
  .news-card {
    background: var(--dola-background-light);
    color: var(--dola-text-dark);
  }
}

/* Accessibility Enhancements */
.search-btn:focus,
.search-input:focus,
.view-all-btn:focus,
.page-link:focus {
  outline: 2px solid var(--dola-primary);
  outline-offset: 2px;
}

/* Reduced Motion Support */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Print Styles */
@media print {
  .search-overlay,
  .search-container {
    display: none !important;
  }

  .search-results-page {
    background: white !important;
  }

  .product-card,
  .news-card {
    break-inside: avoid;
    box-shadow: none !important;
  }
}

/* High Contrast Mode */
@media (prefers-contrast: high) {
  .search-input {
    border-width: 3px;
  }

  .product-card,
  .news-card {
    border: 2px solid var(--dola-text-dark);
  }

  .view-all-btn {
    border: 2px solid var(--dola-primary);
  }
}

/* Custom Scrollbar for Search Results */
.search-results::-webkit-scrollbar {
  width: 8px;
}

.search-results::-webkit-scrollbar-track {
  background: var(--dola-background-light);
  border-radius: 4px;
}

.search-results::-webkit-scrollbar-thumb {
  background: var(--dola-primary);
  border-radius: 4px;
}

.search-results::-webkit-scrollbar-thumb:hover {
  background: var(--dola-primary-dark);
}

/* Enhanced Focus Indicators */
.search-tab:focus-visible {
  outline: 2px solid var(--dola-primary);
  outline-offset: 4px;
  border-radius: 4px;
}

.product-suggestion-item:focus-within,
.news-suggestion-item:focus-within {
  outline: 2px solid var(--dola-primary);
  outline-offset: 2px;
  border-radius: var(--dola-radius);
}

/* Loading State Enhancements */
.search-loading p {
  animation: fadeInOut 1.5s ease-in-out infinite;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

/* Success State Animation */
.search-results-content {
  animation: slideInFromBottom 0.5s ease-out;
}

@keyframes slideInFromBottom {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Micro-interactions */
.search-close-btn {
  transition: var(--dola-transition);
}

.search-close-btn:hover {
  transform: rotate(90deg) scale(1.1);
}

.search-icon {
  transition: var(--dola-transition);
}

.search-input:focus ~ .search-icon {
  animation: searchPulse 0.6s ease-out;
}

@keyframes searchPulse {
  0% { transform: translateY(-50%) scale(1); }
  50% { transform: translateY(-50%) scale(1.2); }
  100% { transform: translateY(-50%) scale(1.1); }
}

</style>

