package com.example.demo.jpas;

import com.example.demo.entity.Cart;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartJPA extends JpaRepository<Cart, Integer> {

    // Lấy danh sách giỏ hàng theo user
    List<Cart> findByUserId(Integer userId);
   // Optional<Cart> findByUserId(Integer userId);
    // (Tuỳ chọn) Nếu mỗi user chỉ có 1 giỏ hàng đang hoạt động
    Optional<Cart> findByUserIdAndId(Integer userId, Integer cartId);
    Cart findByUser(User user);
}
