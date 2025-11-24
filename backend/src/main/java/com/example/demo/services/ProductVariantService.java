package com.example.demo.services;

import com.example.demo.entity.ProductVariant;
import com.example.demo.jpas.ProductVariantJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantJPA productVariantJPA;

    // Lấy danh sách biến thể theo productId
//    public List<ProductVariant> getVariantsByProductId(Integer productId) {
//        return productVariantJPA.findByProductId(productId);
//    }
    public List<ProductVariant> getVariantsByProductId(Integer productId) {
        return productVariantJPA.findByProductIdAndIsActiveTrue(productId);
    }

    public ProductVariant getVariantById(Integer id) {
        return productVariantJPA.findById(id).orElse(null);
    }
}
