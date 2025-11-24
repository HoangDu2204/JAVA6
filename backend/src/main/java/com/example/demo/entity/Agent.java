
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Đổi từ agentId thành id

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @Column(name = "agent_name", nullable = false)
    private String agentName;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "agent-agentDiscount")
    private List<AgentDiscount> agentDiscounts;

    //  Các trường bổ sung bên dưới đây:

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "province_id", nullable = false)
    private Integer provinceId;

    @Column(name = "province_name", nullable = false)
    private String provinceName;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "district_name", nullable = false)
    private String districtName;

    @Column(name = "ward_id", nullable = false)
    private Integer wardId;

    @Column(name = "ward_name", nullable = false)
    private String wardName;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
