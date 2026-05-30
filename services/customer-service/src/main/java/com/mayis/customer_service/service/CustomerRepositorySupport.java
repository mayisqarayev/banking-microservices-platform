package com.mayis.customer_service.service;

import com.mayis.customer_service.exception.CustomerNotFoundException;
import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerRepositorySupport {

    private final CustomerRepository customerRepository;

    public CustomerRepositorySupport(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    protected Customer getActiveCustomerById(UUID id) {
        return customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }
}
