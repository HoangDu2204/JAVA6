package com.example.demo.jpas;

import com.example.demo.entity.Origin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OriginJPANew extends JpaRepository<Origin, Integer> {
    List<Origin> findByIsActiveTrue();
}
