package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductNewHomeDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private Double originalPrice;
    private Integer discountPercentage;
    private Double finalPrice;
}
