package com.mayis.customer_service.dto;

import com.mayis.customer_service.model.enums.AddressType;

import java.util.UUID;

public record CustomerAddressResponseDto(
        UUID id,
        UUID customerId,
        AddressType addressType,
        String country,
        String city,
        String street,
        String postalCode,
        boolean isPrimary
) {
}
