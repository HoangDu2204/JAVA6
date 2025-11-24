package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.entity.Category;
import com.example.demo.services.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // sửa lại nếu frontend đổi port
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/featured")
    public List<HomeProductDTO> getTop4FeaturedProducts() {
        return homeService.getTop4PerfectRatedProducts();
    }

    @GetMapping("/discounted")
    public List<HomeDiscountProductDTO> getDiscountedProducts() {
        return homeService.getAllDiscountedProducts();
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<ProductTopSellingHomeDTO>> getTopSellingProducts() {
        return ResponseEntity.ok(homeService.getTop5SellingProducts());
    }

    @GetMapping("/new-products")
    public List<ProductNewHomeDTO> getTop5NewestProducts() {
        return homeService.getTop5NewestProducts();
    }
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return homeService.getAllCategories();
    }

    @GetMapping("/products-by-category/{categoryId}")
    public List<HomeProductCategoryDTO> getProductsByCategory(@PathVariable Integer categoryId) {
        return homeService.getProductsByCategory(categoryId);
    }

    @GetMapping("/news")
    public ResponseEntity<List<NewsHomeDTO>> getTop3News() {
        return ResponseEntity.ok(homeService.getTop3LatestNews());
    }

}
