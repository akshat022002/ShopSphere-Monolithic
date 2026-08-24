package com.akshat.shopsphere.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "product_images",
        indexes = {
        @Index(name = "idx_product_image_product",columnList = "product_id"),
        @Index(name = "idx_product_image_primary",columnList = "is_primary")
        })
public class ProductImage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false,length = 500)
        private String imageUrl;

        @Column(nullable = false,length = 250)
        private String storageKey;

        @Column(nullable = false,name = "is_primary")
        @Builder.Default
        private boolean isPrimary=false;

        @Column(nullable = false,updatable = false)
        @Builder.Default
        private LocalDateTime createdAt=LocalDateTime.now();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id",nullable = false)
        private Product product;

}
