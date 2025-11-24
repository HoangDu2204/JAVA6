package com.example.demo.dto;

import lombok.Data;

@Data
public class CancelRequestDTO {
    private Integer orderId;
    private String reason;
}
