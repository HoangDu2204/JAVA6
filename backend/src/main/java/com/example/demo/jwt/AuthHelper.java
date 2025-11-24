
package com.example.demo.jwt;

import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final JwtUtil jwtUtil;
    private final UserJPA userJPA;

    // ✅ Lấy user từ token
    public User getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Bỏ tiền tố "Bearer "

                System.out.println("🔐 Token header: " + authHeader);

                // Lấy toàn bộ claims từ JWT
                Claims claims = jwtUtil.extractAllClaims(token);

                String username = claims.getSubject();

                // ✅ Chuyển kiểu an toàn, tránh ClassCastException
                Object userIdObj = claims.get("userId");
                Integer userId = null;
                if (userIdObj instanceof Number) {
                    userId = ((Number) userIdObj).intValue();
                }

                System.out.println("🔐 Username from token: " + username);
                System.out.println("🆔 userId from token: " + userId);

                if (userId != null) {
                    return userJPA.findById(userId).orElse(null);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi giải mã token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
