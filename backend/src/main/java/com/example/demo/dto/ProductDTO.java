package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private Double price;
    private List<String> imageUrls;
    private Double discountPercentage;
    private List<ProductVariantDTO> productVariants;
}
