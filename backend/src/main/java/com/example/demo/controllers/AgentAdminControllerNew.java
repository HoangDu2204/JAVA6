package com.example.demo.controllers;

import com.example.demo.dto.AgentDTONew;
import com.example.demo.services.AgentServiceNew;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/agents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") //  Thêm dòng này
public class AgentAdminControllerNew {

    private final AgentServiceNew agentService;

    //  1. Lấy danh sách đại lý (phân trang)
    @GetMapping
    public ResponseEntity<Page<AgentDTONew>> getAllAgents(Pageable pageable) {
        return ResponseEntity.ok(agentService.getAgentsPage(pageable));
    }

    //  2. Duyệt đại lý
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveAgent(@PathVariable Integer id) {
        agentService.approveAgent(id);
        return ResponseEntity.ok().build();
    }

    //  3. Xoá mềm đại lý
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgent(@PathVariable Integer id) {
        agentService.deleteAgent(id);
        return ResponseEntity.ok().build();
    }
}
