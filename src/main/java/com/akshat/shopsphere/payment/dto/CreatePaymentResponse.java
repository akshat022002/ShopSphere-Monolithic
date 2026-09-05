package com.akshat.shopsphere.payment.dto;

import com.akshat.shopsphere.payment.enums.PaymentMethod;
import com.akshat.shopsphere.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponse(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String razorpayOrderId
) {
}
