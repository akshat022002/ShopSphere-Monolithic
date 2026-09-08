package com.akshat.shopsphere.ai.dto;

import java.math.BigDecimal;

public record ProductAiResponse(
        Long id,
        String name,
        BigDecimal price,
        String sku,
        Long categoryId,
        Long subcategoryId
) {
}
