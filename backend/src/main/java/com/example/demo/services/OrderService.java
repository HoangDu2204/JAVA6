
package com.example.demo.services;

import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.entity.*;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.jpas.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ProductVariantJPA productVariantJPA;
    @Autowired
    private CartJPA cartJPA;

    @Autowired
    private CartItemJPA cartItemJPA;

    @Autowired
    private OrderJPA orderJPA;

    @Autowired
    private VoucherJPA voucherJPA;

    @Autowired
    private GHNService ghnService;

    // ✅ Lấy user từ JWT
    public User getUserFromToken() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("❌ Không lấy được user từ JWT!");
        } else {
            System.out.println("✅ Lấy user từ JWT thành công: " + user.getUsername());
        }
        return user;
    }

    private String generateCustomOrderCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(900) + 100;
        return "DH" + timestamp + random;
    }

//    public Map<String, Object> checkout(OrderRequestDTO dto) {
//        User user = getUserFromToken();
//        if (user == null) throw new RuntimeException("Bạn chưa đăng nhập!");
//
//        Cart cart = cartJPA.findByUser(user);
//        if (cart == null || cart.getCartItems().isEmpty()) {
//            throw new RuntimeException("Giỏ hàng trống");
//        }
//
//        // 1. Tính tổng tiền sản phẩm
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (CartItem item : cart.getCartItems()) {
//            ProductVariant variant = item.getProductVariant();
//            Product product = variant.getProduct();
//
//            double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
//            double discount = 0.0;
//
//            if (product != null && product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
//                discount = product.getProductDiscounts().get(0).getDiscount().getPercentage();
//            }
//
//            double discountedPrice = price * (1 - discount / 100);
//            totalAmount = totalAmount.add(BigDecimal.valueOf(discountedPrice * item.getQuantity()));
//        }
//
//        // 2. Giảm từ voucher
//        BigDecimal voucherDiscountAmount = BigDecimal.ZERO;
//        Voucher voucher = null;
//        if (dto.getVoucherCode() != null && !dto.getVoucherCode().isEmpty()) {
//            voucher = voucherJPA.findByCode(dto.getVoucherCode());
//            if (voucher != null && Boolean.TRUE.equals(voucher.getIsActive())
//                    && voucher.getEndDate().isAfter(LocalDateTime.now())
//                    && voucher.getQuantity() > 0
//                    && totalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
//                BigDecimal calculated = totalAmount.multiply(voucher.getDiscountPercent())
//                        .divide(BigDecimal.valueOf(100));
//                voucherDiscountAmount = calculated.min(voucher.getMaxDiscountAmount());
//                voucher.setQuantity(voucher.getQuantity() - 1);
//                voucherJPA.save(voucher);
//            }
//        }
//
//        // 3. Tính phí ship (GHN)
//        int totalWeight = cart.getCartItems().stream()
//                .mapToInt(item -> {
//                    ProductVariant variant = item.getProductVariant();
//                    return (variant.getWeight() != null ? variant.getWeight() : 500) * item.getQuantity();
//                })
//                .sum();
//
//        BigDecimal shippingFee = ghnService.calculateShippingFee(
//                dto.getToDistrictId(),
//                dto.getToWardCode(),
//                totalWeight,
//                totalAmount.intValue()
//        );
//
//        // 4. Tổng cuối cùng
//        BigDecimal finalTotal = totalAmount
//                .subtract(voucherDiscountAmount)
//                .add(shippingFee);
//
//        // 5. Tạo đơn hàng
//        Order order = new Order();
//        order.setUser(user);
//        order.setName(dto.getName());
//        order.setPhone(dto.getPhone());
//        order.setAddress(dto.getAddress());
//        order.setNote(dto.getNote());
//        order.setPaymentMethod(dto.getPaymentMethod());
//        order.setPaymentStatus(dto.getPaymentStatus());
//        order.setShippingFee(shippingFee);
//        order.setTotalAmount(totalAmount);
//        order.setDiscount(voucherDiscountAmount);
//        order.setDiscountAmount(voucherDiscountAmount); // Giảm giá chung
//        order.setFinalTotal(finalTotal);
//        order.setOrderDate(LocalDateTime.now());
//        order.setOrderStatus("Chờ xác nhận");
//        order.setCustomOrderCode(generateCustomOrderCode());
//
//        if (voucher != null) {
//            order.setVoucher(voucher);
//        }
//
//        // 6. Tạo order items
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (CartItem cartItem : cart.getCartItems()) {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProductVariant(cartItem.getProductVariant());
//            orderItem.setQuantity(cartItem.getQuantity());
//            orderItem.setUnitPrice(BigDecimal.valueOf(cartItem.getProductVariant().getPrice()));
//            orderItems.add(orderItem);
//        }
//        order.setOrderItems(orderItems);
//
//        // 7. Lưu đơn hàng
//        Order savedOrder = orderJPA.save(order);
//        cartItemJPA.deleteAll(cart.getCartItems());
//
//        // 8. Trả kết quả
//        Map<String, Object> result = new HashMap<>();
//        result.put("order", savedOrder);
//        result.put("totalAmount", totalAmount);
//        result.put("voucherDiscountAmount", voucherDiscountAmount);
//        result.put("shippingFee", shippingFee);
//        result.put("finalTotal", finalTotal);
//
//        return result;
//    }

    public Map<String, Object> checkout(OrderRequestDTO dto) {
        User user = getUserFromToken();
        if (user == null) throw new RuntimeException("Bạn chưa đăng nhập!");

        Cart cart = cartJPA.findByUser(user);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // 1. Tính tổng tiền sản phẩm
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();

            double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
            double discount = 0.0;

            if (product != null && product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
                discount = product.getProductDiscounts().get(0).getDiscount().getPercentage();
            }

            double discountedPrice = price * (1 - discount / 100);
            totalAmount = totalAmount.add(BigDecimal.valueOf(discountedPrice * item.getQuantity()));
        }

        // 2. Giảm từ voucher
        BigDecimal voucherDiscountAmount = BigDecimal.ZERO;
        Voucher voucher = null;
        if (dto.getVoucherCode() != null && !dto.getVoucherCode().isEmpty()) {
            voucher = voucherJPA.findByCode(dto.getVoucherCode());
            if (voucher != null && Boolean.TRUE.equals(voucher.getIsActive())
                    && voucher.getEndDate().isAfter(LocalDateTime.now())
                    && voucher.getQuantity() > 0
                    && totalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
                BigDecimal calculated = totalAmount.multiply(voucher.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100));
                voucherDiscountAmount = calculated.min(voucher.getMaxDiscountAmount());
                voucher.setQuantity(voucher.getQuantity() - 1);
                voucherJPA.save(voucher);
            }
        }

        // 3. Tính phí ship (GHN)
        int totalWeight = cart.getCartItems().stream()
                .mapToInt(item -> {
                    ProductVariant variant = item.getProductVariant();
                    return (variant.getWeight() != null ? variant.getWeight() : 500) * item.getQuantity();
                })
                .sum();

        BigDecimal shippingFee = ghnService.calculateShippingFee(
                dto.getToDistrictId(),
                dto.getToWardCode(),
                totalWeight,
                totalAmount.intValue()
        );

        // 4. Tổng cuối cùng
        BigDecimal finalTotal = totalAmount
                .subtract(voucherDiscountAmount)
                .add(shippingFee);

        // 5. Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setName(dto.getName());
        order.setPhone(dto.getPhone());
        order.setAddress(dto.getAddress());
        order.setNote(dto.getNote());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);
        order.setDiscount(voucherDiscountAmount);
        order.setDiscountAmount(voucherDiscountAmount); // Giảm giá chung
        order.setFinalTotal(finalTotal);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("Chờ xác nhận");
        order.setCustomOrderCode(generateCustomOrderCode());

        if (voucher != null) {
            order.setVoucher(voucher);
        }

        // 6. Tạo order items & cập nhật tồn kho biến thể
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();

            // Giảm tồn kho
            int newQuantity = variant.getQuantity() - cartItem.getQuantity();
            if (newQuantity < 0) {
                throw new RuntimeException("Sản phẩm " + variant.getId() + " không đủ số lượng tồn kho");
            }
            variant.setQuantity(newQuantity);
            productVariantJPA.save(variant);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
           // orderItem.setUnitPrice(BigDecimal.valueOf(variant.getPrice()));


            double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
            double discount = 0.0;
            Product product = variant.getProduct();

            if (product != null && product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
                for (ProductDiscount pd : product.getProductDiscounts()) {
                    Discount discountObj = pd.getDiscount();
                    if (Boolean.TRUE.equals(discountObj.getIsActive())
                            && !discountObj.getStartDate().isAfter(LocalDateTime.now())
                            && !discountObj.getEndDate().isBefore(LocalDateTime.now())) {
                        discount = discountObj.getPercentage();
                        break;
                    }
                }
            }

            double discountedPrice = price * (1 - discount / 100);
            orderItem.setUnitPrice(BigDecimal.valueOf(discountedPrice));



            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // 7. Lưu đơn hàng
        Order savedOrder = orderJPA.save(order);

        // 8. Xóa giỏ hàng của user
        cartItemJPA.deleteAll(cart.getCartItems());

        // 9. Trả kết quả
        Map<String, Object> result = new HashMap<>();
        result.put("order", savedOrder);
        result.put("totalAmount", totalAmount);
        result.put("voucherDiscountAmount", voucherDiscountAmount);
        result.put("shippingFee", shippingFee);
        result.put("finalTotal", finalTotal);

        return result;
    }

}
