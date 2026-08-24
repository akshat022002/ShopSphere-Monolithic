package com.akshat.shopsphere.order.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.order.dto.OrderResponse;
import com.akshat.shopsphere.order.dto.OrderSummaryResponse;
import com.akshat.shopsphere.order.service.OrderService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(Authentication authentication){

        UserDetailsImplementation userDetails=getAuthenticatedUser(authentication);

        OrderResponse response = orderService.placeOrder(userDetails.getId());

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order placed successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId,
                                                                   Authentication authentication){

        UserDetailsImplementation userDetails=getAuthenticatedUser(authentication);

        OrderResponse response = orderService.getOrderById(userDetails.getId(),orderId);

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order fetched successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getMyOrders(@RequestParam(defaultValue = "0")
                                                                               @Min(0) int page,

                                                                               @RequestParam(defaultValue = "10")
                                                                               @Min(1)@Max(20) int size,

                                                                               Authentication authentication
                                                                               ){

        UserDetailsImplementation userDetails=getAuthenticatedUser(authentication);

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<OrderSummaryResponse> response = orderService.getMyOrders(userDetails.getId(),pageable);

        ApiResponse<Page<OrderSummaryResponse>> apiResponse = new ApiResponse<>(true,
                "Orders fetched successfully",
                response
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId,
                                                                  Authentication authentication){

        UserDetailsImplementation userDetails=getAuthenticatedUser(authentication);

        OrderResponse response = orderService.cancelOrder(userDetails.getId(),orderId);

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order cancelled successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }






    private UserDetailsImplementation getAuthenticatedUser(Authentication authentication){
        return (UserDetailsImplementation) authentication.getPrincipal();
    }
}
