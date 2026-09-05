package com.akshat.shopsphere.coupon.entity;

import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "coupon_usages",
       uniqueConstraints = {
        @UniqueConstraint(name = "uk_coupon_user",columnNames = {"coupon_id","user_id"})
       })
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "coupon_id",nullable = false)
    private Coupon coupon;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime usedAt=LocalDateTime.now();

}
