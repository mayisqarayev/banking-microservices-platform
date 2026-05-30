package com.mayis.customer_service.service;

import com.mayis.customer_service.dto.CreateCustomerRequestDto;
import com.mayis.customer_service.dto.CustomerResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerRequestDto;
import com.mayis.customer_service.event.CustomerBlockedEvent;
import com.mayis.customer_service.event.CustomerCreatedEvent;
import com.mayis.customer_service.event.CustomerEventPublisher;
import com.mayis.customer_service.event.CustomerUpdatedEvent;
import com.mayis.customer_service.event.UserRegisteredEvent;
import com.mayis.customer_service.exception.CustomerAlreadyDeletedException;
import com.mayis.customer_service.exception.CustomerAlreadyExistsException;
import com.mayis.customer_service.exception.CustomerNotFoundException;
import com.mayis.customer_service.exception.InvalidCustomerStateException;
import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.model.enums.CustomerStatus;
import com.mayis.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerEventPublisher customerEventPublisher;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerEventPublisher customerEventPublisher
    ) {
        this.customerRepository = customerRepository;
        this.customerEventPublisher = customerEventPublisher;
    }

    @Transactional
    public void createFromUserRegistered(UserRegisteredEvent event) {
        if (customerRepository.existsByUserId(event.userId())) {
            return;
        }

        Customer customer = new Customer();
        customer.setUserId(event.userId());
        customer.setCif(generateCif());
        customer.setFirstName(event.firstName());
        customer.setLastName(event.lastName());
        customer.setEmail(event.email());
        customer.setStatus(CustomerStatus.ACTIVE);

        Customer savedCustomer = customerRepository.save(customer);

        customerEventPublisher.publishCustomerCreated(
                new CustomerCreatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        savedCustomer.getId(),
                        savedCustomer.getUserId(),
                        savedCustomer.getCif(),
                        savedCustomer.getFirstName(),
                        savedCustomer.getLastName(),
                        savedCustomer.getEmail()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAllByDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getById(UUID id) {
        return mapToResponse(getActiveCustomerById(id));
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getByUserId(UUID userId) {
        return customerRepository.findByUserIdAndDeletedFalse(userId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    @Transactional
    public CustomerResponseDto create(CreateCustomerRequestDto request) {
        if (customerRepository.existsByUserId(request.userId())) {
            throw new CustomerAlreadyExistsException("Customer already exists for this user");
        }

        if (customerRepository.existsByEmailIgnoreCaseAndDeletedFalse(request.email())) {
            throw new CustomerAlreadyExistsException("Customer email already exists");
        }

        Customer customer = new Customer();
        customer.setUserId(request.userId());
        customer.setCif(generateCif());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setGender(request.gender());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setStatus(resolveStatus(request.status()));

        Customer savedCustomer = customerRepository.save(customer);
        publishCustomerCreated(savedCustomer);

        return mapToResponse(savedCustomer);
    }

    @Transactional
    public CustomerResponseDto update(UUID id, UpdateCustomerRequestDto request) {
        Customer customer = getActiveCustomerById(id);
        CustomerStatus previousStatus = customer.getStatus();

        if (request.status() == CustomerStatus.CLOSED) {
            throw new InvalidCustomerStateException("Customer status cannot be updated to CLOSED from this endpoint");
        }

        if (customerRepository.existsByEmailIgnoreCaseAndDeletedFalseAndIdNot(request.email(), id)) {
            throw new CustomerAlreadyExistsException("Customer email already exists");
        }

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setGender(request.gender());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setStatus(resolveStatus(request.status()));

        Customer savedCustomer = customerRepository.save(customer);
        publishCustomerUpdated(savedCustomer);

        if (previousStatus != CustomerStatus.BLOCKED && savedCustomer.getStatus() == CustomerStatus.BLOCKED) {
            publishCustomerBlocked(savedCustomer);
        }

        return mapToResponse(savedCustomer);
    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        if (customer.isDeleted()) {
            throw new CustomerAlreadyDeletedException("Customer is already deleted");
        }

        customer.setDeleted(true);
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    private String generateCif() {
        return "CIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void publishCustomerCreated(Customer customer) {
        customerEventPublisher.publishCustomerCreated(
                new CustomerCreatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        customer.getId(),
                        customer.getUserId(),
                        customer.getCif(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail()
                )
        );
    }

    private void publishCustomerUpdated(Customer customer) {
        customerEventPublisher.publishCustomerUpdated(
                new CustomerUpdatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        customer.getId(),
                        customer.getUserId(),
                        customer.getCif(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getStatus().name()
                )
        );
    }

    private void publishCustomerBlocked(Customer customer) {
        customerEventPublisher.publishCustomerBlocked(
                new CustomerBlockedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        customer.getId(),
                        customer.getUserId(),
                        customer.getCif(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail()
                )
        );
    }

    private Customer getActiveCustomerById(UUID id) {
        return customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    private CustomerStatus resolveStatus(CustomerStatus status) {
        return status == null ? CustomerStatus.ACTIVE : status;
    }

    private CustomerResponseDto mapToResponse(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getUserId(),
                customer.getCif(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getDateOfBirth(),
                customer.getGender(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getStatus()
        );
    }
}
