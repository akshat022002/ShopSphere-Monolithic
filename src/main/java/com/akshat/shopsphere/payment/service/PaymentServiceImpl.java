package com.akshat.shopsphere.payment.service;

import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.coupon.entity.Coupon;
import com.akshat.shopsphere.coupon.entity.CouponUsage;
import com.akshat.shopsphere.coupon.repository.CouponRepository;
import com.akshat.shopsphere.coupon.repository.CouponUsageRepository;
import com.akshat.shopsphere.notification.service.EmailService;
import com.akshat.shopsphere.order.entity.Order;
import com.akshat.shopsphere.order.enums.OrderStatus;
import com.akshat.shopsphere.order.repository.OrderRepository;
import com.akshat.shopsphere.payment.dto.CreatePaymentResponse;
import com.akshat.shopsphere.payment.dto.PaymentResponse;
import com.akshat.shopsphere.payment.dto.VerifyPaymentRequest;
import com.akshat.shopsphere.payment.entity.Payment;
import com.akshat.shopsphere.payment.enums.PaymentMethod;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import com.akshat.shopsphere.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayService razorpayService;

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    private final EmailService emailService;
    private final PaymentStatusService paymentStatusService;

    @Override
    public CreatePaymentResponse createPayment(Long userId, Long orderId) {

        Order order = orderRepository.findByIdAndUserId(orderId,userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found with id : "+orderId));

        if(paymentRepository.existsByOrderId(orderId)){
            throw new DuplicateResourceException("Payment already exists for order : "+orderId);
        }

        String razorpayOrderId = razorpayService.createOrder("order_"+orderId,order.getTotalAmount());

        Payment payment = Payment.builder()
                .paymentMethod(PaymentMethod.RAZORPAY)
                .razorpayOrderId(razorpayOrderId)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.CREATED)
                .order(order)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return new  CreatePaymentResponse(savedPayment.getId(),
                savedPayment.getOrder().getId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentMethod(),
                savedPayment.getStatus(),
                savedPayment.getRazorpayOrderId());
    }


    @Override
    public PaymentResponse verifyPayment(Long userId, VerifyPaymentRequest request) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(()-> new
                  ResourceNotFoundException("Payment not found for razorpay order : "+request.razorpayOrderId()));

        if(!payment.getOrder().getUser().getId().equals(userId)){
            throw new AccessDeniedException("You are not authorized to verify this payment");
        }

        if(payment.getStatus()==PaymentStatus.SUCCESS){
            throw new DuplicateResourceException("Payment has already been verified");
        }


        boolean signatureValid = razorpayService.verifyPaymentSignature(request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature());

        if(!signatureValid){

           paymentStatusService.markPaymentFailed(payment.getId(),"Invalid Razorpay payment signature");
//             Created this markPaymentFailed method so that if exception occurs later , then status is set to
//             failed it does not roll back under Transaction.
            try{
                emailService.sendPaymentFailureEmail(payment.getOrder().getUser().getEmail(),
                        payment.getOrder().getId(),
                        payment.getAmount());
            } catch (Exception e){
                log.error("Failed to send payment failure email for order {}",payment.getOrder().getId(),e);
            }
            throw new IllegalArgumentException("Payment verification failed");

        }

        Order order = payment.getOrder();


        if(order.getStatus()!= OrderStatus.PENDING){
            throw new IllegalStateException("Order cannot be confirmed in status : "+order.getStatus());
        }

        if (order.getCouponCode() != null) {

            Coupon coupon = couponRepository.findByCodeIgnoreCase(order.getCouponCode())
                    .orElseThrow(()-> new ResourceNotFoundException("Coupon not found"));

            if(couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(),userId)){
                throw new IllegalStateException("Coupon has been already used by this customer");
            }

            CouponUsage couponUsage = CouponUsage.builder()
                    .coupon(coupon)
                    .user(order.getUser())
                    .build();

            couponUsageRepository.save(couponUsage);

            coupon.setUsageCount(coupon.getUsageCount()+1);

        }

        paymentStatusService.markPaymentSuccess(payment.getId(), request.razorpayPaymentId());

        order.setStatus(OrderStatus.CONFIRMED);


        try{
            emailService.sendPaymentSuccessEmail(order.getUser().getEmail()
                    ,order.getId()
                    ,payment.getAmount()
                    ,payment.getRazorpayPaymentId());
        } catch (Exception e){
            log.error("Failed to send payment success email for order {}",order.getId(),e);
        }

        return toPaymentResponse(payment);
    }

    private PaymentResponse toPaymentResponse(Payment payment){

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
