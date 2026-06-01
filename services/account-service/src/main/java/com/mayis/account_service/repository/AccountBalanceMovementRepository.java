package com.mayis.account_service.repository;

import com.mayis.account_service.model.entity.AccountBalanceMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountBalanceMovementRepository extends JpaRepository<AccountBalanceMovement, UUID> {

    List<AccountBalanceMovement> findAllByAccountId(UUID accountId);
}
