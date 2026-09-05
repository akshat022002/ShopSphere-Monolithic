package com.akshat.shopsphere.audit.entity;

import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_logs",
       indexes = {
        @Index(name = "idx_audit_user",columnList = "user_id"),
        @Index(name = "idx_audit_created_at",columnList = "created_at")
       })
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private AuditEntityType entityType;

    private Long entityId;

    @Builder.Default
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

}
