import { createRouter, createWebHistory } from 'vue-router'
//User
import trangChu from '../views/User/trangChu.vue'
import binhLuanTinTuc from '../views/User/binhLuanTinTuc.vue'
import cauHoiThuongGap from '../views/User/cauHoiThuongGap.vue'
import chiTietSP from '../views/User/chiTietSP.vue'
import dangKy from '../views/User/dangKy.vue'
import dangNhap from '../views/User/dangNhap.vue'
import daThanhToan from '../views/User/daThanhToan.vue'
import donHang from '../views/User/donHang.vue'
import gioHang from '../views/User/gioHang.vue'
import gioiThieu from '../views/User/gioiThieu.vue'
import heThongCuaHang from '../views/User/heThongCuaHang.vue'
import lienHe from '../views/User/lienHe.vue'
import sanPham from '../views/User/sanPham.vue'
import thanhToan from '../views/User/thanhToan.vue'
import thanhToanDaiLy from '../views/User/thanhToanDaiLy.vue'
import thongTinCaNhan from '../views/User/thongTinCaNhan.vue'
import tinTuc from '../views/User/tinTuc.vue'
import trangYeuThich from '../views/User/trangYeuThich.vue'
import doiMatKhau from '../views/User/doiMatKhau.vue'
import searchResult from '@/views/User/searchResult.vue'
// import donHangUser from '../views/User/donHangUser.vue'
import soDiaChi from '../views/User/soDiaChi.vue'
import dangKyDaiLy from '../views/User/dangKyDaiLy.vue'
import updateDaiLy from '../views/User/updateDaiLy.vue'
import danhGiaSP from '../views/User/danhGiaSP.vue'
import vnpayreturn from '../views/User/vnpay-return.vue'

import ForgotPasswordSend from '../views/User/ForgotPasswordSend.vue'
import ForgotPasswordVerify from '../views/User/ForgotPasswordVerify.vue'
//Admin
import chiTietDonHang from '../views/Admin/chiTietDonHang.vue'
import danhMucForm from '../views/Admin/danhMucForm.vue'
import giamGiaForm from '../views/Admin/giamGiaForm.vue'
import nguoiDungForm from '../views/Admin/nguoiDungForm.vue'
import sanPhamForm from '../views/Admin/sanPhamForm.vue'
import tinTucForm from '../views/Admin/tinTucForm.vue'
import vouchersForm from '../views/Admin/vouchersForm.vue'
import quanLyBinhLuanSP from '../views/Admin/quanLyBinhLuanSP.vue'
import quanLyBinhLuanTT from '../views/Admin/quanLyBinhLuanTT.vue'
import quanLyDanhGia from '../views/Admin/quanLyDanhGia.vue'
import quanLyDanhMuc from '../views/Admin/quanLyDanhMuc.vue'
import quanLyDonHang from '../views/Admin/quanLyDonHang.vue'
import quanLyGiamGia from '../views/Admin/quanLyGiamGia.vue'
import quanLyNguoiDung from '../views/Admin/quanLyNguoiDung.vue'
import quanLySanPham from '../views/Admin/quanLySanPham.vue'
import quanLyTinTuc from '../views/Admin/quanLyTinTuc.vue'
import quanLyVouchers from '../views/Admin/quanLyVouchers.vue'
import dasboard from '../views/Admin/dasboard.vue'
import quanLyBienThe from '../views/Admin/quanLyBienThe.vue'
import bienTheForm from '../views/Admin/bienTheForm.vue'
import chiTietDanhGia from '../views/Admin/chiTietDanhGia.vue'
import tkDanhGia from '../views/Admin/tkDanhGia.vue'
import tkDoanhThuNam from '../views/Admin/tkDoanhThuNam.vue'
import tkDoanhThuNgay from '../views/Admin/tkDoanhThuNgay.vue'
import tkDoanhThuThang from '../views/Admin/tkDoanhThuThang.vue'
import tkKhachHang from '../views/Admin/tkKhachHang.vue'
import tkSanPhamYeuThich from '../views/Admin/tkSanPhamYeuThich.vue'
import quanLyChietKhau from '../views/Admin/quanLyChietKhau.vue'
import quanLyDaiLy from '../views/Admin/quanLyDaiLy.vue'
import chietKhauForm from '../views/Admin/chietKhauForm.vue'
import daiLyForm from '../views/Admin/daiLyForm.vue'
import quanLyNguonGoc from '../views/Admin/quanLyNguonGoc.vue'
import quanLyHuongVi from '../views/Admin/quanLyHuongVi.vue'
import quanLyHinhDang from '../views/Admin/quanLyHinhDang.vue'
import quanLySize from '../views/Admin/quanLySize.vue'
import quanLyThongTinCuaHang from '../views/Admin/quanLyThongTinCuaHang.vue'
import quanlyLienHe from '../views/Admin/quanlyLienHe.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'trangChu',
      component: trangChu,
    },
      {
      path: '/binhLuanTinTuc/:id',
      name: 'binhLuanTinTucid',
      component: binhLuanTinTuc,
    },
    {
      path: '/cauHoiThuongGap',
      name: 'cauHoiThuongGap',
      component: cauHoiThuongGap,
    },
    {
      path: '/product/:id',
      name: 'chiTietSP',
      component: chiTietSP,
    },
    {
      path: '/chiTietSP/:id',
      name: 'chiTietSPAlt',
      component: chiTietSP,
    },

    {
      path: '/dangKy',
      name: 'dangKy',
      component: dangKy,
    },
    {
      path: '/dangNhap',
      name: 'dangNhap',
      component: dangNhap,
    },
    {
      path: '/daThanhToan',
      name: 'daThanhToan',
      component: daThanhToan,
    },
    {
      path: '/donHang',
      name: 'donHang',
      component: donHang,
    },
    {
      path: '/gioHang',
      name: 'gioHang',
      component: gioHang,
    },
    {
      path: '/gioiThieu',
      name: 'gioiThieu',
      component: gioiThieu,
    },
    {
      path: '/heThongCuaHang',
      name: 'heThongCuaHang',
      component: heThongCuaHang,
    },
    {
      path: '/lienHe',
      name: 'lienHe',
      component: lienHe,
    },
    {
      path: '/sanPham',
      name: 'sanPham',
      component: sanPham,
    },
    {
      path: '/thanhToan',
      name: 'thanhToan',
      component: thanhToan,
    },
    {
      path: '/thanhToanDaiLy',
      name: 'thanhToanDaiLy',
      component: thanhToanDaiLy,
    },
    {
      path: '/thongTinCaNhan',
      name: 'thongTinCaNhan',
      component: thongTinCaNhan,
    },
    {
      path: '/tinTuc',
      name: 'tinTuc',
      component: tinTuc,
    },
    {
      path: '/trangYeuThich',
      name: 'trangYeuThich',
      component: trangYeuThich,
    },
    {
      path: '/doiMatKhau',
      name: 'doiMatKhau',
      component: doiMatKhau,
    },
    // {
    //   path: '/donHangUser',
    //   name: 'donHangUser',
    //   component: donHangUser,
    // },
    {
      path: '/soDiaChi',
      name: 'soDiaChi',
      component: soDiaChi,
    },
    {
      path: '/dangKyDaiLy',
      name: 'dangKyDaiLy',
      component: dangKyDaiLy,
    },
    {
      path: '/updateAgent',
      name: 'updateDaiLy',
      component: updateDaiLy,
    },
    {
      path: '/danhGiaSP',
      name: 'danhGiaSP',
      component: danhGiaSP,
    },
    {
      path: '/vnpay-return',
      name: ' vnpayreturn',
      component: vnpayreturn,
    },
    {
      path: '/forgot-password',
      name: 'ForgotPasswordSend',
      component: ForgotPasswordSend,
    },
    {
      path: '/forgot-password/verify',
      name: 'ForgotPasswordVerify',
      component: ForgotPasswordVerify,
    },

    //Admin
    {
      path: '/order-detail/:id',
      name: 'chiTietDonHang',
      component: chiTietDonHang,
    },
    {
      path: '/danhMucForm',
      name: 'danhMucForm',
      component: danhMucForm,
    },
    {
      path: '/giamGiaForm',
      name: 'giamGiaForm',
      component: giamGiaForm,
    },
    {
      path: '/nguoiDungForm/:id',
      name: 'nguoiDungForm',
      component: nguoiDungForm,
    },
    {
      path: '/sanPhamForm',
      name: 'sanPhamForm',
      component: sanPhamForm,
    },
    {
      path: '/tinTucForm',
      name: 'tinTucForm',
      component: tinTucForm,
    },
    {
      path: '/vouchersForm',
      name: 'vouchersForm',
      component: vouchersForm,
    },
    {
      path: '/quanLyBinhLuanSP',
      name: 'quanLyBinhLuanSP',
      component: quanLyBinhLuanSP,
    },
    {
      path: '/quanLyBinhLuanTT',
      name: 'quanLyBinhLuanTT',
      component: quanLyBinhLuanTT,
    },
    {
      path: '/quanLyDanhGia',
      name: 'quanLyDanhGia',
      component: quanLyDanhGia,
    },
    {
      path: '/quanLyDanhMuc',
      name: 'quanLyDanhMuc',
      component: quanLyDanhMuc,
    },
    {
      path: '/quanLyDonHang',
      name: 'quanLyDonHang',
      component: quanLyDonHang,
    },
    {
      path: '/quanLyGiamGia',
      name: 'quanLyGiamGia',
      component: quanLyGiamGia,
    },
    {
      path: '/quanLyNguoiDung',
      name: 'quanLyNguoiDung',
      component: quanLyNguoiDung,
    },
    {
      path: '/quanLySanPham',
      name: 'quanLySanPham',
      component: quanLySanPham,
    },
    {
      path: '/quanLyTinTuc',
      name: 'quanLyTinTuc',
      component: quanLyTinTuc,
    },
    {
      path: '/quanLyVouchers',
      name: 'quanLyVouchers',
      component: quanLyVouchers,
    },
    {
      path: '/dasboard',
      name: 'dasboard',
      component: dasboard,
    },
    {
      path: '/admin/product/:productId/variants',
      name: 'quanLyBienThe',
      component: quanLyBienThe,
    },

    {
      path: '/bienTheForm',
      name: 'bienTheForm',
      component: bienTheForm,
    },
    {
      path: '/chiTietDanhGia/:id',
      name: 'chiTietDanhGia',
      component: chiTietDanhGia,
    },
    {
      path: '/tkDanhGia',
      name: 'tkDanhGia',
      component: tkDanhGia,
    },
    {
      path: '/tkDoanhThuNam',
      name: 'tkDoanhThuNam',
      component: tkDoanhThuNam,
    },
    {
      path: '/tkDoanhThuNgay',
      name: 'tkDoanhThuNgay',
      component: tkDoanhThuNgay,
    },
    {
      path: '/tkDoanhThuThang',
      name: 'tkDoanhThuThang',
      component: tkDoanhThuThang,
    },
    {
      path: '/tkKhachHang',
      name: 'tkKhachHang',
      component: tkKhachHang,
    },
    {
      path: '/tkSanPhamYeuThich',
      name: 'tkSanPhamYeuThich',
      component: tkSanPhamYeuThich,
    },
    {
      path: '/admin/apply-discount/:agentId',
      name: 'quanLyChietKhau',
      component: quanLyChietKhau,
    },
    {
      path: '/quanLyDaiLy',
      name: 'quanLyDaiLy',
      component: quanLyDaiLy,
    },
    {
      path: '/chietKhauForm',
      name: 'chietKhauForm',
      component: chietKhauForm,
    },
    {
    path: '/timKiem',
    name: 'searchResult',
    component: searchResult,
    },
    {
      path: '/daiLyForm',
      name: 'daiLyForm',
      component: daiLyForm,
    },
     {
      path: '/quanLyNguonGoc',
      name: 'quanLyNguonGoc',
      component: quanLyNguonGoc,
    },
    {
      path: '/quanLyHinhDang',
      name: 'quanLyHinhDang',
      component: quanLyHinhDang,
    },
    {
      path: '/quanLySize',
      name: 'quanLySize',
      component: quanLySize,
    },
    {
      path: '/quanLyHuongVi',
      name: 'quanLyHuongVi',
      component: quanLyHuongVi,
    },
      {
      path: '/quanLyThongTinCuaHang',
      name: 'quanLyThongTinCuaHang',
      component: quanLyThongTinCuaHang,
    },
    {
      path: '/quanlyLienHe',
      name: 'quanlyLienHe',
      component: quanlyLienHe,
    },
  ],
})

export default router
