package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeProductCategoryDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private Double originalPrice;
    private Double finalPrice;
}
