package com.example.demo.jpas;

import com.example.demo.entity.Agent;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgentJPA extends JpaRepository<Agent, Integer> {

    Optional<Agent> findByUserId(Integer userId);
  //  Agent findByUser(User user);

    Page<Agent> findByIsActiveTrueOrderByIdDesc(Pageable pageable);
    Optional<Agent> findByUser(User user);

    @Query("SELECT a FROM Agent a " +
            "WHERE LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(a.agentName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Agent> searchByEmailOrName(@Param("keyword") String keyword, Pageable pageable);
}
