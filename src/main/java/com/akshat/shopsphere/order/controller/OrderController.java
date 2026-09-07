package com.akshat.shopsphere.order.controller;

import com.akshat.shopsphere.common.response.ApiResponse;
import com.akshat.shopsphere.common.security.CurrentUserProvider;
import com.akshat.shopsphere.order.dto.OrderResponse;
import com.akshat.shopsphere.order.dto.OrderSummaryResponse;
import com.akshat.shopsphere.order.dto.UpdateOrderStatusRequest;
import com.akshat.shopsphere.order.service.OrderService;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import jakarta.validation.Valid;
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
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@RequestParam(name = "couponCode",required = false) String couponCode,
                                                                     Authentication authentication){



        OrderResponse response = orderService
                .placeOrder(currentUserProvider.getUserId(authentication), couponCode);

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order placed successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId,
                                                                   Authentication authentication){


        OrderResponse response = orderService.getOrderById(currentUserProvider.getUserId(authentication), orderId);

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

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<OrderSummaryResponse> response = orderService
                .getMyOrders(currentUserProvider.getUserId(authentication),pageable);

        ApiResponse<Page<OrderSummaryResponse>> apiResponse = new ApiResponse<>(true,
                "Orders fetched successfully",
                response
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId,
                                                                  Authentication authentication){


        OrderResponse response = orderService.cancelOrder(currentUserProvider.getUserId(authentication), orderId);

        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>(true,
                "Order cancelled successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@RequestBody @Valid UpdateOrderStatusRequest request,
                                                          @PathVariable Long orderId){

        orderService.updateOrderStatus(orderId,request.status());

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Order status updated successfully",
                null,
                LocalDateTime.now()
                );

        return ResponseEntity.ok(apiResponse);

    }

}
