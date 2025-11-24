<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Doanh Thu</h4>

    <div class="mb-5">
      <h5 class="fw-semibold mb-3">Theo Ngày</h5>

      <div class="row g-2 mb-3">
        <div class="col-md-3">
          <input type="date" class="form-control" v-model="tuNgay">
        </div>
        <div class="col-md-3">
          <input type="date" class="form-control" v-model="denNgay">
        </div>
        <div class="col-md-3">
          <select class="form-select" v-model="hinhThuc">
            <option value="">Tất cả hình thức</option>
            <option value="online">Bán Online</option>
            <option value="offline">Bán Tại Quầy</option>
          </select>
        </div>
        <div class="col-md-3">
          <button class="btn btn-primary w-100" @click="locDoanhThu">Lọc</button>
        </div>
      </div>

      <!-- Biểu đồ cột -->
      <BarChart v-if="filteredData.length > 0" :chart-data="chartData" />

      <!-- Bảng -->
      <table class="table table-bordered text-center mt-4">
        <thead style="color: black; background-color: #f0f0f0;">
          <tr>
            <th>Ngày</th>
            <th>Số đơn hàng</th>
            <th>Doanh thu</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in filteredData" :key="index">
            <td>{{ dinhDangNgay(item.ngay) }}</td>
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
  LinearScale,
} from 'chart.js'
import api from '@/axios'

// Đăng ký Chart.js
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
        legend: {
          display: true,
          position: 'bottom'
        },
        title: {
          display: true,
          text: 'Doanh Thu Theo Ngày (Biểu đồ cột)'
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

// Dữ liệu
const doanhThuNgay = ref([])
const filteredData = ref([])
const tuNgay = ref('')
const denNgay = ref('')
const hinhThuc = ref('')

// Gọi API
const fetchDoanhThuNgay = async () => {
  try {
    const res = await api.get('/api/thong-ke/doanh-thu/ngay')
    doanhThuNgay.value = res.data.map(item => ({
      ngay: item[0],
      doanhThu: item[1],
      soDon: item[2],
      hinhThuc: item[3] // nếu có lọc theo hình thức
    }))
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu doanh thu theo ngày:', err)
  }
}

// Hàm lọc theo ngày & hình thức
const locDoanhThu = () => {
  const from = tuNgay.value ? new Date(tuNgay.value) : null
  const to = denNgay.value ? new Date(denNgay.value) : null

  filteredData.value = doanhThuNgay.value.filter(item => {
    const date = new Date(item.ngay)
    const matchFrom = from ? date >= from : true
    const matchTo = to ? date <= to : true
    const matchType = hinhThuc.value ? item.hinhThuc === hinhThuc.value : true
    return matchFrom && matchTo && matchType
  })
}

// Dữ liệu biểu đồ
const chartData = computed(() => {
  return {
    labels: filteredData.value.map(item => dinhDangNgay(item.ngay)),
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

// Format tiền
const dinhDangTien = (so) => {
  return Number(so).toLocaleString('vi-VN') + ' đ'
}

// Format ngày
const dinhDangNgay = (ngayStr) => {
  const date = new Date(ngayStr)
  return date.toLocaleDateString('vi-VN')
}

// Tải dữ liệu ban đầu
onMounted(async () => {
  await fetchDoanhThuNgay()
  filteredData.value = doanhThuNgay.value
})
</script>

