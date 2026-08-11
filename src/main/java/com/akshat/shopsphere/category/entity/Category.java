package com.akshat.shopsphere.category.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

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



}
