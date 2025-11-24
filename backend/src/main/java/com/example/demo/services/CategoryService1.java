package com.example.demo.services;

import com.example.demo.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService1 {
    Page<CategoryDTO> getAllCategories(String search, Pageable pageable);
    CategoryDTO getCategoryById(Integer id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO);
    void deleteCategory(Integer id);
    // Thêm phương thức mới
    CategoryDTO getCategoryWithProducts(Integer id);
}