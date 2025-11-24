package com.example.demo.controllers;

import com.example.demo.entity.ProductVariant;
import com.example.demo.services.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/product-variants")
public class ProductVariantRestController {

    @Autowired
    private ProductVariantService variantService;

    // GET /api/product-variants/by-product/5
    @GetMapping("/by-product/{productId}")
    public List<ProductVariant> getVariantsByProduct(@PathVariable Integer productId) {
        return variantService.getVariantsByProductId(productId);
    }

    // GET /api/product-variants/{id}
    @GetMapping("/{id}")
    public ProductVariant getVariantById(@PathVariable Integer id) {
        return variantService.getVariantById(id);
    }
}
