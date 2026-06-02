package com.mayis.audit_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponseDto(
        UUID id,
        UUID eventId,
        String eventType,
        String topic,
        String sourceService,
        String aggregateType,
        UUID aggregateId,
        UUID actorUserId,
        LocalDateTime occurredAt,
        LocalDateTime receivedAt,
        String payload
) {
}
