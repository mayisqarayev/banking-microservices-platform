package com.mayis.customer_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID customerId,
        UUID userId,
        String cif,
        String firstName,
        String lastName,
        String email
) {
}
