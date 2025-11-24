package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgentDTONew {
    private Integer id;
    private String agentName;
    private String phone;
    private String receiverName;
    private String addressDetail;
    private String email;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private String username;  // từ User
    private String fullName;
    private String wardName;       // Phường / Xã
    private String districtName;   // Quận / Huyện
    private String provinceName; // từ User
}
