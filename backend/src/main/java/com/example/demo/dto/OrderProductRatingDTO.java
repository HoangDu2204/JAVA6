package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderProductRatingDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userFullName;
    private Integer orderItemId;
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer ratings; // Thay int bằng Integer để xử lý null
    private String comment;
    private LocalDateTime ratingDate;
    private String status;
    private LocalDateTime createdAt;
    private List<ReviewImageDTO> reviewImages; // Thêm trường này
}