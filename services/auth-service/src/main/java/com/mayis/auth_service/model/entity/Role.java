package com.mayis.auth_service.model.entity;

import com.mayis.auth_service.model.base.SoftDeletableEntity;
import com.mayis.auth_service.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends SoftDeletableEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    private String description;
}