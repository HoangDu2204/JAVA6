package com.example.demo.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentDiscountEmailService {

    private static final Logger logger = LoggerFactory.getLogger(AgentDiscountEmailService.class);

    private final JavaMailSender mailSender;

    public void sendDiscountNotification(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 BakeryHub - Cập nhật chiết khấu mới dành cho đại lý!");

            String content = """
                    <p>Kính gửi <strong>%s</strong>,</p>
                    <p>Chúng tôi xin thông báo, mức <strong>chiết khấu mới</strong> đã được cập nhật cho tài khoản đại lý của bạn.</p>
                    <p>Ưu đãi này sẽ được <strong>tự động áp dụng mức cao nhất</strong> khi bạn đặt hàng, 
                    giúp bạn tiết kiệm hơn và kinh doanh hiệu quả hơn.</p>
                    <br>
                    <p>Hãy truy cập ngay hệ thống để trải nghiệm mức chiết khấu mới và đặt hàng với giá tốt nhất!</p>
                    <br>
                    <p>Trân trọng,</p>
                    <p><i>BakeryHub </i></p>
                    """.formatted(fullName);

            helper.setText(content, true);
            mailSender.send(message);

            logger.info("✅ Email chiết khấu mới đã gửi tới: {}", toEmail);
        } catch (MessagingException e) {
            logger.error("❌ Lỗi khi gửi email chiết khấu mới tới {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
