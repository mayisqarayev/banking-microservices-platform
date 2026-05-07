package com.mayis.auth_service.dto;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String status
) {
}
