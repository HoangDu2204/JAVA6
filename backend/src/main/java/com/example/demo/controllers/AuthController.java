//
//package com.example.demo.controllers;
//
//import com.example.demo.dto.LoginDTO;
//import com.example.demo.entity.User;
//import com.example.demo.jpas.UserJPA;
//import com.example.demo.jwt.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:5173") // Cho phép Vue.js truy cập
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final UserJPA userJPA;
//    private final JwtUtil jwtUtil;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
//        Optional<User> optionalUser = userJPA.findByUsername(loginDTO.getUsername());
//        if (optionalUser.isEmpty()) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "Tài khoản không tồn tại");
//            return ResponseEntity.status(401).body(result);
//        }
//
//        User user = optionalUser.get();
//
//        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "Sai mật khẩu");
//            return ResponseEntity.status(401).body(result);
//        }
//
//        // Gọi đúng hàm generateToken với username và userId
//        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("success", true);
//        result.put("token", token);
//        result.put("role", user.getRole()); // ví dụ: 1 là admin, 2 là user
//
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/logout")
//    public Map<String, String> logout() {
//        Map<String, String> res = new HashMap<>();
//        res.put("message", "Đăng xuất thành công (JWT sẽ hết hạn ở phía client)");
//        return res;
//    }
//}
//
package com.example.demo.controllers;

import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserJPA userJPA;
    private final JwtUtil jwtUtil; // ✅ Đã dùng đúng JwtUtil
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Optional<User> optionalUser = userJPA.findByUsername(loginDTO.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Tài khoản không tồn tại"
            ));
        }
        User user = optionalUser.get();
        // ✅ Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Sai mật khẩu"
            ));
        }
        // ❗ Kiểm tra nếu là người dùng thường và bị khóa → từ chối đăng nhập
        if (user.getRole() == 2 && !user.getIsActive()) {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên."
            ));
        }


        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Sai mật khẩu"
            ));
        }

        // ✅ Tạo token chứa userId + username + roles
        String token = jwtUtil.generateToken(user);

        // ✅ Trả response cho FE
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("role", user.getRole());       // dạng số nguyên (1: admin, 2: user)
        result.put("username", user.getUsername());
        result.put("fullName", user.getFullName());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Đăng xuất thành công (JWT sẽ hết hạn ở phía client)");
    }
}
