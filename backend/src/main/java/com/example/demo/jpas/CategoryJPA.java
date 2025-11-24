package com.example.demo.jpas;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryJPA extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products p WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Integer id);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    Page<Category> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Integer id);

}
