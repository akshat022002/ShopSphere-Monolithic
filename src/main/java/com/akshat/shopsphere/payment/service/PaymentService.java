package com.akshat.shopsphere.payment.service;

import com.akshat.shopsphere.payment.dto.CreatePaymentResponse;
import com.akshat.shopsphere.payment.dto.PaymentResponse;
import com.akshat.shopsphere.payment.dto.VerifyPaymentRequest;

public interface PaymentService {

    CreatePaymentResponse createPayment(Long userId,Long orderId);

    PaymentResponse verifyPayment(Long userId, VerifyPaymentRequest request);

}
