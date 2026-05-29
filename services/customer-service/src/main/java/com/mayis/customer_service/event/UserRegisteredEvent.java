package com.mayis.customer_service.event;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID userId,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
}
