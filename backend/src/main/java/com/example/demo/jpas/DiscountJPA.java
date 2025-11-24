package com.example.demo.jpas;

import com.example.demo.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiscountJPA extends JpaRepository<Discount, Long> {
    List<Discount> findByIsActiveTrue();
}
