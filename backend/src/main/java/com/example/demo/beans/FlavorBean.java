package com.example.demo.beans;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlavorBean {
    private Long id;

    @NotBlank(message = "Tên hương vị không được để trống")
    private String name;
    private Boolean isActive; // ✅ Thêm trường trạng thái
}
