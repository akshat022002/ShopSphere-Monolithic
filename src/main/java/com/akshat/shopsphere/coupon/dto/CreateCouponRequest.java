package com.akshat.shopsphere.coupon.dto;

import com.akshat.shopsphere.coupon.enums.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(

        @NotBlank
        @Size(max = 20)
        String code,

        @NotNull
        DiscountType discountType,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal discountValue,

        @DecimalMin("0.00")
        BigDecimal minimumOrderAmount,

        @DecimalMin("0.00")
        BigDecimal maximumDiscount,

        @Future
        @NotNull
        LocalDateTime expiresAt,

        @NotNull
        @Min(1)
        Integer usageLimit,

        Long productId

) {
}
