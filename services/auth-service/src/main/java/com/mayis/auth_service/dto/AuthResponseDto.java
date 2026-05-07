package com.mayis.auth_service.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
