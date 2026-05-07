package com.mayis.auth_service.dto;

import com.mayis.auth_service.model.enums.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserStatus status,
        Set<String> roles
) {
}
