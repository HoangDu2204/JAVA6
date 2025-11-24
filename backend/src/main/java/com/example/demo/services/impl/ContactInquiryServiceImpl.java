package com.example.demo.services.impl;

import com.example.demo.dto.ContactInquiryDTO;
import com.example.demo.entity.ContactInquiry;
import com.example.demo.exceptions.ContactInquiryNotFoundException;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.jpas.ContactInquiryRepository;
import com.example.demo.services.ContactInquiryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ContactInquiryServiceImpl implements ContactInquiryService {

    @Autowired
    private ContactInquiryRepository contactInquiryRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public ContactInquiryDTO createContactInquiry(ContactInquiryDTO contactInquiryDTO) {
        if (contactInquiryDTO.getFullName() == null || contactInquiryDTO.getFullName().trim().isEmpty()) {
            throw new ValidationException("Tên đầy đủ là bắt buộc.", "fullName");
        }
        if (contactInquiryDTO.getEmail() == null || contactInquiryDTO.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email là bắt buộc.", "email");
        }
        if (contactInquiryDTO.getPhone() == null || contactInquiryDTO.getPhone().trim().isEmpty()) {
            throw new ValidationException("Số điện thoại là bắt buộc.", "phone");
        }
        if (contactInquiryDTO.getContent() == null || contactInquiryDTO.getContent().trim().isEmpty()) {
            throw new ValidationException("Nội dung liên hệ là bắt buộc.", "content");
        }

        ContactInquiry contactInquiry = new ContactInquiry();
        BeanUtils.copyProperties(contactInquiryDTO, contactInquiry);
        contactInquiry.setCreatedAt(Instant.now());
        contactInquiry.setStatus("PENDING");

        contactInquiry = contactInquiryRepository.save(contactInquiry);
        BeanUtils.copyProperties(contactInquiry, contactInquiryDTO);
        return contactInquiryDTO;
    }

    @Override
    @Transactional
    public ContactInquiryDTO respondToContactInquiry(Integer id, String adminResponse) {
        ContactInquiry contactInquiry = contactInquiryRepository.findById(id)
                .orElseThrow(() -> new ContactInquiryNotFoundException("Không tìm thấy yêu cầu liên hệ với ID: " + id));

        contactInquiry.setAdminResponse(adminResponse);
        contactInquiry.setResponseDate(Instant.now());
        contactInquiry.setStatus("RESPONDED");

        contactInquiry = contactInquiryRepository.save(contactInquiry);

        // Gửi email phản hồi
        sendResponseEmail(contactInquiry);

        ContactInquiryDTO contactInquiryDTO = new ContactInquiryDTO();
        BeanUtils.copyProperties(contactInquiry, contactInquiryDTO);
        return contactInquiryDTO;
    }

    @Override
    public Page<ContactInquiryDTO> getAllContactInquiries(String keyword, Pageable pageable) {
        Page<ContactInquiry> contactInquiriesPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            contactInquiriesPage = contactInquiryRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, keyword, keyword, pageable);
        } else {
            contactInquiriesPage = contactInquiryRepository.findAll(pageable);
        }
        return contactInquiriesPage.map(contactInquiry -> {
            ContactInquiryDTO dto = new ContactInquiryDTO();
            BeanUtils.copyProperties(contactInquiry, dto);
            return dto;
        });
    }

    private void sendResponseEmail(ContactInquiry contactInquiry) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("your-email@example.com"); // Thay thế bằng email của bạn
            helper.setTo(contactInquiry.getEmail());
            helper.setSubject("Phản hồi yêu cầu liên hệ của bạn");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy").withZone(ZoneId.systemDefault());

            String formattedCreatedAt = contactInquiry.getCreatedAt() != null ? formatter.format(contactInquiry.getCreatedAt()) : "N/A";
            String formattedResponseDate = contactInquiry.getResponseDate() != null ? formatter.format(contactInquiry.getResponseDate()) : "N/A";

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">"+ "<p>Công ty TNHH Phân phối Bánh hộp BakeryHub xin chân thành cảm ơn Quý khách đã quan tâm đến sản phẩm và dịch vụ của chúng tôi.</p>" + "<h2 style=\"color: #0056b3;\">Kính gửi Quý khách: " + contactInquiry.getFullName() + ",</h2>"+ "<p>Chúng tôi đã nhận được yêu cầu liên hệ của Quý khách và xin gửi phản hồi chi tiết như sau:</p>"+ "<div style=\"background-color: #f9f9f9; border-left: 4px solid #007bff; padding: 15px; margin-bottom: 20px;\">"+ "<h3 style=\"color: #007bff; margin-top: 0;\">Yêu cầu của Quý khách:</h3>"+ "<p><strong>Nội dung:</strong> " + contactInquiry.getContent() + "</p>"+ "<p><strong>Thời gian gửi:</strong> " + formattedCreatedAt + "</p>"+ "</div>"+ "<div style=\"background-color: #e9f7ef; border-left: 4px solid #28a745; padding: 15px; margin-bottom: 20px;\">"+ "<h3 style=\"color: #28a745; margin-top: 0;\">Phản hồi từ chúng tôi:</h3>"+ "<p><strong>Nội dung:</strong> " + contactInquiry.getAdminResponse() + "</p>"+ "<p><strong>Thời gian phản hồi:</strong> " + formattedResponseDate + "</p>"+ "</div>"+ "<p>Nếu Quý khách có bất kỳ câu hỏi nào khác, xin đừng ngần ngại liên hệ lại với chúng tôi.</p>"+ "<p>Trân trọng,<br/>"+ "<strong>Đội ngũ Hỗ trợ Khách hàng</strong></p>"+ "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Xử lý lỗi gửi email tại đây, ví dụ: log lỗi
            e.printStackTrace();
        }
    }
}
