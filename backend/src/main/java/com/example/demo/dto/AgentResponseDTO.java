package com.example.demo.dto;

import lombok.Data;

@Data
public class AgentResponseDTO {
    private Integer id;
    private String agentName;
    private String receiverName;
    private String phone;
    private String email;
    private String addressDetail;

    private Integer provinceId;
    private String provinceName;

    private Integer districtId;
    private String districtName;

    private Integer wardId;
    private String wardName;

    private Integer discountAmount;
    // nếu bạn tính được từ chỗ khác thì gán ở service
    private Boolean isApproved;
}
