package com.example.demo.services;

import com.example.demo.dto.NewsCommentDTO;
import com.example.demo.dto.NewsDTO;
import com.example.demo.entity.News;
import com.example.demo.entity.NewsComment;
import com.example.demo.jpas.NewsJPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private NewsJPA tinTucJPA;
    @Autowired
    private NewsJPA newsJPA;

    public Optional<News> getNewsById(Integer id) {
        return newsJPA.findById(id);
    }

    // ✅ Trả về danh sách DTO thay vì entity
    public List<NewsDTO> getAllVisibleNewsDTO() {
        logger.info("Lấy danh sách tin tức hiển thị");
        return tinTucJPA.findAllVisible().stream()
                .map(this::convertToNewsDTO)
                .collect(Collectors.toList());
    }


    public void deleteNews(Integer id) {
        if (!tinTucJPA.existsById(id)) {
            throw new RuntimeException("Tin tức không tìm thấy với ID: " + id);
        }
        tinTucJPA.deleteById(id);
        logger.info("Đã xóa tin tức với ID: {}", id);
    }

    public News createNews(News news) {
        if (news.getCreatedAt() == null) {
            news.setCreatedAt(LocalDateTime.now());
        }
        News savedNews = tinTucJPA.save(news);
        logger.info("Đã tạo tin tức với ID: {}", savedNews.getId());
        return savedNews;
    }

    public News updateNews(Integer id, News news) {
        if (!tinTucJPA.existsById(id)) {
            throw new RuntimeException("Tin tức không tìm thấy với ID: " + id);
        }
        news.setId(id);
        if (news.getCreatedAt() == null) {
            news.setCreatedAt(LocalDateTime.now());
        }
        News updatedNews = tinTucJPA.save(news);
        logger.info("Đã cập nhật tin tức với ID: {}", id);
        return updatedNews;
    }

    public Optional<News> findById(Integer id) {
        return tinTucJPA.findById(id);
    }

    public Optional<News> findByTitle(String title) {
        return tinTucJPA.findByTitle(title);
    }

    public List<News> searchNews(String keyword) {
        return tinTucJPA.searchNews(keyword);
    }

    // 🔁 Chuyển từ Entity sang DTO
    private NewsDTO convertToNewsDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setNewsId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setImage(news.getImage());
        dto.setCreatedAt(news.getCreatedAt());
        dto.setIsVisible(news.getIsVisible());

        if (news.getNewsComments() != null) {
            List<NewsCommentDTO> commentDTOs = news.getNewsComments().stream()
                    .filter(NewsComment::getIsVisible)
                    .map(this::convertToCommentDTO)
                    .collect(Collectors.toList());
            dto.setNewsComments(commentDTOs);
        }

        return dto;
    }

    private NewsCommentDTO convertToCommentDTO(NewsComment comment) {
        NewsCommentDTO dto = new NewsCommentDTO();
        dto.setCommentId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setIsVisible(comment.getIsVisible());
        dto.setUserId(comment.getUser().getId());
        dto.setUserFullName(comment.getUser().getFullName());

        if (comment.getChildComments() != null && !comment.getChildComments().isEmpty()) {
            List<NewsCommentDTO> childDTOs = comment.getChildComments().stream()
                    .filter(NewsComment::getIsVisible)
                    .map(this::convertToCommentDTO)
                    .collect(Collectors.toList());
            dto.setChildComments(childDTOs);
        }

        return dto;
    }
}

