package com.example.demo.jpas;

import com.example.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantJPA extends JpaRepository<ProductVariant, Integer> {

    // Lấy danh sách biến thể của 1 sản phẩm
    List<ProductVariant> findByProductId(Integer productId);

    List<ProductVariant> findByProductIdAndIsActiveTrue(Integer productId);

    boolean existsByShape_Id(Integer shapeId);


}
