package com.example.demo.services;

import com.example.demo.dto.AgentDiscountDTO;
import com.example.demo.entity.Agent;
import com.example.demo.entity.AgentDiscount;
import com.example.demo.jpas.AgentDiscountJPA;
import com.example.demo.jpas.AgentJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentDiscountService {

    private final AgentDiscountJPA discountJPA;
    private final AgentJPA agentJPA;
    private final AgentDiscountEmailService agentDiscountEmailService;
    private final AgentDiscountStatusEmailService agentDiscountStatusEmailService;


    // ✅ Thêm mới chiết khấu cho đại lý
    public void createDiscount(AgentDiscountDTO dto) {
        Agent agent = agentJPA.findById(dto.getAgentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));

        // ✅ Kiểm tra trùng khoảng thời gian đang áp dụng
        List<AgentDiscount> existingDiscounts = discountJPA.findByAgentIdOrderByIdDesc(dto.getAgentId());
        for (AgentDiscount existing : existingDiscounts) {
            if (Boolean.TRUE.equals(existing.getIsActive())) {
                boolean overlap = !(dto.getEndDate().isBefore(existing.getStartDate())
                        || dto.getStartDate().isAfter(existing.getEndDate()));
                if (overlap) {
                    throw new RuntimeException("Đã có chiết khấu đang áp dụng trong khoảng thời gian này.");
                }
            }
        }

        // ✅ Nếu không trùng thì tiếp tục lưu
        AgentDiscount discount = new AgentDiscount();
        discount.setAgent(agent);
        discount.setDiscountPercentage(dto.getDiscountPercentage());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discount.setDescription(dto.getDescription());
        discount.setIsActive(true);

        discountJPA.save(discount);

        // ✅ Gửi email thông báo chiết khấu mới cho đại lý
        if (agent.getUser() != null) {
            String email = agent.getUser().getEmail();       // Email tài khoản đại lý
            String fullName = agent.getUser().getFullName(); // Tên đầy đủ tài khoản
            agentDiscountEmailService.sendDiscountNotification(email, fullName);
        }
    }

    // ✅ Cập nhật trạng thái isActive (ngừng áp dụng hoặc kích hoạt lại)
//    public void toggleActive(Integer id, boolean active) {
//        AgentDiscount discount = discountJPA.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiết khấu"));
//        discount.setIsActive(active);
//        discountJPA.save(discount);
//    }
    public void toggleActive(Integer id, boolean active) {
        AgentDiscount discount = discountJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiết khấu"));

        discount.setIsActive(active);
        discountJPA.save(discount);

        // ✅ Lấy thông tin gửi email
        Agent agent = discount.getAgent();
        if (agent != null && agent.getUser() != null) {
            String email = agent.getUser().getEmail();
            String fullName = agent.getUser().getFullName();
            String discountDescription = discount.getDescription();

            // ✅ Gọi đúng tên hàm gửi email (theo file AgentDiscountEmailService bạn yêu cầu)
            if (active) {
                agentDiscountStatusEmailService.sendReapplyDiscountEmail(
                        fullName, discountDescription, email
                );
            } else {
                agentDiscountStatusEmailService.sendStopDiscountEmail(
                        fullName, discountDescription, email
                );
            }
        }
    }

    // ✅ Convert Entity → DTO
    private AgentDiscountDTO toDTO(AgentDiscount discount) {
        AgentDiscountDTO dto = new AgentDiscountDTO();
        dto.setId(discount.getId());
        dto.setAgentId(discount.getAgent().getId());
        dto.setAgentName(discount.getAgent().getAgentName());
        dto.setDiscountPercentage(discount.getDiscountPercentage());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        dto.setDescription(discount.getDescription());
        dto.setIsActive(discount.getIsActive());
        return dto;
    }

    // ✅ Lấy danh sách chiết khấu theo đại lý
    public List<AgentDiscountDTO> getDiscountsByAgent(Integer agentId) {
        return discountJPA.findByAgentIdOrderByIdDesc(agentId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public Page<AgentDiscountDTO> searchDiscountsByAgentAndDescription(
            Integer agentId, String keyword, Pageable pageable) {

        // keyword có thể null hoặc rỗng
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Page<AgentDiscount> page = discountJPA.findByAgentIdAndDescription(agentId, keyword, pageable);

        // Chuyển Entity sang DTO
        return page.map(this::toDTO);
    }
}
