package com.example.demo.services;

import com.example.demo.dto.VoucherEligibleDTO;
import com.example.demo.entity.Voucher;

import com.example.demo.jpas.VoucherJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherEligibleService {

    private final VoucherJPA voucherRepository;

    public List<VoucherEligibleDTO> getEligibleVouchers(BigDecimal cartTotal) {
        List<Voucher> vouchers = voucherRepository.findAll();

        return vouchers.stream()
//                .filter(v -> v.getIsActive() && v.getQuantity() > 0)
                .filter(v -> v.getEndDate().isAfter(LocalDateTime.now()))
                .map(v -> {
                    BigDecimal missingAmount = v.getMinOrderAmount().subtract(cartTotal);
                    if (missingAmount.compareTo(BigDecimal.ZERO) < 0) {
                        missingAmount = BigDecimal.ZERO;
                    }

                    return new VoucherEligibleDTO(
                            v.getId(),
                            v.getCode(),
                            v.getDiscountPercent(),
                            v.getMaxDiscountAmount(),
                            v.getMinOrderAmount(),
                            v.getEndDate(),
                            missingAmount
                    );
                })
                .collect(Collectors.toList());
    }

}
