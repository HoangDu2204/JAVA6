package com.example.demo.beans;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OriginBean {
    private int id;
    @NotBlank(message = "Tên nguồn gốc không được để trống")
    private String name;


    private Boolean isActive; // thêm field
}
