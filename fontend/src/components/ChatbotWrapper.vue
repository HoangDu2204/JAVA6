<template>
  <ChatbotWidget v-if="shouldShowChatbot" />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ChatbotWidget from './ChatbotWidget.vue'

const route = useRoute()

// Danh sách các trang user được phép hiển thị chatbot
const allowedUserPages = [
  '/', // trang chủ
  '/gioiThieu', // giới thiệu
  '/lienHe', // liên hệ
  '/heThongCuaHang', // hệ thống cửa hàng
  '/sanPham', // sản phẩm
  '/tinTuc', // tin tức
  '/trangYeuThich', // sản phẩm yêu thích
  '/product', // chi tiết sản phẩm (bắt đầu với /product)
  '/binhLuanTinTuc', // bình luận tin tức (bắt đầu với /binhLuanTinTuc)
  '/cauHoiThuongGap', // câu hỏi thường gặp
  '/gioHang', // giỏ hàng
  '/thanhToan', // thanh toán
  '/thanhToanDaiLy', // thanh toán đại lý
  '/daThanhToan', // đã thanh toán
  '/donHang', // đơn hàng
  '/thongTinCaNhan', // thông tin cá nhân
  '/doiMatKhau', // đổi mật khẩu
  '/soDiaChi', // sổ địa chỉ
  '/dangKyDaiLy', // đăng ký đại lý
  '/updateAgent', // cập nhật đại lý
  '/danhGiaSP', // đánh giá sản phẩm
  '/vnpay-return', // vnpay return
  '/forgot-password', // quên mật khẩu
  '/forgot-password/verify', // xác thực quên mật khẩu
  '/timKiem', // tìm kiếm
  '/dangKy', // đăng ký
  '/dangNhap' // đăng nhập
]

// Kiểm tra xem có nên hiển thị chatbot hay không
const shouldShowChatbot = computed(() => {
  const currentPath = route.path
  
  // Kiểm tra xem đường dẫn hiện tại có trong danh sách được phép không
  return allowedUserPages.some(allowedPath => {
    // Nếu đường dẫn được phép có tham số (như /product/:id), kiểm tra xem có bắt đầu với path đó không
    if (allowedPath.includes(':')) {
      const basePath = allowedPath.split('/:')[0]
      return currentPath.startsWith(basePath)
    }
    // Nếu không có tham số, so sánh chính xác
    return currentPath === allowedPath || currentPath.startsWith(allowedPath + '/')
  })
})
</script> 