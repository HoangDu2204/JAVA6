//package com.example.demo.controllers;
//
//import com.example.demo.dto.ChangePasswordDTO;
//import com.example.demo.dto.UserProfileDTO;
//import com.example.demo.entity.User;
//import com.example.demo.jpas.UserJPA;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//@RequiredArgsConstructor
//public class UserRestController {
//
//    private final UserJPA userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final HttpServletRequest request;
//
//    //  Hàm lấy User từ cookie như bạn đã viết
//    private User getUserFromCookie() {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) return null;
//
//        for (Cookie cookie : cookies) {
//            if ("user_id".equals(cookie.getName())) {
//                try {
//                    return userRepository.findById(Integer.parseInt(cookie.getValue())).orElse(null);
//                } catch (NumberFormatException e) {
//                    return null;
//                }
//            }
//        }
//        return null;
//    }
//
//    //  API đổi mật khẩu
//    @PostMapping("/change-password")
//    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO dto) {
//        User user = getUserFromCookie();
//        if (user == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
//
//        // So sánh mật khẩu hiện tại
//        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
//            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng");
//        }
//
//        // Kiểm tra xác nhận mật khẩu
//        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
//            return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận không khớp");
//        }
//
//        // Đổi mật khẩu
//        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
//        userRepository.save(user);
//        return ResponseEntity.ok("Đổi mật khẩu thành công!");
//    }
//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile() {
//        User user = getUserFromCookie();
//        if (user == null) {
//            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
//        }
//
//        UserProfileDTO dto = new UserProfileDTO();
//        dto.setFullName(user.getFullName());
//        dto.setEmail(user.getEmail());
//        dto.setPhone(user.getPhone());
//
//        return ResponseEntity.ok(dto);
//    }
//
//
//}
package com.example.demo.controllers;

import com.example.demo.dto.ChangePasswordDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.AuthHelper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserRestController {

    private final UserJPA userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;
    private final AuthHelper authHelper;

    //  Lấy user từ token JWT
    private User getUserFromToken() {
        return authHelper.getCurrentUser(request);
    }

    //  API đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO dto) {
        User user = getUserFromToken();
        if (user == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    //  API lấy thông tin người dùng
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User user = getUserFromToken();
        if (user == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());

        return ResponseEntity.ok(dto);
    }
}
