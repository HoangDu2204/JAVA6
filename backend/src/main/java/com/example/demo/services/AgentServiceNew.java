package com.example.demo.services;

import com.example.demo.dto.AgentDTONew;
import com.example.demo.entity.Agent;
import com.example.demo.jpas.AgentJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentServiceNew {

    private final AgentJPA agentJPA;
    private final AgentEmailService agentEmailService;
    private final AgentDeactivateEmailService agentDeactivateEmailService;


    // ✅ 1. Trả danh sách đại lý đang hoạt động (isActive = true), phân trang, ID giảm dần
    public Page<AgentDTONew> getAgentsPage(Pageable pageable) {
        return agentJPA.findByIsActiveTrueOrderByIdDesc(pageable)
                .map(this::toDTO);
    }

    //  2. Duyệt đại lý
//    public void approveAgent(Integer id) {
//        Agent agent = agentJPA.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));
//        agent.setIsApproved(true);
//        agentJPA.save(agent);
//    }
//  2. Duyệt đại lý và gửi email
    public void approveAgent(Integer id) {
        Agent agent = agentJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));

        agent.setIsApproved(true);
        agentJPA.save(agent);

        //  Gửi email cho người dùng đăng ký tài khoản
        if (agent.getUser() != null) {
            String email = agent.getUser().getEmail();       // email tài khoản
            String fullName = agent.getUser().getFullName(); // tên đầy đủ tài khoản
            agentEmailService.sendApprovedNotification(email, fullName);
        }
    }

    // ✅ 3. Xoá mềm đại lý bằng cách set isActive = false
//    public void deleteAgent(Integer id) {
//        Agent agent = agentJPA.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));
//        agent.setIsActive(false);
//        agentJPA.save(agent);
//    }
    public void deleteAgent(Integer id) {
        Agent agent = agentJPA.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đại lý"));

        agent.setIsActive(false);
        agentJPA.save(agent);

        // Gửi email thông báo ngừng hoạt động
        if (agent.getUser() != null) {
            String email = agent.getUser().getEmail();       // email tài khoản
            String fullName = agent.getUser().getFullName(); // tên đầy đủ tài khoản
            agentDeactivateEmailService.sendDeactivationNotification(email, fullName);
        }
    }

    //  4. Không hỗ trợ cập nhật thông tin đại lý (nghiệp vụ không cho phép)
    public void updateAgent(Integer id, AgentDTONew updatedDTO) {
        throw new UnsupportedOperationException("Admin không được phép cập nhật thông tin đại lý.");
    }

    // ✅ 5. Chuyển Agent Entity sang AgentDTONew
    private AgentDTONew toDTO(Agent agent) {
        AgentDTONew dto = new AgentDTONew();
        dto.setId(agent.getId());
        dto.setAgentName(agent.getAgentName());
        dto.setPhone(agent.getPhone());
        dto.setReceiverName(agent.getReceiverName());
        dto.setAddressDetail(agent.getAddressDetail());
        dto.setEmail(agent.getEmail());
        dto.setIsApproved(agent.getIsApproved());
        dto.setCreatedAt(agent.getCreatedAt());

        dto.setProvinceName(agent.getProvinceName());
        dto.setDistrictName(agent.getDistrictName());
        dto.setWardName(agent.getWardName());

        if (agent.getUser() != null) {
            dto.setUsername(agent.getUser().getUsername());
            dto.setFullName(agent.getUser().getFullName());
        }

        return dto;
    }
    //tìm kiếm theo email hoặc tên đại lý
    public Page<AgentDTONew> searchAgents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return agentJPA.findByIsActiveTrueOrderByIdDesc(pageable) // Nếu không nhập keyword thì trả danh sách active
                    .map(this::toDTO);
        }
        return agentJPA.searchByEmailOrName(keyword.trim(), pageable)
                .map(this::toDTO);
    }
}
