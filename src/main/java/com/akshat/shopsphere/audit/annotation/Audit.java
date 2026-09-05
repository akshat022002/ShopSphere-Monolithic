package com.akshat.shopsphere.audit.annotation;

import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audit {

    AuditAction action();

    AuditEntityType entityType();

    String entityIdParam() default "";
}
