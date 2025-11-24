package com.example.demo.controllers;

import com.example.demo.beans.FlavorBean;
import com.example.demo.dto.FlavorDTO;
import com.example.demo.services.FlavorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/flavors")
public class FlavorController {
    private final FlavorService service;

    public FlavorController(FlavorService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllFlavors() {
        List<FlavorDTO> flavors = service.getAllFlavors();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách thành công");
        response.put("data", flavors);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlavorById(@PathVariable Integer id) {
        FlavorDTO flavor = service.getFlavorById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy chi tiết hương vị thành công");
        response.put("data", flavor);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody FlavorBean request) {
        FlavorDTO created = service.createFlavor(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thêm hương vị thành công");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody FlavorBean request) {
        FlavorDTO updated = service.updateFlavor(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cập nhật hương vị thành công");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        service.deleteFlavor(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Xóa hương vị thành công");
        return ResponseEntity.ok(response);
    }
}
