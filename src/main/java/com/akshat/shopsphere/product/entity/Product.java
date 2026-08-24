package com.akshat.shopsphere.product.entity;

import com.akshat.shopsphere.category.entity.Category;
import com.akshat.shopsphere.category.entity.Subcategory;
import com.akshat.shopsphere.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="products",
        indexes = {
                    @Index(name = "idx_product_sku",columnList = "sku"),
                    @Index(name = "idx_product_name",columnList = "name"),
                    @Index(name = "idx_product_active",columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 150)
    private String name;

    @Column(nullable = false,length = 2000)
    private String description;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal price;

    //Stock Keeping unit(sku)
    @Column(nullable = false,unique = true,length = 50)
    private String sku;

    @Column(nullable = false)
    @Builder.Default
    private boolean active=true;

    @JoinColumn(name = "seller_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id",nullable = false)
    private Subcategory subcategory;

    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }


}
