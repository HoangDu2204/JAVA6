package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác thực OTP - Đăng ký đại lý");
            helper.setText(
                    "<h3>Mã xác thực OTP của bạn là:</h3>" +
                            "<p style='font-size: 24px; font-weight: bold; color: blue'>" + otpCode + "</p>" +
                            "<p>Mã OTP sẽ hết hạn sau 5 phút và chỉ dùng được một lần.</p>",
                    true
            );

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại");
        }
    }
}
