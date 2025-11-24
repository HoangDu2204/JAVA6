package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO_New {
    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;
    private String categoryName;
    private List<String> imageUrls;
}
