package com.akshat.shopsphere.dashboard.dto;

import java.math.BigDecimal;

public record SellerDashboardResponse(
        long totalProducts,
        long activeProducts,
        long totalOrders,
        BigDecimal totalRevenue,
        long pendingOrders,
        long deliveredOrders
) {
}
