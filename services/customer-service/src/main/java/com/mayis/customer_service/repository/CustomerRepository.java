package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByUserId(UUID userId);

    Optional<Customer> findByUserIdAndDeletedFalse(UUID userId);
}
