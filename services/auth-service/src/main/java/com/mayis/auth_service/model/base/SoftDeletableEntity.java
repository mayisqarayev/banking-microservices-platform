package com.mayis.auth_service.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(nullable = false)
    protected boolean deleted = false;

    protected LocalDateTime deletedAt;

    protected UUID deletedBy;
}
