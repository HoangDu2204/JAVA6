package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsHomeDTO {
    private Integer id;
    private String title;
    private String image;
    private LocalDateTime createdAt;
}
