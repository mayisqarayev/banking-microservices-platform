package com.mayis.audit_service.repository;

import com.mayis.audit_service.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    boolean existsByEventId(UUID eventId);
}
