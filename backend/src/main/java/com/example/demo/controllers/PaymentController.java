package com.example.demo.controllers;

import com.example.demo.entity.Order;
import com.example.demo.jpas.OrderJPA;
import com.example.demo.services.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor

public class PaymentController {
    @Autowired
    private OrderJPA orderJPA;
    private final VnPayService vnPayService;

    @PostMapping("/create")
    public Map<String, String> createPayment(@RequestParam BigDecimal amount,
                                             @RequestParam String orderId,
                                             HttpServletRequest request) throws Exception {
        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderId);

        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return response;
    }
    @GetMapping("/return")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTxnRef = params.get("vnp_TxnRef");

        if ("00".equals(vnpResponseCode)) {
            //  Dùng Optional đúng cách
            Order order = orderJPA.findByCustomOrderCode(vnpTxnRef).orElse(null);

            if (order != null) {
                order.setPaymentStatus("Đã thanh toán");
                order.setOrderStatus("Chờ giao hàng");
                orderJPA.save(order);
                return ResponseEntity.ok(" Thanh toán thành công. Đơn hàng " + vnpTxnRef + " đã được cập nhật.");
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng với mã: " + vnpTxnRef);
            }
        } else {
            return ResponseEntity.ok(" Thanh toán thất bại. Mã lỗi: " + vnpResponseCode);
        }
    }


}
