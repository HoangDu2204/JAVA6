package com.example.demo.controllers;

import com.example.demo.dto.AgentUpdateRequest;
import com.example.demo.entity.Agent;
import com.example.demo.entity.User;
import com.example.demo.jwt.AuthHelper;
import com.example.demo.services.AgentServiceUpdate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents/update")
@RequiredArgsConstructor
public class AgentControllerUpdate {

    private final AgentServiceUpdate agentServiceUpdate;
    private final AuthHelper authHelper;
    private final HttpServletRequest request;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAgent() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(401).body("Không xác thực được người dùng");
        }

        Agent agent = agentServiceUpdate.getByUser(user);
        return ResponseEntity.ok(agent);
    }

    @PutMapping
    public ResponseEntity<?> updateAgent(@RequestBody AgentUpdateRequest updateRequest) {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(401).body("Không xác thực được người dùng");
        }

        Agent updated = agentServiceUpdate.updateAgent(user, updateRequest);
        return ResponseEntity.ok(updated);
    }

    // ✅ Thêm mới: kiểm tra người dùng hiện tại có phải đại lý hay không
    @GetMapping("/check-agent")
    public ResponseEntity<?> checkIfUserIsAgent() {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(401).body("Không xác thực được người dùng");
        }

        boolean isAgent = agentServiceUpdate.isAgent(user);
        return ResponseEntity.ok(isAgent);
    }

}
