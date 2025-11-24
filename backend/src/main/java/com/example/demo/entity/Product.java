package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    //    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    @JsonBackReference
//    private Category category;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference("category-product")
    private Category category;

    //    @OneToMany(mappedBy = "product")
//    @JsonManagedReference
//    private List<ProductVariant> productVariants;
    @OneToMany(mappedBy = "product")
    @JsonBackReference(value = "variant-product")
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference(value = "product-discount")
    private List<ProductDiscount> productDiscounts;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-image")
    private List<Image> images;
}
