package com.mayis.account_service.dto;

import com.mayis.account_service.model.enums.AccountType;
import com.mayis.account_service.model.enums.Currency;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequestDto(
        @NotNull UUID customerId,
        AccountType accountType,
        Currency currency
) {
}
