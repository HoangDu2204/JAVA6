package com.example.demo.services;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceTA {
    @Autowired
    UserJPA userJPA;
    @Autowired
    HttpServletResponse response;
    @Autowired
    HttpServletRequest request;
    @Autowired
    private UserJPA userRepo;

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        // Không set password để bảo mật
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Chỉ set password khi cần
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());
        user.setIsActive(dto.getIsActive());
        return user;
    }

    public Page<UserDTO> findAll(String search, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (!search.isEmpty() && !status.isEmpty()) {
            Boolean isActive = status.equals("active") ? true : false;
            userPage = (Page<User>) userJPA.findByUsernameContainingOrEmailContainingOrFullNameContainingAndIsActive(
                    search, search, search, isActive, pageable);
        } else if (!search.isEmpty()) {
            userPage = userJPA.findByUsernameContainingOrEmailContainingOrFullNameContaining(
                    search, search, search, pageable);
        } else if (!status.isEmpty()) {
            Boolean isActive = status.equals("active") ? true : false;
            userPage = userJPA.findByIsActive(isActive, pageable);
        } else {
            userPage = userJPA.findAllByRole(pageable);
        }
        return userPage.map(this::toDTO);
    }

    public Optional<UserDTO> findById(Integer id) {
        return userRepo.findById(id).map(this::toDTO);
    }

    public UserDTO save(UserDTO dto) {
        User user = toEntity(dto);
        user.setCreatedAt(java.time.LocalDateTime.now());
        return toDTO(userRepo.save(user));
    }

    public UserDTO update(Integer id, UserDTO dto) {
        Optional<User> opt = userRepo.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setIsActive(dto.getIsActive());
            return toDTO(userRepo.save(user));
        }
        throw new RuntimeException("Không tìm thấy người dùng");
    }

    public void delete(Integer id) {
        userRepo.deleteById(id);
    }

    public boolean checkLogin(String username, String password) {
        try {
            Optional<User> userOptional = userJPA.findByUsername1(username);
            if (!userOptional.isPresent()) return false;
            User user = userOptional.get();
            if (!user.getPassword().equals(password)) return false;
            if (!user.getIsActive()) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa");
            }
            int role = user.getRole();
            Cookie cookieUserId = new Cookie("user_id", String.valueOf(user.getId()));
            cookieUserId.setMaxAge(60 * 60 * 10);
            cookieUserId.setPath("/");
            Cookie cookieUserRole = new Cookie("user_role", String.valueOf(role));
            cookieUserRole.setMaxAge(60 * 60 * 10);
            cookieUserRole.setPath("/");
            response.addCookie(cookieUserId);
            response.addCookie(cookieUserRole);
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getRoleFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("role".equals(cookie.getName())) {
                    return Integer.parseInt(cookie.getValue());
                }
            }
        }
        return -1;
    }

    public void clearLoginInformation() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user_id") || cookie.getName().equals("user_role")) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

    public int getRoleByUsername(String username) {
        User user = userJPA.findByUsername2(username);
        return (user != null) ? user.getRole() : -1;
    }

    public boolean checkLoginByEmail(String email, String password) {
        try {
            List<User> users = userJPA.findByEmail(email);
            if (users.isEmpty()) return false;
            User user = users.get(0);
            if (!user.getPassword().equals(password)) return false;
            if (!user.getIsActive()) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa");
            }
            int role = user.getRole();
            Cookie cookieUserId = new Cookie("user_id", String.valueOf(user.getId()));
            cookieUserId.setMaxAge(60 * 60 * 10);
            cookieUserId.setPath("/");
            Cookie cookieUserRole = new Cookie("user_role", String.valueOf(role));
            cookieUserRole.setMaxAge(60 * 60 * 10);
            cookieUserRole.setPath("/");
            response.addCookie(cookieUserId);
            response.addCookie(cookieUserRole);
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getRoleByEmail(String email) {
        List<User> users = userJPA.findByEmail(email);
        return (!users.isEmpty()) ? users.get(0).getRole() : -1;
    }

    public UserDTO getUserDTOById(Integer id) {
        return userRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }
}