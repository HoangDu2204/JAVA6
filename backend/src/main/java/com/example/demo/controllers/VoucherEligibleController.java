package com.example.demo.controllers;

import com.example.demo.dto.VoucherEligibleDTO;
import com.example.demo.services.VoucherEligibleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // hoặc domain của frontend
public class VoucherEligibleController {

    private final VoucherEligibleService voucherEligibleService;

    @GetMapping("/eligible")
    public List<VoucherEligibleDTO> getEligibleVouchers(@RequestParam BigDecimal cartTotal) {
        return voucherEligibleService.getEligibleVouchers(cartTotal);
    }
}
