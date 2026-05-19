package com.mayis.auth_service.dto;

import com.mayis.auth_service.model.enums.RoleName;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequestDto(
        @NotNull
        RoleName role
) {
}
