package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, UUID> {

    List<CustomerDocument> findAllByCustomerIdAndDeletedFalse(UUID customerId);

    Optional<CustomerDocument> findByIdAndCustomerIdAndDeletedFalse(UUID id, UUID customerId);
}
