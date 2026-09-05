package com.akshat.shopsphere.order.dto;

import com.akshat.shopsphere.common.audit.AuditableResponse;
import com.akshat.shopsphere.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        String couponCode,
        BigDecimal discount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) implements AuditableResponse {
}
