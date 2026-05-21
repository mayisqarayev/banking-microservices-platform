package com.mayis.auth_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRestoredEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID actorUserId,
        UUID targetUserId
) {
}
