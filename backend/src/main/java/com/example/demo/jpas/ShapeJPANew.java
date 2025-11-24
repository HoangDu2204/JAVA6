package com.example.demo.jpas;

import com.example.demo.entity.Shape;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShapeJPANew extends JpaRepository<Shape, Integer> {
    List<Shape> findByIsActiveTrue();
}
