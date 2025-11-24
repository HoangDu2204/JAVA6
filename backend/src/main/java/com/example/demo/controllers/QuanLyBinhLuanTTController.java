//package com.example.demo.controllers;
//
//
//import com.example.demo.dto.NewsCommentDTO;
//import com.example.demo.entity.User;
//import com.example.demo.services.NewsCommentService;
//import com.example.demo.jpas.UserJPA;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/news-comments")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//public class QuanLyBinhLuanTTController {
//
//    @Autowired
//    private NewsCommentService newsCommentService;
//
//    @Autowired
//    private UserJPA userRepository;
//
//    @Autowired
//    private HttpServletRequest request;
//
//    // Hàm lấy User từ cookie (tương tự như trong UserRestController)
//    private User getUserFromCookie() {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) return null;
//
//        for (Cookie cookie : cookies) {
//            if ("user_id".equals(cookie.getName())) {
//                try {
//                    return userRepository.findById(Integer.parseInt(cookie.getValue())).orElse(null);
//                } catch (NumberFormatException e) {
//                    return null;
//                }
//            }
//        }
//        return null;
//    }
//
//    // === PHƯƠNG THỨC MỚI: TẠO BÌNH LUẬN ===
//    @PostMapping
//    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequest request) {
//        try {
//            // Kiểm tra người dùng đã đăng nhập chưa
//            User currentUser = getUserFromCookie();
//            if (currentUser == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để bình luận");
//            }
//
//            // Kiểm tra dữ liệu đầu vào
//            if (request.getNewsId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
//                return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
//            }
//
//            // Tạo bình luận mới thông qua service
//            NewsCommentDTO newComment = newsCommentService.createComment(
//                    request.getNewsId(),
//                    currentUser.getId(),
//                    currentUser.getFullName(),
//                    currentUser.getEmail(),
//                    request.getContent()
//            );
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo bình luận");
//        }
//    }
//
//    // Lấy tất cả bình luận hoặc theo newsId
//    // Lấy tất cả bình luận hoặc chỉ bình luận hiển thị theo bài viết
//    @GetMapping
//    public ResponseEntity<List<NewsCommentDTO>> getAllComments(@RequestParam(required = false) Integer newId) {
//        try {
//            List<NewsCommentDTO> comments;
//            if (newId != null && newId > 0) {
//                // ✅ Dùng phương thức mới chỉ lấy bình luận hiển thị
//                comments = newsCommentService.getVisibleCommentsByNewsId(newId);
//            } else {
//                comments = newsCommentService.getAllCommentsDTO();
//            }
//
//            if (comments.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<>(comments, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//
//    // Xóa bình luận
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
//        try {
//            newsCommentService.deleteComment(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    // Cập nhật trạng thái hiển thị (duyệt / ẩn)
//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateVisibility(@PathVariable Integer id, @RequestBody VisibilityRequest request) {
//        try {
//            newsCommentService.updateVisibility(id, request.getStatus());
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
//        }
//    }
//
//    // Class nội bộ để nhận dữ liệu JSON từ frontend khi tạo bình luận mới
//    public static class CreateCommentRequest {
//        private Integer newsId;
//        private String content;
//
//        public Integer getNewsId() { return newsId; }
//        public void setNewsId(Integer newsId) { this.newsId = newsId; }
//
//        public String getContent() { return content; }
//        public void setContent(String content) { this.content = content; }
//    }
//
//    // Class nội bộ để nhận dữ liệu JSON: { "status": "APPROVED" / "HIDDEN" }
//    public static class VisibilityRequest {
//        private String status;
//        public String getStatus() { return status; }
//        public void setStatus(String status) { this.status = status; }
//    }
//}
package com.example.demo.controllers;

import com.example.demo.dto.NewsCommentDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.services.NewsCommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news-comments")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class QuanLyBinhLuanTTController {

    @Autowired
    private NewsCommentService newsCommentService;

    @Autowired
    private UserJPA userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthHelper authHelper;

    // ✅ Thay thế: Lấy user từ JWT thay vì cookie
    private User getCurrentUser() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("❌ Không lấy được user từ JWT token!");
        } else {
            System.out.println("✅ Lấy user từ JWT: " + user.getUsername());
        }
        return user;
    }

    // === PHƯƠNG THỨC MỚI: TẠO BÌNH LUẬN ===
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequest request) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để bình luận");
            }

            if (request.getNewsId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
            }

            NewsCommentDTO newComment = newsCommentService.createComment(
                    request.getNewsId(),
                    currentUser.getId(),
                    currentUser.getFullName(),
                    currentUser.getEmail(),
                    request.getContent()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo bình luận");
        }
    }

    // Lấy tất cả bình luận hoặc theo newsId và/hoặc userId
    @GetMapping
    public ResponseEntity<List<NewsCommentDTO>> getAllComments(
            @RequestParam(required = false) Integer newsId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyVisible) {
        try {
            List<NewsCommentDTO> comments;

            if (newsId != null && userId != null) {
                // Lọc theo cả newsId và userId
                comments = newsCommentService.getCommentsByNewsIdAndUserId(newsId, userId);
            } else if (newsId != null) {
                // Chỉ lọc theo newsId
                if (onlyVisible) {
                    // Cho trang công khai - chỉ hiển thị bình luận đã duyệt
                    comments = newsCommentService.getVisibleCommentsByNewsId(newsId);
                } else {
                    // Cho trang quản lý - hiển thị tất cả bình luận
                    comments = newsCommentService.getCommentsByNewsId(newsId);
                }
            } else if (userId != null) {
                // Chỉ lọc theo userId (hiển thị tất cả bình luận của user đó)
                comments = newsCommentService.getCommentsByUserId(userId);
            } else {
                // Lấy tất cả bình luận
                comments = newsCommentService.getAllCommentsDTO();
            }

            if (comments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Xóa bình luận
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        try {
            newsCommentService.deleteComment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cập nhật trạng thái hiển thị (duyệt / ẩn)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateVisibility(@PathVariable Integer id, @RequestBody VisibilityRequest request) {
        try {
            newsCommentService.updateVisibility(id, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }

    // Class nội bộ để nhận dữ liệu JSON từ frontend khi tạo bình luận mới
    public static class CreateCommentRequest {
        private Integer newsId;
        private String content;

        public Integer getNewsId() { return newsId; }
        public void setNewsId(Integer newsId) { this.newsId = newsId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // Class nội bộ để nhận dữ liệu JSON: { "status": "APPROVED" / "HIDDEN" }
    public static class VisibilityRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}