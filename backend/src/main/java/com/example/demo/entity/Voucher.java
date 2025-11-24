//package com.example.demo.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Table(name = "voucher")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Voucher {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "voucher_id")
//    private Integer voucherId;
//
//    @Column(name = "code", nullable = false, length = 50)
//    private String code;
//
//    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
//    private BigDecimal discountPercent;
//
//    @Column(name = "max_discount_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal maxDiscountAmount;
//
//    @Column(name = "min_order_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal minOrderAmount;
//
//    @Column(name = "created_date", nullable = false)
//    private LocalDateTime createdDate;
//
//    @Column(name = "end_date", nullable = false)
//    private LocalDateTime endDate;
//
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive;
//
//    @Column(name = "quantity", nullable = false)
//    private Integer quantity;
//
//    // Liên kết 1 voucher có thể được dùng trong nhiều đơn hàng
//    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Order> orders;
//}
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "voucher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "max_discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;
}
