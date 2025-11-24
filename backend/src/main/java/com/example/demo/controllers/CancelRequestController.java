package com.example.demo.controllers;

import com.example.demo.dto.CancelRequestDTO;
import com.example.demo.entity.CancelRequest;
import com.example.demo.services.CancelRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancel-requests")
@RequiredArgsConstructor
public class CancelRequestController {

    private final CancelRequestService cancelRequestService;

    @PostMapping
    public ResponseEntity<CancelRequest> createRequest(@RequestBody CancelRequestDTO dto) {
        CancelRequest request = cancelRequestService.createCancelRequest(dto);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<List<CancelRequest>> getAllRequests() {
        return ResponseEntity.ok(cancelRequestService.getAllRequests());
    }

    // 🔁 Sửa lại: không cần truyền userId
    @GetMapping("/user")
    public ResponseEntity<List<CancelRequest>> getRequestsByUser() {
        return ResponseEntity.ok(cancelRequestService.getRequestsByUser());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Integer id) {
        cancelRequestService.approveRequest(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Integer id) {
        cancelRequestService.rejectRequest(id);
        return ResponseEntity.ok().build();
    }
}
