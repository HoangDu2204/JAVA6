package com.example.demo.jpas;
import com.example.demo.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NewsJPA extends JpaRepository<News, Integer> {
    @Query("SELECT n FROM News n WHERE n.isVisible = true ORDER BY n.createdAt DESC")
    List<News> findAllVisible();

    Optional<News> findById(Integer id);

    Optional<News> findByTitle(String title);

    @Query("SELECT n FROM News n WHERE n.title LIKE %:keyword% AND n.isVisible = true")
    List<News> searchNews(String keyword);
}