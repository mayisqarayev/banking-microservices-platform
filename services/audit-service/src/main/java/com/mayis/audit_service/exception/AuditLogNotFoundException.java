package com.mayis.audit_service.exception;

public class AuditLogNotFoundException extends RuntimeException {

    public AuditLogNotFoundException(String message) {
        super(message);
    }
}
