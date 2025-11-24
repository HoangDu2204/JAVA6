package com.example.demo.jpas;

import com.example.demo.entity.AgentDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AgentDiscountJPA extends JpaRepository<AgentDiscount, Integer> {

    // Lấy danh sách chiết khấu của 1 đại lý (hiện bạn đang dùng)
    List<AgentDiscount> findByAgentIdOrderByIdDesc(Integer agentId);

    // ✅ Truy vấn kiểm tra trùng khoảng thời gian áp dụng
    @Query("""
        SELECT ad FROM AgentDiscount ad
        WHERE ad.agent.id = :agentId
          AND ad.isActive = true
          AND (
            (:startDate BETWEEN ad.startDate AND ad.endDate)
            OR (:endDate BETWEEN ad.startDate AND ad.endDate)
            OR (:startDate <= ad.startDate AND :endDate >= ad.endDate)
          )
    """)
    List<AgentDiscount> findOverlappingDiscounts(
            @Param("agentId") Integer agentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("SELECT ad FROM AgentDiscount ad " +
            "WHERE LOWER(ad.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<AgentDiscount> searchByDescription(@Param("keyword") String keyword, Pageable pageable);@Query("""
    SELECT ad FROM AgentDiscount ad
    WHERE ad.agent.id = :agentId
      AND (:keyword IS NULL OR LOWER(ad.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<AgentDiscount> findByAgentIdAndDescription(
                    @Param("agentId") Integer agentId,
                    @Param("keyword") String keyword,
                    Pageable pageable
            );
}
