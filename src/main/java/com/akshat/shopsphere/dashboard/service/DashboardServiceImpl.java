package com.akshat.shopsphere.dashboard.service;

import com.akshat.shopsphere.dashboard.dto.AdminDashboardResponse;
import com.akshat.shopsphere.dashboard.dto.SellerDashboardResponse;
import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.order.repository.OrderItemRepository;
import com.akshat.shopsphere.order.repository.OrderRepository;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import com.akshat.shopsphere.payment.repository.PaymentRepository;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.enums.Role;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {

        long totalUsers = userRepository.countByDeletedFalse();

        long totalSellers = userRepository.countByRoleAndDeletedFalse(Role.ROLE_SELLER);

        long totalCustomers = userRepository.countByRoleAndDeletedFalse(Role.ROLE_CUSTOMER);

        long totalProducts = productRepository.countByActiveTrue();

        long totalOrders = orderRepository.count();

        BigDecimal totalRevenue = paymentRepository.getTotalAmountByStatus(PaymentStatus.SUCCESS);

        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);

        long cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        return new AdminDashboardResponse(
                totalUsers,
                totalSellers,
                totalCustomers,
                totalProducts,
                totalOrders,
                totalRevenue,
                pendingOrders,
                cancelledOrders);

    }

    @Override
    @Transactional(readOnly = true)
    public SellerDashboardResponse getSellerDashboard(Long sellerId) {

        long totalProducts = productRepository.countBySellerId(sellerId);

        long activeProducts = productRepository.countBySellerIdAndActiveTrue(sellerId);

        long totalOrders = orderRepository.countOrdersBySellerId(sellerId);

        BigDecimal totalRevenue = orderItemRepository.getSellerRevenue(sellerId,OrderStatus.DELIVERED);

        long pendingOrders = orderRepository.countOrdersBySellerIdAndStatus(sellerId,OrderStatus.PENDING);

        long deliveredOrders = orderRepository.countOrdersBySellerIdAndStatus(sellerId,OrderStatus.DELIVERED);

        return new SellerDashboardResponse(
                totalProducts,
                activeProducts,
                totalOrders,
                totalRevenue,
                pendingOrders,
                deliveredOrders
                );

    }
}
