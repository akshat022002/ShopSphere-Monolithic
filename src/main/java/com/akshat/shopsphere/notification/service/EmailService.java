package com.akshat.shopsphere.notification.service;

import java.math.BigDecimal;

public interface EmailService {

     void sendEmail(String to, String subject, String body);

     void sendOrderConfirmationEmail(String to, Long orderId, BigDecimal amount);

     void sendPaymentSuccessEmail(String to, Long orderId,BigDecimal amount,String razorpayPaymentId);

     void sendPaymentFailureEmail(String to ,Long orderId, BigDecimal amount);

     void sendOrderCancellationEmail(String to,Long orderId);
}
