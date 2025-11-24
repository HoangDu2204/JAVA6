package com.example.demo.jpas;

import com.example.demo.entity.Shape;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShapeJPA extends JpaRepository<Shape, Integer> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    Page<Shape> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Shape> findByIsActive(boolean isActive, Pageable pageable);
}