package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlavorDTO {
    private Integer id;
    private String name;
    private Boolean isActive; // ✅ Thêm để trả trạng thái về frontend
}
