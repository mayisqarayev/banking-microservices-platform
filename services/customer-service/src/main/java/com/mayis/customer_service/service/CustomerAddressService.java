package com.mayis.customer_service.service;

import com.mayis.customer_service.dto.CreateCustomerAddressRequestDto;
import com.mayis.customer_service.dto.CustomerAddressResponseDto;
import com.mayis.customer_service.dto.UpdateCustomerAddressRequestDto;
import com.mayis.customer_service.exception.CustomerAddressNotFoundException;
import com.mayis.customer_service.exception.InvalidCustomerStateException;
import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.model.entity.CustomerAddress;
import com.mayis.customer_service.repository.CustomerAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerAddressService {

    private final CustomerRepositorySupport customerRepositorySupport;
    private final CustomerAddressRepository customerAddressRepository;

    public CustomerAddressService(
            CustomerRepositorySupport customerRepositorySupport,
            CustomerAddressRepository customerAddressRepository
    ) {
        this.customerRepositorySupport = customerRepositorySupport;
        this.customerAddressRepository = customerAddressRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerAddressResponseDto> getAll(UUID customerId) {
        Customer customer = customerRepositorySupport.getActiveCustomerById(customerId);

        return customerAddressRepository.findAllByCustomerIdAndDeletedFalse(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CustomerAddressResponseDto create(UUID customerId, CreateCustomerAddressRequestDto request) {
        Customer customer = customerRepositorySupport.getActiveCustomerById(customerId);

        if (request.isPrimary()) {
            clearPrimaryAddresses(customerId);
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setAddressType(request.addressType());
        address.setCountry(request.country());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setPostalCode(request.postalCode());
        address.setPrimary(request.isPrimary());

        return mapToResponse(customerAddressRepository.save(address));
    }

    @Transactional
    public CustomerAddressResponseDto update(
            UUID customerId,
            UUID addressId,
            UpdateCustomerAddressRequestDto request
    ) {
        customerRepositorySupport.getActiveCustomerById(customerId);

        CustomerAddress address = customerAddressRepository
                .findByIdAndCustomerIdAndDeletedFalse(addressId, customerId)
                .orElseThrow(() -> new CustomerAddressNotFoundException("Customer address not found"));

        if (request.isPrimary()) {
            clearPrimaryAddresses(customerId);
        }

        address.setAddressType(request.addressType());
        address.setCountry(request.country());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setPostalCode(request.postalCode());
        address.setPrimary(request.isPrimary());

        return mapToResponse(customerAddressRepository.save(address));
    }

    @Transactional
    public void delete(UUID customerId, UUID addressId) {
        customerRepositorySupport.getActiveCustomerById(customerId);

        CustomerAddress address = customerAddressRepository
                .findByIdAndCustomerIdAndDeletedFalse(addressId, customerId)
                .orElseThrow(() -> new CustomerAddressNotFoundException("Customer address not found"));

        if (address.isDeleted()) {
            throw new InvalidCustomerStateException("Customer address is already deleted");
        }

        address.setDeleted(true);
        address.setDeletedAt(LocalDateTime.now());
        address.setPrimary(false);
        customerAddressRepository.save(address);
    }

    private void clearPrimaryAddresses(UUID customerId) {
        List<CustomerAddress> addresses = customerAddressRepository.findAllByCustomerIdAndDeletedFalse(customerId);
        for (CustomerAddress address : addresses) {
            if (address.isPrimary()) {
                address.setPrimary(false);
            }
        }
    }

    private CustomerAddressResponseDto mapToResponse(CustomerAddress address) {
        return new CustomerAddressResponseDto(
                address.getId(),
                address.getCustomer().getId(),
                address.getAddressType(),
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getPostalCode(),
                address.isPrimary()
        );
    }
}
