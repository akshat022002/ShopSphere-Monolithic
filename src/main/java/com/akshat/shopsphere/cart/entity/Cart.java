package com.akshat.shopsphere.cart.entity;

import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carts",
        indexes = {
            @Index(name = "idx_cart_user",columnList = "user_id")
    }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL
            ,mappedBy = "cart"
            ,orphanRemoval = true
    )
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }


}
