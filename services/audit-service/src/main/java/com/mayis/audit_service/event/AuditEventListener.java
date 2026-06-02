package com.mayis.audit_service.event;

import com.mayis.audit_service.service.AuditLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private final AuditLogService auditLogService;

    public AuditEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @KafkaListener(
            topics = {
                    "${app.kafka.topics.user-registered}",
                    "${app.kafka.topics.user-role-assigned}",
                    "${app.kafka.topics.user-role-removed}",
                    "${app.kafka.topics.user-deleted}",
                    "${app.kafka.topics.user-suspended}",
                    "${app.kafka.topics.user-restored}",
                    "${app.kafka.topics.user-activated}",
                    "${app.kafka.topics.user-deactivated}",
                    "${app.kafka.topics.user-unsuspended}",
                    "${app.kafka.topics.customer-created}",
                    "${app.kafka.topics.customer-updated}",
                    "${app.kafka.topics.customer-blocked}",
                    "${app.kafka.topics.account-created}",
                    "${app.kafka.topics.account-blocked}",
                    "${app.kafka.topics.account-debited}",
                    "${app.kafka.topics.account-credited}"
            },
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(String payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        auditLogService.record(topic, payload);
    }
}
