package com.mayis.account_service.dto;

import jakarta.validation.constraints.Size;

public record ChangeAccountStatusRequestDto(
        @Size(max = 500) String reason
) {
}
