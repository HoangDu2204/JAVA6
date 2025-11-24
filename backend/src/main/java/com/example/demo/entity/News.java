package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String image;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<NewsComment> newsComments;

}
