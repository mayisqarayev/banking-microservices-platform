package com.mayis.account_service.dto;

import com.mayis.account_service.model.enums.AccountStatus;
import com.mayis.account_service.model.enums.AccountType;
import com.mayis.account_service.model.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        UUID customerId,
        String accountNumber,
        String iban,
        AccountType accountType,
        Currency currency,
        BigDecimal balance,
        BigDecimal availableBalance,
        AccountStatus status
) {
}
