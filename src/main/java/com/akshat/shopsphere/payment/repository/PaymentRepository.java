package com.akshat.shopsphere.payment.repository;


import com.akshat.shopsphere.payment.entity.Payment;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

        Optional<Payment> findByOrderId(Long orderId);

        Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

        Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

        boolean existsByOrderId(Long orderId);

        boolean existsByRazorpayOrderId(String razorpayOrderId);

        boolean existsByRazorpayPaymentId(String razorpayPaymentId);

        @Query("""
                SELECT COALESCE(SUM(p.amount),0)
                FROM Payment p
                WHERE p.status = :status
                """)
        BigDecimal getTotalAmountByStatus(@Param("status") PaymentStatus status);// Added in Dashboard Module
}
