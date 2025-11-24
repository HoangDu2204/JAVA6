package com.example.demo.services;

import com.example.demo.dto.CancelRequestDTO;
import com.example.demo.entity.CancelRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.jpas.CancelRequestJPA;
import com.example.demo.jpas.OrderJPA;
import com.example.demo.jpas.UserJPA;
import com.example.demo.jwt.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CancelRequestService {

    private final CancelRequestJPA cancelRequestJPA;
    private final OrderJPA orderRepository;
    private final UserJPA userRepository;

    private final HttpServletRequest request;
    private final AuthHelper authHelper;
    private final CancelOrderEmailService cancelOrderEmailService;
    public CancelRequest createCancelRequest(CancelRequestDTO dto) {
        // Lấy user hiện tại từ token
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            throw new RuntimeException("Không thể xác thực người dùng từ token");
        }

        // Lấy order từ ID
        Optional<Order> orderOpt = orderRepository.findById(dto.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        CancelRequest requestEntity = CancelRequest.builder()
                .order(orderOpt.get())
                .user(user)
                .reason(dto.getReason())
                .status("Chờ duyệt")
                .createdAt(LocalDateTime.now())
                .build();

        return cancelRequestJPA.save(requestEntity);
    }

    public List<CancelRequest> getAllRequests() {
        return cancelRequestJPA.findAll();
    }

    public List<CancelRequest> getRequestsByUser() {
        // Lấy user từ token
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            throw new RuntimeException("Không thể xác thực người dùng từ token");
        }

        return cancelRequestJPA.findByUserId(user.getId());
    }

//    public void approveRequest(Integer requestId) {
//        CancelRequest request = cancelRequestJPA.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hủy"));
//        request.setStatus("Đã duyệt");
//        cancelRequestJPA.save(request);
//    }
//
//    public void rejectRequest(Integer requestId) {
//        CancelRequest request = cancelRequestJPA.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hủy"));
//        request.setStatus("Từ chối");
//        cancelRequestJPA.save(request);
//    }
public void approveRequest(Integer requestId) {
    CancelRequest request = cancelRequestJPA.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hủy"));
    request.setStatus("Đã duyệt");
    cancelRequestJPA.save(request);

    request.getOrder().setOrderStatus("Đã hủy");
    orderRepository.save(request.getOrder());

    cancelOrderEmailService.sendCancelApprovedEmail(request.getUser(), request.getOrder());
}

    public void rejectRequest(Integer requestId) {
        CancelRequest request = cancelRequestJPA.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu hủy"));
        request.setStatus("Từ chối");
        cancelRequestJPA.save(request);

        cancelOrderEmailService.sendCancelRejectedEmail(request.getUser(), request.getOrder());
    }

}
