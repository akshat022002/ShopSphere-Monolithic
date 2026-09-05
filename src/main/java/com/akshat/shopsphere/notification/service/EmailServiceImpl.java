package com.akshat.shopsphere.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);

        log.info("Email sent successfully to {} with subject '{}'",to,subject);

    }

    @Override
    public void sendOrderConfirmationEmail(String to, Long orderId, BigDecimal amount) {

        String subject = "ShopSphere - Order Confirmation";

        String body = """
                Hello,
                
                Your shopsphere order has been placed successfully
                
                Order ID : %d
                Amount : ₹%s
                
                Thanks for shopping with us.
                
                Regards,
                Shopsphere Team
                
                """.formatted(orderId,amount);

        sendEmail(to,subject,body);
    }

    @Override
    public void sendPaymentSuccessEmail(String to, Long orderId, BigDecimal amount, String razorpayPaymentId) {

        String subject = "ShopSphere - Payment Successful";

        String body = """
                Hello,
                
                Your payment for shopsphere order %d was successful.
                
                Amount Paid : ₹%s
                Razorpay Payment ID : %s
                
                Your order is now confirmed.
                Thanks for shopping with us.
                
                Regards,
                Shopsphere Team
                
                """.formatted(orderId,amount,razorpayPaymentId);

        sendEmail(to,subject,body);
    }

    @Override
    public void sendPaymentFailureEmail(String to, Long orderId, BigDecimal amount) {

        String subject = "ShopSphere - Payment Failed";

        String body = """
            Hello,

            Your payment for ShopSphere order #%d has failed.

            Amount: ₹%s

            Please try the payment again.

            Regards,
            ShopSphere Team
            """.formatted(orderId, amount);

        sendEmail(to,subject,body);
    }

    @Override
    public void sendOrderCancellationEmail(String to, Long orderId) {

        String subject = "ShopSphere - Order Cancelled";

        String body = """
            Hello,

            Your ShopSphere order #%d has been cancelled successfully.

            If a payment was already made, the refund will be
            processed according to the applicable refund process.

            Regards,
            ShopSphere Team
            """.formatted(orderId);

        sendEmail(to,subject,body);
    }
}
