<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Doanh Thu</h4>

    <div class="mb-5">
      <h5 class="fw-semibold mb-3">Theo Tháng</h5>

      <!-- Bộ lọc tháng / năm -->
      <div class="row g-2 mb-3">
        <div class="col-md-3">
          <select v-model="selectedMonth" class="form-select">
            <option value="">-- Chọn Tháng --</option>
            <option v-for="m in 12" :key="m" :value="m">Tháng {{ m }}</option>
          </select>
        </div>
        <div class="col-md-3">
          <select v-model="selectedYear" class="form-select">
            <option value="">-- Chọn Năm --</option>
            <option v-for="year in uniqueYears" :key="year" :value="year">{{ year }}</option>
          </select>
        </div>
        <div class="col-md-3">
          <button class="btn btn-primary w-100" @click="locDoanhThu">Lọc</button>
        </div>
      </div>

      <!-- Biểu đồ cột -->
      <BarChart v-if="filteredData.length > 0" :chart-data="chartData" />

      <!-- Bảng dữ liệu -->
      <table class="table table-bordered text-center mt-4">
        <thead style="color: black; background-color: #f0f0f0;">
          <tr>
            <th>Tháng</th>
            <th>Số đơn hàng</th>
            <th>Doanh thu</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredData" :key="item.nam + '-' + item.thang">
            <td>{{ item.thang }}/{{ item.nam }}</td>
            <td>{{ item.soDon }}</td>
            <td>{{ dinhDangTien(item.doanhThu) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineComponent, h } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import api from '@/axios'

// Đăng ký biểu đồ
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// Biểu đồ cột
const BarChart = defineComponent({
  props: {
    chartData: Object
  },
  setup(props) {
    const chartOptions = {
      responsive: true,
      plugins: {
        legend: { display: true, position: 'bottom' },
        title: {
          display: true,
          text: 'Doanh Thu Theo Tháng (Biểu đồ cột)'
        }
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
    return () => h(Bar, { data: props.chartData, options: chartOptions })
  }
})

// Dữ liệu API
const doanhThuThang = ref([])
const selectedMonth = ref('')
const selectedYear = ref('')
const filteredData = ref([])

// Gọi API
const fetchDoanhThuTheoThang = async () => {
  try {
    const res = await api.get('/api/thong-ke/doanh-thu/thang')
    doanhThuThang.value = res.data.map(item => ({
      nam: item[0],
      thang: item[1],
      doanhThu: item[2],
      soDon: item[3] || 0
    }))
    filteredData.value = doanhThuThang.value
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu thống kê tháng:', err)
  }
}

// Lọc dữ liệu khi bấm nút
const locDoanhThu = () => {
  filteredData.value = doanhThuThang.value.filter(item => {
    const matchMonth = selectedMonth.value ? item.thang == selectedMonth.value : true
    const matchYear = selectedYear.value ? item.nam == selectedYear.value : true
    return matchMonth && matchYear
  })
}

// Năm duy nhất
const uniqueYears = computed(() => {
  return [...new Set(doanhThuThang.value.map(item => item.nam))].sort((a, b) => b - a)
})

// Dữ liệu biểu đồ
const chartData = computed(() => {
  return {
    labels: filteredData.value.map(item => `Tháng ${item.thang}/${item.nam}`),
    datasets: [
      {
        label: 'Số đơn hàng',
        backgroundColor: '#42A5F5',
        data: filteredData.value.map(item => item.soDon)
      },
      {
        label: 'Tổng tiền (VNĐ)',
        backgroundColor: '#66BB6A',
        data: filteredData.value.map(item => item.doanhThu)
      }
    ]
  }
})

// Định dạng VNĐ
const dinhDangTien = (so) => {
  return so.toLocaleString('vi-VN') + ' đ'
}

onMounted(fetchDoanhThuTheoThang)
</script>

