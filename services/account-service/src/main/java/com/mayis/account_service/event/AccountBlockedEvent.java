package com.mayis.account_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountBlockedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        String iban,
        String status
) {
}
