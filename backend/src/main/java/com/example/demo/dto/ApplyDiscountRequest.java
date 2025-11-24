package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ApplyDiscountRequest {
    private Integer discountId;
    private List<Integer> productIds;
}
