package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentEmailService {

    private final JavaMailSender mailSender;

    public void sendApprovedNotification(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Thông báo duyệt tài khoản đại lý");

            String content = """
                    <p>Chào %s,</p>
                    <p>Tài khoản đại lý của bạn đã được <strong>admin duyệt</strong>.</p>
                    <p>Bây giờ bạn có thể đặt hàng với chiết khấu dành riêng cho đại lý!</p>
                    <br>
                    <p>Trân trọng,</p>
                    <p><i>Hệ thống cửa hàng</i></p>
                    """.formatted(fullName);

            helper.setText(content, true); // Gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("❌ Lỗi gửi email duyệt đại lý: " + e.getMessage());
        }
    }
}
