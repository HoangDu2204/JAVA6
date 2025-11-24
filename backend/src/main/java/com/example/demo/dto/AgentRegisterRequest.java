package com.example.demo.dto;

import lombok.Data;

@Data
public class AgentRegisterRequest {
    private Integer userId;
    private String otpCode;

    private String agentName;
    private String phone;
    private String receiverName;

    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private Integer wardId;
    private String wardName;

    private String addressDetail;
    private String email;
}
