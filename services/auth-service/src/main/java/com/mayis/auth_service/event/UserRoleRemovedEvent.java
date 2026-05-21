package com.mayis.auth_service.event;

import com.mayis.auth_service.model.enums.RoleName;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRoleRemovedEvent(
        UUID eventId,
        LocalDateTime occurredAt,
        UUID actorUserId,
        UUID targetUserId,
        RoleName role
) {
}
