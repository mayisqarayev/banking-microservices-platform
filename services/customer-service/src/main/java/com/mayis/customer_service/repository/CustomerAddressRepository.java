package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {
}
