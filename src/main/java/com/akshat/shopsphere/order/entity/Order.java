package com.akshat.shopsphere.order.entity;

import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders",
       indexes = {
        @Index(name = "idx_order_user",columnList = "user_id"),
        @Index(name = "idx_order_status",columnList = "status")
       })
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private OrderStatus status;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL,
               mappedBy = "order",
               orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default                //Added in discount module
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2) //Added in discount module
    private BigDecimal subtotal;

    @Column(length = 20) //Added in discount module
    private String couponCode;

    @PreUpdate
    public void onUpdate(){
        updatedAt=LocalDateTime.now();
    }
}
