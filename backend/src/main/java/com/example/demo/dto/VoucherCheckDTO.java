package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VoucherCheckDTO {
    private String voucherCode;
    private BigDecimal totalAmount;
}
