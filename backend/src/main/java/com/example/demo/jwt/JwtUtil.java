package com.example.demo.jwt;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "my-very-secret-key-my-very-secret-key"; // ít nhất 256-bit (HS256)
    private final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 1 ngày

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Chuyển số role sang chuỗi ROLE_*
    private String convertRoleToString(Integer role) {
        return switch (role) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_USER";
            default -> "ROLE_UNKNOWN";
        };
    }

    // ✅ Tạo token từ entity User
    public String generateToken(User user) {
        String roleStr = convertRoleToString(user.getRole());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", List.of(roleStr)); // đưa vai trò vào token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Trích xuất username từ token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Trích xuất userId từ token
    public Integer extractUserId(String token) {
        Object rawId = extractAllClaims(token).get("userId");
        if (rawId instanceof Integer) return (Integer) rawId;
        if (rawId instanceof Number) return ((Number) rawId).intValue();
        return null;
    }

    // ✅ Trích xuất roles từ token
    public List<String> extractRoles(String token) {
        return (List<String>) extractAllClaims(token).get("roles");
    }

    // ✅ Kiểm tra token còn hạn và đúng username
    public boolean isTokenValid(String token, String expectedUsername) {
        return extractUsername(token).equals(expectedUsername) && !isTokenExpired(token);
    }

    // ✅ Kiểm tra token hết hạn chưa
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // ✅ Trích xuất toàn bộ claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Kiểm tra token có hợp lệ tổng quát
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
