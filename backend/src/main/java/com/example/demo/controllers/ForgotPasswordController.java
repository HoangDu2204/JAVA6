package com.example.demo.controllers;

import com.example.demo.services.ForgotPasswordOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordOtpService forgotPasswordOtpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống"));
        }

        forgotPasswordOtpService.sendOtp(email);
        return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi tới email của bạn"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu thông tin"));
        }

        forgotPasswordOtpService.verifyOtpAndResetPassword(email, otp, newPassword);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
    }
}
