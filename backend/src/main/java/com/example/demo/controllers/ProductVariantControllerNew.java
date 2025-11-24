package com.example.demo.controllers;

import com.example.demo.dto.ProductVariantCreateDTONew;
import com.example.demo.entity.ProductVariant;
import com.example.demo.services.ProductVariantServiceNew;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/variants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductVariantControllerNew {

    private final ProductVariantServiceNew variantService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductVariantCreateDTONew dto) {
        try {
            ProductVariant variant = variantService.create(dto);
            return ResponseEntity.ok(variant);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ProductVariantCreateDTONew dto) {
        try {
            ProductVariant variant = variantService.update(id, dto);
            return ResponseEntity.ok(variant);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage())); //  Gửi thông báo lỗi về FE
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            variantService.delete(id);
            return ResponseEntity.ok("Đã xóa biến thể");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage())); //  Trả lỗi khi biến thể nằm trong đơn hàng
        }
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<?> getByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(variantService.getByProductId(productId));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterVariants(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer weight,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductVariant> result = variantService.filterVariants(productId, weight, isActive, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/toggle-active/{id}")
    public ResponseEntity<?> toggleActive(@PathVariable Integer id) {
        try {
            ProductVariant updated = variantService.toggleActiveStatus(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }


}
