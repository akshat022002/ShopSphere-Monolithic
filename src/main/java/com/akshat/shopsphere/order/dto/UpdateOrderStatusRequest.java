package com.akshat.shopsphere.order.dto;

import com.akshat.shopsphere.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatus status
) {
}
