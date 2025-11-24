package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

//    @Column(name = "total_price", nullable = false)
//    private BigDecimal totalPrice;

    //    @ManyToOne
//    @JoinColumn(name = "order_id", nullable = false)
//    @JsonBackReference
//    private Order order;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference(value = "order-orderItems")
    private Order order;

    //    @ManyToOne
//    @JoinColumn(name = "product_variants_id", nullable = false)
//    @JsonBackReference
//    private ProductVariant productVariant;
    @ManyToOne
    @JoinColumn(name = "product_variants_id", nullable = false)
    @JsonManagedReference(value = "orderItem-variant")
    private ProductVariant productVariant;

}
