package com.akshat.shopsphere.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(

        Long id,
        Long productId,
        Long userId,
        String username,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
