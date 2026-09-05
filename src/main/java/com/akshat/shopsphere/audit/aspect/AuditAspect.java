package com.akshat.shopsphere.audit.aspect;

import com.akshat.shopsphere.audit.annotation.Audit;
import com.akshat.shopsphere.audit.service.AuditService;
import com.akshat.shopsphere.common.audit.AuditableResponse;
import com.akshat.shopsphere.payment.dto.PaymentResponse;
import com.akshat.shopsphere.security.userdetails.UserDetailsImplementation;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(pointcut = "@annotation(audit)", returning = "result")
    public void audit(JoinPoint joinPoint,Object result, Audit audit ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;

        if(authentication!=null && authentication.getPrincipal() instanceof UserDetailsImplementation userDetails){

            userId= userDetails.getId();
        }

        Long entityId = extractEntityId(joinPoint, audit.entityIdParam(),result);

        auditService.saveAuditLog(userId,audit.action(),audit.entityType(),entityId);

    }

    private Long extractEntityId(JoinPoint joinPoint,String entityIdParam,Object result){

        if(!entityIdParam.isBlank()){

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            String [] parameterNames =  signature.getParameterNames();
            Object [] arguments = joinPoint.getArgs();

            for(int i =0;i<parameterNames.length;i++){

                if(entityIdParam.equals(parameterNames[i]) && arguments[i] instanceof Long id){
                    return id;
                }
            }
        }

        if(result instanceof AuditableResponse response){
            return response.id();
        }

        if(result instanceof PaymentResponse response){
            return response.paymentId();
        }
        return null;
    }
}
