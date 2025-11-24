package com.example.demo.controllers;

import com.example.demo.entity.Origin;
import com.example.demo.jpas.OriginJPANew;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OriginController_New {

    private final OriginJPANew originRepo;

    @GetMapping("/origins-new")
    public List<Origin> getAll() {
        return originRepo.findByIsActiveTrue();
    }
}
