package com.example.demo.services;

import com.example.demo.entity.AgentOtp;
import com.example.demo.entity.User;
import com.example.demo.jpas.AgentOtpJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AgentOtpService {

    private final AgentOtpJPA otpJPA;
    private final EmailService emailService;

    // ✅ Gửi OTP đến email người dùng
    public void sendOtp(User user) {
        AgentOtp existingOtp = otpJPA.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user).orElse(null);
        LocalDateTime now = LocalDateTime.now();

        // ❌ Nếu còn hiệu lực và gửi quá 5 lần → chặn
        if (existingOtp != null && existingOtp.getExpiresAt().isAfter(now) && existingOtp.getSendCount() >= 5) {
            throw new RuntimeException("❌ Bạn đã vượt quá số lần gửi OTP. Vui lòng đợi mã cũ hết hạn (5 phút).");
        }

        // ✅ Nếu mã đã hết hạn → đánh dấu đã dùng để reset
        if (existingOtp != null && existingOtp.getExpiresAt().isBefore(now)) {
            existingOtp.setUsed(true);
            otpJPA.save(existingOtp);
        }

        // Tạo mã OTP mới
        String otpCode = generateOtp();
        LocalDateTime expiresAt = now.plusMinutes(5);

        AgentOtp otp = new AgentOtp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setCreatedAt(now);
        otp.setExpiresAt(expiresAt);
        otp.setUsed(false);
        otp.setSendCount(existingOtp == null ? 1 : existingOtp.getSendCount() + 1);

        otpJPA.save(otp);

        // ✅ Gửi OTP đến email đã xác thực của user
        emailService.sendOtpEmail(user.getEmail(), otpCode);
    }

    // ✅ Xác thực OTP
    public void verifyOtp(User user, String otpCode) {
        AgentOtp otp = otpJPA.findByUserAndOtpCodeAndUsedFalse(user, otpCode)
                .orElseThrow(() -> new RuntimeException("❌ Mã OTP không hợp lệ."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("❌ Mã OTP đã hết hạn.");
        }

        otp.setUsed(true);
        otpJPA.save(otp);
    }

    // ✅ Sinh mã OTP ngẫu nhiên 6 số
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
