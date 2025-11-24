package com.example.demo.controllers;


import com.example.demo.beans.OriginBean;
import com.example.demo.dto.OriginDTO;
import com.example.demo.services.OriginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/origins")
public class OriginController {

    private final OriginService service;

    public OriginController(OriginService service) {
        this.service = service;
    }

    // GET: Lấy danh sách xuất xứ
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllOrigins() {
        List<OriginDTO> origins = service.getAllOrigins();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách nguồn gốc thành công");
        response.put("data", origins);
        return ResponseEntity.ok(response);
    }

    // POST: Tạo mới xuất xứ
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody OriginBean request) {
        OriginDTO created = service.createOrigin(request); // <--- sửa ở đây
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thêm xuất xứ thành công!");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    // PUT: Cập nhật xuất xứ
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody OriginBean request) {
        OriginDTO updated = service.updateOrigin(request); // <--- sửa ở đây
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sửa xuất xứ thành công!");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }


    // DELETE: Xoá xuất xứ
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.deleteOrigin(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Xoá xuất xứ thành công!");
        return ResponseEntity.ok(response);
    }
    // GET: Kiểm tra xem nguồn gốc có đang được dùng
    @GetMapping("/{id}/check-used")
    public ResponseEntity<Map<String, Object>> checkUsed(@PathVariable Integer id) {
        boolean isUsed = service.isOriginUsed(id);
        Map<String, Object> response = new HashMap<>();
        response.put("isUsed", isUsed);
        return ResponseEntity.ok(response);
    }

}
