package com.example.demo.controllers;

import com.example.demo.beans.SizeBean;
import com.example.demo.dto.SizeDTO;
import com.example.demo.services.SizeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sizes")
public class SizeController {
    private final SizeService service;

    public SizeController(SizeService service) {
        this.service = service;
    }

    // GET: Lấy tất cả kích thước
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllSizes() {
        List<SizeDTO> sizes = service.getAllSizes();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách thành công");
        response.put("data", sizes);
        return ResponseEntity.ok(response);
    }

    // GET: Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSizeById(@PathVariable Integer id) {
        SizeDTO size = service.getSizeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy chi tiết kích thước thành công");
        response.put("data", size);
        return ResponseEntity.ok(response);
    }

    // POST: Tạo mới
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SizeBean request) {
        SizeDTO created = service.createSize(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thêm kích thước thành công");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    // PUT: Cập nhật
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody SizeBean request) {
        SizeDTO updated = service.updateSize(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cập nhật kích thước thành công");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    // DELETE: Xóa
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.deleteSize(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Xóa kích thước thành công");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/check-used")
    public ResponseEntity<Map<String, Object>> checkUsed(@PathVariable Integer id) {
        boolean isUsed = service.isSizeUsed(id); // dùng 'service' chứ không phải 'sizeService'
        Map<String, Object> response = new HashMap<>();
        response.put("isUsed", isUsed);
        return ResponseEntity.ok(response);
    }


} 