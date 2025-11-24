package com.example.demo.jpas;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface OrderJPA extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<Order> findByOrderStatus(String orderStatus);
   // List<Order> findAllByOrderByIdDesc();
   //  Phân trang theo id giảm dần (đơn hàng mới nhất nằm ở đầu)
   Page<Order> findAllByOrderByIdDesc(Pageable pageable);

    boolean existsByUserIdAndVoucherId(Integer userId, Integer voucherId);

    Optional<Order> findByCustomOrderCode(String customOrderCode);

    Page<Order> findByOrderStatusAndAgentIsNull(String status, Pageable pageable);
    Page<Order> findByOrderStatusAndAgentIsNotNull(String status, Pageable pageable);
    Page<Order> findByOrderStatus(String status, Pageable pageable);
    Page<Order> findByAgentIsNull(Pageable pageable);
    Page<Order> findByAgentIsNotNull(Pageable pageable);

    boolean existsByOrderItems_ProductVariant_Shape_Id(Integer shapeId);

}
