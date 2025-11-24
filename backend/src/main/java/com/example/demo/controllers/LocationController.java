package com.example.demo.controllers;


import com.example.demo.services.GHNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private GHNService ghnService;

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        try {
            List<Map<String, Object>> provinces = ghnService.getProvinces();
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 log stack trace để dễ debug
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Không thể lấy danh sách tỉnh",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<?> getDistricts(@PathVariable int provinceId) {
        try {
            List<Map<String, Object>> districts = ghnService.getDistricts(provinceId);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Không thể lấy danh sách quận/huyện",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<?> getWards(@PathVariable int districtId) {
        try {
            List<Map<String, Object>> wards = ghnService.getWards(districtId);
            return ResponseEntity.ok(wards);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Không thể lấy danh sách phường/xã",
                    "message", e.getMessage()
            ));
        }
    }


}
