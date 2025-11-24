package com.example.demo.controllers;

import com.example.demo.beans.ShapeBean;
import com.example.demo.dto.ShapeDTO;
import com.example.demo.services.ShapeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shapes")
public class ShapeController {

    private final ShapeService shapeService;

    public ShapeController(ShapeService shapeService) {
        this.shapeService = shapeService;
    }

    @GetMapping("")
    public ResponseEntity<List<ShapeDTO>> getAllShapes() {
        List<ShapeDTO> shapes = shapeService.getAllShape();
        return ResponseEntity.ok(shapes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody ShapeBean request) {
        try {
            ShapeDTO createdShape = shapeService.createShape(request);
            return ResponseEntity.ok(createdShape);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ShapeBean request) {
        try {
            ShapeDTO updatedShape = shapeService.updateShape(request);
            return ResponseEntity.ok(updatedShape);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShapeDTO> getById(@PathVariable Integer id) {
        ShapeDTO shape = shapeService.getById(id);
        return ResponseEntity.ok(shape);
    }

    // ✅ API kiểm tra shape có đang được sử dụng không
    @GetMapping("/{id}/check-used")
    public ResponseEntity<?> checkShapeUsed(@PathVariable Integer id) {
        boolean isUsed = shapeService.isShapeUsed(id);
        return ResponseEntity.ok(Map.of("isUsed", isUsed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            shapeService.deleteShape(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Lỗi khi xoá hình dạng."));
        }
    }
}
