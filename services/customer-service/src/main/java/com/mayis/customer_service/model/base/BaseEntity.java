package com.mayis.customer_service.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;
}
