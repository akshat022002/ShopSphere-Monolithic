package com.akshat.shopsphere.payment.dto;

import com.akshat.shopsphere.payment.enums.PaymentMethod;
import com.akshat.shopsphere.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long paymentId,
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String razorpayOrderId,
        String razorpayPaymentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
