package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailListDTO {
    private Integer id;
    private String name;
    private String phone;
    private String address;
    private String username;
    private String note;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal finalTotal;
    private boolean agent;

    private List<OrderItemListDTO> items;
}
