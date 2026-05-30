package com.mayis.account_service.repository;

import com.mayis.account_service.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByCustomerId(UUID customerId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);
}
