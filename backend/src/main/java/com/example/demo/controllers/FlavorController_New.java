package com.example.demo.controllers;

import com.example.demo.entity.Flavor;
import com.example.demo.jpas.FlavorJPANew;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class FlavorController_New {

    private final FlavorJPANew flavorRepo;

    @GetMapping("/flavors-new")
    public List<Flavor> getAll() {
        return flavorRepo.findByIsActiveTrue();
    }
}
