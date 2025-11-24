package com.example.demo.jpas;

import com.example.demo.entity.AgentOtp;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentOtpJPA extends JpaRepository<AgentOtp, Integer> {

    // Lấy OTP mới nhất theo user và chưa dùng
    Optional<AgentOtp> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    // Lấy theo code để xác thực
    Optional<AgentOtp> findByUserAndOtpCodeAndUsedFalse(User user, String otpCode);

    //  Cần thêm cái này để resolve được method trong ForgotPasswordOtpService
    Optional<AgentOtp> findTopByUserAndOtpCodeAndUsedFalseOrderByCreatedAtDesc(User user, String otpCode);
}
