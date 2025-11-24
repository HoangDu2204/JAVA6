package com.example.demo.services;

import com.example.demo.dto.ContactInquiryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactInquiryService {
    ContactInquiryDTO createContactInquiry(ContactInquiryDTO contactInquiryDTO);
    ContactInquiryDTO respondToContactInquiry(Integer id, String adminResponse);
    Page<ContactInquiryDTO> getAllContactInquiries(String keyword, Pageable pageable);
}
