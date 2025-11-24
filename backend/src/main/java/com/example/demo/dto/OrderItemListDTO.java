package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemListDTO {
    private Integer productId;
    private String productName;
    private String flavor;
    private String size;
    private String shape;
    private String origin;
    private Integer quantity;
    private BigDecimal unitPrice;        // Giá sau giảm
    private BigDecimal originalPrice;    // Giá gốc
    private Double discountPercent;      // Tỷ lệ giảm (%)
    private String productImage;
    private Integer orderItemId; // ID của mục hàng trong đơn hàng
    private Integer orderId; // ID của đơn hàng
    private String imageUrl; // Đường dẫn ảnh sản phẩm
    private Integer productVariantId;

    // NEW: Rating related fields
    private Boolean canRate = false; // Có thể đánh giá hay không
    private String ratingMessage; // Thông báo về trạng thái đánh giá
    private Boolean alreadyRated = false; // Đã đánh giá chưa
    private Integer currentRating; // Số sao đã đánh giá (nếu có)
    private String currentComment; // Nhận xét đã đánh giá (nếu có)
}
