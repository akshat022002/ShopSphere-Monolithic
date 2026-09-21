package com.akshat.shopsphere.payment.service;

import com.akshat.shopsphere.payment.config.RazorpayProperties;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService{

    private final RazorpayProperties razorpayProperties;

    @Override
    public String createOrder(String receipt, BigDecimal amount) {

        try{
            RazorpayClient razorpayClient = new RazorpayClient(razorpayProperties.keyId(),
                                                               razorpayProperties.keySecret());

            JSONObject orderRequest = new JSONObject();

            long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValueExact();

            orderRequest.put("amount",amountInPaise);
            orderRequest.put("currency","INR");
            orderRequest.put("receipt",receipt);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            log.info("Razorpay order created successfully : {}",razorpayOrder.get("id").toString());

            return razorpayOrder.get("id");

        } catch (RazorpayException e){

            log.error("Failed to create razorpay order for receipt : {} ",receipt,e);

            throw new RuntimeException("Unable to create razorpay order");

        }

    }

    @Override
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {

        try{

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id",razorpayOrderId);
            attributes.put("razorpay_payment_id",razorpayPaymentId);
            attributes.put("razorpay_signature",razorpaySignature);

            return Utils.verifyPaymentSignature(attributes,razorpayProperties.keySecret());

        } catch (RazorpayException e){

            log.error("Razorpay payment signature verification failed ",e);

            return false;
        }

    }
}
