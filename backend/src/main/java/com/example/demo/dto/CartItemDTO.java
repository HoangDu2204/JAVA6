package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDTO {
    private Integer id;
    private Integer quantity;

    private Integer variantId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private Double discountPercentage;
    private Double discountedPrice;

    private List<String> variantLines;

}
