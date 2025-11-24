<template>
  <div class="p-4">
    <h4 class="fw-bold mb-4">Thống Kê Doanh Thu</h4>

    <!-- Biểu đồ cột -->
    <BarChart v-if="doanhThuNam.length > 0" :chart-data="chartData" />

    <!-- Bảng thống kê -->
    <table class="table table-bordered text-center mt-4">
      <thead style="color: black; background-color: #f0f0f0;">
        <tr>
          <th>Năm</th>
          <th>Số đơn hàng</th>
          <th>Doanh thu</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in doanhThuNam" :key="item.nam">
          <td>{{ item.nam }}</td>
          <td>{{ item.soDon }}</td>
          <td>{{ formatCurrency(item.doanhThu) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { defineComponent, h } from 'vue'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js'
import api from '@/axios' // ✅ Dùng axios đã cấu hình sẵn

// Đăng ký biểu đồ
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// Biểu đồ cột gồm 2 giá trị mỗi năm
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
          text: 'Doanh Thu Theo Năm (Biểu đồ cột)'
        }
      }
    }
    return () => h(Bar, { data: props.chartData, options: chartOptions })
  }
})

// Dữ liệu API
const doanhThuNam = ref([])

// Gọi API
const fetchDoanhThuTheoNam = async () => {
  try {
    const res = await api.get('/api/thong-ke/doanh-thu/nam') // ✅ Dùng api thay vì axios
    doanhThuNam.value = res.data.map(item => ({
      nam: item[0],
      doanhThu: item[1],
      soDon: item[2]
    }))
  } catch (err) {
    console.error('Lỗi khi lấy dữ liệu thống kê năm:', err)
  }
}

// Dữ liệu biểu đồ
const chartData = computed(() => {
  return {
    labels: doanhThuNam.value.map(item => item.nam.toString()),
    datasets: [
      {
        label: 'Số Lượng Đơn Hàng',
        backgroundColor: '#42A5F5',
        data: doanhThuNam.value.map(item => item.soDon)
      },
      {
        label: 'Tổng Tiền (VNĐ)',
        backgroundColor: '#66BB6A',
        data: doanhThuNam.value.map(item => item.doanhThu)
      }
    ]
  }
})

// Format VNĐ
const formatCurrency = (value) => {
  return value.toLocaleString('vi-VN') + ' đ'
}

onMounted(fetchDoanhThuTheoNam)
</script>

