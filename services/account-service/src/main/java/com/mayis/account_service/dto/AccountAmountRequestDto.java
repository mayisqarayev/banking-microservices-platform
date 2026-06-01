package com.mayis.account_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountAmountRequestDto(
        UUID transactionId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
