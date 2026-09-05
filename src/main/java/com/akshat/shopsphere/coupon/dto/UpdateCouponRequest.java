package com.akshat.shopsphere.coupon.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateCouponRequest(

        @DecimalMin("0.01")
        BigDecimal discountValue,

        @DecimalMin("0.00")
        BigDecimal minimumOrderAmount,

        @DecimalMin("0.00")
        BigDecimal maximumDiscount,

        @Future
        LocalDateTime expiresAt,

        @Min(1)
        Integer usageLimit,

        Boolean active
) {
}
