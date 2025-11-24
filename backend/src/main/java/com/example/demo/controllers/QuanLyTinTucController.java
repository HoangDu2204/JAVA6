package com.example.demo.controllers;


import com.example.demo.dto.NewsDTO;
import com.example.demo.entity.News;
import com.example.demo.services.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class QuanLyTinTucController {

    private static final Logger logger = LoggerFactory.getLogger(QuanLyTinTucController.class);

    @Autowired
    private NewsService tinTucService;

    private final String uploadDir = "images";


    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<?> getAllNews() {
        try {
            logger.info("Gọi phương thức getAllNews()");
            List<NewsDTO> newsList = tinTucService.getAllVisibleNewsDTO();

            logger.info("Đã lấy được {} tin tức hiển thị", newsList.size());
            if (newsList.isEmpty()) {
                logger.warn("Không có tin tức nào hiển thị");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(newsList);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách tin tức: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable("id") Integer id) {
        try {
            logger.info("Gọi phương thức getNewsById với ID: {}", id);
            return tinTucService.findById(id)
                    .map(news -> {
                        logger.info("Đã tìm thấy tin tức với ID: {}", id);
                        NewsDTO dto = new NewsDTO();
                        dto.setNewsId(news.getId());
                        dto.setTitle(news.getTitle());
                        dto.setContent(news.getContent());
                        dto.setImage(news.getImage());
                        dto.setCreatedAt(news.getCreatedAt());
                        dto.setIsVisible(news.getIsVisible());
                        return ResponseEntity.ok(dto);
                    })
                    .orElseGet(() -> {
                        logger.warn("Không tìm thấy tin tức với ID: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            logger.error("Lỗi khi lấy tin tức với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable("id") Integer id) {
        try {
            logger.info("Gọi phương thức deleteNews với ID: {}", id);
            tinTucService.deleteNews(id);
            logger.info("Đã xóa tin tức với ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            logger.error("Tin tức không tìm thấy với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Lỗi khi xóa tin tức với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createNews(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("createdAt") String createdAt,
            @RequestParam("isVisible") Boolean isVisible,
            @RequestParam("image") MultipartFile image //Bỏ required = false để bắt buộc gửi ảnh
    ) {
        try {
            logger.info("Gọi phương thức createNews với tiêu đề: {}", title);

            // ✅ Kiểm tra các trường bắt buộc
            if (title.trim().isEmpty() || content.trim().isEmpty() || createdAt.trim().isEmpty() || isVisible == null) {
                return ResponseEntity.badRequest().body("Vui lòng điền đầy đủ các trường bắt buộc.");
            }

            // ✅ Kiểm tra file ảnh
            if (image == null || image.isEmpty() || !image.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Ảnh không hợp lệ hoặc không tồn tại.");
            }

            // ✅ Parse ngày
            LocalDateTime createdAtDateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            // ✅ Lưu ảnh
            String originalFileName = StringUtils.cleanPath(image.getOriginalFilename());
            Path path = Paths.get(uploadDir, originalFileName);
            Files.createDirectories(path.getParent());

            int counter = 1;
            String extension = StringUtils.getFilenameExtension(originalFileName);
            String nameWithoutExtension = extension != null ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) : originalFileName;
            while (Files.exists(path)) {
                String newFileName = extension != null
                        ? nameWithoutExtension + "_" + counter + "." + extension
                        : nameWithoutExtension + "_" + counter;
                path = Paths.get(uploadDir, newFileName);
                counter++;
            }
            Files.write(path, image.getBytes());

            // ✅ Lưu tin tức
            News news = new News();
            news.setTitle(title.trim());
            news.setContent(content.trim());
            news.setCreatedAt(createdAtDateTime);
            news.setIsVisible(isVisible);
            news.setImage("/images/" + path.getFileName());

            News savedNews = tinTucService.createNews(news);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Định dạng ngày không hợp lệ, sử dụng YYYY-MM-DDThh:mm");
        } catch (Exception e) {
            logger.error("Lỗi khi tạo tin tức: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateNews(
            @PathVariable("id") Integer id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("createdAt") String createdAt,
            @RequestParam("isVisible") Boolean isVisible,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "imagePath", required = false) String imagePath) {
        try {
            logger.info("Gọi phương thức updateNews với ID: {}", id);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime createdAtDateTime = LocalDateTime.parse(createdAt, formatter);
            News existingNews = tinTucService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tin tức không tìm thấy với ID: " + id));

            existingNews.setTitle(title);
            existingNews.setContent(content);
            existingNews.setCreatedAt(createdAtDateTime);
            existingNews.setIsVisible(isVisible);

            if (image != null && !image.isEmpty()) {
                if (!image.getContentType().startsWith("image/")) {
                    logger.warn("File không phải hình ảnh: {}", image.getOriginalFilename());
                    return ResponseEntity.badRequest().body("Chỉ chấp nhận file hình ảnh.");
                }

                String originalFileName = StringUtils.cleanPath(image.getOriginalFilename());
                Path path = Paths.get(uploadDir, originalFileName);
                Files.createDirectories(path.getParent());

                int counter = 1;
                String extension = StringUtils.getFilenameExtension(originalFileName);
                String nameWithoutExtension = extension != null ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) : originalFileName;
                while (Files.exists(path)) {
                    String newFileName = extension != null
                            ? nameWithoutExtension + "_" + counter + "." + extension
                            : nameWithoutExtension + "_" + counter;
                    path = Paths.get(uploadDir, newFileName);
                    counter++;
                }

                Files.write(path, image.getBytes());
                existingNews.setImage("/images/" + path.getFileName().toString());
                logger.info("Đã cập nhật hình ảnh: {}", path);
            } else if (imagePath != null && !imagePath.isEmpty()) {
                existingNews.setImage(imagePath);
            }

            News updatedNews = tinTucService.updateNews(id, existingNews);
            logger.info("Đã cập nhật tin tức với ID: {}", id);
            return ResponseEntity.ok(updatedNews);
        } catch (DateTimeParseException e) {
            logger.error("Định dạng ngày không hợp lệ: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Định dạng ngày không hợp lệ, sử dụng YYYY-MM-DDThh:mm");
        } catch (IOException e) {
            logger.error("Lỗi khi tải lên hình ảnh: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên hình ảnh: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Tin tức không tìm thấy với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật tin tức với ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật tin tức: " + e.getMessage());
        }
    }
}
