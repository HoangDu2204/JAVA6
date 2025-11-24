package com.example.demo.controllers;

import com.example.demo.entity.Category;
import com.example.demo.jpas.CategoryJPA_New;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController_New {

    private final CategoryJPA_New categoryRepo;

    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        return ResponseEntity.ok(categoryRepo.findAllActive());
    }
}
