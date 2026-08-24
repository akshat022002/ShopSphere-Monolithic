package com.akshat.shopsphere.order.dto;

import com.akshat.shopsphere.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
