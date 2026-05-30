package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.CustomerStatus;
import com.mayis.customer_service.model.enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponseDto(
        UUID id,
        UUID userId,
        String cif,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String email,
        String phoneNumber,
        CustomerStatus status
) {
}
