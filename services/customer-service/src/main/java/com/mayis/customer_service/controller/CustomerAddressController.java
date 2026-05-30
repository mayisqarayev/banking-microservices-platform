package com.mayis.customer_service.controller;

import com.mayis.customer_service.dto.CreateCustomerAddressRequestDto;
import com.mayis.customer_service.dto.CustomerAddressResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerAddressRequestDto;
import com.mayis.customer_service.service.CustomerAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;

    public CustomerAddressController(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    @GetMapping
    public List<CustomerAddressResponseDto> getAll(@PathVariable UUID customerId) {
        return customerAddressService.getAll(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAddressResponseDto create(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateCustomerAddressRequestDto request
    ) {
        return customerAddressService.create(customerId, request);
    }

    @PatchMapping("/{addressId}")
    public CustomerAddressResponseDto update(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateCustomerAddressRequestDto request
    ) {
        return customerAddressService.update(customerId, addressId, request);
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId
    ) {
        customerAddressService.delete(customerId, addressId);
    }
}
