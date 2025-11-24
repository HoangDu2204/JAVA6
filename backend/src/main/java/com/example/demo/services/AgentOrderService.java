package com.example.demo.services;

import com.example.demo.dto.AgentResponseDTO;
import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.OrderRequestDTO.OrderItemDTO;
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
public class AgentOrderService {

    @Autowired private OrderJPA orderJPA;
    @Autowired private AgentJPA agentJPA;
    @Autowired private UserJPA userJPA;
    @Autowired private ProductVariantJPA productVariantJPA;
    @Autowired private VoucherJPA voucherJPA;
    @Autowired private OrderItemJPA orderItemJPA;
    @Autowired private GHNService ghnService;
    @Autowired private HttpServletRequest request;
    @Autowired private AuthHelper authHelper;
    @Autowired
    private CartItemJPA cartItemJPA;
    @Autowired
    private CartJPA cartJPA;

    // ✅ Lấy user từ JWT
    public User getUserFromToken() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("Không lấy được user từ JWT!");
        } else {
            System.out.println("Lấy user từ JWT thành công: " + user.getUsername());
        }
        return user;
    }

    public AgentResponseDTO getAgentInfo() {
        User user = getUserFromToken();
        if (user == null) return null;

        Agent agent = agentJPA.findByUserId(user.getId()).orElse(null);
        if (agent == null) return null;

        AgentResponseDTO dto = new AgentResponseDTO();
        dto.setId(agent.getId());
//        dto.setReceiverName(agent.getReceiverName());
        dto.setAgentName(agent.getAgentName());
        dto.setPhone(agent.getPhone());
        dto.setEmail(agent.getEmail());
        dto.setAddressDetail(agent.getAddressDetail());
        dto.setProvinceId(agent.getProvinceId());
        dto.setDistrictId(agent.getDistrictId());
        dto.setWardId(agent.getWardId());
        dto.setProvinceName(agent.getProvinceName());
        dto.setDistrictName(agent.getDistrictName());
        dto.setWardName(agent.getWardName());
        dto.setIsApproved(agent.getIsApproved());

        BigDecimal agentDiscountAmount = BigDecimal.ZERO;
        if (agent.getAgentDiscounts() != null) {
            LocalDate today = LocalDate.now();
            double maxDiscount = 0.0;

            for (AgentDiscount discount : agent.getAgentDiscounts()) {
                if (Boolean.TRUE.equals(discount.getIsActive())
                        && !discount.getStartDate().isAfter(today)
                        && !discount.getEndDate().isBefore(today)) {
                    maxDiscount = Math.max(maxDiscount, discount.getDiscountPercentage());
                }
            }

            dto.setDiscountAmount((int) maxDiscount); // ✅ Gán vào DTO
        }

        return dto;
    }

    // ✅ Tạo mã đơn hàng
    private String generateCustomOrderCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(900) + 100;
        return "DH" + timestamp + random;
    }

    // ✅ Xử lý đặt hàng dành cho đại lý
//    public Map<String, Object> checkoutAgent(OrderRequestDTO dto) {
//        User user = getUserFromToken();
//        if (user == null) throw new RuntimeException("Bạn chưa đăng nhập!");
//
//        Agent agent = agentJPA.findByUserId(user.getId()).orElse(null);
//        if (agent == null) throw new RuntimeException("Không tìm thấy đại lý");
//
//        if (!Boolean.TRUE.equals(agent.getIsApproved())) {
//            throw new RuntimeException("Tài khoản đại lý của bạn chưa được duyệt");
//        }
//        // 1. Tính tổng tiền
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (OrderItemDTO itemDTO : dto.getItems()) {
//            ProductVariant variant = productVariantJPA.findById(itemDTO.getProductVariantId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
//            double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
//            double discount = 0.0;
//
//            Product product = variant.getProduct();
//            if (product != null && product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
//                discount = product.getProductDiscounts().get(0).getDiscount().getPercentage();
//            }
//
//            double discountedPrice = price * (1 - discount / 100);
//            totalAmount = totalAmount.add(BigDecimal.valueOf(discountedPrice * itemDTO.getQuantity()));
//        }
//
//        // 2. Tính chiết khấu đại lý
//        BigDecimal agentDiscountAmount = BigDecimal.ZERO;
//        if (agent.getAgentDiscounts() != null) {
//            LocalDate today = LocalDate.now();
//            double maxDiscount = 0.0;
//
//            for (AgentDiscount discount : agent.getAgentDiscounts()) {
//                if (Boolean.TRUE.equals(discount.getIsActive())
//                        && !discount.getStartDate().isAfter(today)
//                        && !discount.getEndDate().isBefore(today)) {
//                    maxDiscount = Math.max(maxDiscount, discount.getDiscountPercentage());
//                }
//            }
//
//            if (maxDiscount > 0.0) {
//                agentDiscountAmount = totalAmount.multiply(BigDecimal.valueOf(maxDiscount)).divide(BigDecimal.valueOf(100));
//            }
//        }
//
//        // 3. Tính giảm từ voucher
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
//        // 4. Tính phí ship qua GHN
//        int totalWeight = dto.getItems().stream()
//                .mapToInt(item -> {
//                    ProductVariant variant = productVariantJPA.findById(item.getProductVariantId()).orElse(null);
//                    return (variant != null && variant.getWeight() != null ? variant.getWeight() : 500) * item.getQuantity();
//                }).sum();
//
//        BigDecimal shippingFee = ghnService.calculateShippingFee(
//                agent.getDistrictId(),
//                agent.getWardId().toString(),
//                totalWeight,
//                totalAmount.intValue()
//        );
//
//        // 5. Tổng tiền cuối cùng
//        BigDecimal finalTotal = totalAmount
//                .subtract(voucherDiscountAmount)
//                .subtract(agentDiscountAmount)
//                .add(shippingFee);
//
//        // 6. Tạo đơn hàng
//        Order order = new Order();
//        order.setAgent(agent);
//        order.setUser(user);
////        order.setName(agent.getReceiverName());
//        order.setName(agent.getAgentName());
//        order.setPhone(agent.getPhone());
//        order.setAddress(agent.getAddressDetail() + ", " + agent.getWardName() + ", " +
//                agent.getDistrictName() + ", " + agent.getProvinceName());
//        order.setNote(dto.getNote());
//        order.setPaymentMethod(dto.getPaymentMethod());
//        order.setPaymentStatus(dto.getPaymentStatus());
//        order.setShippingFee(shippingFee);
//        order.setTotalAmount(totalAmount);
//        order.setDiscount(voucherDiscountAmount);
//        order.setDiscountAmount(agentDiscountAmount);
//        order.setFinalTotal(finalTotal);
//        order.setOrderDate(LocalDateTime.now());
//        order.setOrderStatus("Chờ xác nhận");
//        order.setCustomOrderCode(generateCustomOrderCode());
//        if (voucher != null) {
//            order.setVoucher(voucher);
//        }
//
//        // 7. Gắn sản phẩm
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (OrderItemDTO itemDTO : dto.getItems()) {
//            ProductVariant variant = productVariantJPA.findById(itemDTO.getProductVariantId()).orElseThrow();
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProductVariant(variant);
//            orderItem.setQuantity(itemDTO.getQuantity());
//            orderItem.setUnitPrice(BigDecimal.valueOf(variant.getPrice()));
//            orderItems.add(orderItem);
//        }
//        order.setOrderItems(orderItems);
//
//        // 8. Lưu đơn
//        Order savedOrder = orderJPA.save(order);
//
//
//// ✅ Xóa giỏ hàng nếu có (dành cho đại lý cũng xài giỏ hàng)
//        Cart cart = cartJPA.findByUser(user);
//        if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
//            cartItemJPA.deleteAll(cart.getCartItems());
//        }
//
//        // 9. Kết quả
//        Map<String, Object> result = new HashMap<>();
//        result.put("order", savedOrder);
//        result.put("totalAmount", totalAmount);
//        result.put("agentDiscountAmount", agentDiscountAmount);
//        result.put("voucherDiscountAmount", voucherDiscountAmount);
//        result.put("shippingFee", shippingFee);
//        result.put("finalTotal", finalTotal);
//
//        return result;
//    }

public Map<String, Object> checkoutAgent(OrderRequestDTO dto) {
    User user = getUserFromToken();
    if (user == null) throw new RuntimeException("Bạn chưa đăng nhập!");

    Agent agent = agentJPA.findByUserId(user.getId()).orElse(null);
    if (agent == null) throw new RuntimeException("Không tìm thấy đại lý");

    if (!Boolean.TRUE.equals(agent.getIsApproved())) {
        throw new RuntimeException("Tài khoản đại lý của bạn chưa được duyệt");
    }

    // 1. Tính tổng tiền
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (OrderItemDTO itemDTO : dto.getItems()) {
        ProductVariant variant = productVariantJPA.findById(itemDTO.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
        double discount = 0.0;

        Product product = variant.getProduct();
        if (product != null && product.getProductDiscounts() != null && !product.getProductDiscounts().isEmpty()) {
            discount = product.getProductDiscounts().get(0).getDiscount().getPercentage();
        }

        double discountedPrice = price * (1 - discount / 100);
        totalAmount = totalAmount.add(BigDecimal.valueOf(discountedPrice * itemDTO.getQuantity()));
    }

    // 2. Tính chiết khấu đại lý
    BigDecimal agentDiscountAmount = BigDecimal.ZERO;
    if (agent.getAgentDiscounts() != null) {
        LocalDate today = LocalDate.now();
        double maxDiscount = 0.0;

        for (AgentDiscount discount : agent.getAgentDiscounts()) {
            if (Boolean.TRUE.equals(discount.getIsActive())
                    && !discount.getStartDate().isAfter(today)
                    && !discount.getEndDate().isBefore(today)) {
                maxDiscount = Math.max(maxDiscount, discount.getDiscountPercentage());
            }
        }

        if (maxDiscount > 0.0) {
            agentDiscountAmount = totalAmount.multiply(BigDecimal.valueOf(maxDiscount)).divide(BigDecimal.valueOf(100));
        }
    }

    // 3. Tính giảm từ voucher
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

    // 4. Tính phí ship qua GHN
    int totalWeight = dto.getItems().stream()
            .mapToInt(item -> {
                ProductVariant variant = productVariantJPA.findById(item.getProductVariantId()).orElse(null);
                return (variant != null && variant.getWeight() != null ? variant.getWeight() : 500) * item.getQuantity();
            }).sum();

    BigDecimal shippingFee = ghnService.calculateShippingFee(
            agent.getDistrictId(),
            agent.getWardId().toString(),
            totalWeight,
            totalAmount.intValue()
    );

    // 5. Tổng tiền cuối cùng
    BigDecimal finalTotal = totalAmount
            .subtract(voucherDiscountAmount)
            .subtract(agentDiscountAmount)
            .add(shippingFee);

    // 6. Tạo đơn hàng
    Order order = new Order();
    order.setAgent(agent);
    order.setUser(user);
    order.setName(agent.getAgentName());
    order.setPhone(agent.getPhone());
    order.setAddress(agent.getAddressDetail() + ", " + agent.getWardName() + ", " +
            agent.getDistrictName() + ", " + agent.getProvinceName());
    order.setNote(dto.getNote());
    order.setPaymentMethod(dto.getPaymentMethod());
    order.setPaymentStatus(dto.getPaymentStatus());
    order.setShippingFee(shippingFee);
    order.setTotalAmount(totalAmount);
    order.setDiscount(voucherDiscountAmount);
    order.setDiscountAmount(agentDiscountAmount);
    order.setFinalTotal(finalTotal);
    order.setOrderDate(LocalDateTime.now());
    order.setOrderStatus("Chờ xác nhận");
    order.setCustomOrderCode(generateCustomOrderCode());
    if (voucher != null) {
        order.setVoucher(voucher);
    }

    // 7. Gắn sản phẩm và cập nhật tồn kho
    List<OrderItem> orderItems = new ArrayList<>();
    for (OrderItemDTO itemDTO : dto.getItems()) {
        ProductVariant variant = productVariantJPA.findById(itemDTO.getProductVariantId()).orElseThrow();

        // Cập nhật tồn kho
        int newQuantity = variant.getQuantity() - itemDTO.getQuantity();
        if (newQuantity < 0) {
            throw new RuntimeException("Sản phẩm " + variant.getId() + " không đủ số lượng tồn kho");
        }
        variant.setQuantity(newQuantity);
        productVariantJPA.save(variant);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductVariant(variant);
        orderItem.setQuantity(itemDTO.getQuantity());

     //   orderItem.setUnitPrice(BigDecimal.valueOf(variant.getPrice()));

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

    // 8. Lưu đơn
    Order savedOrder = orderJPA.save(order);

    // 9. Xóa giỏ hàng nếu có (dành cho đại lý cũng xài giỏ hàng)
    Cart cart = cartJPA.findByUser(user);
    if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
        cartItemJPA.deleteAll(cart.getCartItems());
    }

    // 10. Kết quả
    Map<String, Object> result = new HashMap<>();
    result.put("order", savedOrder);
    result.put("totalAmount", totalAmount);
    result.put("agentDiscountAmount", agentDiscountAmount);
    result.put("voucherDiscountAmount", voucherDiscountAmount);
    result.put("shippingFee", shippingFee);
    result.put("finalTotal", finalTotal);

    return result;
}

}
