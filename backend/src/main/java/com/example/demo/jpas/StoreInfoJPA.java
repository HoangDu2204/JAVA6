package com.example.demo.jpas;

import com.example.demo.entity.StoreInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreInfoJPA extends JpaRepository<StoreInfo, Integer> {
} 