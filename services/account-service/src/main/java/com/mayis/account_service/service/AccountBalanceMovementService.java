package com.mayis.account_service.service;

import com.mayis.account_service.model.entity.Account;
import com.mayis.account_service.model.entity.AccountBalanceMovement;
import com.mayis.account_service.model.enums.BalanceMovementType;
import com.mayis.account_service.repository.AccountBalanceMovementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountBalanceMovementService {

    private final AccountBalanceMovementRepository accountBalanceMovementRepository;

    public AccountBalanceMovementService(AccountBalanceMovementRepository accountBalanceMovementRepository) {
        this.accountBalanceMovementRepository = accountBalanceMovementRepository;
    }

    public void record(
            Account account,
            UUID transactionId,
            BalanceMovementType movementType,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter
    ) {
        AccountBalanceMovement movement = new AccountBalanceMovement();
        movement.setAccount(account);
        movement.setTransactionId(transactionId);
        movement.setMovementType(movementType);
        movement.setAmount(amount);
        movement.setBalanceBefore(balanceBefore);
        movement.setBalanceAfter(balanceAfter);
        accountBalanceMovementRepository.save(movement);
    }
}
