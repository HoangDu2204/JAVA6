package com.example.demo.services;

import com.example.demo.beans.FlavorBean;
import com.example.demo.dto.FlavorDTO;
import com.example.demo.entity.Flavor;
import com.example.demo.jpas.FlavorJPA;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlavorService {

    private final FlavorJPA flavorJPA;

    public FlavorService(FlavorJPA flavorJPA) {
        this.flavorJPA = flavorJPA;
    }

    public List<FlavorDTO> getAllFlavors() {
        return flavorJPA.findAll()
                .stream()
                .map(f -> new FlavorDTO(f.getId(), f.getName(), f.getIsActive()))
                .collect(Collectors.toList());
    }

    public FlavorDTO getFlavorById(Integer id) {
        Flavor f = flavorJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hương vị"));
        return new FlavorDTO(f.getId(), f.getName(), f.getIsActive());
    }

    public FlavorDTO createFlavor(FlavorBean bean) {
        Flavor f = new Flavor();
        f.setName(bean.getName());
        f.setIsActive(bean.getIsActive() != null ? bean.getIsActive() : true);
        f = flavorJPA.save(f);
        return new FlavorDTO(f.getId(), f.getName(), f.getIsActive());
    }

    public FlavorDTO updateFlavor(FlavorBean bean) {
        Flavor f = flavorJPA.findById(Math.toIntExact(bean.getId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hương vị"));

        // Kiểm tra nếu đang được dùng trong sản phẩm/đơn hàng
        if (!f.getProductVariants().isEmpty()) {
            throw new RuntimeException("Hương vị đang được sử dụng trong sản phẩm/đơn hàng, không thể đổi trạng thái");
        }

        f.setName(bean.getName());
        f.setIsActive(bean.getIsActive());
        f = flavorJPA.save(f);
        return new FlavorDTO(f.getId(), f.getName(), f.getIsActive());
    }

    public void deleteFlavor(Integer id) {
        Flavor f = flavorJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hương vị"));

        // Kiểm tra nếu đang được dùng
        if (!f.getProductVariants().isEmpty()) {
            throw new RuntimeException("Hương vị đang được sử dụng trong sản phẩm/đơn hàng, không thể xóa");
        }

        flavorJPA.deleteById(id);
    }

    // API check-used cho frontend
    public boolean isFlavorUsed(Integer id) {
        Flavor f = flavorJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hương vị"));
        return !f.getProductVariants().isEmpty();
    }
}
