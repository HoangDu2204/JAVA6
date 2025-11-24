package com.example.demo.services;

import com.example.demo.dto.AgentRegisterRequest;
import com.example.demo.entity.Agent;
import com.example.demo.entity.User;
import com.example.demo.jpas.AgentJPA;
import com.example.demo.jwt.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgentRegisterService {

    private final AgentJPA agentJPA;
    private final AgentOtpService otpService;
    private final AuthHelper authHelper; // ✅ helper để lấy user từ JWT

    @Transactional
    public void registerAgent(AgentRegisterRequest req, HttpServletRequest request) {
        // ✅ Lấy user từ JWT
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            throw new RuntimeException("Không xác định được người dùng từ token.");
        }



        // ✅ Kiểm tra đã đăng ký làm đại lý chưa
        if (agentJPA.findByUser(user).isPresent()) {
            throw new RuntimeException("Người dùng đã đăng ký làm đại lý.");
        }

        // ✅ Tạo agent mới
        Agent agent = new Agent();
        agent.setUser(user);
        agent.setAgentName(req.getAgentName());
        agent.setPhone(req.getPhone());
        agent.setReceiverName(req.getReceiverName());
        agent.setProvinceId(req.getProvinceId());
        agent.setProvinceName(req.getProvinceName());
        agent.setDistrictId(req.getDistrictId());
        agent.setDistrictName(req.getDistrictName());
        agent.setWardId(req.getWardId());
        agent.setWardName(req.getWardName());
        agent.setAddressDetail(req.getAddressDetail());
        agent.setEmail(req.getEmail());

        agent.setIsApproved(false);
        agent.setIsActive(true);
        agent.setCreatedAt(LocalDateTime.now());

        agentJPA.save(agent);
    }
}
