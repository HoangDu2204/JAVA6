package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentDeactivateEmailService {

    private final JavaMailSender mailSender;

    public void sendDeactivationNotification(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Thông báo ngừng hoạt động tài khoản đại lý");

            String content = """
        <p>Kính gửi <strong>%s</strong>,</p>

        <p>Chúng tôi xin thông báo rằng tài khoản đại lý của Quý khách đã 
        <span style="color: red; font-weight: bold;">ngừng hoạt động</span> kể từ thời điểm hiện tại.</p>

        <p>Để biết thêm thông tin chi tiết hoặc giải đáp thắc mắc, 
        vui lòng liên hệ với chúng tôi qua một trong các kênh sau:</p>

        <ul>
            <li>Email: <a href="mailto:bakeryhub@gmail.com" style="color: #0066cc;">ungptpc08966@gmail.com</a></li>
            <li>Điện thoại: <a href="tel:0942508073" style="color: #0066cc;">0942508073</a></li>
        </ul>

        <p>Chúng tôi rất tiếc về sự bất tiện này và mong nhận được sự thông cảm từ Quý khách.</p>

        <br>
        <p>Trân trọng,</p>
        <p><i>BakeryHub</i></p>
        """.formatted(fullName);


            helper.setText(content, true); // Gửi HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("❌ Lỗi gửi email ngừng hoạt động đại lý: " + e.getMessage());
        }
    }
}
