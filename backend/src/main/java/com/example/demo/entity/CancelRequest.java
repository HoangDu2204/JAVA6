package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancel_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String reason;

    private String status; // "Chờ duyệt", "Đã duyệt", "Từ chối"

    private LocalDateTime createdAt = LocalDateTime.now();
}
