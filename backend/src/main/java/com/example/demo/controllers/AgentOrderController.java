package com.example.demo.controllers;

import com.example.demo.dto.AgentResponseDTO;
import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.services.AgentOrderService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AgentOrderController {

    @Autowired
    private AgentOrderService agentService;

    @Autowired
    private AuthHelper authHelper;

    @GetMapping("/info")
    public ResponseEntity<?> getAgentInfo(HttpServletRequest request) {
        try {
            User user = authHelper.getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            AgentResponseDTO agentInfo = agentService.getAgentInfo();
            if (agentInfo == null) {
                return ResponseEntity.status(404).body("Không tìm thấy thông tin đại lý");
            }

            return ResponseEntity.ok(agentInfo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi Server: " + e.getMessage());
        }
    }


    // ✅ API 2: Đặt hàng dành cho đại lý
    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutAgent(@RequestBody OrderRequestDTO dto) {
        try {
            Map<String, Object> response = agentService.checkoutAgent(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
