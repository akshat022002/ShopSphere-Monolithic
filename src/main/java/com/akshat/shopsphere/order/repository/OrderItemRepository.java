package com.akshat.shopsphere.order.repository;

import com.akshat.shopsphere.order.entity.OrderItem;
import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("""
    SELECT COUNT(oi) > 0
    FROM OrderItem oi
    WHERE oi.order.user.id = :userId
      AND oi.product.id = :productId
      AND oi.order.status = :status
    """)
    boolean existsByUserAndProductAndOrderStatus(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("status") OrderStatus status
    );

    @Query("""
    SELECT COALESCE(SUM(oi.subtotal), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE oi.product.seller.id = :sellerId
      AND o.status = :status
    """)
    BigDecimal getSellerRevenue(@Param("sellerId") Long sellerId, @Param("status")OrderStatus status);

}
