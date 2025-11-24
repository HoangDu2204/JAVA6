package com.example.demo.jpas;

import com.example.demo.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageJPA extends JpaRepository<ReviewImage, Integer> {

    /**
     * Tìm tất cả ảnh theo ID đánh giá
     */
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.orderProductRating.id = :ratingId")
    List<ReviewImage> findByOrderProductRatingId(@Param("ratingId") Integer ratingId);

    /**
     * Xóa tất cả ảnh theo ID đánh giá
     */
    @Query("DELETE FROM ReviewImage ri WHERE ri.orderProductRating.id = :ratingId")
    void deleteByOrderProductRatingId(@Param("ratingId") Integer ratingId);

    /**
     * Đếm số lượng ảnh theo ID đánh giá
     */
    @Query("SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.orderProductRating.id = :ratingId")
    Long countByOrderProductRatingId(@Param("ratingId") Integer ratingId);

    /**
     * Kiểm tra xem có ảnh nào với đường dẫn cụ thể không
     */
    @Query("SELECT COUNT(ri) > 0 FROM ReviewImage ri WHERE ri.images = :imagePath")
    boolean existsByImagePath(@Param("imagePath") String imagePath);

    /**
     * Tìm ảnh theo đường dẫn
     */
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.images = :imagePath")
    ReviewImage findByImagePath(@Param("imagePath") String imagePath);
}
