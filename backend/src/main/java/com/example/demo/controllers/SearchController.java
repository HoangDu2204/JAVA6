package com.example.demo.controllers;

import com.example.demo.dto.NewsDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.SearchResultDTO;
import com.example.demo.entity.News;
import com.example.demo.entity.Product;
import com.example.demo.jpas.NewsJPA;
import com.example.demo.jpas.ProductJPA;
import com.example.demo.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductJPA productJPA;
    private final ProductService productService;
    private final NewsJPA newsJPA;

    /**
     * Tìm kiếm sản phẩm theo tên
     * @param name Tên sản phẩm cần tìm kiếm
     * @return Danh sách sản phẩm phù hợp
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Tìm kiếm sản phẩm theo tên
            List<Product> products = productJPA.searchByName(name.trim());

            // Lọc chỉ những sản phẩm đang active
            List<Product> activeProducts = products.stream()
                    .filter(product -> Boolean.TRUE.equals(product.getIsActive()))
                    .collect(Collectors.toList());

            // Convert sang DTO
            List<ProductDTO> productDTOs = convertToProductDTOs(activeProducts);

            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tìm kiếm sản phẩm với giới hạn số lượng (cho gợi ý)
     * @param name Tên sản phẩm cần tìm kiếm
     * @param limit Số lượng kết quả tối đa (mặc định 5)
     * @return Danh sách sản phẩm phù hợp
     */
    @GetMapping("/products/suggestions")
    public ResponseEntity<List<ProductDTO>> searchProductSuggestions(
            @RequestParam String name,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Tìm kiếm sản phẩm theo tên
            List<Product> products = productJPA.searchByName(name.trim());

            // Lọc chỉ những sản phẩm đang active và giới hạn số lượng
            List<Product> activeProducts = products.stream()
                    .filter(product -> Boolean.TRUE.equals(product.getIsActive()))
                    .limit(limit)
                    .collect(Collectors.toList());

            // Convert sang DTO
            List<ProductDTO> productDTOs = convertToProductDTOs(activeProducts);

            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tìm kiếm tin tức
     * @param query Từ khóa tìm kiếm
     * @return Danh sách tin tức phù hợp
     */
    @GetMapping("/news")
    public ResponseEntity<List<NewsDTO>> searchNews(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<News> news = newsJPA.searchNews(query.trim());
            List<NewsDTO> newsDTOs = convertToNewsDTOs(news);
            return ResponseEntity.ok(newsDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tìm kiếm tổng hợp (cả sản phẩm và tin tức)
     * @param query Từ khóa tìm kiếm
     * @return Kết quả tìm kiếm tổng hợp
     */
    @GetMapping("/all")
    public ResponseEntity<SearchResultDTO> searchAll(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Tìm kiếm sản phẩm
            List<Product> products = productJPA.searchByName(query.trim());
            List<Product> activeProducts = products.stream()
                    .filter(product -> Boolean.TRUE.equals(product.getIsActive()))
                    .collect(Collectors.toList());
            List<ProductDTO> productDTOs = convertToProductDTOs(activeProducts);

            // Tìm kiếm tin tức
            List<News> news = newsJPA.searchNews(query.trim());
            List<NewsDTO> newsDTOs = convertToNewsDTOs(news);

            // Tạo kết quả tổng hợp
            SearchResultDTO result = new SearchResultDTO();
            result.setProducts(productDTOs);
            result.setNews(newsDTOs);
            result.setTotalProducts(productDTOs.size());
            result.setTotalNews(newsDTOs.size());
            result.setQuery(query.trim());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Convert danh sách Product sang ProductDTO
     */
    private List<ProductDTO> convertToProductDTOs(List<Product> products) {
        return products.stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());

            // Lấy giá từ variant đầu tiên (nếu có)
            if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
                dto.setPrice(product.getProductVariants().get(0).getPrice());
            }

            // Lấy danh sách URL hình ảnh
            if (product.getImages() != null) {
                List<String> imageUrls = product.getImages().stream()
                        .map(image -> image.getUrl())
                        .collect(Collectors.toList());
                dto.setImageUrls(imageUrls);
            }

            // Tính discount percentage (nếu có)
            if (product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
                // Lấy discount đầu tiên (có thể cần logic phức tạp hơn)
                dto.setDiscountPercentage(
                        product.getProductDiscounts().get(0).getDiscount().getPercentage()
                );
            } else {
                dto.setDiscountPercentage(0.0);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Convert danh sách News sang NewsDTO
     */
    private List<NewsDTO> convertToNewsDTOs(List<News> newsList) {
        return newsList.stream().map(news -> {
            NewsDTO dto = new NewsDTO();
            dto.setNewsId(news.getId());
            dto.setTitle(news.getTitle());
            dto.setContent(news.getContent());
            dto.setImage(news.getImage());
            dto.setCreatedAt(news.getCreatedAt());
            dto.setIsVisible(news.getIsVisible());
            //dto.setNewsComments(news.getNewsComments());
            return dto;
        }).collect(Collectors.toList());
    }
}

