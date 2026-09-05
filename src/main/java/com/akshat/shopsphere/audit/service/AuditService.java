package com.akshat.shopsphere.audit.service;

import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;

public interface AuditService {

    void saveAuditLog(Long userId,
                      AuditAction action,
                      AuditEntityType entityType,
                      Long entityId);
}
