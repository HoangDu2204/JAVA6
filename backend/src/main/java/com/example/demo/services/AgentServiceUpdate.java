package com.example.demo.services;

import com.example.demo.dto.AgentUpdateRequest;
import com.example.demo.entity.Agent;
import com.example.demo.entity.User;
import com.example.demo.jpas.AgentJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentServiceUpdate {

    private final AgentJPA agentJPA;

    public Agent updateAgent(User user, AgentUpdateRequest request) {
        Agent agent = agentJPA.findByUser(user)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đại lý cho user ID: " + user.getId()));

        // ✅ Cập nhật thông tin
        agent.setAgentName(request.getAgentName());
        agent.setPhone(request.getPhone());
        agent.setReceiverName(request.getReceiverName());
        agent.setProvinceId(request.getProvinceId());
        agent.setProvinceName(request.getProvinceName());
        agent.setDistrictId(request.getDistrictId());
        agent.setDistrictName(request.getDistrictName());
        agent.setWardId(request.getWardId());
        agent.setWardName(request.getWardName());
        agent.setAddressDetail(request.getAddressDetail());
        agent.setEmail(request.getEmail());

        Agent saved = agentJPA.save(agent);
        System.out.println("✅ Đã cập nhật đại lý cho user ID: " + user.getId());
        return saved;
    }

    public Agent getByUser(User user) {
        return agentJPA.findByUser(user)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy đại lý cho user ID: " + user.getId()));
    }

    // ✅ Kiểm tra người dùng có phải đại lý không
    public boolean isAgent(User user) {
        return agentJPA.findByUser(user).isPresent();
    }
}
