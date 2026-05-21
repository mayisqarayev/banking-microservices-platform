package com.mayis.customer_service.model.entity;

import com.mayis.customer_service.model.base.SoftDeletableEntity;
import com.mayis.customer_service.model.enums.CustomerStatus;
import com.mayis.customer_service.model.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends SoftDeletableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 50)
    private String cif;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private Set<CustomerDocument> documents = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private Set<CustomerAddress> addresses = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
