package com.akshat.shopsphere.payment.service;

import java.math.BigDecimal;

public interface RazorpayService {

    String createOrder(String receipt, BigDecimal amount);

    boolean verifyPaymentSignature(String razorpayOrderId,
                                   String razorpayPaymentId,
                                   String razorpaySignature);

}


