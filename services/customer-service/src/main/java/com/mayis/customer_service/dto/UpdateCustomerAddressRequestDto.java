package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCustomerAddressRequestDto(
        @NotNull AddressType addressType,
        @NotBlank @Size(max = 100) String country,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 255) String street,
        @Size(max = 50) String postalCode,
        boolean isPrimary
) {
}
