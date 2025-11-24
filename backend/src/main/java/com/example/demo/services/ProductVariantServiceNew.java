package com.example.demo.services;

import com.example.demo.dto.ProductVariantCreateDTONew;
import com.example.demo.dto.ProductVariantDTONew;
import com.example.demo.entity.*;
import com.example.demo.jpas.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceNew {

    private final ProductVariantJPANew variantRepo;
    private final ProductJPA productRepo;
    private final SizeJPANew sizeRepo;
    private final FlavorJPANew flavorRepo;
    private final ShapeJPANew shapeRepo;
    private final OriginJPANew originRepo;
    private final OrderItemJPA orderItemJPA; //  Thêm dòng này để kiểm tra đơn hàng

    public ProductVariant create(ProductVariantCreateDTONew dto) {
        boolean exists = variantRepo.existsBySizeIdAndFlavorIdAndShapeIdAndOriginIdAndProductId(
                dto.getSizeId(), dto.getFlavorId(), dto.getShapeId(), dto.getOriginId(), dto.getProductId());
        if (exists) {
            throw new RuntimeException("Biến thể đã tồn tại với tổ hợp Size, Hương vị, Hình dạng, Xuất xứ này");
        }

        Product product = productRepo.findById(dto.getProductId()).orElseThrow();
        Size size = sizeRepo.findById(dto.getSizeId()).orElseThrow();
        Flavor flavor = flavorRepo.findById(dto.getFlavorId()).orElseThrow();
        Shape shape = shapeRepo.findById(dto.getShapeId()).orElseThrow();
        Origin origin = originRepo.findById(dto.getOriginId()).orElseThrow();

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSize(size);
        variant.setFlavor(flavor);
        variant.setShape(shape);
        variant.setOrigin(origin);
        variant.setPrice(dto.getPrice());
        variant.setQuantity(dto.getQuantity());
        variant.setWeight(dto.getWeight());
        variant.setIsActive(dto.getIsActive());

        return variantRepo.save(variant);
    }

    public ProductVariant update(Integer id, ProductVariantCreateDTONew dto) {
        //  NGHIỆP VỤ: Nếu biến thể đã nằm trong đơn hàng thì KHÔNG cho update
        if (orderItemJPA.existsByProductVariant_Id(id)) {
            throw new RuntimeException("Không thể chỉnh sửa biến thể đã tồn tại trong đơn hàng.");
        }

        boolean exists = variantRepo.existsBySizeIdAndFlavorIdAndShapeIdAndOriginIdAndProductIdAndIdNot(
                dto.getSizeId(), dto.getFlavorId(), dto.getShapeId(), dto.getOriginId(), dto.getProductId(), id);
        if (exists) {
            throw new RuntimeException("Biến thể đã tồn tại với tổ hợp Size, Hương vị, Hình dạng, Xuất xứ này");
        }

        ProductVariant variant = variantRepo.findById(id).orElseThrow();
        variant.setSize(sizeRepo.findById(dto.getSizeId()).orElseThrow());
        variant.setFlavor(flavorRepo.findById(dto.getFlavorId()).orElseThrow());
        variant.setShape(shapeRepo.findById(dto.getShapeId()).orElseThrow());
        variant.setOrigin(originRepo.findById(dto.getOriginId()).orElseThrow());
        variant.setPrice(dto.getPrice());
        variant.setQuantity(dto.getQuantity());
        variant.setWeight(dto.getWeight());
        variant.setIsActive(dto.getIsActive());
        return variantRepo.save(variant);
    }

    public void delete(Integer id) {
        //  NGHIỆP VỤ: Nếu biến thể đã nằm trong đơn hàng thì KHÔNG cho xóa
        if (orderItemJPA.existsByProductVariant_Id(id)) {
            throw new RuntimeException("Không thể xóa biến thể đã tồn tại trong đơn hàng.");
        }

        ProductVariant variant = variantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        variant.setIsActive(false);
        variantRepo.save(variant);
    }


    public ProductVariant toggleActiveStatus(Integer id) {
        ProductVariant variant = variantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        // Nếu biến thể đã tồn tại trong đơn hàng thì không được ẩn (isActive=false)
        if (!variant.getIsActive() && orderItemJPA.existsByProductVariant_Id(id)) {
            throw new RuntimeException("Không thể ẩn biến thể đã tồn tại trong đơn hàng.");
        }

        variant.setIsActive(!variant.getIsActive());
        return variantRepo.save(variant);
    }

//    public List<ProductVariantDTONew> getByProductId(Integer productId) {
//        List<ProductVariant> variants = variantRepo.findByProductId(productId);
//        List<ProductVariantDTONew> result = new ArrayList<>();
//        for (ProductVariant v : variants) {
//            ProductVariantDTONew dto = new ProductVariantDTONew();
//            dto.setId(v.getId());
//            dto.setProductName(v.getProduct().getName());
//            dto.setSizeName(v.getSize().getName());
//            dto.setFlavorName(v.getFlavor().getName());
//            dto.setShapeName(v.getShape().getName());
//            dto.setOriginName(v.getOrigin().getName());
//            dto.setPrice(v.getPrice());
//            dto.setQuantity(v.getQuantity());
//            dto.setWeight(v.getWeight());
//            dto.setIsActive(v.getIsActive());
//            result.add(dto);
//        }
//        return result;
//    }
public List<ProductVariantCreateDTONew> getByProductId(Integer productId) {
    List<ProductVariant> variants = variantRepo.findByProductId(productId);
    List<ProductVariantCreateDTONew> result = new ArrayList<>();
    for (ProductVariant v : variants) {
        ProductVariantCreateDTONew dto = new ProductVariantCreateDTONew();

        dto.setId(v.getId());
        dto.setProductId(productId);
        dto.setSizeId(v.getSize().getId());
        dto.setFlavorId(v.getFlavor().getId());
        dto.setShapeId(v.getShape().getId());
        dto.setOriginId(v.getOrigin().getId());
        dto.setPrice(v.getPrice());
        dto.setQuantity(v.getQuantity());
        dto.setWeight(v.getWeight());
        dto.setIsActive(v.getIsActive());

        // Thêm tên để hiển thị ở bảng Vue
        dto.setSizeName(v.getSize().getName());
        dto.setFlavorName(v.getFlavor().getName());
        dto.setShapeName(v.getShape().getName());
        dto.setOriginName(v.getOrigin().getName());

        result.add(dto);
    }
    return result;
}


    // Method lọc có phân trang và filter theo productId, weight, isActive, price
    public Page<ProductVariant> filterVariants(
            Integer productId,
            Integer weight,
            Boolean isActive,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    ) {
        return variantRepo.filterVariantsByProductId(productId, weight, isActive, minPrice, maxPrice, pageable);
    }

}
