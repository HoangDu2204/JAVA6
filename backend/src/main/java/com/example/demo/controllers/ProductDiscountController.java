package com.example.demo.controllers;

import com.example.demo.dto.ApplyDiscountRequest;
import com.example.demo.services.ProductDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-discounts")
@RequiredArgsConstructor
public class ProductDiscountController {

    private final ProductDiscountService productDiscountService;

//    @PostMapping("/apply")
//    public ResponseEntity<?> applyDiscountToProducts(@RequestBody ApplyDiscountRequest request) {
//        productDiscountService.applyDiscountToProducts(request);
//        return ResponseEntity.ok("Áp dụng giảm giá thành công!");
//    }
@PostMapping("/apply")
public ResponseEntity<?> applyDiscountToProducts(@RequestBody ApplyDiscountRequest request) {
    try {
        productDiscountService.applyDiscountToProducts(request);
        return ResponseEntity.ok("Áp dụng giảm giá thành công!");
    } catch (IllegalArgumentException ex) {
        // Trả về lỗi rõ ràng cho FE xử lý
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

}
