package com.example.demo.services;


import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderEmailService {

    private final JavaMailSender mailSender;

    public void sendCancelApprovedEmail(User user, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Xác nhận hủy đơn hàng #" + order.getId());

            String content = "<h3>Xin chào " + user.getUsername() + ",</h3>"
                    + "<p>Yêu cầu hủy đơn hàng <strong>#" + order.getId() + "</strong> của bạn đã được "
                    + "<span style='color:green; font-weight:bold;'>duyệt thành công</span>.</p>"
                    + "<p>Trạng thái đơn hàng hiện tại: <strong>" + order.getOrderStatus() + "</strong></p>"
                    + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại");
        }
    }

    public void sendCancelRejectedEmail(User user, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Từ chối yêu cầu hủy đơn hàng #" + order.getId());

            String content = "<h3>Xin chào " + user.getUsername() + ",</h3>"
                    + "<p>Yêu cầu hủy đơn hàng <strong>#" + order.getId() + "</strong> của bạn đã bị "
                    + "<span style='color:red; font-weight:bold;'>từ chối</span>.</p>"
                    + "<p>Trạng thái đơn hàng vẫn là: <strong>" + order.getOrderStatus() + "</strong></p>"
                    + "<p>Nếu có thắc mắc, vui lòng liên hệ bộ phận hỗ trợ khách hàng.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại");
        }
    }
}
