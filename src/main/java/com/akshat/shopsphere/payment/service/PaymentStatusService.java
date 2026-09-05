package com.akshat.shopsphere.payment.service;

import com.akshat.shopsphere.audit.annotation.Audit;
import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.payment.entity.Payment;
import com.akshat.shopsphere.payment.enums.PaymentStatus;
import com.akshat.shopsphere.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentRepository paymentRepository;

    @Audit(action = AuditAction.PAYMENT_FAILED,entityType = AuditEntityType.PAYMENT,entityIdParam = "paymentId")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPaymentFailed(Long paymentId,String reason){

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Payment not found with id : "+paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

    }

    @Audit(action = AuditAction.PAYMENT_SUCCESS,entityType = AuditEntityType.PAYMENT,entityIdParam = "paymentId")
    @Transactional
    public void markPaymentSuccess(Long paymentId,String razorpayPaymentId){

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Payment not found with id : "+paymentId));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setFailureReason(null);

    }
}
