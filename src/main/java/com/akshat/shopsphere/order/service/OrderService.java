package com.akshat.shopsphere.order.service;

import com.akshat.shopsphere.order.dto.OrderResponse;
import com.akshat.shopsphere.order.dto.OrderSummaryResponse;
import com.akshat.shopsphere.order.dto.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long userId,String couponCode);

    OrderResponse getOrderById(Long userId,Long orderId);

    Page<OrderSummaryResponse> getMyOrders(Long userId, Pageable pageable);

    OrderResponse cancelOrder(Long userId,Long orderId);
}
