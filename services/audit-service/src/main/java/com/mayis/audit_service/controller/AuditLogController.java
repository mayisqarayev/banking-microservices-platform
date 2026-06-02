package com.mayis.audit_service.controller;

import com.mayis.audit_service.dto.AuditLogResponseDto;
import com.mayis.audit_service.service.AuditLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLogResponseDto> getAll() {
        return auditLogService.getAll();
    }

    @GetMapping("/{id}")
    public AuditLogResponseDto getById(@PathVariable UUID id) {
        return auditLogService.getById(id);
    }
}
