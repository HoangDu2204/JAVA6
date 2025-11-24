package com.example.demo.beans;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShapeBean {
    private int id;
    @NotBlank(message = "Tên hình dạng không được để trống")
    private String name;



}