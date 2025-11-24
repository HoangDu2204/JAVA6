package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscountDTO {
    private Integer discountId;
    private String name;
    private Double percentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
