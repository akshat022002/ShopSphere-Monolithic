package com.akshat.shopsphere.order.service;

import com.akshat.shopsphere.audit.annotation.Audit;
import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import com.akshat.shopsphere.cart.entity.Cart;
import com.akshat.shopsphere.cart.entity.CartItem;
import com.akshat.shopsphere.cart.repository.CartRepository;
import com.akshat.shopsphere.common.exception.BadRequestException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.coupon.service.CouponService;
import com.akshat.shopsphere.inventory.service.InventoryService;
import com.akshat.shopsphere.notification.service.EmailService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderItemMapper orderItemMapper;
    private final InventoryService inventoryService;
    private final CouponService couponService;
    private final EmailService emailService;

    @Audit(action = AuditAction.ORDER_CREATED,entityType = AuditEntityType.ORDER)
    @Override
    public OrderResponse placeOrder(Long userId,String couponCode) {

        User user = userRepository.findById(userId)
        .orElseThrow(()->
                new ResourceNotFoundException("User not found with id : "+userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Cart not found for user : "+userId));

        if(cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;

        if(couponCode!=null && !couponCode.isBlank()){

            discount = couponService.calculateDiscount(couponCode,cart.getItems());

        }

        BigDecimal totalAmount = subtotal.subtract(discount);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .subtotal(subtotal)
                .discount(discount)
                .items(new ArrayList<>())
                .couponCode(
                        couponCode!=null && !couponCode.isBlank()
                        ? couponCode.trim().toUpperCase() : null )
                .build();



        for(CartItem cartItem : cart.getItems()){

            Product product = cartItem.getProduct();

            if(!product.isActive()){
                throw new ResourceNotFoundException("Product not found with id : "+product.getId());
            }
            //DECREASING STOCK
            inventoryService.decreaseStockForOrder(product.getId(),cartItem.getQuantity());

            BigDecimal price = product.getPrice();

            BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .price(price)
                    .product(product)
                    .subtotal(itemSubtotal)
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getItems().add(orderItem);

        }

        Order savedOrder = orderRepository.save(order);

        try{
            emailService.sendOrderConfirmationEmail(user.getEmail(),
                    savedOrder.getId(),
                    savedOrder.getTotalAmount());

        } catch (Exception e){
            log.error("Failed to send order confirmation email for order {}",savedOrder.getId(),e);
        }

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

    @Audit(action = AuditAction.ORDER_CANCELLED,entityType = AuditEntityType.ORDER,entityIdParam = "orderId")
    @Override
    public OrderResponse cancelOrder(Long userId,Long orderId) {

    Order order = orderRepository.findByIdAndUserId(orderId,userId)
            .orElseThrow(()->
                    new ResourceNotFoundException("Order not found"));

    if(order.getStatus()!=OrderStatus.PENDING && order.getStatus()!=OrderStatus.CONFIRMED){
        throw new IllegalStateException("Order cannot be cancelled during : "+order.getStatus());
    }

    // RESTORATION OF STOCK AFTER CANCELLATION
    for(OrderItem item: order.getItems()){
        inventoryService.increaseStockForOrder(item.getProduct().getId(),item.getQuantity());
    }

    order.setStatus(OrderStatus.CANCELLED);

    try{
        emailService.sendOrderCancellationEmail(order.getUser().getEmail(),orderId);
    } catch (Exception e){

        log.error("Failed to send cancellation email for order {}",orderId,e);
    }

    return buildOrderResponse(order);

    }

    // Added in Production hardening module
    @Audit(action = AuditAction.ORDER_STATUS_UPDATED,entityType = AuditEntityType.ORDER,entityIdParam = "orderId")
    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found with id : "+orderId));

        OrderStatus currentStatus = order.getStatus();

        if(!isTransitionValid(currentStatus,newStatus)){
            throw new BadRequestException("Order cannot transition from "+ currentStatus+" to "+newStatus);
        }

        order.setStatus(newStatus);

        try {
            emailService.sendOrderStatusUpdateEmail(order.getUser().getEmail(),orderId,newStatus);
        } catch (Exception e){
            log.error("Failed to send order status email for orderId : {}",orderId,e);
        }
    }

    private boolean isTransitionValid(OrderStatus current,OrderStatus next){

       return switch(current){

           case PENDING -> next==OrderStatus.CANCELLED;

           case CONFIRMED -> next==OrderStatus.PROCESSING || next==OrderStatus.CANCELLED;

           case PROCESSING -> next==OrderStatus.SHIPPED || next==OrderStatus.CANCELLED;

           case SHIPPED -> next==OrderStatus.DELIVERED;

           case DELIVERED,CANCELLED -> false;
        };

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
                order.getCouponCode(),
                order.getDiscount(),
                order.getCreatedAt(),
                order.getUpdatedAt());

    }
}
