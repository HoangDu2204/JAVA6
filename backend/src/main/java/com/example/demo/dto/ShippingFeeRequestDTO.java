package com.example.demo.dto;

import lombok.Data;

@Data
public class ShippingFeeRequestDTO {
    private int toDistrictId;
    private String toWardCode;
    private int weight;
    private int insuranceValue;
}
