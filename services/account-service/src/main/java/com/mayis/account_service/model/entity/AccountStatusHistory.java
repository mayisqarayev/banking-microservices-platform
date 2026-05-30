package com.mayis.account_service.model.entity;

import com.mayis.account_service.model.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_status_history")
public class AccountStatusHistory {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private AccountStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus newStatus;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private UUID changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}
