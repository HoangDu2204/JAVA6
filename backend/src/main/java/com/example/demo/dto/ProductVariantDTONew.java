package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductVariantDTONew {
    private Integer id;
    private String productName;
    private String sizeName;
    private String flavorName;
    private String shapeName;
    private String originName;
    private Double price;
    private Integer quantity;
    private Integer weight;
    private Boolean isActive;
}