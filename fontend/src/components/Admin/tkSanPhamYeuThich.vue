<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Sản Phẩm Yêu Thích</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-3">
        <select v-model="selectedCategoryId" class="form-select">
          <option value="">Tất cả danh mục</option>
          <option v-for="(cat, index) in categories" :key="index" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>
      <div class="col-md-3">
        <input type="month" class="form-control" v-model="selectedMonth" />
      </div>
      <div class="col-md-3">
        <select v-model="selectedTop" class="form-select">
          <option value="5">Top 5</option>
          <option value="10">Top 10</option>
          <option value="20">Top 20</option>
        </select>
      </div>
      <div class="col-md-3">
        <button class="btn btn-primary w-100" @click="locSanPham">Lọc</button>
      </div>
    </div>

    <!-- Biểu đồ cột -->
    <div class="mb-4" v-if="dsSanPham.length > 0" style="max-height: 400px;">
      <h5 class="fw-semibold mb-3">Biểu Đồ Lượt Yêu Thích</h5>
      <Bar :data="chartData" :options="chartOptions" />
    </div>

    <!-- Bảng dữ liệu -->
    <table class="table table-bordered text-center mt-5">
      <thead style="color: black; background-color: #f0f0f0;">
        <tr>
          <th>STT</th>
          <th>Tên sản phẩm</th>
          <th>Lượt yêu thích</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in dsSanPham" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ item.tenSanPham }}</td>
          <td>{{ item.luotYeuThich }}</td>
        </tr>
        <tr v-if="dsSanPham.length === 0">
          <td colspan="3">Không có dữ liệu phù hợp</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
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

// Đăng ký các thành phần của Chart.js
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// Dữ liệu thống kê
const dsSanPham = ref([])
const categories = ref([])

// Bộ lọc
const selectedCategoryId = ref('')
const selectedMonth = ref('')
const selectedTop = ref(5)

// Gọi API sản phẩm yêu thích
const fetchSanPhamYeuThich = async () => {
  try {
    const thang = selectedMonth.value
      ? new Date(selectedMonth.value).getMonth() + 1
      : null

    const params = { top: selectedTop.value }
    if (selectedCategoryId.value) params.id = selectedCategoryId.value
    if (thang) params.thang = thang

    console.log('📤 Gửi params đến API:', params)

    const res = await api.get('/api/thong-ke/yeu-thich', { params })

    console.log('📥 Dữ liệu trả về từ API:', res.data)

    dsSanPham.value = res.data.map(item => ({
      tenSanPham: item[0],
      luotYeuThich: item[1],
      id: item[2]
    }))
  } catch (err) {
    console.error('❌ Lỗi khi lấy dữ liệu sản phẩm yêu thích:', err)
  }
}

// Gọi API danh mục
const fetchCategories = async () => {
  try {
    const res = await api.get('/api/categories')
    categories.value = res.data
    console.log('📦 Danh mục trả về:', res.data)
  } catch (err) {
    console.error('❌ Lỗi khi lấy danh mục:', err)
  }
}

// Lọc dữ liệu
const locSanPham = () => {
  fetchSanPhamYeuThich()
}

// Tính trục Y tối đa
const suggestedMax = computed(() => {
  const maxValue = Math.max(...dsSanPham.value.map(sp => sp.luotYeuThich), 0)
  return maxValue < 5 ? 5 : maxValue + 1
})

// Dữ liệu cho biểu đồ
const chartData = computed(() => ({
  labels: dsSanPham.value.map(sp => sp.tenSanPham),
  datasets: [
    {
      label: 'Lượt yêu thích',
      data: dsSanPham.value.map(sp => sp.luotYeuThich),
      backgroundColor: '#F5623D',
      borderRadius: 6
    }
  ]
}))

// Tuỳ chọn biểu đồ
const chartOptions = computed(() => ({
  responsive: true,
  animation: {
    duration: 1500,
    easing: 'easeOutQuart'
  },
  plugins: {
    legend: { display: false },
    title: {
      display: true,
      text: 'Biểu đồ sản phẩm yêu thích theo lượt yêu thích'
    }
  },
  scales: {
    x: {
      ticks: { color: '#000' },
      grid: { display: false }
    },
    y: {
      beginAtZero: true,
      suggestedMax: suggestedMax.value,
      ticks: { stepSize: 1, color: '#000' },
      grid: { display: true }
    }
  }
}))

// Khi component được mount
onMounted(() => {
  fetchCategories()
  fetchSanPhamYeuThich()
})
</script>

