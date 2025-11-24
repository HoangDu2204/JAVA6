
package com.example.demo.controllers;

import com.example.demo.dto.OrderDetailListDTO;
import com.example.demo.dto.OrderListDTO;
import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.VoucherCheckDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.entity.Voucher;
import com.example.demo.jpas.OrderJPA;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jpas.VoucherJPA;
import com.example.demo.services.GHNService;
import com.example.demo.services.OrderListService;
import com.example.demo.services.OrderService;
import com.example.demo.jwt.AuthHelper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/orders")
public class OrderRestController {

    @Autowired
    private OrderJPA orderJPA;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VoucherJPA voucherJPA;
    @Autowired
    private UserJPA userJPA;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private OrderListService orderListService;
    @Autowired
    private GHNService ghnService;
    @Autowired
    private AuthHelper authHelper;

    private User getUserFromToken() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("❌ Không lấy được user từ JWT");
        } else {
            System.out.println("✅ Lấy user từ JWT thành công: " + user.getUsername());
        }
        return user;
    }

//    @GetMapping("/admin")
//    public Page<OrderListDTO> getAllOrdersForAdmin(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size
//    ) {
//        return orderListService.getAllOrdersForAdmin(page, size);
//    }
@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<OrderListDTO>> getOrdersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type // "user", "agent", hoặc null
    ) {
        Page<OrderListDTO> result = orderListService.getOrdersForAdminFiltered(page, size, status, type);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("/filter")
//    public ResponseEntity<List<OrderListDTO>> filterOrders(
//            @RequestParam String status,
//            @RequestParam(required = false) String type // "agent", "user", hoặc null
//    ) {
//        List<OrderListDTO> filtered = orderListService.filterOrders(status, type);
//        return ResponseEntity.ok(filtered);
//    }

    @GetMapping("/user/{userId}")
    public List<OrderListDTO> getOrdersByUserId(@PathVariable Integer userId) {
        return orderListService.getOrdersByUserId(userId);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody OrderRequestDTO orderRequest) {
        try {
            Map<String, Object> result = orderService.checkout(orderRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/voucher/apply")
    public ResponseEntity<?> applyVoucher(@RequestBody VoucherCheckDTO dto) {
        // ✅ Lấy user từ JWT
        User user = getUserFromToken();
        if (user == null) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Bạn chưa đăng nhập"));
        }

        // Tìm mã voucher
        Voucher voucher = voucherJPA.findByCode(dto.getVoucherCode());
        if (voucher == null) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Mã giảm giá không tồn tại"));
        }

        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Mã giảm giá đã bị vô hiệu hóa"));
        }

        if (voucher.getEndDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Mã giảm giá đã hết hạn"));
        }

        if (voucher.getQuantity() <= 0) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Mã giảm giá đã hết lượt sử dụng"));
        }

        if (dto.getTotalAmount().compareTo(voucher.getMinOrderAmount()) < 0) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá"));
        }

        // Kiểm tra nếu user đã từng dùng voucher này
        boolean alreadyUsed = orderJPA.existsByUserIdAndVoucherId(user.getId(), voucher.getId());
        if (alreadyUsed) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Bạn đã sử dụng mã giảm giá này rồi"));
        }

        // OK, tính số tiền được giảm
        BigDecimal calculated = dto.getTotalAmount()
                .multiply(voucher.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = calculated.min(voucher.getMaxDiscountAmount());

        return ResponseEntity.ok(Map.of("discountAmount", discountAmount));
    }

    @PostMapping("/shipping-fee")
    public ResponseEntity<?> getShippingFee(@RequestBody Map<String, Object> payload) {
        try {
            Number toDistrictIdRaw = (Number) payload.get("toDistrictId");
            Object wardCodeObj = payload.get("toWardCode");
            String toWardCode = wardCodeObj == null ? null : wardCodeObj.toString();
            Number weightRaw = (Number) payload.get("weight");
            Number insuranceValueRaw = (Number) payload.get("insuranceValue");

            if (toDistrictIdRaw == null || toWardCode == null || weightRaw == null || insuranceValueRaw == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin tính phí"));
            }

            int toDistrictId = toDistrictIdRaw.intValue();
            int weight = weightRaw.intValue();
            int insuranceValue = insuranceValueRaw.intValue();

            BigDecimal shippingFee = ghnService.calculateShippingFee(toDistrictId, toWardCode, weight, insuranceValue);

            return ResponseEntity.ok(Map.of("shippingFee", shippingFee));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi khi tính phí: " + e.getMessage()));
        }
    }

}
