package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    private String name;
    private String phone;
    private String address;
    private String note;

    private String paymentMethod;
    private String paymentStatus;

    private BigDecimal shippingFee;        // phí ship
    private BigDecimal discount;           // giảm từ voucher
    private BigDecimal discountAmount;     // giảm cho đại lý
    private BigDecimal totalAmount;        // tổng tiền gốc
    private BigDecimal finalTotal;         // tiền thực trả

    private String voucherCode;
    private Integer agentId;
    private Boolean agentOrder;

    private Integer toDistrictId;
    private String toWardCode;

    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Integer productVariantId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
