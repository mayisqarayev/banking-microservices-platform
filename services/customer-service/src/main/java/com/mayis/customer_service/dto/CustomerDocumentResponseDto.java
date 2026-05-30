package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerDocumentResponseDto(
        UUID id,
        UUID customerId,
        DocumentType documentType,
        String documentNumber,
        String issuingCountry,
        LocalDate issueDate,
        LocalDate expiryDate
) {
}
