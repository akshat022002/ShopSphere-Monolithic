package com.akshat.shopsphere.audit.service;

import com.akshat.shopsphere.audit.entity.AuditLog;
import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import com.akshat.shopsphere.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService{

    private final AuditLogRepository auditLogRepository;

    @Override
    public void saveAuditLog(Long userId,
                             AuditAction action,
                             AuditEntityType entityType,
                             Long entityId) {


        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityId(entityId)
                .entityType(entityType)
                .build();

        auditLogRepository.save(auditLog);

        log.info( "Audit log saved: action={}, entityType={}, entityId={}, userId={}",
                action, entityType, entityId, userId);

    }
}
