package com.akshat.shopsphere.product.dto;

import com.akshat.shopsphere.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    boolean active,
    String sku,
    Long sellerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
}
