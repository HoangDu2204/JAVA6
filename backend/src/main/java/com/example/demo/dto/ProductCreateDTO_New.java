package com.example.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductCreateDTO_New {
    private String name;
    private String description;
    private Integer categoryId;
    private Boolean isActive;
    private List<MultipartFile> images;


}
