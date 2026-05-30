package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCustomerDocumentRequestDto(
        @NotNull DocumentType documentType,
        @NotBlank @Size(max = 100) String documentNumber,
        @NotBlank @Size(max = 100) String issuingCountry,
        LocalDate issueDate,
        LocalDate expiryDate
) {
}
