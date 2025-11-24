package com.example.demo.jpas;

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressJPA extends JpaRepository<Address, Integer> {
    List<Address> findByUserAndIsActiveTrue(User user);
}