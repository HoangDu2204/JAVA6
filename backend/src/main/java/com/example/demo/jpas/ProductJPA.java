package com.example.demo.jpas;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJPA extends JpaRepository<Product, Integer> {

	// Lấy toàn bộ sản phẩm, mới nhất nằm trên cùng (id giảm dần)
	@Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.id DESC")
	List<Product> findAllOrderByIdDesc();

	@Query("SELECT DISTINCT p FROM Product p " +
			"LEFT JOIN FETCH p.category " +
			"LEFT JOIN FETCH p.images " +
			"ORDER BY p.id DESC")
	List<Product> findAllWithCategoryAndImages();

	boolean existsByName(String name);


	//  Search + Filter + Pagination
	@Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.images
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:isActive IS NULL OR p.isActive = :isActive)
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY p.id DESC
    """)
	Page<Product> searchProducts(Integer categoryId, Boolean isActive, String keyword, Pageable pageable);

//user
//	@Query("""
//        SELECT p FROM Product p
//        WHERE p.category.isActive = true
//          AND (:categoryId IS NULL OR p.category.id = :categoryId)
//          AND (:weight IS NULL OR p.weight = :weight)
//          AND (:minPrice IS NULL OR p.price >= :minPrice)
//          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
//        """)
//	List<Product> filterProducts(
//			@Param("categoryId") Integer categoryId,
//			@Param("weight") Double weight,
//			@Param("minPrice") Double minPrice,
//			@Param("maxPrice") Double maxPrice
//	);
@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
List<Product> searchByName(@Param("name") String name);
	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
	Optional<Product> findByIdWithImages(@Param("id") Integer id);
}
