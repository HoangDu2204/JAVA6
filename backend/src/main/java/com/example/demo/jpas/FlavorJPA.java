package com.example.demo.jpas;

import com.example.demo.entity.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlavorJPA extends JpaRepository<Flavor, Integer> {

}
