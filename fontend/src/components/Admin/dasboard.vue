```vue
<template>
  <div class="dashboard-container">
    <!-- Header với thông báo trạng thái -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">
          <i class="bi bi-graph-up-arrow"></i>
          Dashboard Thống Kê
        </h1>
        <div class="dashboard-subtitle">
          Tổng quan về doanh thu, khách hàng và sản phẩm
        </div>
        <div class="status-indicator" :class="{ 'online': isOnline, 'offline': !isOnline }">
          <div class="status-dot"></div>
          <span>{{ isOnline ? 'Đang cập nhật' : 'Mất kết nối' }}</span>
          <small v-if="lastUpdated">Cập nhật lần cuối: {{ formatTime(lastUpdated) }}</small>
        </div>
      </div>

      <!-- Controls -->
      <div class="dashboard-controls">
        <div class="auto-refresh-toggle">
          <label class="switch">
            <input type="checkbox" v-model="autoRefresh" @change="toggleAutoRefresh">
            <span class="slider"></span>
          </label>
          <span>Tự động cập nhật</span>
        </div>

        <select v-model="refreshInterval" @change="updateRefreshInterval" class="interval-select">
          <option value="5000">5 giây</option>
          <option value="10000">10 giây</option>
          <option value="30000">30 giây</option>
          <option value="60000">1 phút</option>
          <option value="300000">5 phút</option>
        </select>

        <button @click="refreshAllData" class="refresh-btn" :disabled="isLoading">
          <i class="bi bi-arrow-clockwise" :class="{ 'spin': isLoading }"></i>
        </button>
      </div>
    </div>

    <!-- Loading overlay -->
    <div v-if="isLoading && !hasData" class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
    </div>

    <!-- Summary Cards với animation -->
    <div class="summary-cards" v-if="hasData">
      <div class="summary-card revenue" :class="{ 'updated': recentlyUpdated.revenue }">
        <div class="card-icon">
          <i class="bi bi-currency-dollar"></i>
        </div>
        <div class="card-content">
          <div class="card-value">{{ formatCurrency(totalRevenue) }}</div>
          <div class="card-label">Tổng Doanh Thu</div>
          <div class="card-trend" v-if="trends.revenue">
            <i :class="trends.revenue > 0 ? 'bi bi-arrow-up text-success' : 'bi bi-arrow-down text-danger'"></i>
            <span :class="trends.revenue > 0 ? 'text-success' : 'text-danger'">
              {{ Math.abs(trends.revenue).toFixed(1) }}%
            </span>
          </div>
        </div>
      </div>

      <div class="summary-card orders" :class="{ 'updated': recentlyUpdated.orders }">
        <div class="card-icon">
          <i class="bi bi-cart-check-fill"></i>
        </div>
        <div class="card-content">
          <div class="card-value">{{ totalOrders.toLocaleString() }}</div>
          <div class="card-label">Tổng Đơn Hàng</div>
          <div class="card-trend" v-if="trends.orders">
            <i :class="trends.orders > 0 ? 'bi bi-arrow-up text-success' : 'bi bi-arrow-down text-danger'"></i>
            <span :class="trends.orders > 0 ? 'text-success' : 'text-danger'">
              {{ Math.abs(trends.orders).toFixed(1) }}%
            </span>
          </div>
        </div>
      </div>

      <div class="summary-card customers" :class="{ 'updated': recentlyUpdated.customers }">
        <div class="card-icon">
          <i class="bi bi-people-fill"></i>
        </div>
        <div class="card-content">
          <div class="card-value">{{ totalCustomers.toLocaleString() }}</div>
          <div class="card-label">Khách Hàng VIP</div>
          <div class="card-trend" v-if="trends.customers">
            <i :class="trends.customers > 0 ? 'bi bi-arrow-up text-success' : 'bi bi-arrow-down text-danger'"></i>
            <span :class="trends.customers > 0 ? 'text-success' : 'text-danger'">
              {{ Math.abs(trends.customers).toFixed(1) }}%
            </span>
          </div>
        </div>
      </div>

      <div class="summary-card rating" :class="{ 'updated': recentlyUpdated.rating }">
        <div class="card-icon">
          <i class="bi bi-star-fill"></i>
        </div>
        <div class="card-content">
          <div class="card-value">{{ averageRating.toFixed(1) }}</div>
          <div class="card-label">Đánh Giá TB</div>
          <div class="rating-stars">
            <i v-for="n in 5" :key="n"
               :class="n <= Math.round(averageRating) ? 'bi bi-star-fill' : 'bi bi-star'"
               class="star"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Grid với lazy loading -->
    <div class="charts-grid" v-if="hasData">
      <!-- Doanh thu theo thời gian -->
      <div class="chart-container" :class="{ 'loading': chartLoading.revenue }">
        <div class="chart-header">
          <h3>
            <i class="bi bi-graph-up"></i>
            Doanh Thu Theo Thời Gian
          </h3>
          <div class="chart-controls">
            <select v-model="revenueTimeframe" @change="updateRevenueChart" class="form-select">
              <option value="day">Theo Ngày</option>
              <option value="month">Theo Tháng</option>
              <option value="year">Theo Năm</option>
            </select>
          </div>
        </div>
        <div class="chart-content">
          <div v-if="chartLoading.revenue" class="chart-loading">
            <div class="chart-skeleton"></div>
          </div>
          <Bar v-else-if="revenueChartData.labels.length > 0"
               :data="revenueChartData"
               :options="revenueChartOptions" />
          <div v-else class="no-data">
            <i class="bi bi-exclamation-circle"></i>
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>

      <!-- Đánh giá sản phẩm -->
      <div class="chart-container" :class="{ 'loading': chartLoading.rating }">
        <div class="chart-header">
          <h3>
            <i class="bi bi-star"></i>
            Top Sản Phẩm Được Đánh Giá Cao
          </h3>
          <div class="chart-controls">
            <select v-model="ratingTopLimit" @change="updateRatingChart" class="form-select">
              <option :value="5">Top 5</option>
              <option :value="10">Top 10</option>
              <option :value="15">Top 15</option>
            </select>
          </div>
        </div>
        <div class="chart-content">
          <div v-if="chartLoading.rating" class="chart-loading">
            <div class="chart-skeleton"></div>
          </div>
          <Bar v-else-if="ratingChartData.labels.length > 0"
               :data="ratingChartData"
               :options="ratingChartOptions" />
          <div v-else class="no-data">
            <i class="bi bi-exclamation-circle"></i>
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>

      <!-- Khách hàng VIP -->
      <div class="chart-container" :class="{ 'loading': chartLoading.vip }">
        <div class="chart-header">
          <h3>
            <i class="bi bi-gem"></i>
            Top Khách Hàng VIP
          </h3>
          <div class="chart-controls">
            <select v-model="vipTopLimit" @change="updateVipChart" class="form-select">
              <option :value="5">Top 5</option>
              <option :value="10">Top 10</option>
              <option :value="15">Top 15</option>
            </select>
          </div>
        </div>
        <div class="chart-content">
          <div v-if="chartLoading.vip" class="chart-loading">
            <div class="chart-skeleton"></div>
          </div>
          <Bar v-else-if="vipChartData.labels.length > 0"
               :data="vipChartData"
               :options="vipChartOptions" />
          <div v-else class="no-data">
            <i class="bi bi-exclamation-circle"></i>
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>

      <!-- Sản phẩm yêu thích -->
      <div class="chart-container" :class="{ 'loading': chartLoading.favorite }">
        <div class="chart-header">
          <h3>
            <i class="bi bi-heart-fill"></i>
            Top Sản Phẩm Yêu Thích
          </h3>
          <div class="chart-controls">
            <select v-model="favoriteTopLimit" @change="updateFavoriteChart" class="form-select">
              <option :value="5">Top 5</option>
              <option :value="10">Top 10</option>
              <option :value="15">Top 15</option>
            </select>
          </div>
        </div>
        <div class="chart-content">
          <div v-if="chartLoading.favorite" class="chart-loading">
            <div class="chart-skeleton"></div>
          </div>
          <Bar v-else-if="favoriteChartData.labels.length > 0"
               :data="favoriteChartData"
               :options="favoriteChartOptions" />
          <div v-else class="no-data">
            <i class="bi bi-exclamation-circle"></i>
            <p>Không có dữ liệu</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Error notification -->
    <div v-if="error" class="error-notification" @click="dismissError">
      <i class="bi bi-exclamation-triangle-fill"></i>
      <span>{{ error }}</span>
      <button class="close-btn">
        <i class="bi bi-x"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js'
import api from '@/axios'

// Đăng ký ChartJS
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// Reactive data
const isLoading = ref(false)
const hasData = ref(false)
const isOnline = ref(true)
const lastUpdated = ref(null)
const error = ref(null)
const autoRefresh = ref(true)
const refreshInterval = ref(30000) // 30 giây mặc định
let refreshTimer = null

// Chart loading states
const chartLoading = ref({
  revenue: false,
  rating: false,
  vip: false,
  favorite: false
})

// Controls
const revenueTimeframe = ref('month')
const ratingTopLimit = ref(5)
const vipTopLimit = ref(5)
const favoriteTopLimit = ref(5)

// Summary data với previous values để tính trend
const totalRevenue = ref(0)
const totalOrders = ref(0)
const totalCustomers = ref(0)
const averageRating = ref(0)

const previousValues = ref({
  revenue: 0,
  orders: 0,
  customers: 0,
  rating: 0
})

// Recently updated indicators
const recentlyUpdated = ref({
  revenue: false,
  orders: false,
  customers: false,
  rating: false
})

// Trends calculation
const trends = computed(() => ({
  revenue: calculateTrend(totalRevenue.value, previousValues.value.revenue),
  orders: calculateTrend(totalOrders.value, previousValues.value.orders),
  customers: calculateTrend(totalCustomers.value, previousValues.value.customers),
  rating: calculateTrend(averageRating.value, previousValues.value.rating)
}))

// Chart data
const revenueChartData = ref({ labels: [], datasets: [] })
const ratingChartData = ref({ labels: [], datasets: [] })
const vipChartData = ref({ labels: [], datasets: [] })
const favoriteChartData = ref({ labels: [], datasets: [] })

// Enhanced chart options với animations
const baseChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 1000,
    easing: 'easeInOutQuart'
  },
  plugins: {
    legend: {
      display: true,
      position: 'bottom',
      labels: {
        usePointStyle: true,
        padding: 20,
        font: {
          size: 12,
          family: "'Inter', sans-serif"
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: '#fff',
      bodyColor: '#fff',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      cornerRadius: 8,
      displayColors: true,
      titleFont: {
        size: 14,
        weight: 'bold'
      },
      bodyFont: {
        size: 13
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        font: {
          size: 11,
          family: "'Inter', sans-serif"
        }
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
      },
      ticks: {
        font: {
          size: 11,
          family: "'Inter', sans-serif"
        }
      }
    }
  }
}

const revenueChartOptions = {
  ...baseChartOptions,
  scales: {
    ...baseChartOptions.scales,
    y: {
      ...baseChartOptions.scales.y,
      ticks: {
        ...baseChartOptions.scales.y.ticks,
        callback: function(value) {
          return value.toLocaleString('vi-VN') + ' đ'
        }
      }
    }
  }
}

const ratingChartOptions = {
  ...baseChartOptions,
  plugins: {
    ...baseChartOptions.plugins,
    legend: { display: false }
  },
  scales: {
    ...baseChartOptions.scales,
    y: {
      ...baseChartOptions.scales.y,
      min: 0,
      max: 5,
      ticks: {
        ...baseChartOptions.scales.y.ticks,
        stepSize: 1
      }
    }
  }
}

const vipChartOptions = baseChartOptions

const favoriteChartOptions = {
  ...baseChartOptions,
  plugins: {
    ...baseChartOptions.plugins,
    legend: { display: false }
  }
}

// Utility functions
const calculateTrend = (current, previous) => {
  if (previous === 0) return 0
  return ((current - previous) / previous) * 100
}

const formatCurrency = (value) => {
  return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}

const formatTime = (date) => {
  return new Intl.DateTimeFormat('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(date)
}

const showUpdateIndicator = (type) => {
  recentlyUpdated.value[type] = true
  setTimeout(() => {
    recentlyUpdated.value[type] = false
  }, 2000)
}

const dismissError = () => {
  error.value = null
}

// API calls với error handling và loading states
const fetchRevenueData = async () => {
  chartLoading.value.revenue = true
  try {
    const endpoint = revenueTimeframe.value === 'year' ? '/api/thong-ke/doanh-thu/nam' :
                    revenueTimeframe.value === 'day' ? '/api/thong-ke/doanh-thu/ngay' :
                    '/api/thong-ke/doanh-thu/thang'

    const res = await api.get(endpoint)

    // Store previous value for trend calculation
    previousValues.value.revenue = totalRevenue.value
    previousValues.value.orders = totalOrders.value

    if (revenueTimeframe.value === 'year') {
      const data = res.data.map(item => ({
        period: item[0].toString(),
        revenue: item[1],
        orders: item[2]
      }))

      revenueChartData.value = {
        labels: data.map(d => d.period),
        datasets: [
          {
            label: 'Doanh thu (VNĐ)',
            data: data.map(d => d.revenue),
            backgroundColor: 'rgba(99, 102, 241, 0.8)',
            borderColor: 'rgb(99, 102, 241)',
            borderWidth: 2,
            borderRadius: 6,
            borderSkipped: false,
          },
          {
            label: 'Số đơn hàng',
            data: data.map(d => d.orders),
            backgroundColor: 'rgba(34, 197, 94, 0.8)',
            borderColor: 'rgb(34, 197, 94)',
            borderWidth: 2,
            borderRadius: 6,
            borderSkipped: false,
          }
        ]
      }

      totalRevenue.value = data.reduce((sum, d) => sum + d.revenue, 0)
      totalOrders.value = data.reduce((sum, d) => sum + d.orders, 0)
    } else {
      const data = res.data.map(item => ({
        period: revenueTimeframe.value === 'day' ?
          new Date(item[0]).toLocaleDateString('vi-VN') :
          `${item[1]}/${item[0]}`,
        revenue: item[2] || item[1],
        orders: item[3] || item[2] || 0
      }))

      revenueChartData.value = {
        labels: data.map(d => d.period),
        datasets: [
          {
            label: 'Doanh thu (VNĐ)',
            data: data.map(d => d.revenue),
            backgroundColor: 'rgba(99, 102, 241, 0.8)',
            borderColor: 'rgb(99, 102, 241)',
            borderWidth: 2,
            borderRadius: 6,
            borderSkipped: false,
          },
          {
            label: 'Số đơn hàng',
            data: data.map(d => d.orders),
            backgroundColor: 'rgba(34, 197, 94, 0.8)',
            borderColor: 'rgb(34, 197, 94)',
            borderWidth: 2,
            borderRadius: 6,
            borderSkipped: false,
          }
        ]
      }

      totalRevenue.value = data.reduce((sum, d) => sum + d.revenue, 0)
      totalOrders.value = data.reduce((sum, d) => sum + d.orders, 0)
    }

    showUpdateIndicator('revenue')
    showUpdateIndicator('orders')
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu doanh thu:', err)
    error.value = 'Không thể tải dữ liệu doanh thu'
    isOnline.value = false
  } finally {
    chartLoading.value.revenue = false
  }
}

const fetchRatingData = async () => {
  chartLoading.value.rating = true
  try {
    const res = await api.get('/api/thong-ke/danh-gia', {
      params: { top: ratingTopLimit.value }
    })

    previousValues.value.rating = averageRating.value

    const data = res.data.map(item => ({
      name: item[0],
      rating: item[1],
      count: item[2]
    }))

    ratingChartData.value = {
      labels: data.map(d => d.name),
      datasets: [{
        label: 'Đánh giá trung bình',
        data: data.map(d => d.rating),
        backgroundColor: 'rgba(251, 146, 60, 0.8)',
        borderColor: 'rgb(251, 146, 60)',
        borderWidth: 2,
        borderRadius: 6,
        borderSkipped: false,
      }]
    }

    if (data.length > 0) {
      averageRating.value = data.reduce((sum, d) => sum + d.rating, 0) / data.length
    }

    showUpdateIndicator('rating')
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu đánh giá:', err)
    error.value = 'Không thể tải dữ liệu đánh giá'
  } finally {
    chartLoading.value.rating = false
  }
}

const fetchVipData = async () => {
  chartLoading.value.vip = true
  try {
    const res = await api.get('/api/thong-ke/vip', {
      params: { top: vipTopLimit.value }
    })

    previousValues.value.customers = totalCustomers.value

    const data = res.data.map(item => ({
      name: item[1],
      orders: item[4],
      spending: item[5]
    }))

    vipChartData.value = {
      labels: data.map(d => d.name),
      datasets: [
        {
          label: 'Số đơn hàng',
          data: data.map(d => d.orders),
          backgroundColor: 'rgba(168, 85, 247, 0.8)',
          borderColor: 'rgb(168, 85, 247)',
          borderWidth: 2,
          borderRadius: 6,
          borderSkipped: false,
        },
        {
          label: 'Tổng chi tiêu (VNĐ)',
          data: data.map(d => d.spending),
          backgroundColor: 'rgba(236, 72, 153, 0.8)',
          borderColor: 'rgb(236, 72, 153)',
          borderWidth: 2,
          borderRadius: 6,
          borderSkipped: false,
        }
      ]
    }

    totalCustomers.value = data.length
    showUpdateIndicator('customers')
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu khách hàng VIP:', err)
    error.value = 'Không thể tải dữ liệu khách hàng VIP'
  } finally {
    chartLoading.value.vip = false
  }
}

const fetchFavoriteData = async () => {
  chartLoading.value.favorite = true
  try {
    const res = await api.get('/api/thong-ke/yeu-thich', {
      params: { top: favoriteTopLimit.value }
    })

    const data = res.data.map(item => ({
      name: item[0],
      count: item[1]
    }))

    favoriteChartData.value = {
      labels: data.map(d => d.name),
      datasets: [{
        label: 'Số lượt yêu thích',
        data: data.map(d => d.count),
        backgroundColor: 'rgba(20, 184, 166, 0.8)',
        borderColor: 'rgb(20, 184, 166)',
        borderWidth: 2,
        borderRadius: 6,
        borderSkipped: false,
      }]
    }
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu sản phẩm yêu thích:', err)
    error.value = 'Không thể tải dữ liệu sản phẩm yêu thích'
  } finally {
    chartLoading.value.favorite = false
  }
}

// Update functions
const updateRevenueChart = () => {
  fetchRevenueData()
}

const updateRatingChart = () => {
  fetchRatingData()
}

const updateVipChart = () => {
  fetchVipData()
}

const updateFavoriteChart = () => {
  fetchFavoriteData()
}

const refreshAllData = async () => {
  if (isLoading.value) return

  isLoading.value = true
  error.value = null

  try {
    await Promise.all([
      fetchRevenueData(),
      fetchRatingData(),
      fetchVipData(),
      fetchFavoriteData()
    ])

    hasData.value = true
    lastUpdated.value = new Date()
    isOnline.value = true
  } catch (err) {
    console.error('Lỗi khi làm mới dữ liệu:', err)
    error.value = 'Không thể làm mới dữ liệu'
    isOnline.value = false
  } finally {
    isLoading.value = false
  }
}

// Auto refresh functionality
const toggleAutoRefresh = () => {
  if (autoRefresh.value) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

const updateRefreshInterval = () => {
  if (autoRefresh.value) {
    stopAutoRefresh()
    startAutoRefresh()
  }
}

const startAutoRefresh = () => {
  stopAutoRefresh()
  refreshTimer = setInterval(() => {
    refreshAllData()
  }, refreshInterval.value)
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// Lifecycle hooks
onMounted(() => {
  refreshAllData()
  if (autoRefresh.value) {
    startAutoRefresh()
  }

  // Listen for online/offline events
  window.addEventListener('online', () => {
    isOnline.value = true
    if (autoRefresh.value) {
      refreshAllData()
    }
  })

  window.addEventListener('offline', () => {
    isOnline.value = false
  })
})

onUnmounted(() => {
  stopAutoRefresh()
  window.removeEventListener('online', () => {})
  window.removeEventListener('offline', () => {})
})

// Watch for visibility change to pause/resume auto refresh
document.addEventListener('visibilitychange', () => {
  if (document.hidden) {
    stopAutoRefresh()
  } else if (autoRefresh.value) {
    startAutoRefresh()
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

* {
  font-family: 'Inter', sans-serif;
}

.dashboard-container {
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  color: white;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-content {
  flex: 1;
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.dashboard-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 1rem;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-indicator.online .status-dot {
  background-color: #10b981;
}

.status-indicator.offline .status-dot {
  background-color: #ef4444;
}

.dashboard-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.auto-refresh-toggle {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.3);
  transition: 0.3s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #10b981;
}

.interval-select {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  padding: 0.5rem;
  color: white;
  font-size: 0.9rem;
  min-width: 100px;
}

.interval-select option {
  background: #1f2937;
  color: white;
}

.refresh-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  padding: 0.5rem;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner {
  text-align: center;
  color: white;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.summary-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.summary-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.summary-card.updated::before {
  transform: scaleX(1);
}

.summary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  position: relative;
}

.revenue .card-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}
.orders .card-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}
.customers .card-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}
.rating .card-icon {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.25rem;
  line-height: 1;
}

.card-label {
  font-size: 0.9rem;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  font-weight: 600;
}

.rating-stars {
  display: flex;
  gap: 0.25rem;
  margin-top: 0.5rem;
}

.star {
  color: #fbbf24;
  font-size: 0.8rem;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.chart-container {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
}

.chart-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.chart-container.loading {
  opacity: 0.7;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f3f4f6;
}

.chart-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chart-controls .form-select {
  min-width: 120px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 0.5rem;
  font-size: 0.9rem;
  font-weight: 500;
  transition: border-color 0.2s ease;
}

.chart-controls .form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.chart-content {
  height: 350px;
  position: relative;
}

.chart-loading {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-skeleton {
  width: 100%;
  height: 200px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 8px;
}

.no-data {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  gap: 1rem;
}

.no-data i {
  font-size: 3rem;
  opacity: 0.5;
}

.error-notification {
  position: fixed;
  top: 2rem;
  right: 2rem;
  background: #fee2e2;
  border: 1px solid #fecaca;
  border-left: 4px solid #ef4444;
  color: #991b1b;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  max-width: 400px;
  z-index: 1000;
  cursor: pointer;
  transition: all 0.3s ease;
}

.error-notification:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.close-btn {
  background: none;
  border: none;
  color: #991b1b;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.close-btn:hover {
  background: rgba(153, 27, 27, 0.1);
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@media (max-width: 1024px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 1rem;
  }

  .dashboard-header {
    flex-direction: column;
    align-items: stretch;
  }

  .dashboard-title {
    font-size: 2rem;
  }

  .dashboard-controls {
    justify-content: center;
  }

  .summary-cards {
    grid-template-columns: 1fr;
  }

  .chart-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .chart-content {
    height: 300px;
  }

  .error-notification {
    top: 1rem;
    right: 1rem;
    left: 1rem;
    max-width: none;
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 0.5rem;
  }

  .summary-card {
    padding: 1rem;
  }

  .card-icon {
    width: 50px;
    height: 50px;
    font-size: 1.4rem;
  }

  .card-value {
    font-size: 1.5rem;
  }

  .chart-container {
    padding: 1rem;
  }

  .chart-content {
    height: 250px;
  }
}
</style>

