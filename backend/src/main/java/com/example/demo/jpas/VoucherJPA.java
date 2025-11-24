package com.example.demo.jpas;

import com.example.demo.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherJPA extends JpaRepository<Voucher, Integer> {

    // Bạn có thể thêm các hàm custom ở đây nếu cần
    Voucher findByCode(String code); // tìm voucher theo mã code

    boolean existsByCode(String code); // kiểm tra trùng mã

}
