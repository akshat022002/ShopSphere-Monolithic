package com.akshat.shopsphere.category.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponse(

        Long id,
        String name,
        String description,
        Boolean active,
        List<SubcategoryResponse> subcategories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
