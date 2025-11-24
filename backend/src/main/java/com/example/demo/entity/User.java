
package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "agent")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private Integer role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    //    @OneToMany(mappedBy = "user")
//    @JsonManagedReference
//    private List<Order> orders;
    @OneToMany(mappedBy = "user")
    @JsonBackReference(value = "order-user")
    private List<Order> orders;
    // Các quan hệ khác giữ nguyên
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Address> addresses;

    @OneToOne(mappedBy = "user")
    @JsonBackReference
    private Agent agent;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Cart> carts;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<ProductComment> productComments;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<OrderProductRating> orderProductRatings;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<NewsComment> newsComments;
}
