package com.example.demo.services;

import com.example.demo.dto.OrderProductRatingDTO;
import com.example.demo.dto.ReviewImageDTO;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderProductRating;
import com.example.demo.entity.ReviewImage;
import com.example.demo.entity.User;
import com.example.demo.jpas.OrderItemJPA;
import com.example.demo.jpas.OrderProductRatingJPA;
import com.example.demo.jpas.ReviewImageJPA;
import com.example.demo.jpas.UserJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderProductRatingService {
    @Autowired
    private OrderProductRatingJPA danhGiaJPA;

    @Autowired
    private OrderItemJPA orderItemJPA;

    @Autowired
    private UserJPA userJPA;

    @Autowired
    private ReviewImageJPA reviewImageJPA;

    @Autowired
    private FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public List<OrderProductRatingDTO> getAllRatingsForAdmin() {
        try {
            List<OrderProductRating> ratings = danhGiaJPA.findAllRatingsForAdmin();
            return convertToDTO(ratings);
        } catch (Exception e) {
            System.err.println("Error in getAllRatingsForAdmin: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // IMPROVED: Fixed status mapping and filtering logic
    @Transactional(readOnly = true)
    public List<OrderProductRatingDTO> getFilteredRatings(String status, String userName, Integer stars) {
        try {
            // Map status từ frontend sang backend với logic chính xác
            List<String> validStatuses = mapStatusFromFrontend(status);
            String validUserName = (userName != null && !userName.trim().isEmpty()) ? userName.trim() : null;
            Integer validStars = (stars != null && stars >= 1 && stars <= 5) ? stars : null;

            System.out.println("Filtering with - Status: " + validStatuses + ", User: " + validUserName + ", Stars: " + validStars);

            List<OrderProductRating> ratings = danhGiaJPA.findFilteredRatings(validStatuses, validUserName, validStars);
            return convertToDTO(ratings);
        } catch (Exception e) {
            System.err.println("Error in getFilteredRatings: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // FIXED: Correct status mapping from frontend to backend
    private List<String> mapStatusFromFrontend(String frontendStatus) {
        if (frontendStatus == null || frontendStatus.trim().isEmpty() || "all".equals(frontendStatus)) {
            return null; // Return all statuses
        }

        switch (frontendStatus.trim()) {
            case "visible":
                return Collections.singletonList("Hiện");
            case "hidden":
                return Collections.singletonList("Ẩn");
            default:
                return Collections.singletonList(frontendStatus.trim());
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllUsers() {
        try {
            return danhGiaJPA.findAllUserNames();
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public boolean updateRatingStatus(Integer id, String status) {
        try {
            if (!danhGiaJPA.existsById(id)) {
                System.err.println("Rating not found with id: " + id);
                return false;
            }

            // Validate status
            if (!isValidStatus(status)) {
                System.err.println("Invalid status: " + status);
                return false;
            }

            int updated = danhGiaJPA.updateRatingStatus(id, status);
            System.out.println("Updated rating " + id + " to status: " + status + ", rows affected: " + updated);
            return updated > 0;
        } catch (Exception e) {
            System.err.println("Error in updateRatingStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // NEW: Validate status values
    private boolean isValidStatus(String status) {
        return status != null && (
                "Hiện".equals(status) ||
                        "Ẩn".equals(status)
        );
    }

    @Transactional
    public boolean deleteRating(Integer id) {
        try {
            if (!danhGiaJPA.existsById(id)) {
                System.err.println("Rating not found with id: " + id);
                return false;
            }

            // Get rating to delete associated images
            OrderProductRating rating = danhGiaJPA.findById(id).orElse(null);
            if (rating != null && rating.getReviewImages() != null) {
                // Delete physical image files
                for (ReviewImage reviewImage : rating.getReviewImages()) {
                    if (reviewImage.getImages() != null) {
                        fileUploadService.deleteImage(reviewImage.getImages());
                    }
                }
            }

            danhGiaJPA.deleteById(id);
            System.out.println("Deleted rating with id: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error in deleteRating: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Transactional(readOnly = true)
    public OrderProductRatingDTO getRatingById(Integer id) {
        try {
            return danhGiaJPA.findById(id)
                    .map(this::mapToDTO)
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error in getRatingById: " + e.getMessage());
            return null;
        }
    }

    // IMPROVED: Enhanced rating submission with better validation
    @Transactional
    public boolean submitRating(Integer userId, Integer orderItemId, Integer rating, String comment) {
        return submitRatingWithImages(userId, orderItemId, rating, comment, null);
    }

    // NEW: Submit rating with images support
    @Transactional
    public boolean submitRatingWithImages(Integer userId, Integer orderItemId, Integer rating, String comment, List<String> imagePaths) {
        try {
            // Validate input parameters
            if (userId == null || orderItemId == null || rating == null) {
                System.err.println("Missing required parameters");
                return false;
            }

            if (rating < 1 || rating > 5) {
                System.err.println("Invalid rating value: " + rating);
                return false;
            }

            // Check if user exists
            User user = userJPA.findById(userId).orElse(null);
            if (user == null) {
                System.err.println("User not found: " + userId);
                return false;
            }

            // Check if order item exists
            OrderItem orderItem = orderItemJPA.findById(orderItemId).orElse(null);
            if (orderItem == null) {
                System.err.println("Order item not found: " + orderItemId);
                return false;
            }

            // Validate ownership
            if (!orderItem.getOrder().getUser().getId().equals(userId)) {
                System.err.println("Order does not belong to user");
                return false;
            }

            // Check if order is delivered
            if (!"Đã giao hàng".equals(orderItem.getOrder().getOrderStatus())) {
                System.err.println("Order not delivered yet: " + orderItem.getOrder().getOrderStatus());
                return false;
            }

            // Check if already rated
            if (danhGiaJPA.existsByOrderItemId(orderItemId)) {
                System.err.println("Order item already rated");
                return false;
            }

            // Create and save new rating
            OrderProductRating newRating = new OrderProductRating();
            newRating.setUser(user);
            newRating.setOrderItem(orderItem);
            newRating.setRatings(rating);
            newRating.setComment(comment != null ? comment.trim() : "");
            newRating.setRatingDate(LocalDateTime.now());
            newRating.setCreatedAt(LocalDateTime.now());
            newRating.setStatus("Hiện"); // Default status for new ratings

            // Save rating first to get ID
            OrderProductRating savedRating = danhGiaJPA.save(newRating);
            System.out.println("Rating saved successfully for orderItemId: " + orderItemId);

            // Save review images if provided
            if (imagePaths != null && !imagePaths.isEmpty()) {
                saveReviewImages(savedRating, imagePaths);
            }


            return true;

        } catch (Exception e) {
            System.err.println("Error in submitRatingWithImages: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // NEW: Save review images
    @Transactional
    private void saveReviewImages(OrderProductRating rating, List<String> imagePaths) {
        try {
            for (String imagePath : imagePaths) {
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    ReviewImage reviewImage = new ReviewImage();
                    reviewImage.setOrderProductRating(rating);
                    reviewImage.setImages(imagePath.trim());
                    reviewImageJPA.save(reviewImage);
                    System.out.println("Saved review image: " + imagePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving review images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // IMPROVED: Enhanced rating status check with detailed information
    @Transactional(readOnly = true)
    public Map<String, Object> getRatingStatus(Integer userId, Integer orderItemId) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (userId == null || orderItemId == null) {
                result.put("canRate", false);
                result.put("message", "Thông tin không hợp lệ");
                return result;
            }

            OrderItem orderItem = orderItemJPA.findById(orderItemId).orElse(null);
            if (orderItem == null) {
                result.put("canRate", false);
                result.put("message", "Không tìm thấy sản phẩm trong đơn hàng");
                return result;
            }

            // Check ownership
            if (!orderItem.getOrder().getUser().getId().equals(userId)) {
                result.put("canRate", false);
                result.put("message", "Bạn không có quyền đánh giá sản phẩm này");
                return result;
            }

            // Check if already rated
            if (danhGiaJPA.existsByOrderItemId(orderItemId)) {
                result.put("canRate", false);
                result.put("message", "Sản phẩm này đã được đánh giá");
                result.put("alreadyRated", true);

                // Get existing rating
                OrderProductRating existingRating = danhGiaJPA.findByOrderItemId(orderItemId);
                if (existingRating != null) {
                    result.put("currentRating", existingRating.getRatings());
                    result.put("currentComment", existingRating.getComment());
                }
                return result;
            }

            // Check if order is delivered
            if (!"Đã giao hàng".equals(orderItem.getOrder().getOrderStatus())) {
                result.put("canRate", false);
                result.put("message", "Chỉ có thể đánh giá sau khi đơn hàng được giao thành công");
                result.put("orderStatus", orderItem.getOrder().getOrderStatus());
                return result;
            }

            // All checks passed
            result.put("canRate", true);
            result.put("message", "Có thể đánh giá sản phẩm này");
            result.put("productName", orderItem.getProductVariant().getProduct().getName());
            result.put("orderStatus", orderItem.getOrder().getOrderStatus());
            return result;

        } catch (Exception e) {
            System.err.println("Error in getRatingStatus: " + e.getMessage());
            e.printStackTrace();
            result.put("canRate", false);
            result.put("message", "Có lỗi xảy ra khi kiểm tra trạng thái đánh giá");
            return result;
        }
    }

    // NEW: Get comprehensive order item info for rating
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderItemForRating(Integer userId, Integer orderItemId) {
        try {
            if (userId == null || orderItemId == null) {
                return null;
            }

            OrderItem orderItem = orderItemJPA.findById(orderItemId).orElse(null);
            if (orderItem == null) {
                return null;
            }

            // Check ownership
            if (!orderItem.getOrder().getUser().getId().equals(userId)) {
                return null;
            }

            Map<String, Object> result = new HashMap<>();

            // Basic info
            result.put("orderItemId", orderItem.getId());
            result.put("orderId", orderItem.getOrder().getId());
            result.put("productId", orderItem.getProductVariant().getProduct().getId());
            result.put("productName", orderItem.getProductVariant().getProduct().getName());

            // Product image
            if (orderItem.getProductVariant().getProduct().getImages() != null &&
                    !orderItem.getProductVariant().getProduct().getImages().isEmpty()) {
                result.put("imageUrl", orderItem.getProductVariant().getProduct().getImages().get(0).getUrl());
                result.put("productImage", orderItem.getProductVariant().getProduct().getImages().get(0).getUrl());
            }

            // Variant info
            if (orderItem.getProductVariant().getSize() != null) {
                result.put("size", orderItem.getProductVariant().getSize().getName());
            }
            if (orderItem.getProductVariant().getFlavor() != null) {
                result.put("flavor", orderItem.getProductVariant().getFlavor().getName());
            }
            if (orderItem.getProductVariant().getShape() != null) {
                result.put("shape", orderItem.getProductVariant().getShape().getName());
            }
            if (orderItem.getProductVariant().getOrigin() != null) {
                result.put("origin", orderItem.getProductVariant().getOrigin().getName());
            }

            // Order info
            result.put("quantity", orderItem.getQuantity());
            result.put("unitPrice", orderItem.getProductVariant().getPrice());
            result.put("orderStatus", orderItem.getOrder().getOrderStatus());

            // Rating status
            Map<String, Object> ratingStatus = getRatingStatus(userId, orderItemId);
            result.putAll(ratingStatus);

            return result;

        } catch (Exception e) {
            System.err.println("Error in getOrderItemForRating: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // IMPROVED: Get existing rating with better error handling
    @Transactional(readOnly = true)
    public OrderProductRatingDTO getExistingRating(Integer userId, Integer orderItemId) {
        try {
            if (userId == null || orderItemId == null) {
                return null;
            }

            OrderProductRating rating = danhGiaJPA.findByUserIdAndOrderItemId(userId, orderItemId);
            return rating != null ? mapToDTO(rating) : null;

        } catch (Exception e) {
            System.err.println("Error in getExistingRating: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // DEPRECATED: Use getRatingStatus instead
    @Transactional(readOnly = true)
    public boolean canRateOrderItem(Integer userId, Integer orderItemId) {
        Map<String, Object> status = getRatingStatus(userId, orderItemId);
        return (Boolean) status.getOrDefault("canRate", false);
    }

    // IMPROVED: Enhanced DTO conversion with better error handling
    private List<OrderProductRatingDTO> convertToDTO(List<OrderProductRating> ratings) {
        if (ratings == null) {
            return Collections.emptyList();
        }

        return ratings.stream()
                .map(this::mapToDTO)
                .filter(dto -> dto != null) // Filter out null DTOs from failed conversions
                .collect(Collectors.toList());
    }

    // IMPROVED: Enhanced DTO mapping with comprehensive error handling and full image support
    private OrderProductRatingDTO mapToDTO(OrderProductRating rating) {
        if (rating == null) return null;

        OrderProductRatingDTO dto = new OrderProductRatingDTO();
        try {
            dto.setId(rating.getId());

            // User info with null safety
            if (rating.getUser() != null && rating.getUser().getFullName() != null) {
                dto.setUserFullName(rating.getUser().getFullName());
            } else {
                dto.setUserFullName("N/A");
            }

            // Product info with null safety
            dto.setProductName(getProductName(rating));

            // Rating with validation
            dto.setRatings(rating.getRatings() != null ? rating.getRatings() : 0);

            // Comment with default
            dto.setComment(rating.getComment() != null && !rating.getComment().trim().isEmpty()
                    ? rating.getComment() : "Chưa có đánh giá");

            // Dates
            dto.setRatingDate(rating.getRatingDate());
            dto.setCreatedAt(rating.getCreatedAt());

            // Status with default
            dto.setStatus(rating.getStatus() != null ? rating.getStatus() : "Hiện");

            // ENHANCED: Review images with comprehensive mapping and validation
            if (rating.getReviewImages() != null && !rating.getReviewImages().isEmpty()) {
                List<ReviewImageDTO> imageDTOs = rating.getReviewImages().stream()
                        .filter(image -> image != null && image.getImages() != null && !image.getImages().trim().isEmpty())
                        .map(image -> {
                            ReviewImageDTO imageDTO = new ReviewImageDTO();
                            imageDTO.setReviewImageId(image.getId());
                            imageDTO.setImages(image.getImages().trim());
                            return imageDTO;
                        })
                        .collect(Collectors.toList());

                dto.setReviewImages(imageDTOs);
                System.out.println("Mapped " + imageDTOs.size() + " review images for rating ID: " + rating.getId());
            } else {
                dto.setReviewImages(Collections.emptyList());
            }

        } catch (Exception e) {
            System.err.println("Error in mapToDTO for rating ID " +
                    (rating.getId() != null ? rating.getId() : "null") + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return dto;
    }

    // IMPROVED: Enhanced product name extraction with better error handling
    private String getProductName(OrderProductRating rating) {
        try {
            if (rating == null || rating.getOrderItem() == null) {
                return "N/A";
            }

            if (rating.getOrderItem().getProductVariant() == null ||
                    rating.getOrderItem().getProductVariant().getProduct() == null) {
                return "N/A";
            }

            String productName = rating.getOrderItem().getProductVariant().getProduct().getName();
            return productName != null && !productName.trim().isEmpty() ? productName : "N/A";

        } catch (Exception e) {
            System.err.println("Error getting product name: " + e.getMessage());
            return "N/A";
        }
    }

    // ENHANCED: Get visible ratings for product detail page with full image support
    @Transactional(readOnly = true)
    public List<OrderProductRatingDTO> getVisibleRatingsByProductId(Integer productId) {
        try {
            if (productId == null) {
                return Collections.emptyList();
            }

            List<OrderProductRating> ratings = danhGiaJPA.findVisibleRatingsByProductId(productId);
            List<OrderProductRatingDTO> dtoList = convertToDTO(ratings);

            // Log for debugging
            System.out.println("Found " + ratings.size() + " visible ratings for product ID: " + productId);
            for (OrderProductRatingDTO dto : dtoList) {
                if (dto.getReviewImages() != null && !dto.getReviewImages().isEmpty()) {
                    System.out.println("Rating ID " + dto.getId() + " has " + dto.getReviewImages().size() + " images");
                }
            }

            return dtoList;
        } catch (Exception e) {
            System.err.println("Error in getVisibleRatingsByProductId: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // NEW: Get rating with full details including images for detail view
    @Transactional(readOnly = true)
    public OrderProductRatingDTO getRatingWithImages(Integer ratingId) {
        try {
            if (ratingId == null) {
                return null;
            }

            OrderProductRating rating = danhGiaJPA.findById(ratingId).orElse(null);
            if (rating == null) {
                return null;
            }

            OrderProductRatingDTO dto = mapToDTO(rating);

            // Log for debugging
            if (dto != null && dto.getReviewImages() != null) {
                System.out.println("Rating ID " + ratingId + " loaded with " + dto.getReviewImages().size() + " images");
            }

            return dto;
        } catch (Exception e) {
            System.err.println("Error in getRatingWithImages: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
