package com.mayis.account_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        String iban,
        String accountType,
        String currency,
        String status,
        BigDecimal balance,
        BigDecimal availableBalance
) {
}
