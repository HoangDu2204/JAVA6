package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsCommentDTO {
    private Integer commentId;
    private Integer newsId;              // ID của tin tức
    private String userFullName;         // Họ tên người dùng
    private String userEmail;            // Email người dùng
    private String content;
    private LocalDateTime createdDate;
    private Boolean isVisible;


    private Integer userId;              // ID người dùng
    private Integer parentCommentId;     // Nếu là trả lời bình luận khác
    private List<NewsCommentDTO> childComments; // Trả lời
}