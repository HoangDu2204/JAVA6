package com.example.demo.controllers;

import com.example.demo.entity.Shape;
import com.example.demo.jpas.ShapeJPANew;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ShapeController_New {

    private final ShapeJPANew shapeRepo;

    @GetMapping("/shapes-new")
    public List<Shape> getAll() {
        return shapeRepo.findByIsActiveTrue();
    }
}
