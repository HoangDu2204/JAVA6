<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Đánh Giá Sản Phẩm</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-3">
        <select v-model="filterRating" class="form-select">
          <option value="0">Tất cả</option>
          <option value="4">≥ 4 sao</option>
          <option value="3">≥ 3 sao</option>
          <option value="2">≥ 2 sao</option>
        </select>
      </div>
      <div class="col-md-3">
        <input type="month" class="form-control" v-model="selectedMonth" />
      </div>
      <div class="col-md-3">
        <select v-model="topLimit" class="form-select">
          <option :value="5">Top 5</option>
          <option :value="10">Top 10</option>
          <option :value="20">Top 20</option>
        </select>
      </div>
      <div class="col-md-3">
        <button class="btn btn-primary w-100" @click="fetchDanhGia">Lọc</button>
      </div>
    </div>

    <!-- Biểu đồ cột -->
    <div class="mb-4">
      <h5 class="fw-semibold mb-3">Biểu Đồ Đánh Giá Trung Bình</h5>
      <Bar :data="chartData" :options="chartOptions" />
    </div>

    <!-- Bảng dữ liệu -->
    <table class="table table-bordered text-center">
      <thead style="color: black; background-color: #f0f0f0;">
        <tr>
          <th>STT</th>
          <th>Tên sản phẩm</th>
          <th>Số lượt đánh giá</th>
          <th>Đánh giá trung bình</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(sp, index) in danhGiaLoc" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ sp.tenSanPham }}</td>
          <td>{{ sp.soDanhGia }}</td>
          <td>
            <span v-for="(sao, i) in tinhSoSao(sp.danhGiaTB)" :key="i">{{ sao }}</span>
            <small class="text-muted ms-1">({{ sp.danhGiaTB.toFixed(1) }})</small>
          </td>
        </tr>
        <tr v-if="danhGiaLoc.length === 0">
          <td colspan="4">Không có dữ liệu phù hợp</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
import api from '@/axios' // ✅ dùng axios cấu hình sẵn

// Đăng ký thành phần ChartJS
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const danhGiaList = ref([])
const selectedMonth = ref('')
const filterRating = ref(0)
const topLimit = ref(10)

// Gọi API thống kê đánh giá
const fetchDanhGia = async () => {
  try {
    const thang = selectedMonth.value
      ? new Date(selectedMonth.value).getMonth() + 1
      : null

    const params = { top: topLimit.value }
    if (thang) params.thang = thang

    const res = await api.get('/api/thong-ke/danh-gia', { params }) // ✅ dùng api
    danhGiaList.value = res.data.map(item => ({
      tenSanPham: item[0],
      danhGiaTB: item[1],
      soDanhGia: item[2]
    }))
  } catch (err) {
    console.error('Lỗi khi lấy thống kê đánh giá sản phẩm:', err)
  }
}

// Lọc dữ liệu theo điểm đánh giá
const danhGiaLoc = computed(() => {
  return danhGiaList.value.filter(sp => sp.danhGiaTB >= filterRating.value)
})

// Dữ liệu biểu đồ
const chartData = computed(() => ({
  labels: danhGiaLoc.value.map(sp => sp.tenSanPham),
  datasets: [
    {
      label: 'Đánh giá trung bình',
      data: danhGiaLoc.value.map(sp => sp.danhGiaTB),
      backgroundColor: '#f9b233',
      borderRadius: 5,
    }
  ]
}))

// Cấu hình biểu đồ
const chartOptions = {
  responsive: true,
  scales: {
    y: {
      min: 0,
      max: 5,
      ticks: { stepSize: 1 },
      title: {
        display: true,
        text: 'Sao trung bình'
      }
    }
  },
  plugins: {
    legend: {
      display: false
    }
  }
}

// Hiển thị số sao bằng ký tự
const tinhSoSao = (diem) => {
  const saoDay = '⭐'
  const saoRong = '☆'
  const soSao = Math.round(diem)
  return Array.from({ length: 5 }, (_, i) => i < soSao ? saoDay : saoRong)
}

onMounted(fetchDanhGia)
</script>

