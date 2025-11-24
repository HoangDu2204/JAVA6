package com.example.demo.jpas;

import com.example.demo.entity.ContactInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, Integer> {
    Page<ContactInquiry> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrContentContainingIgnoreCase(String fullName, String email, String phone, String content, Pageable pageable);
}
