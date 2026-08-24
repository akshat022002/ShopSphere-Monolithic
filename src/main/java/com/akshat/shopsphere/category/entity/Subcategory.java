package com.akshat.shopsphere.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subcategories",
       indexes = {
                @Index(name = "idx_subcategory_name",columnList = "name"),
                @Index(name = "idx_subcategory_category",columnList = "category_id")
       }    )
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length=100)
    private String name;

    @Column(nullable = false,length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active=true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

}
