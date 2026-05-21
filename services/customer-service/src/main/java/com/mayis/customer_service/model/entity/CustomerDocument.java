package com.mayis.customer_service.model.entity;

import com.mayis.customer_service.model.base.SoftDeletableEntity;
import com.mayis.customer_service.model.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customer_documents")
public class CustomerDocument extends SoftDeletableEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false, length = 100)
    private String documentNumber;

    @Column(nullable = false, length = 100)
    private String issuingCountry;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = LocalDateTime.now();
    }
}
