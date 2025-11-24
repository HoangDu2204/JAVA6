package com.example.demo.jpas;


import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJPA extends JpaRepository<OrderItem, Integer>{
    // Kiểm tra biến thể đã nằm trong đơn hàng chưa
    boolean existsByProductVariant_Id(Integer variantId);

    // Kiểm tra sản phẩm đã nằm trong đơn hàng chưa
    boolean existsByProductVariant_Product_Id(Integer productId);
}
