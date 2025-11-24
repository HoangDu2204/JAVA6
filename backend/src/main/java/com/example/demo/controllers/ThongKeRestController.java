package com.example.demo.controllers;

import com.example.demo.services.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/thong-ke")
@PreAuthorize("hasRole('ADMIN')")
public class ThongKeRestController {

    @Autowired
    private ThongKeService thongKeService;

    // 1. Doanh thu theo ngày
    @GetMapping("/doanh-thu/ngay")
    public List<Object[]> getDoanhThuTheoNgay() {
        return thongKeService.thongKeDoanhThuNgay();
    }

    // 2. Doanh thu theo tháng
    @GetMapping("/doanh-thu/thang")
    public List<Object[]> getDoanhThuTheoThang() {
        return thongKeService.thongKeDoanhThuThang();
    }

    // 3. Doanh thu theo năm
    @GetMapping("/doanh-thu/nam")
    public List<Object[]> getDoanhThuTheoNam() {
        return thongKeService.thongKeDoanhThuNam();
    }

    // 4. Thống kê đánh giá sản phẩm
//    @GetMapping("/danh-gia")
//    public List<Object[]> getThongKeDanhGia() {
//        return thongKeService.thongKeDanhGiaSanPham();
//    }

    // 5. Thống kê sản phẩm yêu thích
    @GetMapping("/yeu-thich")
    public List<Object[]> getYeuThich(
            @RequestParam(required = false) Integer thang,
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(defaultValue = "10") Integer top) {
        return thongKeService.thongKeYeuThich(thang, id, top);
    }


    // 6. Thống kê khách hàng VIP
//    @GetMapping("/vip")
//    public List<Object[]> getThongKeKhachHangVip() {
//        return thongKeService.thongKeKhachHangVip();
//    }
    // GET /api/thong-ke/vip
    @GetMapping("/vip")
    public List<Object[]> getKhachHangVip(
            @RequestParam(required = false) Integer thang,
            @RequestParam(defaultValue = "10") Integer top
    ) {
        return thongKeService.thongKeKhachHangVip(thang, top);
    }

    // GET /api/thong-ke/danh-gia
    @GetMapping("/danh-gia")
    public List<Object[]> getThongKeDanhGia(
            @RequestParam(required = false) Integer thang,
            @RequestParam(defaultValue = "10") Integer top
    ) {
        return thongKeService.thongKeDanhGia(thang, top);
    }

}
