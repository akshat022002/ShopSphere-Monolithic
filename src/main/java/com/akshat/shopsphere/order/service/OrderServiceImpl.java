package com.akshat.shopsphere.order.service;

import com.akshat.shopsphere.cart.entity.Cart;
import com.akshat.shopsphere.cart.entity.CartItem;
import com.akshat.shopsphere.cart.repository.CartRepository;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.order.dto.OrderResponse;
import com.akshat.shopsphere.order.dto.OrderSummaryResponse;
import com.akshat.shopsphere.order.entity.Order;
import com.akshat.shopsphere.order.entity.OrderItem;
import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.order.mapper.OrderItemMapper;
import com.akshat.shopsphere.order.repository.OrderRepository;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponse placeOrder(Long userId) {

        User user = userRepository.findById(userId)
        .orElseThrow(()->
                new ResourceNotFoundException("User not found with id : "+userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Cart not found for user : "+userId));

        if(cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(CartItem cartItem : cart.getItems()){

            Product product = cartItem.getProduct();

            if(!product.isActive()){
                throw new ResourceNotFoundException("Product not found with id : "+product.getId());
            }

            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .price(price)
                    .product(product)
                    .subtotal(subtotal)
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getItems().add(orderItem);

            totalAmount= totalAmount.add(subtotal);

        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();

        return buildOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {

        Order order = orderRepository.findByIdAndUserId(orderId,userId).
                orElseThrow(()-> new  ResourceNotFoundException("Order not found"));

        return  buildOrderResponse(order);

    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderSummaryResponse> getMyOrders(Long userId, Pageable pageable) {

        return orderRepository.findAllByUserId(userId,pageable)
                .map(order -> new OrderSummaryResponse(order.getId()
                        ,order.getStatus()
                        ,order.getTotalAmount()
                        ,order.getCreatedAt()));
    }

    @Override
    public OrderResponse cancelOrder(Long userId,Long orderId) {

    Order order = orderRepository.findByIdAndUserId(orderId,userId)
            .orElseThrow(()->
                    new ResourceNotFoundException("Order not found"));

    if(order.getStatus()!=OrderStatus.PENDING && order.getStatus()!=OrderStatus.CONFIRMED){
        throw new IllegalStateException("Order cannot be cancelled during : "+order.getStatus());
    }

    order.setStatus(OrderStatus.CANCELLED);

    return buildOrderResponse(order);

    }

    private OrderResponse buildOrderResponse(Order order){

        var items = order.getItems()
                .stream()
                .map(orderItemMapper::toResponse)
                .toList();

        return new OrderResponse(order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt());

    }
}
