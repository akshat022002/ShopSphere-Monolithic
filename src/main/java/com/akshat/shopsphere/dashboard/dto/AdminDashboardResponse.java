package com.akshat.shopsphere.dashboard.dto;

import java.math.BigDecimal;

public record AdminDashboardResponse(
        long totalUsers,
        long totalSellers,
        long totalCustomers,
        long totalProducts,
        long totalOrders,
        BigDecimal totalRevenue,
        long pendingOrders,
        long cancelledOrders
) {
}
