package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AgentDiscountDTO {
    private Integer id;
    private Integer agentId;
    private String agentName;
    private Double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;
}
