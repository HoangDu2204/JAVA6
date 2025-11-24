package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeDiscountProductDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private Double originalPrice;
    private Double discountPercentage;
    private Double finalPrice;
}
