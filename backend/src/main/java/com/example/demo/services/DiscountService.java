package com.example.demo.services;

import com.example.demo.dto.DiscountDTO;
import com.example.demo.entity.Discount;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.jpas.DiscountJPA;

import com.example.demo.jpas.ProductJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private DiscountJPA discountJPA;



    @Autowired
    private ProductJPA productJPA;

    // Lấy tất cả chương trình giảm giá
    public List<DiscountDTO> getAll() {
        return discountJPA.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy các chương trình đang hoạt động
    public List<DiscountDTO> getAllActive() {
        return discountJPA.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy discount theo ID
    public Optional<Discount> getById(Integer id) {
        return discountJPA.findById(id.longValue());
    }

    // Thêm mới chương trình giảm giá
    public Discount add(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.setName(dto.getName());
        discount.setPercentage(dto.getPercentage());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discount.setIsActive(dto.getIsActive());

        return discountJPA.save(discount);
    }

    // Cập nhật chương trình giảm giá
    public Discount update(Integer id, DiscountDTO dto) {
        Discount existing = discountJPA.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        existing.setName(dto.getName());
        existing.setPercentage(dto.getPercentage());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setIsActive(dto.getIsActive());

        return discountJPA.save(existing);
    }

    // Xóa chương trình giảm giá
    public void delete(Integer id) {
        if (!discountJPA.existsById(id.longValue())) {
            throw new RuntimeException("Discount not found");
        }
        discountJPA.deleteById(id.longValue());
    }

    // Bật/tắt trạng thái hoạt động của chương trình
    public void setActive(Integer id, boolean active) {
        Discount discount = discountJPA.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        discount.setIsActive(active);
        discountJPA.save(discount);
    }

    // Chuyển đổi sang DTO
    public DiscountDTO convertToDTO(Discount d) {
        DiscountDTO dto = new DiscountDTO();
        dto.setDiscountId(d.getId());
        dto.setName(d.getName());
        dto.setPercentage(d.getPercentage());
        dto.setStartDate(d.getStartDate());
        dto.setEndDate(d.getEndDate());
        dto.setIsActive(d.getIsActive());
        return dto;
    }




}
