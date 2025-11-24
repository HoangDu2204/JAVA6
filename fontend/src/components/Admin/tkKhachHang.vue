<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Khách Hàng VIP</h4>

    <!-- Bộ lọc -->
    <div class="row mb-3 g-2">
      <div class="col-md-3">
        <input v-model="selectedMonth" type="month" class="form-control" />
      </div>
      <div class="col-md-3">
        <select v-model="topLimit" class="form-select">
          <option :value="5">Top 5</option>
          <option :value="10">Top 10</option>
          <option :value="20">Top 20</option>
        </select>
      </div>
      <div class="col-md-3">
        <button class="btn btn-primary w-100" @click="locKhachHangVip">Lọc</button>
      </div>
    </div>

    <!-- Biểu đồ cột -->
    <BarChart v-if="khachHangVip.length > 0" :chart-data="chartData" :key="chartDataKey" />

    <!-- Bảng thống kê -->
    <table class="table table-bordered text-center mt-4">
      <thead style="color: black; background-color: #f0f0f0;">
        <tr>
          <th>STT</th>
          <th>Tên khách hàng</th>
          <th>Email</th>
          <th>SĐT</th>
          <th>Số đơn hàng</th>
          <th>Tổng chi tiêu</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(kh, index) in khachHangVip" :key="kh.id">
          <td>{{ index + 1 }}</td>
          <td>{{ kh.fullName }}</td>
          <td>{{ kh.email }}</td>
          <td>{{ kh.phone }}</td>
          <td>{{ kh.soDon }}</td>
          <td>{{ formatCurrency(kh.tongChiTieu) }}</td>
        </tr>
        <tr v-if="khachHangVip.length === 0">
          <td colspan="6">Không có dữ liệu phù hợp</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, defineComponent, h } from 'vue'
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

// Biểu đồ cột có hiệu ứng
const BarChart = defineComponent({
  props: {
    chartData: Object
  },
  setup(props) {
    const chartOptions = {
      responsive: true,
      animation: {
        duration: 1500,
        easing: 'easeOutQuart'
      },
      plugins: {
        legend: { display: true, position: 'bottom' },
        title: { display: true, text: 'Top Khách Hàng VIP (Biểu đồ cột)' }
      },
      scales: {
        x: {
          barPercentage: 0.5,
          categoryPercentage: 0.5,
          ticks: { color: 'black' },
          grid: { display: false }
        },
        y: {
          ticks: { color: 'black' },
          grid: { display: true }
        }
      }
    }

    return () => h(Bar, {
      data: props.chartData,
      options: chartOptions,
      updateMode: 'active'
    })
  }
})

// Biến dữ liệu
const khachHangVip = ref([])
const selectedMonth = ref('')
const topLimit = ref(5)
const chartDataKey = ref(0)

// Gọi API
const fetchKhachHangVip = async () => {
  try {
    const thang = selectedMonth.value
      ? new Date(selectedMonth.value).getMonth() + 1
      : null

    const params = { top: topLimit.value }
    if (thang) params.thang = thang

    const res = await api.get('/api/thong-ke/vip', { params })

    khachHangVip.value = res.data.map(item => ({
      id: item[0],
      fullName: item[1],
      email: item[2],
      phone: item[3],
      soDon: item[4],
      tongChiTieu: item[5]
    }))

    chartDataKey.value++ // ép biểu đồ vẽ lại
  } catch (err) {
    console.error('❌ Lỗi khi lấy dữ liệu khách hàng VIP:', err)
  }
}

// Lọc dữ liệu
const locKhachHangVip = () => {
  fetchKhachHangVip()
}

// Format tiền
const formatCurrency = (value) => {
  return Number(value).toLocaleString('vi-VN') + ' đ'
}

// Dữ liệu biểu đồ
const chartData = computed(() => ({
  labels: khachHangVip.value.map(kh => kh.fullName),
  datasets: [
    {
      label: 'Số đơn hàng',
      backgroundColor: '#42A5F5',
      data: khachHangVip.value.map(kh => kh.soDon)
    },
    {
      label: 'Tổng chi tiêu (VNĐ)',
      backgroundColor: '#FFA726',
      data: khachHangVip.value.map(kh => kh.tongChiTieu)
    }
  ]
}))

// Gọi lần đầu
fetchKhachHangVip()
</script>

