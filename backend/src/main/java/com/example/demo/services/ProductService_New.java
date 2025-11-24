package com.example.demo.services;

import com.example.demo.dto.ProductCreateDTO_New;
import com.example.demo.dto.ProductDTO_New;
import com.example.demo.entity.*;
import com.example.demo.jpas.CategoryJPA_New;
import com.example.demo.jpas.ImageJPA;
import com.example.demo.jpas.ProductJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService_New {

    private final ProductJPA productRepo;
    private final CategoryJPA_New categoryRepo;
    private final ImageJPA imageRepo;
    private final ImageService imageService;

    // Tạo mới sản phẩm
    public Product createProduct(ProductCreateDTO_New dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setIsActive(dto.getIsActive());
        product.setCreatedDate(LocalDateTime.now());

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        product.setCategory(category);

        product = productRepo.save(product);

        // Lưu hình ảnh (chỉ lưu tên file)
        List<Image> images = new ArrayList<>();
        for (var file : dto.getImages()) {
            String fileName = imageService.saveImage(file);
            if (fileName != null) {
                Image img = new Image();
                img.setUrl(fileName); // ✅ chỉ lưu tên file
                img.setProduct(product);
                images.add(img);
            }
        }
        imageRepo.saveAll(images);

        return product;
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Integer productId, ProductCreateDTO_New dto) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setIsActive(dto.getIsActive());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }

        // Nếu có ảnh mới thì xóa ảnh cũ và lưu ảnh mới
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<Image> oldImages = imageRepo.findByProductId(productId);
            imageRepo.deleteAll(oldImages);


            List<Image> images = new ArrayList<>();
            for (var file : dto.getImages()) {
                String fileName = imageService.saveImage(file);
                if (fileName != null) {
                    Image img = new Image();
                    img.setUrl(fileName); // ✅ chỉ lưu tên file
                    img.setProduct(product);
                    images.add(img);
                }
            }
            imageRepo.saveAll(images);
        }

        return productRepo.save(product);
    }

    // Xóa sản phẩm (soft delete bằng isActive = false)
    public void softDeleteProduct(Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setIsActive(false);
        productRepo.save(product);
    }

    public Product toggleProductStatus(Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setIsActive(!product.getIsActive());
        return productRepo.save(product);
    }


    // Lấy danh sách sản phẩm và chỉ lấy 1 ảnh đại diện
    public List<ProductDTO_New> getAll() {
        List<Product> products = productRepo.findAllWithCategoryAndImages(); // đảm bảo query này fetch hình ảnh
        List<ProductDTO_New> result = new ArrayList<>();

        for (Product p : products) {
            ProductDTO_New dto = new ProductDTO_New();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setIsActive(p.getIsActive());
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);

            //  Lấy tất cả hình ảnh
            List<String> imageUrls = new ArrayList<>();
            if (p.getImages() != null) {
                for (Image img : p.getImages()) {
                    imageUrls.add(img.getUrl()); // chỉ tên file
                }
            }
            dto.setImageUrls(imageUrls);

            result.add(dto);
        }

        return result;
    }


    // Lấy sản phẩm theo ID
    public Product getProductById(Integer id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
    }

    //  Lọc + tìm kiếm + phân trang
    public Page<ProductDTO_New> searchProducts(Integer categoryId, Boolean isActive, String keyword, Pageable pageable) {
        Page<Product> page = productRepo.searchProducts(categoryId, isActive, keyword, pageable);

        return page.map(p -> {
            ProductDTO_New dto = new ProductDTO_New();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setIsActive(p.getIsActive());
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);

            List<String> imageUrls = new ArrayList<>();
            if (p.getImages() != null) {
                for (Image img : p.getImages()) {
                    imageUrls.add(img.getUrl());
                }
            }
            dto.setImageUrls(imageUrls);

            return dto;
        });
    }


}
