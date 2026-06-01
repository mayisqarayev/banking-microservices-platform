package com.mayis.customer_service.service;

import com.mayis.customer_service.dto.CreateCustomerDocumentRequestDto;
import com.mayis.customer_service.dto.CustomerDocumentResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerDocumentRequestDto;
import com.mayis.customer_service.exception.CustomerDocumentNotFoundException;
import com.mayis.customer_service.exception.InvalidCustomerDocumentException;
import com.mayis.customer_service.exception.InvalidCustomerStateException;
import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.model.entity.CustomerDocument;
import com.mayis.customer_service.repository.CustomerDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerDocumentService {

    private final CustomerRepositorySupport customerRepositorySupport;
    private final CustomerDocumentRepository customerDocumentRepository;

    public CustomerDocumentService(
            CustomerRepositorySupport customerRepositorySupport,
            CustomerDocumentRepository customerDocumentRepository
    ) {
        this.customerRepositorySupport = customerRepositorySupport;
        this.customerDocumentRepository = customerDocumentRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerDocumentResponseDto> getAll(UUID customerId) {
        Customer customer = customerRepositorySupport.getActiveCustomerById(customerId);

        return customerDocumentRepository.findAllByCustomerIdAndDeletedFalse(customer.getId())
                .stream()
                .map(document -> new CustomerDocumentResponseDto(
                        document.getId(),
                        document.getCustomer().getId(),
                        document.getDocumentType(),
                        document.getDocumentNumber(),
                        document.getIssuingCountry(),
                        document.getIssueDate(),
                        document.getExpiryDate()
                ))
                .toList();
    }

    @Transactional
    public CustomerDocumentResponseDto create(UUID customerId, CreateCustomerDocumentRequestDto request) {
        Customer customer = customerRepositorySupport.getActiveCustomerById(customerId);

        if (request.issueDate() != null && request.expiryDate() != null && request.expiryDate().isBefore(request.issueDate())) {
            throw new InvalidCustomerDocumentException("Document expiry date cannot be before issue date");
        }

        CustomerDocument document = new CustomerDocument();
        document.setCustomer(customer);
        document.setDocumentType(request.documentType());
        document.setDocumentNumber(request.documentNumber());
        document.setIssuingCountry(request.issuingCountry());
        document.setIssueDate(request.issueDate());
        document.setExpiryDate(request.expiryDate());

        CustomerDocument savedDocument = customerDocumentRepository.save(document);
        return new CustomerDocumentResponseDto(
                savedDocument.getId(),
                savedDocument.getCustomer().getId(),
                savedDocument.getDocumentType(),
                savedDocument.getDocumentNumber(),
                savedDocument.getIssuingCountry(),
                savedDocument.getIssueDate(),
                savedDocument.getExpiryDate()
        );
    }

    @Transactional
    public CustomerDocumentResponseDto update(
            UUID customerId,
            UUID documentId,
            UpdateCustomerDocumentRequestDto request
    ) {
        customerRepositorySupport.getActiveCustomerById(customerId);

        if (request.issueDate() != null && request.expiryDate() != null && request.expiryDate().isBefore(request.issueDate())) {
            throw new InvalidCustomerDocumentException("Document expiry date cannot be before issue date");
        }

        CustomerDocument document = customerDocumentRepository
                .findByIdAndCustomerIdAndDeletedFalse(documentId, customerId)
                .orElseThrow(() -> new CustomerDocumentNotFoundException("Customer document not found"));

        document.setDocumentType(request.documentType());
        document.setDocumentNumber(request.documentNumber());
        document.setIssuingCountry(request.issuingCountry());
        document.setIssueDate(request.issueDate());
        document.setExpiryDate(request.expiryDate());

        CustomerDocument savedDocument = customerDocumentRepository.save(document);
        return new CustomerDocumentResponseDto(
                savedDocument.getId(),
                savedDocument.getCustomer().getId(),
                savedDocument.getDocumentType(),
                savedDocument.getDocumentNumber(),
                savedDocument.getIssuingCountry(),
                savedDocument.getIssueDate(),
                savedDocument.getExpiryDate()
        );
    }

    @Transactional
    public void delete(UUID customerId, UUID documentId) {
        customerRepositorySupport.getActiveCustomerById(customerId);

        CustomerDocument document = customerDocumentRepository
                .findByIdAndCustomerIdAndDeletedFalse(documentId, customerId)
                .orElseThrow(() -> new CustomerDocumentNotFoundException("Customer document not found"));

        if (document.isDeleted()) {
            throw new InvalidCustomerStateException("Customer document is already deleted");
        }

        document.setDeleted(true);
        document.setDeletedAt(LocalDateTime.now());
        customerDocumentRepository.save(document);
    }
}
