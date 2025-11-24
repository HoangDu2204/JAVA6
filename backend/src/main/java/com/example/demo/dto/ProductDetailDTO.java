package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailDTO {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private Double originalPrice;
    private Double finalPrice;
    private Double discountAmount;
    private Double discountPercentage;
    private List<String> imageUrls;
    private List<ProductVariantDTO> productVariants;
}
