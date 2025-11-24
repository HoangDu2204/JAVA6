package com.example.demo.controllers;

import com.example.demo.dto.StoreInfoDTO;
import com.example.demo.services.StoreInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store-info")
@CrossOrigin(origins = "http://localhost:5173")
public class StoreInfoController {
    @Autowired
    private StoreInfoService storeInfoService;

    @GetMapping("")
    public ResponseEntity<?> getStoreInfo() {
        StoreInfoDTO dto = storeInfoService.getStoreInfo();
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("")
    public ResponseEntity<?> updateStoreInfo(@RequestBody StoreInfoDTO dto) {
        StoreInfoDTO updated = storeInfoService.updateStoreInfo(dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.badRequest().body("Không tìm thấy thông tin cửa hàng để cập nhật");
    }

    @PostMapping("")
    public ResponseEntity<?> createStoreInfo(@RequestBody StoreInfoDTO dto) {
        StoreInfoDTO created = storeInfoService.createStoreInfo(dto);
        return ResponseEntity.ok(created);
    }
} 