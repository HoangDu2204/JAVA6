package com.example.demo.jpas;

import com.example.demo.entity.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsCommentJPA extends JpaRepository<NewsComment, Integer> {

    // Lấy tất cả bình luận theo ID của News (bài viết)
    List<NewsComment> findByNews_Id(Integer newsId);

    // Trả về chỉ những bình luận có isVisible = true (đã được duyệt/bỏ ẩn)
    List<NewsComment> findByNews_IdAndIsVisibleTrue(Integer newsId);

    // Lấy bình luận theo newsId và userId
    List<NewsComment> findByNews_IdAndUser_Id(Integer newsId, Integer userId);

    // Lấy bình luận theo userId
    List<NewsComment> findByUser_Id(Integer userId);

}