package com.example.demo.jpas;

import com.example.demo.entity.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginJPA extends JpaRepository<Origin, Integer> {

    // Kiểm tra tồn tại theo tên (ignore case)
    boolean existsByNameIgnoreCase(String name);

    // Kiểm tra origin có đang được dùng trong productVariants
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END FROM ProductVariant pv WHERE pv.origin.id = :originId")
    boolean isUsedInProductVariants(@Param("originId") Integer originId);

}
