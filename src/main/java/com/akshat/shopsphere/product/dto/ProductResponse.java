package com.akshat.shopsphere.product.dto;

import com.akshat.shopsphere.common.audit.AuditableResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse (
    Long id,
    String name,
    String description,
    BigDecimal price,
    boolean active,
    String sku,
    Integer stockQuantity,
    Long sellerId,
    Long categoryId,
    Long subcategoryId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) implements AuditableResponse {
}
