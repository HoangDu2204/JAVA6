package com.example.demo.controllers;

import com.example.demo.dto.DiscountDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Discount;
import com.example.demo.services.DiscountService;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quanLyGiamGia")
@CrossOrigin(origins = "http://localhost:5173")
public class DiscountController {

    @Autowired
    private DiscountService service;

    @Autowired
    private ProductService productService;

    // Lấy tất cả giảm giá
    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Lấy các chương trình đang hoạt động
    @GetMapping("/active")
    public ResponseEntity<List<DiscountDTO>> getActivePrograms() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // Lấy giảm giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DiscountDTO> getById(@PathVariable Long id) {
        return service.getById(id.intValue())
                .map(discount -> ResponseEntity.ok(service.convertToDTO(discount)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo mới chương trình giảm giá
    @PostMapping
    public ResponseEntity<DiscountDTO> create(@RequestBody DiscountDTO dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }

        Discount created = service.add(dto);
        return ResponseEntity.status(201).body(service.convertToDTO(created));
    }

    // Cập nhật chương trình
    @PutMapping("/{id}")
    public ResponseEntity<DiscountDTO> update(@PathVariable Long id, @RequestBody DiscountDTO dto) {
        try {
            Discount updated = service.update(id.intValue(), dto);
            return ResponseEntity.ok(service.convertToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa chương trình
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id.intValue());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Kích hoạt chương trình
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.setActive(id.intValue(), true);
        return ResponseEntity.ok().build();
    }

    // Vô hiệu hóa chương trình
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.setActive(id.intValue(), false);
        return ResponseEntity.ok().build();
    }

    // 🎯 Áp dụng chương trình giảm giá cho danh sách sản phẩm
//    @PostMapping("/{id}/apply-products")
//    public ResponseEntity<Void> applyDiscountToProducts(
//            @PathVariable Long id,
//            @RequestBody List<Integer> productIds
//    ) {
//        try {
//            service.applyDiscountToProducts(id.intValue(), productIds);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    // 🎯 API lấy danh sách sản phẩm đơn giản (áp dụng cho discount)
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProductsForDiscount() {
        return ResponseEntity.ok(productService.getAllProductDTOs());
    }
}
