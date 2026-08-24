package com.akshat.shopsphere.product.dto;

import java.time.LocalDateTime;

public record ProductImageResponse(
        Long id,

        String imageUrl,

        boolean isPrimary,

        LocalDateTime createdAt
) {
}
