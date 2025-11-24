package com.example.demo.services.impl;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.jpas.CategoryJPA;
import com.example.demo.jpas.ProductJPA;
import com.example.demo.services.CategoryService1;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl1 implements CategoryService1 {

    private final CategoryJPA categoryJPA;
    private final ProductJPA productJPA;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(String search, Pageable pageable) {
        Page<Category> page;
        if (search != null && !search.trim().isEmpty()) {
            page = categoryJPA.searchByName(search.trim(), pageable);
        } else {
            page = categoryJPA.findAll(pageable);
        }
        return page.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        if (categoryJPA.existsByName(dto.getName())) {
            throw new RuntimeException("Danh mục đã tồn tại");
        }
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        category.setCreatedDate(LocalDateTime.now());
        category = categoryJPA.save(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto) {
        Optional<Category> optional = categoryJPA.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Danh mục không tồn tại");
        }
        Category category = optional.get();
        category.setName(dto.getName());
        category.setIsActive(dto.getIsActive());
        categoryJPA.save(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryJPA.existsById(id)) {
            throw new RuntimeException("Danh mục không tồn tại");
        }
        categoryJPA.deleteById(id);
    }

    /**
     * Lấy danh mục với danh sách sản phẩm và tải thêm hình ảnh của sản phẩm.
     *
     * @param id ID của danh mục.
     * @return CategoryDTO chứa thông tin danh mục và danh sách sản phẩm (có images).
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryWithProducts(Integer id) {
        Category category = categoryJPA.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        for (Product product : category.getProducts()) {
            productJPA.findByIdWithImages(product.getId()).ifPresent(p -> {
                // Gán danh sách ảnh (images) từ bản ghi đã nạp đầy đủ
                product.setImages(p.getImages());
            });

            // Nếu bạn muốn tải thêm giảm giá, bật đoạn sau:
            // productJPA.findByIdWithDiscounts(product.getId()).ifPresent(d -> {
            //     product.setProductDiscounts(d.getProductDiscounts());
            // });
        }

        return convertToDTO(category);
    }

    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .isActive(category.getIsActive())
                .createdDate(category.getCreatedDate())
                .build();
    }
}
