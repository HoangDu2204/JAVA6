package com.example.demo.controllers;

import com.example.demo.dto.OrderProductRatingDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.services.FileUploadService;
import com.example.demo.services.OrderProductRatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/ratings")
public class OrderProductRatingController {

    @Autowired
    private OrderProductRatingService danhGiaService;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserJPA userJPA;

    @Autowired
    private HttpServletRequest request;

    // Lấy user từ JWT
    private User getUserFromToken() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("❌ Không lấy được user từ token!");
        } else {
            System.out.println("✅ Đã lấy user từ token: " + user.getUsername());
        }
        return user;
    }

    // NEW: API kiểm tra trạng thái đánh giá cho một order item
//    @GetMapping("/check-status/{orderItemId}")
//    public ResponseEntity<?> checkRatingStatus(@PathVariable Integer orderItemId) {
//        try {
//            User user = getUserFromToken();
//            if (user == null) {
//                return ResponseEntity.status(401)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(createErrorResponse("Bạn chưa đăng nhập"));
//            }
//
//            if (orderItemId == null || orderItemId <= 0) {
//                return ResponseEntity.badRequest()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
//            }
//
//            Map<String, Object> ratingStatus = danhGiaService.getRatingStatus(user.getId(), orderItemId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("data", ratingStatus);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            System.err.println("Error in checkRatingStatus: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(createErrorResponse("Có lỗi xảy ra khi kiểm tra trạng thái đánh giá"));
//        }
//    }

    // NEW: API lấy đánh giá hiện có của user cho một order item
    @GetMapping("/existing/{orderItemId}")
    public ResponseEntity<?> getExistingRating(@PathVariable Integer orderItemId) {
        try {
            User user = getUserFromToken();
            if (user == null) {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Bạn chưa đăng nhập"));
            }

            if (orderItemId == null || orderItemId <= 0) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
            }

            OrderProductRatingDTO existingRating = danhGiaService.getExistingRating(user.getId(), orderItemId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", existingRating);
            response.put("hasRating", existingRating != null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in getExistingRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy thông tin đánh giá"));
        }
    }

    // IMPROVED: Enhanced endpoint for product ratings with statistics
    @GetMapping("/visible/product/{productId}")
    public ResponseEntity<?> getVisibleRatingsByProductId(@PathVariable Integer productId) {
        try {
            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
            }

            List<OrderProductRatingDTO> ratings = danhGiaService.getVisibleRatingsByProductId(productId);

            // Calculate statistics
            Map<String, Object> statistics = calculateRatingStatistics(ratings);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ratings);
            response.put("total", ratings.size());
            response.put("productId", productId);
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getApprovedRatingsByProductId: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy đánh giá sản phẩm"));
        }
    }

    // NEW: Get rating statistics for a product
    @GetMapping("/statistics/product/{productId}")
    public ResponseEntity<?> getRatingStatistics(@PathVariable Integer productId) {
        try {
            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
            }

            List<OrderProductRatingDTO> ratings = danhGiaService.getVisibleRatingsByProductId(productId);
            Map<String, Object> statistics = calculateRatingStatistics(ratings);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productId", productId);
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getRatingStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi tính toán thống kê đánh giá"));
        }
    }

    // HELPER: Calculate rating statistics
    private Map<String, Object> calculateRatingStatistics(List<OrderProductRatingDTO> ratings) {
        Map<String, Object> stats = new HashMap<>();

        if (ratings == null || ratings.isEmpty()) {
            stats.put("averageRating", 0.0);
            stats.put("totalReviews", 0);
            stats.put("ratingDistribution", Map.of(
                    "1", 0, "2", 0, "3", 0, "4", 0, "5", 0
            ));
            stats.put("ratingPercentages", Map.of(
                    "1", 0.0, "2", 0.0, "3", 0.0, "4", 0.0, "5", 0.0
            ));
            return stats;
        }

        // Calculate average rating
        double totalRating = ratings.stream()
                .mapToInt(r -> r.getRatings() != null ? r.getRatings() : 0)
                .sum();
        double averageRating = totalRating / ratings.size();

        // Calculate rating distribution
        Map<String, Integer> distribution = new HashMap<>();
        Map<String, Double> percentages = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            distribution.put(String.valueOf(i), 0);
            percentages.put(String.valueOf(i), 0.0);
        }

        for (OrderProductRatingDTO rating : ratings) {
            Integer ratingValue = rating.getRatings();
            if (ratingValue != null && ratingValue >= 1 && ratingValue <= 5) {
                String key = String.valueOf(ratingValue);
                distribution.put(key, distribution.get(key) + 1);
            }
        }

        // Calculate percentages
        int totalReviews = ratings.size();
        for (int i = 1; i <= 5; i++) {
            String key = String.valueOf(i);
            double percentage = (distribution.get(key) * 100.0) / totalReviews;
            percentages.put(key, Math.round(percentage * 10.0) / 10.0); // Round to 1 decimal place
        }

        stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0); // Round to 1 decimal place
        stats.put("totalReviews", totalReviews);
        stats.put("ratingDistribution", distribution);
        stats.put("ratingPercentages", percentages);

        return stats;
    }

    // EXISTING ENDPOINTS (keeping all existing functionality)

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllRatingsForAdmin() {
        try {
            List<OrderProductRatingDTO> ratings = danhGiaService.getAllRatingsForAdmin();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ratings);
            response.put("total", ratings.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAllRatingsForAdmin: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy danh sách đánh giá"));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredRatings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer stars) {
        try {
            System.out.println("Filter request - Status: " + status + ", UserName: " + userName + ", Stars: " + stars);

            if (stars != null && (stars < 1 || stars > 5)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Số sao phải từ 1 đến 5"));
            }

            List<OrderProductRatingDTO> ratings = danhGiaService.getFilteredRatings(status, userName, stars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ratings);
            response.put("total", ratings.size());
            response.put("filters", Map.of(
                    "status", status != null ? status : "all",
                    "userName", userName != null ? userName : "",
                    "stars", stars != null ? stars : ""
            ));

            System.out.println("Filter response - Found " + ratings.size() + " ratings");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in getFilteredRatings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi lọc đánh giá"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<String> users = danhGiaService.getAllUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("total", users.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy danh sách người dùng"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRatingStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> requestBody) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("ID đánh giá không hợp lệ"));
            }

            String status = requestBody.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Trạng thái không được để trống"));
            }

            if (!("Hiện".equals(status) || "Ẩn".equals(status))) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Trạng thái không hợp lệ: " + status + ". Chỉ chấp nhận 'Hiện' hoặc 'Ẩn'."));
            }

            boolean success = danhGiaService.updateRatingStatus(id, status);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Cập nhật trạng thái thành công");
                response.put("data", Map.of("id", id, "newStatus", status));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Không thể cập nhật trạng thái. Đánh giá có thể không tồn tại."));
            }

        } catch (Exception e) {
            System.err.println("Error in updateRatingStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRating(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("ID đánh giá không hợp lệ"));
            }

            boolean success = danhGiaService.deleteRating(id);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Xóa đánh giá thành công");
                response.put("data", Map.of("deletedId", id));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Không thể xóa đánh giá. Đánh giá có thể không tồn tại."));
            }

        } catch (Exception e) {
            System.err.println("Error in deleteRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi xóa đánh giá: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRatingById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("ID đánh giá không hợp lệ"));
            }

            OrderProductRatingDTO rating = danhGiaService.getRatingById(id);

            if (rating != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", rating);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getRatingById: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy thông tin đánh giá"));
        }
    }

    // IMPROVED: Enhanced submit rating with better error handling
    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(@RequestBody Map<String, Object> requestBody) {
        try {
            User user = getUserFromToken();
            if (user == null) {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Bạn chưa đăng nhập"));
            }

            Integer orderItemId = extractInteger(requestBody, "orderItemId");
            Integer rating = extractInteger(requestBody, "rating");
            String comment = (String) requestBody.get("comment");

            // NEW: Extract image paths from request
            @SuppressWarnings("unchecked")
            List<String> imagePaths = (List<String>) requestBody.get("imagePaths");

            if (orderItemId == null) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Thiếu thông tin sản phẩm"));
            }

            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Số sao đánh giá phải từ 1 đến 5"));
            }

            // IMPROVED: Better validation with detailed error messages
            Map<String, Object> ratingStatus = danhGiaService.getRatingStatus(user.getId(), orderItemId);
            Boolean canRate = (Boolean) ratingStatus.get("canRate");

            if (!Boolean.TRUE.equals(canRate)) {
                String message = (String) ratingStatus.getOrDefault("message", "Không thể đánh giá sản phẩm này");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse(message));
            }

            boolean success = danhGiaService.submitRatingWithImages(user.getId(), orderItemId, rating, comment, imagePaths);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Đánh giá thành công");
                response.put("data", Map.of(
                        "orderItemId", orderItemId,
                        "rating", rating,
                        "comment", comment != null ? comment : "",
                        "imageCount", imagePaths != null ? imagePaths.size() : 0
                ));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Không thể gửi đánh giá"));
            }

        } catch (Exception e) {
            System.err.println("Error in submitRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createErrorResponse("Có lỗi xảy ra khi gửi đánh giá: " + e.getMessage()));
        }
    }

    // IMPROVED: Enhanced image upload with better validation
    @PostMapping(value = "/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestParam("images") MultipartFile[] files) {
        try {
            User user = getUserFromToken();
            if (user == null) {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Bạn chưa đăng nhập"));
            }

            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Không có file nào được chọn"));
            }

            if (files.length > 5) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Chỉ được upload tối đa 5 ảnh"));
            }

            // Validate file types and sizes
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                if (!file.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(createErrorResponse("File " + file.getOriginalFilename() + " không phải là ảnh"));
                }

                if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                    return ResponseEntity.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(createErrorResponse("File " + file.getOriginalFilename() + " quá lớn (tối đa 5MB)"));
                }
            }

            List<String> imagePaths = fileUploadService.uploadMultipleImages(files);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload ảnh thành công");
            response.put("data", Map.of(
                    "imagePaths", imagePaths,
                    "count", imagePaths.size()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in uploadImages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createErrorResponse("Có lỗi xảy ra khi upload ảnh: " + e.getMessage()));
        }
    }

    // HELPER METHODS

    private Integer extractInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    //có thể đánh giá k
    @GetMapping("/can-rate/{orderItemId}")
    public ResponseEntity<?> canRateOrderItem(@PathVariable Integer orderItemId) {
        try {
            if (orderItemId == null || orderItemId <= 0) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
            }

            User user = getUserFromToken();
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("canRate", false);
                response.put("message", "Bạn chưa đăng nhập");
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            Map<String, Object> ratingStatus = danhGiaService.getRatingStatus(user.getId(), orderItemId);
            return ResponseEntity.ok(ratingStatus);

        } catch (Exception e) {
            System.err.println("Error in canRateOrderItem: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("canRate", false);
            response.put("message", "Có lỗi xảy ra khi kiểm tra trạng thái đánh giá: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @GetMapping("/order-item/{orderItemId}")
    public ResponseEntity<?> getOrderItemForRating(@PathVariable Integer orderItemId) {
        try {
            if (orderItemId == null || orderItemId <= 0) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("ID sản phẩm không hợp lệ"));
            }

            User user = getUserFromToken();
            if (user == null) {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Bạn chưa đăng nhập"));
            }

            Map<String, Object> orderItemInfo = danhGiaService.getOrderItemForRating(user.getId(), orderItemId);

            if (orderItemInfo == null) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createErrorResponse("Không tìm thấy thông tin sản phẩm hoặc bạn không có quyền truy cập"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orderItemInfo);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in getOrderItemForRating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createErrorResponse("Có lỗi xảy ra khi lấy thông tin sản phẩm: " + e.getMessage()));
        }
    }


}
