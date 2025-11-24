package com.example.demo.controllers;

import com.example.demo.dto.AgentRegisterRequest;
import com.example.demo.services.AgentRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class AgentRegisterController {

    private final AgentRegisterService agentRegisterService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAgent(@RequestBody AgentRegisterRequest req,
                                           HttpServletRequest request) {
        try {
            agentRegisterService.registerAgent(req, request);
            return ResponseEntity.ok("Đăng ký đại lý thành công. Vui lòng chờ duyệt.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
