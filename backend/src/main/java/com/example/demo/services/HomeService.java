package com.example.demo.services;

import com.example.demo.dto.*;
import com.example.demo.entity.Category;
import com.example.demo.jpas.HomeJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeJPA homeJPA;

    public List<HomeProductDTO> getTop4PerfectRatedProducts() {
        List<Object[]> rawData = homeJPA.findTop4PerfectRatedProducts();
        List<HomeProductDTO> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Integer id = (Integer) row[0];
            String name = (String) row[1];
            String image = (String) row[2];
            result.add(new HomeProductDTO(id, name, image));
        }

        return result;
    }


    public List<HomeDiscountProductDTO> getAllDiscountedProducts() {
        List<Object[]> rows = homeJPA.findAllDiscountedProducts();
        List<HomeDiscountProductDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            Integer id = (Integer) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            Double originalPrice = ((Number) row[3]).doubleValue();
            Double discountPercentage = ((Number) row[4]).doubleValue();
            Double finalPrice = ((Number) row[5]).doubleValue();

            result.add(new HomeDiscountProductDTO(id, name, imageUrl, originalPrice, discountPercentage, finalPrice));
        }
        return result;
    }

    public List<ProductTopSellingHomeDTO> getTop5SellingProducts() {
        List<Object[]> rows = homeJPA.findTop5SellingProducts();
        List<ProductTopSellingHomeDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Integer id = (Integer) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            Double originalPrice = row[3] != null ? ((Number) row[3]).doubleValue() : null;
            Double discountPercentage = row[4] != null ? ((Number) row[4]).doubleValue() : null;
            Double finalPrice = row[5] != null ? ((Number) row[5]).doubleValue() : null;

            result.add(new ProductTopSellingHomeDTO(id, name, imageUrl, originalPrice, discountPercentage, finalPrice));
        }

        return result;
    }
    public List<ProductNewHomeDTO> getTop5NewestProducts() {
        List<Object[]> rows = homeJPA.findTop5NewestProducts();
        List<ProductNewHomeDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Integer id = (Integer) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            Double originalPrice = row[3] != null ? ((Number) row[3]).doubleValue() : null;
            Integer discountPercentage = row[4] != null ? ((Number) row[4]).intValue() : null;
            Double finalPrice = row[5] != null ? ((Number) row[5]).doubleValue() : null;

            result.add(new ProductNewHomeDTO(id, name, imageUrl, originalPrice, discountPercentage, finalPrice));
        }

        return result;
    }

    public List<Category> getAllCategories() {
        return homeJPA.findAllActiveCategories();
    }

    public List<HomeProductCategoryDTO> getProductsByCategory(Integer categoryId) {
        List<Object[]> rows = homeJPA.findProductsByCategoryId(categoryId);
        List<HomeProductCategoryDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Integer id = (Integer) row[0];
            String name = (String) row[1];
            String imageUrl = (String) row[2];
            Double originalPrice = row[3] != null ? ((Number) row[3]).doubleValue() : null;
            Double finalPrice = row[4] != null ? ((Number) row[4]).doubleValue() : null;

            result.add(new HomeProductCategoryDTO(id, name, imageUrl, originalPrice, finalPrice));
        }

        return result;
    }

    public List<NewsHomeDTO> getTop3LatestNews() {
        List<Object[]> rows = homeJPA.findTop3LatestNews();
        List<NewsHomeDTO> list = new ArrayList<>();

        for (Object[] row : rows) {
            NewsHomeDTO dto = new NewsHomeDTO();
            dto.setId((Integer) row[0]);
            dto.setTitle((String) row[1]);
            dto.setImage((String) row[2]);

            Object timestampObj = row[3];
            if (timestampObj instanceof Timestamp ts) {
                dto.setCreatedAt(ts.toLocalDateTime());
            }

            list.add(dto);
        }

        return list;
    }



}
