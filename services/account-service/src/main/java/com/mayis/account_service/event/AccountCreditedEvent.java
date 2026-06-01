package com.mayis.account_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountCreditedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID accountId,
        UUID customerId,
        UUID transactionId,
        BigDecimal amount,
        BigDecimal balanceAfter,
        BigDecimal availableBalanceAfter
) {
}
