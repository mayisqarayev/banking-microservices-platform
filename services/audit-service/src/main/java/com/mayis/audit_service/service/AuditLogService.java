package com.mayis.audit_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayis.audit_service.dto.AuditLogResponseDto;
import com.mayis.audit_service.exception.AuditLogNotFoundException;
import com.mayis.audit_service.model.entity.AuditLog;
import com.mayis.audit_service.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            ObjectMapper objectMapper
    ) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void record(String topic, String payload) {
        JsonNode root;
        try {
            root = objectMapper.readTree(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Failed to parse audit event payload", exception);
        }

        UUID eventId = root.hasNonNull("eventId") ? UUID.fromString(root.get("eventId").asText()) : null;
        if (eventId == null) {
            throw new IllegalArgumentException("Audit event payload does not contain eventId");
        }

        if (auditLogRepository.existsByEventId(eventId)) {
            return;
        }

        String aggregateType = topic.contains(".")
                ? topic.substring(0, topic.indexOf('.')).toUpperCase()
                : topic.toUpperCase();
        String sourceService = "unknown";
        UUID aggregateId = null;

        if ("USER".equals(aggregateType)) {
            sourceService = "auth-service";
            if (root.hasNonNull("targetUserId")) {
                aggregateId = UUID.fromString(root.get("targetUserId").asText());
            } else if (root.hasNonNull("userId")) {
                aggregateId = UUID.fromString(root.get("userId").asText());
            }
        } else if ("CUSTOMER".equals(aggregateType)) {
            sourceService = "customer-service";
            if (root.hasNonNull("customerId")) {
                aggregateId = UUID.fromString(root.get("customerId").asText());
            }
        } else if ("ACCOUNT".equals(aggregateType)) {
            sourceService = "account-service";
            if (root.hasNonNull("accountId")) {
                aggregateId = UUID.fromString(root.get("accountId").asText());
            }
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setEventId(eventId);
        auditLog.setTopic(topic);
        auditLog.setEventType(topic);
        auditLog.setSourceService(sourceService);
        auditLog.setAggregateType(aggregateType);
        auditLog.setAggregateId(aggregateId);
        auditLog.setActorUserId(root.hasNonNull("actorUserId") ? UUID.fromString(root.get("actorUserId").asText()) : null);
        auditLog.setOccurredAt(root.hasNonNull("occurredAt")
                ? LocalDateTime.parse(root.get("occurredAt").asText())
                : LocalDateTime.now());
        auditLog.setPayload(payload);

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> getAll() {
        return auditLogRepository.findAll()
                .stream()
                .map(auditLog -> new AuditLogResponseDto(
                        auditLog.getId(),
                        auditLog.getEventId(),
                        auditLog.getEventType(),
                        auditLog.getTopic(),
                        auditLog.getSourceService(),
                        auditLog.getAggregateType(),
                        auditLog.getAggregateId(),
                        auditLog.getActorUserId(),
                        auditLog.getOccurredAt(),
                        auditLog.getReceivedAt(),
                        auditLog.getPayload()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AuditLogResponseDto getById(UUID id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new AuditLogNotFoundException("Audit log not found"));

        return new AuditLogResponseDto(
                auditLog.getId(),
                auditLog.getEventId(),
                auditLog.getEventType(),
                auditLog.getTopic(),
                auditLog.getSourceService(),
                auditLog.getAggregateType(),
                auditLog.getAggregateId(),
                auditLog.getActorUserId(),
                auditLog.getOccurredAt(),
                auditLog.getReceivedAt(),
                auditLog.getPayload()
        );
    }
}
