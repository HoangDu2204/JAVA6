package com.example.demo.controllers;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductDetailDTO;
import com.example.demo.entity.Product;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @GetMapping("/simple")
    public List<ProductDTO> getAllProductDTOs() {
        return productService.getAllProductDTOs();
    }



    // Lấy chi tiết sản phẩm theo id (dành cho trang chi tiết)
    @GetMapping("/{id}")
    public ProductDetailDTO getProductDetailById(@PathVariable Integer id) {
        return productService.getProductDetailById(id);
    }

    // Lọc sản phẩm theo tiêu chí
//    @GetMapping("/filter")
//    public List<ProductDTO> filterProducts(
//            @RequestParam(required = false) Integer categoryId,
//            @RequestParam(required = false) Double weight,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false, defaultValue = "newest") String sortBy
//    ) {
//        return productService.filterProducts(categoryId, weight, minPrice, maxPrice, sortBy);
//    }

}
