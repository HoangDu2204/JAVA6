
package com.example.demo.services;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductDetailDTO;
import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.entity.Discount;
import com.example.demo.entity.Image;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.entity.ProductVariant;
import com.example.demo.jpas.ProductJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductJPA productJPA;

    public List<ProductDTO> getAllProductDTOs() {
        List<Product> products = productJPA.findAllOrderByIdDesc();
        List<ProductDTO> dtos = new ArrayList<>();

        for (Product p : products) {
            // Lọc các biến thể còn đang active
            List<ProductVariantDTO> variantDTOs = new ArrayList<>();
            if (p.getProductVariants() != null) {
                for (ProductVariant variant : p.getProductVariants()) {
                    if (Boolean.TRUE.equals(variant.getIsActive())) {
                        ProductVariantDTO vDto = new ProductVariantDTO();
                        vDto.setId(variant.getId());
                        vDto.setSize(variant.getSize().getName());
                        vDto.setFlavor(variant.getFlavor().getName());
                        vDto.setOrigin(variant.getOrigin().getName());
                        vDto.setShape(variant.getShape().getName());
                        vDto.setPrice(variant.getPrice());
                        vDto.setQuantity(variant.getQuantity());
                        variantDTOs.add(vDto);
                    }
                }
            }

            // Nếu sản phẩm không có biến thể active thì bỏ qua
            if (variantDTOs.isEmpty()) continue;

            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());

            // Hình ảnh
            List<String> imageUrls = new ArrayList<>();
            if (p.getImages() != null) {
                p.getImages().forEach(img -> imageUrls.add(img.getUrl()));
            }
            dto.setImageUrls(imageUrls);

            // Giảm giá (có kiểm tra thời gian hiệu lực)
            if (p.getProductDiscounts() != null && !p.getProductDiscounts().isEmpty()) {
                ProductDiscount pd = p.getProductDiscounts().get(0);
                Discount d = pd.getDiscount();
                if (d != null
                        && Boolean.TRUE.equals(d.getIsActive())
                        && d.getStartDate() != null && d.getEndDate() != null
                        && !d.getStartDate().isAfter(LocalDateTime.now())
                        && !d.getEndDate().isBefore(LocalDateTime.now())) {
                    dto.setDiscountPercentage(d.getPercentage());
                }
            }

            dto.setProductVariants(variantDTOs);
            dto.setPrice(variantDTOs.get(0).getPrice()); // Dùng biến thể đầu tiên làm giá mặc định

            dtos.add(dto);
        }

        return dtos;
    }

    public ProductDetailDTO getProductDetailById(Integer id) {
        Product p = productJPA.findById(id).orElse(null);
        if (p == null) return null;

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());

        // Category
        if (p.getCategory() != null) {
            dto.setCategory(p.getCategory().getName());
        }

        // Image URLs
        List<String> imageUrls = new ArrayList<>();
        if (p.getImages() != null) {
            for (Image img : p.getImages()) {
                imageUrls.add(img.getUrl());
            }
        }
        dto.setImageUrls(imageUrls);

        // Variants (chỉ lấy các biến thể đang active)
        List<ProductVariantDTO> variantDTOs = new ArrayList<>();
        if (p.getProductVariants() != null) {
            for (ProductVariant variant : p.getProductVariants()) {
                if (Boolean.TRUE.equals(variant.getIsActive())) {
                    ProductVariantDTO vDto = new ProductVariantDTO();
                    vDto.setId(variant.getId());
                    vDto.setSize(variant.getSize().getName());
                    vDto.setFlavor(variant.getFlavor().getName());
                    vDto.setOrigin(variant.getOrigin().getName());
                    vDto.setShape(variant.getShape().getName());
                    vDto.setPrice(variant.getPrice());
                    vDto.setQuantity(variant.getQuantity());
                    variantDTOs.add(vDto);
                }
            }
        }
        dto.setProductVariants(variantDTOs);

        // Giá mặc định nếu có biến thể
        if (!variantDTOs.isEmpty()) {
            Double price = variantDTOs.get(0).getPrice();
            dto.setOriginalPrice(price);

            double discountPercent = 0;
            if (p.getProductDiscounts() != null && !p.getProductDiscounts().isEmpty()) {
                ProductDiscount pd = p.getProductDiscounts().get(0);
                Discount d = pd.getDiscount();
                if (d != null
                        && Boolean.TRUE.equals(d.getIsActive())
                        && d.getStartDate() != null && d.getEndDate() != null
                        && !d.getStartDate().isAfter(LocalDateTime.now())
                        && !d.getEndDate().isBefore(LocalDateTime.now())) {
                    discountPercent = d.getPercentage();
                    dto.setDiscountPercentage(discountPercent);
                }
            }

            double discountAmount = price * discountPercent / 100.0;
            dto.setDiscountAmount(discountAmount);
            dto.setFinalPrice(price - discountAmount);
        }

        return dto;
    }




//    public List<ProductDTO> filterProducts(Integer categoryId, Double weight, Double minPrice, Double maxPrice, String sortBy) {
//        List<Product> products = productJPA.filterProducts(categoryId, weight, minPrice, maxPrice);
//
//        // Sắp xếp theo yêu cầu
//        if ("newest".equalsIgnoreCase(sortBy)) {
//            products.sort((a, b) -> b.getId().compareTo(a.getId())); // Mới nhất
//        } else if ("priceAsc".equalsIgnoreCase(sortBy)) {
//            products.sort((a, b) -> {
//                Double priceA = a.getProductVariants().stream().filter(ProductVariant::getIsActive).map(ProductVariant::getPrice).min(Double::compare).orElse(0.0);
//                Double priceB = b.getProductVariants().stream().filter(ProductVariant::getIsActive).map(ProductVariant::getPrice).min(Double::compare).orElse(0.0);
//                return priceA.compareTo(priceB);
//            });
//        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
//            products.sort((a, b) -> {
//                Double priceA = a.getProductVariants().stream().filter(ProductVariant::getIsActive).map(ProductVariant::getPrice).max(Double::compare).orElse(0.0);
//                Double priceB = b.getProductVariants().stream().filter(ProductVariant::getIsActive).map(ProductVariant::getPrice).max(Double::compare).orElse(0.0);
//                return priceB.compareTo(priceA);
//            });
//        }
//
//        // Chuyển sang DTO (tái sử dụng code từ getAllProductDTOs)
//        List<ProductDTO> dtos = new ArrayList<>();
//        for (Product p : products) {
//            List<ProductVariantDTO> variantDTOs = new ArrayList<>();
//            if (p.getProductVariants() != null) {
//                for (ProductVariant variant : p.getProductVariants()) {
//                    if (Boolean.TRUE.equals(variant.getIsActive())) {
//                        ProductVariantDTO vDto = new ProductVariantDTO();
//                        vDto.setId(variant.getId());
//                        vDto.setSize(variant.getSize().getName());
//                        vDto.setFlavor(variant.getFlavor().getName());
//                        vDto.setOrigin(variant.getOrigin().getName());
//                        vDto.setShape(variant.getShape().getName());
//                        vDto.setPrice(variant.getPrice());
//                        vDto.setQuantity(variant.getQuantity());
//                        variantDTOs.add(vDto);
//                    }
//                }
//            }
//            if (variantDTOs.isEmpty()) continue;
//
//            ProductDTO dto = new ProductDTO();
//            dto.setId(p.getId());
//            dto.setName(p.getName());
//
//            List<String> imageUrls = new ArrayList<>();
//            if (p.getImages() != null) {
//                p.getImages().forEach(img -> imageUrls.add(img.getUrl()));
//            }
//            dto.setImageUrls(imageUrls);
//
//            dto.setProductVariants(variantDTOs);
//            dto.setPrice(variantDTOs.get(0).getPrice());
//
//            dtos.add(dto);
//        }
//
//        return dtos;
//    }

}
