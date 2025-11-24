package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class NewsDTO {

    private Integer newsId;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createdAt;
    private Boolean isVisible;
    private List<NewsCommentDTO> newsComments;


}
