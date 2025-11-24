package com.example.demo.jwt;

import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJPA userJPA;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ✅ Sửa lỗi Optional: Dùng orElseThrow để lấy user an toàn
        User user = userJPA.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // ✅ Trả về đối tượng UserDetails để Spring Security xác thực
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // Nếu chưa phân quyền, để danh sách quyền rỗng
        );
    }
}
