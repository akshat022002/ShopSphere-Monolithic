package com.akshat.shopsphere.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories",
        indexes = {
                @Index(name="idx_category_name",columnList = "name")
        })
public class Category {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false,unique = true,length = 100)
        private String name;

        @Column(length = 500)
        private String description;

        @Builder.Default
        @Column(nullable = false)
        private boolean active=true;

        @OneToMany(
                mappedBy = "category",
                cascade = CascadeType.ALL
        )
        @Builder.Default
        private List<Subcategory> subcategories = new ArrayList<>();

        @Column(nullable = false,updatable = false)
        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();

        @Column(nullable = false)
        @Builder.Default
        private LocalDateTime updatedAt = LocalDateTime.now();

        @PreUpdate
        protected void onUpdate(){
                updatedAt=LocalDateTime.now();
        }





}
