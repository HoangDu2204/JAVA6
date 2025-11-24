package com.example.demo.services;

import com.example.demo.dto.NewsCommentDTO;
import com.example.demo.entity.News;
import com.example.demo.entity.NewsComment;
import com.example.demo.entity.User;
import com.example.demo.jpas.NewsCommentJPA;
import com.example.demo.jpas.NewsJPA;
import com.example.demo.jpas.UserJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsCommentService {

    @Autowired
    private NewsCommentJPA newsCommentJPA;

    @Autowired
    private NewsJPA newsJPA; // Cần NewsJPA để tìm News theo ID

    @Autowired
    private UserJPA userJPA; // Cần UserJPA để tìm User theo ID

    public NewsComment saveComment(NewsComment comment) {
        return newsCommentJPA.save(comment);
    }

    public Optional<NewsComment> getCommentById(Integer id) {
        return newsCommentJPA.findById(id);
    }

    public List<NewsCommentDTO> getAllCommentsDTO() {
        List<NewsComment> comments = newsCommentJPA.findAll();
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<NewsCommentDTO> getCommentsByNewsId(Integer newsId) {
        List<NewsComment> comments = newsCommentJPA.findByNews_Id(newsId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public void deleteComment(Integer id) {
        newsCommentJPA.deleteById(id);
    }

    public NewsCommentDTO convertToDTO(NewsComment comment) {
        NewsCommentDTO dto = new NewsCommentDTO();

        dto.setCommentId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setIsVisible(comment.getIsVisible());

        if (comment.getNews() != null) {
            dto.setNewsId(comment.getNews().getId());
        }

        if (comment.getUser() != null) {
            if (comment.getUser().getFullName() != null)
                dto.setUserFullName(comment.getUser().getFullName());
            if (comment.getUser().getEmail() != null)
                dto.setUserEmail(comment.getUser().getEmail());
        }

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        return dto;
    }

    // Cập nhật trạng thái duyệt / ẩn bình luận
    public void updateVisibility(Integer id, String status) {
        NewsComment comment = newsCommentJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + id));

        if ("APPROVED".equalsIgnoreCase(status)) {
            comment.setIsVisible(true);
        } else if ("HIDDEN".equalsIgnoreCase(status)) {
            comment.setIsVisible(false);
        }

        newsCommentJPA.save(comment);
    }

    // === PHƯƠNG THỨC MỚI: TẠO BÌNH LUẬN ===
    public NewsCommentDTO createComment(Integer newsId, Integer userId, String userFullName, String userEmail, String content) {
        // Tìm News và User theo ID
        News news = newsJPA.findById(newsId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + newsId));
        User user = userJPA.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Tạo đối tượng NewsComment mới
        NewsComment newComment = new NewsComment();
        newComment.setNews(news);
        newComment.setUser(user);
        newComment.setContent(content);
        newComment.setCreatedDate(LocalDateTime.now());
        newComment.setIsVisible(true); // Mặc định là hiển thị ngay lập tức

        // Lưu vào database
        NewsComment savedComment = newsCommentJPA.save(newComment);

        // Chuyển đổi sang DTO và trả về
        return convertToDTO(savedComment);
    }
    public List<NewsCommentDTO> getVisibleCommentsByNewsId(Integer newsId) {
        List<NewsComment> comments = newsCommentJPA.findByNews_IdAndIsVisibleTrue(newsId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy bình luận theo newsId và userId
    public List<NewsCommentDTO> getCommentsByNewsIdAndUserId(Integer newsId, Integer userId) {
        List<NewsComment> comments = newsCommentJPA.findByNews_IdAndUser_Id(newsId, userId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy bình luận theo userId
    public List<NewsCommentDTO> getCommentsByUserId(Integer userId) {
        List<NewsComment> comments = newsCommentJPA.findByUser_Id(userId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

}