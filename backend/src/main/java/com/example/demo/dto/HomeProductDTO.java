package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeProductDTO {
    private Integer id;
    private String name;
    private String imageUrl;

}
