package com.example.demo.jpas;

import com.example.demo.entity.CancelRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancelRequestJPA extends JpaRepository<CancelRequest, Integer> {
    List<CancelRequest> findByUserId(Integer userId);
    List<CancelRequest> findByOrderId(Integer orderId);
}
