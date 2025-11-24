package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListDTO {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private BigDecimal finalTotal;
    private boolean agent;
}
