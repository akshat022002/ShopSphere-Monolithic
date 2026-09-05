package com.akshat.shopsphere.review.entity;

import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "reviews"
      // uniqueConstraints = {
       // @UniqueConstraint(name = "uk_review_user_product",columnNames = {"user_id","product_id"})}

//         this above unique constraints of class is removed because we have added unique constraints through our
//        sql query because customer was not able to add review for non active products also

        // -> new  implementation -> CREATE UNIQUE INDEX uk_review_active_user_product
        //ON reviews (user_id, product_id)
        //WHERE active = true;
       )
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate(){
        updatedAt=LocalDateTime.now();
    }
}
