package com.example.demo.services;

import com.example.demo.beans.ShapeBean;
import com.example.demo.dto.ShapeDTO;
import com.example.demo.entity.Shape;
import com.example.demo.jpas.OrderJPA;
import com.example.demo.jpas.ProductVariantJPA;
import com.example.demo.jpas.ShapeJPA;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShapeService {

    private final ShapeJPA repo;
    private final OrderJPA orderJPA;
    private final ProductVariantJPA productVariantJPA;

    public ShapeService(ShapeJPA repo, OrderJPA orderJPA, ProductVariantJPA productVariantJPA) {
        this.repo = repo;
        this.orderJPA = orderJPA;
        this.productVariantJPA = productVariantJPA;
    }

    // ✅ Tạo mới
    public ShapeDTO createShape(ShapeBean dto) {
        String name = dto.getName().trim();
        if (name.isEmpty() || !name.matches("^[a-zA-ZÀ-ỹ\\s]{1,50}$")) {
            throw new IllegalArgumentException("Tên không hợp lệ. Không được để trống hoặc chứa số/ký tự đặc biệt.");
        }

        boolean exists = repo.findAll().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name));

        if (exists) {
            throw new IllegalArgumentException("Tên hình dạng đã tồn tại.");
        }

        Shape shape = new Shape();
        shape.setName(name);
        shape.setIsActive(true);
        return toDTO(repo.save(shape));
    }

    // ✅ Cập nhật
    public ShapeDTO updateShape(ShapeBean dto) {
        Shape existing = repo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy shape với id: " + dto.getId()));

        String name = dto.getName().trim();
        if (name.isEmpty() || !name.matches("^[a-zA-ZÀ-ỹ\\s]{1,50}$")) {
            throw new IllegalArgumentException("Tên không hợp lệ. Không được để trống hoặc chứa số/ký tự đặc biệt.");
        }

        boolean exists = repo.findAll().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name) && !s.getId().equals(dto.getId()));

        if (exists) {
            throw new IllegalArgumentException("Tên hình dạng đã tồn tại.");
        }

        existing.setName(name);
        return toDTO(repo.save(existing));
    }

    // ✅ Lấy tất cả
    public List<ShapeDTO> getAllShape() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Shape::getId).reversed())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Lấy theo ID
    public ShapeDTO getById(Integer id) {
        Shape shape = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hình dạng với id: " + id));
        return toDTO(shape);
    }

    // ✅ Kiểm tra shape có được sử dụng hay không
    public boolean isShapeUsed(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy hình dạng với id: " + id);
        }
        boolean usedInProduct = productVariantJPA.existsByShape_Id(id);
        boolean usedInOrder = orderJPA.existsByOrderItems_ProductVariant_Shape_Id(id);
        return usedInProduct || usedInOrder;
    }

    // ✅ Xoá an toàn
    public void deleteShape(Integer id) {
        Shape shape = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hình dạng với id: " + id));

        boolean isUsed = isShapeUsed(id);
        if (isUsed) {
            throw new IllegalStateException("Hình dạng đang được sử dụng trong sản phẩm hoặc đơn hàng, không thể xoá.");
        }

        repo.delete(shape);
    }

    // Convert entity -> DTO
    private ShapeDTO toDTO(Shape entity) {
        return new ShapeDTO(entity.getId(), entity.getName(), entity.getIsActive());
    }
}
