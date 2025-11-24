package com.example.demo.controllers;

import com.example.demo.entity.User;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.services.AgentOtpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class AgentOtpController {

    private final AgentOtpService otpService;
    private final AuthHelper authHelper;

    // ✅ Gửi mã OTP
    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(HttpServletRequest request) {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "❌ Bạn chưa đăng nhập."));
        }

        otpService.sendOtp(user);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "✅ Mã OTP đã được gửi đến email đại lý của bạn."); // ✅ Sửa thông báo
        return ResponseEntity.ok(res);
    }

    // ✅ Xác thực mã OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            HttpServletRequest request,
            @RequestParam("otpCode") String otpCode
    ) {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "❌ Bạn chưa đăng nhập."));
        }

        otpService.verifyOtp(user, otpCode);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "✅ Xác thực OTP thành công.");
        return ResponseEntity.ok(res);
    }
}
