package com.example.demo.services;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentDiscountStatusEmailService {

    private final JavaMailSender mailSender;

    // ✅ Gửi thông báo khi ngừng áp dụng chiết khấu
    public void sendStopDiscountEmail(String agentName, String discountDescription, String toEmail) {
        String subject = "BakeryHub - Ngừng áp dụng chiết khấu";
        String body = String.format(
                "Xin chào %s,\n\n"
                        + "Chúng tôi xin thông báo rằng chương trình chiết khấu \"%s\" dành cho đại lý của bạn đã được NGỪNG áp dụng.\n"
                        + "Mọi đơn hàng tiếp theo sẽ tính theo giá thông thường.\n\n"
                        + "Trân trọng,\nBakeryHub",
                agentName, discountDescription
        );
        sendEmail(toEmail, subject, body);
    }

    // ✅ Gửi thông báo khi áp dụng lại chiết khấu
    public void sendReapplyDiscountEmail(String agentName, String discountDescription, String toEmail) {
        String subject = "BakeryHub - Áp dụng lại chiết khấu";
        String body = String.format(
                "Xin chào %s,\n\n"
                        + "Chúng tôi xin thông báo rằng chương trình chiết khấu \"%s\" dành cho đại lý của bạn đã được ÁP DỤNG LẠI.\n"
                        + "Hãy tận dụng ưu đãi này để tăng doanh số nhé!\n\n"
                        + "Trân trọng,\nBakeryHub",
                agentName, discountDescription
        );
        sendEmail(toEmail, subject, body);
    }

    // ✅ Hàm gửi email chung
    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("📧 Email đã gửi tới: " + toEmail);
    }
}

