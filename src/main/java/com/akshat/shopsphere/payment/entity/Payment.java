package com.akshat.shopsphere.payment.entity;

import com.akshat.shopsphere.order.entity.Order;
import com.akshat.shopsphere.payment.enums.PaymentMethod;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments",
       indexes = {
        @Index(name = "idx_payment_order",columnList = "order_id"),
        @Index(name = "idx_payment_status",columnList = "status")
       })
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",
                unique = true,
                nullable = false)
    private Order order;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private PaymentStatus status;

    @Column(unique = true,length = 100)
    private String razorpayOrderId;

    @Column(unique = true,length = 100)
    private String razorpayPaymentId;

    @Column(length = 500)
    private String failureReason;

    @Column(nullable = false,updatable = false)
    @Builder.Default
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt=LocalDateTime.now();

    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

}
