package com.example.demo.jpas;

import com.example.demo.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageJPA extends JpaRepository<Image, Integer> {
    List<Image> findByProductId(Integer productId);
    void deleteByUrl(String url); // <--- sửa lại tên field
    Optional<Image> findByUrl(String url);
}
