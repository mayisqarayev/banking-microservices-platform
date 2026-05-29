package com.mayis.customer_service.service;

import com.mayis.customer_service.event.CustomerCreatedEvent;
import com.mayis.customer_service.event.CustomerEventPublisher;
import com.mayis.customer_service.event.UserRegisteredEvent;
import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.model.enums.CustomerStatus;
import com.mayis.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private String generateCif() {
        return "CIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
