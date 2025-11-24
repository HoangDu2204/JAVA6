package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {

    private Integer voucherId;
    private String code;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime createdDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Integer quantity;

    // Constructor để convert từ Entity sang DTO
    public VoucherDTO(com.example.demo.entity.Voucher voucher) {
        this.voucherId = voucher.getId();
        this.code = voucher.getCode();
        this.discountPercent = voucher.getDiscountPercent();
        this.maxDiscountAmount = voucher.getMaxDiscountAmount();
        this.minOrderAmount = voucher.getMinOrderAmount();
        this.createdDate = voucher.getCreatedDate();
        this.endDate = voucher.getEndDate();
        this.isActive = voucher.getIsActive();
        this.quantity = voucher.getQuantity();
    }

    // Method để convert từ DTO sang Entity
    public com.example.demo.entity.Voucher toEntity() {
        com.example.demo.entity.Voucher voucher = new com.example.demo.entity.Voucher();
        voucher.setId(this.voucherId);
        voucher.setCode(this.code);
        voucher.setDiscountPercent(this.discountPercent);
        voucher.setMaxDiscountAmount(this.maxDiscountAmount);
        voucher.setMinOrderAmount(this.minOrderAmount);
        voucher.setCreatedDate(this.createdDate);
        voucher.setEndDate(this.endDate);
        voucher.setIsActive(this.isActive);
        voucher.setQuantity(this.quantity);
        return voucher;
    }
}
