package com.mayis.customer_service.controller;

import com.mayis.customer_service.dto.CreateCustomerDocumentRequestDto;
import com.mayis.customer_service.dto.CustomerDocumentResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerDocumentRequestDto;
import com.mayis.customer_service.service.CustomerDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/documents")
public class CustomerDocumentController {

    private final CustomerDocumentService customerDocumentService;

    public CustomerDocumentController(CustomerDocumentService customerDocumentService) {
        this.customerDocumentService = customerDocumentService;
    }

    @GetMapping
    public List<CustomerDocumentResponseDto> getAll(@PathVariable UUID customerId) {
        return customerDocumentService.getAll(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDocumentResponseDto create(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateCustomerDocumentRequestDto request
    ) {
        return customerDocumentService.create(customerId, request);
    }

    @PatchMapping("/{documentId}")
    public CustomerDocumentResponseDto update(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId,
            @Valid @RequestBody UpdateCustomerDocumentRequestDto request
    ) {
        return customerDocumentService.update(customerId, documentId, request);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId
    ) {
        customerDocumentService.delete(customerId, documentId);
    }
}
