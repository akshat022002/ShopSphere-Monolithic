package com.akshat.shopsphere.payment.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.payment.dto.CreatePaymentResponse;
import com.akshat.shopsphere.payment.dto.PaymentResponse;
import com.akshat.shopsphere.payment.dto.VerifyPaymentRequest;
import com.akshat.shopsphere.payment.service.PaymentService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@PreAuthorize("hasRole('CUSTOMER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPayment(@PathVariable Long orderId,
                                                                            Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        CreatePaymentResponse response = paymentService.createPayment(userDetails.getId(), orderId);

        ApiResponse<CreatePaymentResponse> apiResponse = new ApiResponse<>(true,
                "Payment created successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(@RequestBody VerifyPaymentRequest request,
                                                                      Authentication authentication){

        UserDetailsImplementation userDetails = getAuthenticatedUser(authentication);

        PaymentResponse response = paymentService.verifyPayment(userDetails.getId(),request);

        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>(true,
                "Payment verified successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication) {

        return (UserDetailsImplementation) authentication.getPrincipal();
    }

}
