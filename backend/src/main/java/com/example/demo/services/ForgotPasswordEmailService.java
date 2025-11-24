package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordEmailService {

    private final JavaMailSender mailSender;

    public void sendForgotPasswordOtp(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔐 Mã OTP - Khôi phục mật khẩu");
            helper.setText(
                    "<h3>Mã OTP để đặt lại mật khẩu của bạn là:</h3>" +
                            "<p style='font-size: 24px; font-weight: bold; color: red'>" + otpCode + "</p>" +
                            "<p>Mã có hiệu lực trong vòng <b>5 phút</b>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>",
                    true
            );

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("❌ Gửi email khôi phục mật khẩu thất bại", e);
        }
    }
}
