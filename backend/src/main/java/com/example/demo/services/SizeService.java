package com.example.demo.services;

import com.example.demo.beans.SizeBean;
import com.example.demo.dto.SizeDTO;
import com.example.demo.entity.Size;
import com.example.demo.jpas.SizeJPANew;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeService {
    private final SizeJPANew sizeRepo;

    public SizeService(SizeJPANew sizeRepo) {
        this.sizeRepo = sizeRepo;
    }

    // Lấy tất cả kích thước
    public List<SizeDTO> getAllSizes() {
        return sizeRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy theo ID
    public SizeDTO getSizeById(Integer id) {
        Size size = sizeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước với ID: " + id));
        return convertToDTO(size);
    }

    // Tạo mới
    public SizeDTO createSize(SizeBean request) {
        // Kiểm tra tên đã tồn tại chưa
        if (sizeRepo.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new RuntimeException("Tên kích thước đã tồn tại");
        }

        Size size = new Size();
        size.setName(request.getName());
        size.setIsActive(true);

        Size saved = sizeRepo.save(size);
        return convertToDTO(saved);
    }

    // Cập nhật
    public SizeDTO updateSize(SizeBean request) {
        Size size = sizeRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước với ID: " + request.getId()));

        // Kiểm tra tên đã tồn tại chưa (trừ chính nó)
        sizeRepo.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(request.getId())) {
                        throw new RuntimeException("Tên kích thước đã tồn tại");
                    }
                });

        size.setName(request.getName());
        size.setIsActive(request.getIsActive());

        Size saved = sizeRepo.save(size);
        return convertToDTO(saved);
    }

    // Xóa
    public void deleteSize(Integer id) {
        Size size = sizeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước với ID: " + id));

        // Kiểm tra xem có đang được sử dụng không
        if (!size.getProductVariants().isEmpty()) {
            throw new RuntimeException("Không thể xóa kích thước đang được sử dụng trong sản phẩm");
        }

        sizeRepo.delete(size);
    }

    // Chuyển đổi Entity sang DTO
    private SizeDTO convertToDTO(Size size) {
        SizeDTO dto = new SizeDTO();
        dto.setId(size.getId());
        dto.setName(size.getName());
        dto.setIsActive(size.getIsActive());
        return dto;
    }
    public boolean isSizeUsed(Integer id) {
        Size size = sizeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước với ID: " + id));
        return !size.getProductVariants().isEmpty(); // Nếu đang được dùng thì trả về true
    }

} 