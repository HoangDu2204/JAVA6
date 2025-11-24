package com.example.demo.controllers;

import com.example.demo.dto.ProductCreateDTO_New;
import com.example.demo.dto.ProductDTO_New;
import com.example.demo.entity.Image;
import com.example.demo.entity.Product;
import com.example.demo.jpas.ImageJPA;
import com.example.demo.jpas.OrderItemJPA;
import com.example.demo.jpas.ProductJPA;
import com.example.demo.services.ProductService_New;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductController_New {

    private final ProductService_New productService;
    private final ProductJPA productJPA;
    private final OrderItemJPA orderItemJPA;

    @Autowired
    private ImageJPA imageJPA;

    // Tạo sản phẩm mới
    @PostMapping("/create")
    public ResponseEntity<?> create(@ModelAttribute ProductCreateDTO_New dto) {
        try {
            Product created = productService.createProduct(dto);
            return ResponseEntity.ok(created);
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Lỗi định dạng file hình ảnh!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Cập nhật sản phẩm
    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @ModelAttribute ProductCreateDTO_New dto) {
        try {
            // NGHIỆP VỤ: Không được cập nhật nếu sản phẩm đã nằm trong đơn hàng
            if (orderItemJPA.existsByProductVariant_Product_Id(id)) {
                return ResponseEntity.badRequest()
                        .body("Không thể cập nhật. Sản phẩm đã nằm trong đơn hàng.");
            }

            Product updated = productService.updateProduct(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // Ẩn sản phẩm (deactivate)
//    @PutMapping("/deactivate/{id}")
//    public ResponseEntity<?> softDelete(@PathVariable("id") Integer id) {
//        try {
//            // NGHIỆP VỤ: Không được ẩn nếu sản phẩm đã nằm trong đơn hàng
//            if (orderItemJPA.existsByProductVariant_Product_Id(id)) {
//                return ResponseEntity.badRequest()
//                        .body("Không thể ẩn. Sản phẩm đã nằm trong đơn hàng.");
//            }
//
//            productService.softDeleteProduct(id);
//            return ResponseEntity.ok("Đã cập nhật isActive = false");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
//        }
//    }
    @PutMapping("/toggle-active/{id}")
    public ResponseEntity<?> toggleProductActive(@PathVariable("id") Integer id) {
        try {
            Product product = productService.getProductById(id);

            // Chỉ chặn khi đang active và muốn ẩn
            if (product.getIsActive() && orderItemJPA.existsByProductVariant_Product_Id(id)) {
                return ResponseEntity.badRequest()
                        .body("Không thể ẩn. Sản phẩm đã nằm trong đơn hàng.");
            }

            Product updatedProduct = productService.toggleProductStatus(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }



    // Lấy tất cả sản phẩm
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO_New>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // Lấy chi tiết sản phẩm theo ID
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm");
        }
    }

    // Kiểm tra tên sản phẩm đã tồn tại
    @GetMapping("/exists-name")
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return ResponseEntity.ok(productJPA.existsByName(name.trim()));
    }

    // Xóa hình ảnh sản phẩm
    @DeleteMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestParam String url) {
        Optional<Image> imageOpt = imageJPA.findByUrl(url);
        if (imageOpt.isPresent()) {
            imageJPA.delete(imageOpt.get());

            // Nếu có lưu trên server vật lý thì xóa file
            String imagePath = "src/main/resources/static/images/" + url;
            File file = new File(imagePath);
            if (file.exists()) file.delete();

            return ResponseEntity.ok("Xóa ảnh thành công!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ảnh không tồn tại!");
    }

    //  API lọc + tìm kiếm + phân trang
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        try {
            return ResponseEntity.ok(
                    productService.searchProducts(categoryId, isActive, keyword,
                            org.springframework.data.domain.PageRequest.of(page, size))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

}
