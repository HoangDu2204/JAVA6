package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminResponseDTO {
    private Integer id;
    @NotBlank(message = "Admin response is required")
    private String adminResponse;
}
