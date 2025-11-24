package com.example.demo.services;

import com.example.demo.beans.OriginBean;
import com.example.demo.dto.OriginDTO;
import com.example.demo.entity.Origin;
import com.example.demo.jpas.OriginJPA;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OriginService {

    private final OriginJPA repo;

    public OriginService(OriginJPA repo) {
        this.repo = repo;
    }

    // Chuyển entity -> DTO
    private OriginDTO toDto(Origin origin) {
        OriginDTO dto = new OriginDTO();
        dto.setId(origin.getId());
        dto.setName(origin.getName());
        dto.setIsActive(origin.getIsActive());
        return dto;
    }

    // Chuyển Bean -> Entity (chỉ dùng trong service)
    private Origin toEntity(OriginBean bean) {
        Origin origin = new Origin();
        origin.setId(bean.getId() != 0 ? bean.getId() : null);
        origin.setName(bean.getName());
        origin.setIsActive(bean.getIsActive() != null ? bean.getIsActive() : true);
        return origin;
    }

    // Tạo mới nguồn gốc
    public OriginDTO createOrigin(OriginBean bean) {
        if (repo.existsByNameIgnoreCase(bean.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên nguồn gốc đã tồn tại!");
        }
        Origin origin = toEntity(bean);
        Origin saved = repo.save(origin);
        return toDto(saved);
    }

    // Cập nhật nguồn gốc
    public OriginDTO updateOrigin(OriginBean bean) {
        Origin existing = repo.findById(bean.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy nguồn gốc cần cập nhật."));

        // Kiểm tra xem đang được dùng trong productVariants
        if (repo.isUsedInProductVariants(bean.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nguồn gốc đang được sử dụng trong sản phẩm, không thể cập nhật.");
        }

        // Kiểm tra tên trùng với nguồn gốc khác
        boolean isDuplicate = repo.existsByNameIgnoreCase(bean.getName()) &&
                !existing.getName().equalsIgnoreCase(bean.getName());
        if (isDuplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tên nguồn gốc đã tồn tại!");
        }

        existing.setName(bean.getName());
        existing.setIsActive(bean.getIsActive() != null ? bean.getIsActive() : existing.getIsActive());

        Origin updated = repo.save(existing);
        return toDto(updated);
    }

    // Lấy danh sách tất cả nguồn gốc
    public List<OriginDTO> getAllOrigins() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Xoá nguồn gốc
    public void deleteOrigin(Integer id) {
        Origin existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy nguồn gốc cần xóa."));

        if (repo.isUsedInProductVariants(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nguồn gốc đang được sử dụng trong sản phẩm, không thể xóa.");
        }

        repo.deleteById(id);
    }

    // Kiểm tra xem nguồn gốc có đang được dùng
    public boolean isOriginUsed(Integer id) {
        return repo.isUsedInProductVariants(id);
    }
}
