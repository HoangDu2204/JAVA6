package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    @JsonBackReference
//    private Product product;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference(value = "variant-product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_size", nullable = false)
    @JsonManagedReference(value = "variant-size")
    private Size size;


    @ManyToOne
    @JoinColumn(name = "id_flavor", nullable = false)
    @JsonManagedReference(value = "variant-flavor")
    private Flavor flavor;

    @ManyToOne
    @JoinColumn(name = "id_shape", nullable = false)
    @JsonManagedReference(value = "variant-shape")
    private Shape shape;

    @ManyToOne
    @JoinColumn(name = "id_origin", nullable = false)
    @JsonManagedReference(value = "variant-origin")
    private Origin origin;


    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "productVariant")
    @JsonBackReference(value = "orderItem-variant")
    private List<OrderItem> orderItems;

}
