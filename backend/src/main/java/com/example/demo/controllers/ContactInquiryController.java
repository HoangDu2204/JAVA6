package com.example.demo.controllers;

import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.dto.ContactInquiryDTO;
import com.example.demo.services.ContactInquiryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact-inquiries")
public class ContactInquiryController {

    @Autowired
    private ContactInquiryService contactInquiryService;

    @PostMapping
    public ResponseEntity<ContactInquiryDTO> createContactInquiry(@Valid  @RequestBody ContactInquiryDTO contactInquiryDTO) {
        ContactInquiryDTO createdInquiry = contactInquiryService.createContactInquiry(contactInquiryDTO);
        return ResponseEntity.ok(createdInquiry);
    }

    @PutMapping("/respond")
    public ResponseEntity<ContactInquiryDTO> respondToContactInquiry(@Valid @RequestBody AdminResponseDTO adminResponseDTO) {
        ContactInquiryDTO respondedInquiry = contactInquiryService.respondToContactInquiry(adminResponseDTO.getId(), adminResponseDTO.getAdminResponse());
        return ResponseEntity.ok(respondedInquiry);
    }

    @GetMapping
    public ResponseEntity<Page<ContactInquiryDTO>> getAllContactInquiries(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(contactInquiryService.getAllContactInquiries(keyword, pageable));
    }

}
