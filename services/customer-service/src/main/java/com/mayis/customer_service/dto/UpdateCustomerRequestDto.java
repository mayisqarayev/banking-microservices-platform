package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.CustomerStatus;
import com.mayis.customer_service.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCustomerRequestDto(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 50) String phoneNumber,
        CustomerStatus status
) {
}
