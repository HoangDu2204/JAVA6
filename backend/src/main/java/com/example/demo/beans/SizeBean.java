package com.example.demo.beans;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SizeBean {
    private Integer id;
    
    @NotBlank(message = "Tên kích thước không được để trống")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ0-9\\s()]{1,50}$", 
             message = "Tên kích thước chỉ được chứa chữ cái, số, khoảng trắng và dấu ngoặc đơn ()")
    private String name;
    
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;
} 