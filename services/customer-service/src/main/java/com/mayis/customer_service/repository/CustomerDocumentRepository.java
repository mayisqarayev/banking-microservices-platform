package com.mayis.customer_service.repository;

import com.mayis.customer_service.model.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, UUID> {
}
