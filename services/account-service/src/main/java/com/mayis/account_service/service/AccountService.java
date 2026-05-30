package com.mayis.account_service.service;

import com.mayis.account_service.event.AccountCreatedEvent;
import com.mayis.account_service.event.AccountEventPublisher;
import com.mayis.account_service.event.CustomerCreatedEvent;
import com.mayis.account_service.model.entity.Account;
import com.mayis.account_service.model.enums.AccountStatus;
import com.mayis.account_service.model.enums.AccountType;
import com.mayis.account_service.model.enums.Currency;
import com.mayis.account_service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEventPublisher accountEventPublisher;

    public AccountService(
            AccountRepository accountRepository,
            AccountEventPublisher accountEventPublisher
    ) {
        this.accountRepository = accountRepository;
        this.accountEventPublisher = accountEventPublisher;
    }

    @Transactional
    public void createDefaultAccount(CustomerCreatedEvent event) {
        if (accountRepository.existsByCustomerId(event.customerId())) {
            return;
        }

        Account account = new Account();
        account.setCustomerId(event.customerId());
        account.setAccountNumber(generateAccountNumber());
        account.setIban(generateIban());
        account.setAccountType(AccountType.CURRENT);
        account.setCurrency(Currency.AZN);
        account.setBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        accountEventPublisher.publishAccountCreated(
                new AccountCreatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        savedAccount.getId(),
                        savedAccount.getCustomerId(),
                        savedAccount.getAccountNumber(),
                        savedAccount.getIban(),
                        savedAccount.getAccountType().name(),
                        savedAccount.getCurrency().name(),
                        savedAccount.getStatus().name(),
                        savedAccount.getBalance(),
                        savedAccount.getAvailableBalance()
                )
        );
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String generateIban() {
        return "AZ00BANK" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
