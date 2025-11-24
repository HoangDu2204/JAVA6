package com.example.demo.jpas;

import com.example.demo.dto.ProductTopSellingHomeDTO;
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.demo.entity.Product;

import java.util.List;

public interface HomeJPA extends CrudRepository<Product, Integer> {

    @Query(value = """
                SELECT 
                    p.id AS id,
                    p.name AS name,
                    (
                        SELECT i.url 
                        FROM images i 
                        WHERE i.product_id = p.id 
                        ORDER BY i.id ASC 
                        LIMIT 1
                    ) AS image
                FROM products p
                WHERE p.is_active = true
                  AND (
                      SELECT AVG(r.ratings)
                      FROM order_product_ratings r
                      JOIN order_item oi ON r.order_item_id = oi.id
                      JOIN product_variants pv ON oi.product_variants_id = pv.id
                      WHERE pv.product_id = p.id
                  ) = 5
                ORDER BY (
                    SELECT MAX(r2.raiting_date)
                    FROM order_product_ratings r2
                    JOIN order_item oi2 ON r2.order_item_id = oi2.id
                    JOIN product_variants pv2 ON oi2.product_variants_id = pv2.id
                    WHERE pv2.product_id = p.id
                ) DESC
                LIMIT 4
            """, nativeQuery = true)
    List<Object[]> findTop4PerfectRatedProducts();


    @Query(value = """
                SELECT
                    p.id AS id,
                    p.name AS name,
                    (
                        SELECT i.url
                        FROM images i
                        WHERE i.product_id = p.id
                        ORDER BY i.id ASC
                        LIMIT 1
                    ) AS imageUrl,
                    (
                        SELECT MIN(pv.price)
                        FROM product_variants pv
                        WHERE pv.product_id = p.id
                    ) AS originalPrice,
                    d.percentage AS discountPercentage,
                    ROUND(
                        (
                            SELECT MIN(pv.price)
                            FROM product_variants pv
                            WHERE pv.product_id = p.id
                        ) * (1 - d.percentage / 100), 0
                    ) AS finalPrice
                FROM products p
                JOIN product_discount pd ON pd.product_id = p.id
                JOIN discounts d ON d.id = pd.discount_id
                WHERE p.is_active = TRUE
                  AND d.is_active = TRUE
                  AND NOW() BETWEEN d.start_date AND d.end_date
                ORDER BY d.percentage DESC
            """, nativeQuery = true)
    List<Object[]> findAllDiscountedProducts();


    @Query(value = """
                SELECT
                                       p.id AS id,
                                       p.name AS name,
                                       (
                                           SELECT i.url
                                           FROM images i
                                           WHERE i.product_id = p.id
                                           ORDER BY i.id ASC
                                           LIMIT 1
                                       ) AS imageUrl,
                                       (
                                           SELECT MIN(pv.price)
                                           FROM product_variants pv
                                           WHERE pv.product_id = p.id
                                       ) AS originalPrice,
                                       d.percentage AS discountPercentage,
                                       ROUND(
                                           (
                                               SELECT MIN(pv.price)
                                               FROM product_variants pv
                                               WHERE pv.product_id = p.id
                                           ) * (1 - d.percentage / 100), 0
                                       ) AS finalPrice
                                   FROM products p
                                   JOIN product_variants pv ON pv.product_id = p.id
                                   JOIN order_item oi ON oi.product_variants_id = pv.id
                                   JOIN product_discount pd ON pd.product_id = p.id
                                   JOIN discounts d ON d.id = pd.discount_id
                                   WHERE p.is_active = TRUE
                                     AND d.is_active = TRUE
                                     AND NOW() BETWEEN d.start_date AND d.end_date
                                   GROUP BY
                                       p.id,
                                       p.name,
                                       d.percentage
                                   ORDER BY SUM(oi.quantity) DESC
                                   LIMIT 5
            
            """, nativeQuery = true)
    List<Object[]> findTop5SellingProducts();

    @Query(value = """
                SELECT
                    p.id AS id,
                    p.name AS name,
                    (
                        SELECT i.url
                        FROM images i
                        WHERE i.product_id = p.id
                        ORDER BY i.id ASC
                        LIMIT 1
                    ) AS imageUrl,
                    (
                        SELECT MIN(pv.price)
                        FROM product_variants pv
                        WHERE pv.product_id = p.id
                    ) AS originalPrice,
                    d.percentage AS discountPercentage,
                    ROUND(
                        (
                            SELECT MIN(pv.price)
                            FROM product_variants pv
                            WHERE pv.product_id = p.id
                        ) * (1 - d.percentage / 100), 0
                    ) AS finalPrice
                FROM products p
                LEFT JOIN product_discount pd ON pd.product_id = p.id
                LEFT JOIN discounts d ON d.id = pd.discount_id
                    AND d.is_active = TRUE
                    AND NOW() BETWEEN d.start_date AND d.end_date
                WHERE p.is_active = TRUE
                ORDER BY p.created_date DESC
                LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTop5NewestProducts();


    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    List<Category> findAllActiveCategories();

    // Lấy sản phẩm theo category_id
    @Query(value = """
        SELECT 
            p.id,
            p.name,
            (SELECT i.url FROM images i WHERE i.product_id = p.id LIMIT 1) AS imageUrl,
            (SELECT MIN(pv.price) FROM product_variants pv WHERE pv.product_id = p.id) AS originalPrice,
            IF(d.percentage IS NOT NULL, 
                ROUND(
                    (SELECT MIN(pv.price) FROM product_variants pv WHERE pv.product_id = p.id) * (1 - d.percentage / 100), 
                    0
                ), 
                NULL
            ) AS finalPrice
        FROM products p
        LEFT JOIN product_discount pd ON pd.product_id = p.id
        LEFT JOIN discounts d ON d.id = pd.discount_id
            AND d.is_active = TRUE
            AND NOW() BETWEEN d.start_date AND d.end_date
        WHERE p.is_active = TRUE
          AND p.category_id = :categoryId
    """, nativeQuery = true)
    List<Object[]> findProductsByCategoryId(Integer categoryId);

    @Query(value = """
    SELECT n.id, n.title, n.image, n.created_at
    FROM news n
    WHERE n.is_visible = true
    ORDER BY n.created_at DESC
    LIMIT 3
""", nativeQuery = true)
    List<Object[]> findTop3LatestNews();

}
