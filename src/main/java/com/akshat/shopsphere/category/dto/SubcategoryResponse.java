package com.akshat.shopsphere.category.dto;

import java.time.LocalDateTime;

public record SubcategoryResponse(
        Long id,
        String name,
        String description,
        Boolean active,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
