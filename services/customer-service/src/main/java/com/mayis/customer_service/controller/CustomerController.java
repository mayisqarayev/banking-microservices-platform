package com.mayis.customer_service.controller;

import com.mayis.customer_service.dto.CreateCustomerRequestDto;
import com.mayis.customer_service.dto.CustomerResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerRequestDto;
import com.mayis.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponseDto> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getById(@PathVariable UUID id) {
        return customerService.getById(id);
    }

    @GetMapping("/by-user/{userId}")
    public CustomerResponseDto getByUserId(@PathVariable UUID userId) {
        return customerService.getByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDto create(@Valid @RequestBody CreateCustomerRequestDto request) {
        return customerService.create(request);
    }

    @PatchMapping("/{id}")
    public CustomerResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequestDto request
    ) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        customerService.delete(id);
    }
}
