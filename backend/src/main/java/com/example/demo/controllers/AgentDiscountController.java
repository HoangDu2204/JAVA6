package com.example.demo.controllers;

import com.example.demo.dto.AgentDiscountDTO;
import com.example.demo.services.AgentDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agent-discounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AgentDiscountController {

    private final AgentDiscountService discountService;

    //  2. Thêm mới chiết khấu cho đại lý
//    @PostMapping
//    public ResponseEntity<?> createDiscount(@RequestBody AgentDiscountDTO dto) {
//        discountService.createDiscount(dto);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping
    public ResponseEntity<?> createDiscount(@RequestBody AgentDiscountDTO dto) {
        try {
            discountService.createDiscount(dto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //  3. Ngừng hoặc kích hoạt lại chiết khấu
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id, @RequestParam boolean active) {
        discountService.toggleActive(id, active);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/by-agent/{agentId}")
    public ResponseEntity<List<AgentDiscountDTO>> getByAgent(@PathVariable Integer agentId) {
        return ResponseEntity.ok(discountService.getDiscountsByAgent(agentId));
    }
    // 5. Tìm kiếm chiết khấu theo đại lý và mô tả (có phân trang)
    @GetMapping("/search")
    public ResponseEntity<Page<AgentDiscountDTO>> searchDiscounts(
            @RequestParam Integer agentId,
            @RequestParam(required = false) String keyword,
            @PageableDefault Pageable pageable) {

        return ResponseEntity.ok(
                discountService.searchDiscountsByAgentAndDescription(agentId, keyword, pageable)
        );
    }
}
