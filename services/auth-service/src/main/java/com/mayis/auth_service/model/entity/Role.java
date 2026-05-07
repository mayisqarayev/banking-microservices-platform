package com.mayis.auth_service.model.entity;

import com.mayis.auth_service.model.base.SoftDeletableEntity;
import com.mayis.auth_service.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends SoftDeletableEntity implements GrantedAuthority {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    private String description;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Override
    public String getAuthority() {
        return name.name();
    }
}