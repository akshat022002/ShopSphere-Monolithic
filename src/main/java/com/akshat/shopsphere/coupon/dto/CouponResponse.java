package com.akshat.shopsphere.coupon.dto;

import com.akshat.shopsphere.coupon.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(

        Long id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minimumOrderAmount,
        BigDecimal maximumDiscount,
        LocalDateTime expiresAt,
        Integer usageLimit,
        Integer usageCount,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long productId

) {
}
