package com.example.demo.controllers;

import com.example.demo.beans.RegisterBean;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RegisterController {

    @Autowired
    private UserJPA userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterBean bean) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra trùng username/email/sdt
        if (userRepo.findByUsername(bean.getUsername()).isPresent()) {
            errors.put("username", "Tên đăng nhập đã tồn tại");
        }
        if (userRepo.existsByEmail(bean.getEmail())) {
            errors.put("email", "Email đã tồn tại");
        }
        if (userRepo.existsByPhone(bean.getPhone())) {
            errors.put("phone", "Số điện thoại đã tồn tại");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Tạo User mới
        User user = new User();
        user.setUsername(bean.getUsername().trim());
        user.setPassword(passwordEncoder.encode(bean.getPassword()));
        user.setFullName(bean.getFullName().trim());
        user.setPhone(bean.getPhone());
        user.setEmail(bean.getEmail());
        user.setRole(2); // user thường
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        userRepo.save(user);

        return ResponseEntity.ok("Đăng ký thành công");
    }
}