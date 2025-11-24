package com.example.demo.services;

import com.example.demo.entity.AgentOtp;
import com.example.demo.entity.User;
import com.example.demo.jpas.AgentOtpJPA;
import com.example.demo.jpas.UserJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordOtpService {

    private final UserJPA userJPA;
    private final AgentOtpJPA otpJPA;
    private final ForgotPasswordEmailService emailService;

    public void sendOtp(String email) {
        User user = userJPA.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        LocalDateTime now = LocalDateTime.now();

        AgentOtp existingOtp = otpJPA.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user).orElse(null);

        if (existingOtp != null && existingOtp.getExpiresAt().isAfter(now) && existingOtp.getSendCount() >= 5) {
            throw new RuntimeException("Bạn đã gửi OTP quá số lần cho phép. Vui lòng chờ mã cũ hết hạn.");
        }

        if (existingOtp != null && existingOtp.getExpiresAt().isBefore(now)) {
            existingOtp.setUsed(true);
            otpJPA.save(existingOtp);
        }

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
        emailService.sendForgotPasswordOtp(email, otpCode);
    }

    public void verifyOtpAndResetPassword(String email, String otpCode, String newPassword) {
        User user = userJPA.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        AgentOtp otp = otpJPA.findTopByUserAndOtpCodeAndUsedFalseOrderByCreatedAtDesc(user, otpCode)
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        otp.setUsed(true);
        otpJPA.save(otp);

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userJPA.save(user);
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
