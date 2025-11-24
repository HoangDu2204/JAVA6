package com.example.demo.services;

import com.example.demo.jpas.ThongKeJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThongKeService {

    @Autowired
    private ThongKeJPA thongKeJPA;

    public List<Object[]> thongKeDoanhThuNgay() {
        return thongKeJPA.thongKeDoanhThuTheoNgay();
    }

    public List<Object[]> thongKeDoanhThuThang() {
        return thongKeJPA.thongKeDoanhThuTheoThang();
    }

    public List<Object[]> thongKeDoanhThuNam() {
        return thongKeJPA.thongKeDoanhThuTheoNam();
    }

    //	public List<Object[]> thongKeDanhGiaSanPham() {
//		return thongKeJPA.thongKeDanhGiaSanPham();
//	}
    public List<Object[]> thongKeYeuThich(Integer thang, Integer id, Integer top) {
        var pageable = PageRequest.of(0, top != null ? top : 10);

        if (thang != null && id != null)
            return thongKeJPA.thongKeYeuThichTheoThangVaDanhMuc(thang, id, pageable);
        else if (thang != null)
            return thongKeJPA.thongKeYeuThichTheoThang(thang, pageable);
        else if (id != null)
            return thongKeJPA.thongKeYeuThichTheoDanhMuc(id, pageable);
        else
            return thongKeJPA.thongKeYeuThich(pageable);
    }


    // Thống kê khách hàng VIP
    public List<Object[]> thongKeKhachHangVip(Integer thang, Integer top) {
        Pageable pageable = PageRequest.of(0, top != null ? top : 10);
        if (thang != null)
            return thongKeJPA.thongKeKhachVipTheoThang(thang, pageable);
        else
            return thongKeJPA.thongKeKhachVip(pageable);
    }

    // Thống kê đánh giá sản phẩm
    public List<Object[]> thongKeDanhGia(Integer thang, Integer top) {
        Pageable pageable = PageRequest.of(0, top != null ? top : 10);
        if (thang != null)
            return thongKeJPA.thongKeDanhGiaTheoThang(thang, pageable);
        else
            return thongKeJPA.thongKeDanhGia(pageable);
    }


//	public List<Object[]> thongKeKhachHangVip() {
//		return thongKeJPA.thongKeKhachHangVip();
//	}
}
