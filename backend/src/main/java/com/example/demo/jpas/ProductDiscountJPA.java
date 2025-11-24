package com.example.demo.jpas;

import com.example.demo.entity.Discount;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDiscountJPA extends JpaRepository<ProductDiscount, Integer> {
    boolean existsByProductAndDiscount(Product product, Discount discount);
}
