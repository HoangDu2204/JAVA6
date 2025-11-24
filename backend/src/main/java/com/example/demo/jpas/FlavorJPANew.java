package com.example.demo.jpas;

import com.example.demo.entity.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlavorJPANew extends JpaRepository<Flavor, Integer> {
    List<Flavor> findByIsActiveTrue();
}
