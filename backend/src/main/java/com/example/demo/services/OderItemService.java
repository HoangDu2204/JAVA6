package com.example.demo.services;

import com.example.demo.entity.Order;
import com.example.demo.jpas.OrderItemJPA;
import com.example.demo.jpas.OrderJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OderItemService {
	@Autowired
    private OrderJPA orderJPA;
    @Autowired
    private OrderItemJPA orderItemJPA;
	public List<Order> findAll() {
        return orderJPA.findAll();
    }

    public Order findById(int id) {
        return orderJPA.findById(id).orElse(null);
    }

    public void updateStatus(int id, String orderStatus) {
        Order order = findById(id);
        if (order != null) {
          order.setOrderStatus(orderStatus);
            orderJPA.save(order);
        }
    }
    public void checkProductEditable(Integer productId) {
        if (orderItemJPA.existsByProductVariant_Product_Id(productId)) {
            throw new IllegalStateException("Sản phẩm đã tồn tại trong đơn hàng, không thể chỉnh sửa.");
        }
    }

    public void checkVariantEditable(Integer variantId) {
        if (orderItemJPA.existsByProductVariant_Id(variantId)) {
            throw new IllegalStateException("Biến thể đã tồn tại trong đơn hàng, không thể chỉnh sửa.");
        }
    }
}
