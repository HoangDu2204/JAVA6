package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductVariantDTO {
    private Integer id;
    private String size;
    private String flavor;
    private String shape;
    private String origin;
    private Double price;
    private Integer quantity;
}
