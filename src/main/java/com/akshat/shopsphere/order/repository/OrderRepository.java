package com.akshat.shopsphere.order.repository;

import com.akshat.shopsphere.order.entity.Order;
import com.akshat.shopsphere.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId,Long userId);

    long countByStatus(OrderStatus status);// Added in Dashboard module to count orders by status

    @Query("""
            SELECT COUNT(DISTINCT oi.order.id)
            FROM OrderItem oi
            WHERE oi.product.seller.id = :sellerId
            """)
    long countOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("""
            SELECT COUNT(DISTINCT oi.order.id)
            FROM OrderItem oi
            WHERE oi.product.seller.id = :sellerId
            AND oi.order.status= :status
            """)
    long countOrdersBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") OrderStatus status);

}
