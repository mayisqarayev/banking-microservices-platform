package com.mayis.auth_service.dto;

import java.util.UUID;

public record GenerateRefreshTokenRequestDto(
        UUID userId
) {
}
