package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByUserId(UUID userId);

    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    boolean existsByEmailIgnoreCaseAndDeletedFalseAndIdNot(String email, UUID id);

    List<Customer> findAllByDeletedFalse();

    Optional<Customer> findByIdAndDeletedFalse(UUID id);

    Optional<Customer> findByUserIdAndDeletedFalse(UUID userId);
}
