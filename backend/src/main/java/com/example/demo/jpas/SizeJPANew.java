package com.example.demo.jpas;

import com.example.demo.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeJPANew extends JpaRepository<Size, Integer> {
    List<Size> findByIsActiveTrue();

    Optional<Size> findByNameIgnoreCase(String name);
}
