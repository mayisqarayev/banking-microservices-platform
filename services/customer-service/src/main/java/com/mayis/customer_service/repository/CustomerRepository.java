package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByUserId(UUID userId);

    Optional<Customer> findByUserIdAndDeletedFalse(UUID userId);
}
