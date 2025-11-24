package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultDTO {
    private List<ProductDTO> products;
    private List<NewsDTO> news;
    private int totalProducts;
    private int totalNews;
    private String query;
}

