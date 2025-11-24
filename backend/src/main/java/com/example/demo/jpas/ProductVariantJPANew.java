
package com.example.demo.jpas;

import com.example.demo.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantJPANew extends JpaRepository<ProductVariant, Integer> {
    List<ProductVariant> findByProductId(Integer productId);

    boolean existsBySizeIdAndFlavorIdAndShapeIdAndOriginIdAndProductId(Integer sizeId, Integer flavorId, Integer shapeId, Integer originId, Integer productId);

    boolean existsBySizeIdAndFlavorIdAndShapeIdAndOriginIdAndProductIdAndIdNot(Integer sizeId, Integer flavorId, Integer shapeId, Integer originId, Integer productId, Integer excludeId);

    @Query("""
    SELECT pv FROM ProductVariant pv
    WHERE (:productId IS NULL OR pv.product.id = :productId)
      AND (:weight IS NULL OR pv.weight = :weight)
      AND (:isActive IS NULL OR pv.isActive = :isActive)
      AND (
           (:minPrice IS NULL AND :maxPrice IS NULL) OR
           (:minPrice IS NULL AND pv.price <= :maxPrice) OR
           (:maxPrice IS NULL AND pv.price >= :minPrice) OR
           (pv.price BETWEEN :minPrice AND :maxPrice)
      )
    ORDER BY pv.id DESC
""")
    Page<ProductVariant> filterVariantsByProductId(
            @Param("productId") Integer productId,
            @Param("weight") Integer weight,
            @Param("isActive") Boolean isActive,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
}
