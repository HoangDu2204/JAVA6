//package com.example.demo.services;
//
//
//import com.example.demo.beans.UserBean;
//import com.example.demo.entity.User;
//import com.example.demo.jpas.UserJPA;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//	@Autowired
//	UserJPA userJPA;
//	@Autowired
//	HttpServletResponse response;
//
//	@Autowired
//	HttpServletRequest request;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//
//
//
//	public boolean checkLogin(String username, String password) {
//		try {
//			Optional<User> userOptional = userJPA.findByUsername1(username);
//
//			if (!userOptional.isPresent() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
//				return false;
//			}
//
//
//			// Lấy role
//			int role = userOptional.get().getRole();
//			System.out.println("Role lấy từ DB: " + role);
//
//			// Lưu cookie
//			Cookie cookieUserId = new Cookie("user_id", String.valueOf(userOptional.get().getId()));
//			cookieUserId.setMaxAge(60 * 60 * 10);
//			Cookie cookieUserRole = new Cookie("user_role", String.valueOf(role));
//			cookieUserRole.setMaxAge(60 * 60 * 10);
//
//			// **Lưu cookie với đường dẫn hợp lệ**
//			cookieUserId.setPath("/");
//			cookieUserRole.setPath("/");
//
//			response.addCookie(cookieUserId);
//			response.addCookie(cookieUserRole);
//
//		} catch (Exception e) {
//			e.printStackTrace(); // In lỗi ra console để debug
//			return false;
//		}
//		return true;
//	}
//
//	public Integer getRoleFromCookie(HttpServletRequest request) {
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if ("role".equals(cookie.getName())) {
//					return Integer.parseInt(cookie.getValue());
//				}
//			}
//		}
//		return -1;
//
//	}
//
//	public void clearLoginInformation() {
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if (cookie.getName().equals("user_id") || cookie.getName().equals("user_role")) {
//					cookie.setMaxAge(0);
//					cookie.setPath("/");
//					response.addCookie(cookie);
//				}
//			}
//		}
//	}
//
//	public int getRoleByUsername(String username) {
//		User user = userJPA.findByUsername2(username);
//		return (user != null) ? user.getRole() : -1;
//	}
//}
package com.example.demo.services;

import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.jwt.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserJPA userJPA;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil; //  Để tạo token
	private final AuthHelper authHelper; // Để giải mã token lấy user

	//  Đăng nhập → tạo JWT
	public ResponseEntity<?> checkLogin(String username, String password) {
		Optional<User> userOptional = userJPA.findByUsername1(username);

		if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
			return ResponseEntity.status(401).body("Tên đăng nhập hoặc mật khẩu không đúng");
		}

		User user = userOptional.get();

		//  Tạo JWT có chứa userId và username
		String token = jwtUtil.generateToken(user);


		return ResponseEntity.ok(Map.of(
				"token", token,
				"role", user.getRole(),
				"fullName", user.getFullName()
		));
	}

	//  Lấy vai trò từ token (Authorization header)
	public int getRoleFromToken(HttpServletRequest request) {
		User user = authHelper.getCurrentUser(request);
		return (user != null) ? user.getRole() : -1;
	}

	//  Xoá đăng nhập (client xóa token ở localStorage, server không cần làm gì)
	public void clearLogin() {
		// Nếu JWT lưu ở localStorage → không cần xử lý server
		// Nếu bạn lưu JWT ở cookie thì mới cần xóa cookie như trước
	}

	public int getRoleByUsername(String username) {
		User user = userJPA.findByUsername2(username);
		return (user != null) ? user.getRole() : -1;
	}
}
