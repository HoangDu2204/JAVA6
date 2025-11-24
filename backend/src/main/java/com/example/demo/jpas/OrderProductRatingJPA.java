package com.example.demo.jpas;

import com.example.demo.entity.OrderProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderProductRatingJPA extends JpaRepository<OrderProductRating, Integer> {

    @Query("SELECT r FROM OrderProductRating r WHERE LOWER(r.status) = LOWER('Đã duyệt')")
    @Transactional(readOnly = true)
    List<OrderProductRating> findAllApprovedRatings();

    @Query("SELECT r FROM OrderProductRating r ORDER BY r.createdAt DESC")
    @Transactional(readOnly = true)
    List<OrderProductRating> findAllRatingsForAdmin();

    // Thêm query lọc theo user name
    @Query("SELECT r FROM OrderProductRating r WHERE LOWER(r.user.fullName) LIKE LOWER(CONCAT('%', :userName, '%')) ORDER BY r.createdAt DESC")
    @Transactional(readOnly = true)
    List<OrderProductRating> findRatingsByUserName(@Param("userName") String userName);

    // Thêm query lọc theo số sao
    @Query("SELECT r FROM OrderProductRating r WHERE r.ratings = :stars ORDER BY r.createdAt DESC")
    @Transactional(readOnly = true)
    List<OrderProductRating> findRatingsByStars(@Param("stars") Integer stars);

    // Thêm query lọc kết hợp
    @Query("SELECT r FROM OrderProductRating r WHERE " +
            "(:statuses IS NULL OR r.status IN :statuses) AND " +
            "(:userName IS NULL OR LOWER(r.user.fullName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND " +
            "(:stars IS NULL OR r.ratings = :stars) " +
            "ORDER BY r.createdAt DESC")
    @Transactional(readOnly = true)
    List<OrderProductRating> findFilteredRatings(
            @Param("statuses") List<String> statuses,
            @Param("userName") String userName,
            @Param("stars") Integer stars);

    // Thêm query lấy danh sách tất cả user đã đánh giá
    @Query("SELECT DISTINCT r.user.fullName FROM OrderProductRating r WHERE r.user.fullName IS NOT NULL ORDER BY r.user.fullName")
    @Transactional(readOnly = true)
    List<String> findAllUserNames();

    @Modifying
    @Transactional
    @Query("UPDATE OrderProductRating r SET r.status = :status WHERE r.id = :id")
    int updateRatingStatus(@Param("id") Integer id, @Param("status") String status);

    @Query("SELECT COUNT(r) > 0 FROM OrderProductRating r WHERE r.id = :id")
    boolean existsById(@Param("id") Integer id);

    // Kiểm tra xem order item đã có đánh giá chưa
    @Query("SELECT COUNT(r) > 0 FROM OrderProductRating r WHERE r.orderItem.id = :orderItemId")
    boolean existsByOrderItemId(@Param("orderItemId") Integer orderItemId);
    // Lấy đánh giá theo order item ID
    @Query("SELECT r FROM OrderProductRating r WHERE r.orderItem.id = :orderItemId")
    OrderProductRating findByOrderItemId(@Param("orderItemId") Integer orderItemId);

    // Lấy đánh giá theo user và order item
    @Query("SELECT r FROM OrderProductRating r WHERE r.user.id = :userId AND r.orderItem.id = :orderItemId")
    OrderProductRating findByUserIdAndOrderItemId(@Param("userId") Integer userId, @Param("orderItemId") Integer orderItemId);

    //lấy đánh giá lên chi tiết sp
    @Query("SELECT r FROM OrderProductRating r WHERE LOWER(r.status) = LOWER(\'Hiện\') AND r.orderItem.productVariant.product.id = :productId ORDER BY r.ratingDate DESC")
    @Transactional(readOnly = true)
    List<OrderProductRating> findVisibleRatingsByProductId(@Param("productId") Integer productId);
}
