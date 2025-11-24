package com.example.demo.services;

import com.example.demo.dto.ApplyDiscountRequest;
import com.example.demo.entity.Discount;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.jpas.DiscountJPA;
import com.example.demo.jpas.ProductDiscountJPA;
import com.example.demo.jpas.ProductJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDiscountService {

    private final ProductJPA productJPA;
    private final DiscountJPA discountJPA;
    private final ProductDiscountJPA productDiscountJPA;

//    public void applyDiscountToProducts(ApplyDiscountRequest request) {
//        Optional<Discount> optionalDiscount = discountJPA.findById(Long.valueOf(request.getDiscountId()));
//        if (optionalDiscount.isEmpty()) {
//            throw new RuntimeException("Không tìm thấy mã giảm giá có ID = " + request.getDiscountId());
//        }
//
//        Discount discount = optionalDiscount.get();
//
//        for (Integer productId : request.getProductIds()) {
//            Optional<Product> optionalProduct = productJPA.findById(productId);
//            if (optionalProduct.isEmpty()) {
//                continue; // Bỏ qua sản phẩm không tồn tại
//            }
//
//            Product product = optionalProduct.get();
//
//            boolean exists = productDiscountJPA.existsByProductAndDiscount(product, discount);
//            if (!exists) {
//                ProductDiscount pd = new ProductDiscount();
//                pd.setProduct(product);
//                pd.setDiscount(discount);
//                productDiscountJPA.save(pd);
//            }
//        }
//    }

    public void applyDiscountToProducts(ApplyDiscountRequest request) {
        Optional<Discount> optionalDiscount = discountJPA.findById(Long.valueOf(request.getDiscountId()));
        if (optionalDiscount.isEmpty()) {
            throw new RuntimeException("Không tìm thấy mã giảm giá có ID = " + request.getDiscountId());
        }

        Discount discount = optionalDiscount.get();

        List<String> conflictedProducts = new ArrayList<>();

        for (Integer productId : request.getProductIds()) {
            Optional<Product> optionalProduct = productJPA.findById(productId);
            if (optionalProduct.isEmpty()) {
                continue; // Bỏ qua sản phẩm không tồn tại
            }

            Product product = optionalProduct.get();

            boolean exists = productDiscountJPA.existsByProductAndDiscount(product, discount);
            if (exists) {
                conflictedProducts.add(product.getName());
            }
        }

        // ❌ Nếu có sản phẩm trùng → ném lỗi
        if (!conflictedProducts.isEmpty()) {
            throw new IllegalArgumentException("Các sản phẩm đã được áp dụng giảm giá này: " + String.join(", ", conflictedProducts));
        }

        // ✅ Nếu không có trùng → tiến hành gán giảm giá
        for (Integer productId : request.getProductIds()) {
            Optional<Product> optionalProduct = productJPA.findById(productId);
            if (optionalProduct.isEmpty()) continue;

            Product product = optionalProduct.get();

            ProductDiscount pd = new ProductDiscount();
            pd.setProduct(product);
            pd.setDiscount(discount);
            productDiscountJPA.save(pd);
        }
    }

}
