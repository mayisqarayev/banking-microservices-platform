package com.mayis.account_service.service;

import com.mayis.account_service.dto.AccountResponseDto;
import com.mayis.account_service.dto.AccountAmountRequestDto;
import com.mayis.account_service.dto.ChangeAccountStatusRequestDto;
import com.mayis.account_service.dto.CreateAccountRequestDto;
import com.mayis.account_service.event.AccountBlockedEvent;
import com.mayis.account_service.event.AccountCreditedEvent;
import com.mayis.account_service.event.AccountCreatedEvent;
import com.mayis.account_service.event.AccountDebitedEvent;
import com.mayis.account_service.event.AccountEventPublisher;
import com.mayis.account_service.event.CustomerCreatedEvent;
import com.mayis.account_service.exception.AccountNotFoundException;
import com.mayis.account_service.exception.CustomerValidationException;
import com.mayis.account_service.exception.InsufficientBalanceException;
import com.mayis.account_service.exception.InvalidAccountOperationException;
import com.mayis.account_service.exception.InvalidAccountStateException;
import com.mayis.account_service.grpc.CustomerValidationClient;
import com.mayis.account_service.model.entity.Account;
import com.mayis.account_service.model.enums.AccountStatus;
import com.mayis.account_service.model.enums.AccountType;
import com.mayis.account_service.model.enums.BalanceMovementType;
import com.mayis.account_service.model.enums.Currency;
import com.mayis.account_service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountStatusHistoryService accountStatusHistoryService;
    private final AccountBalanceMovementService accountBalanceMovementService;
    private final AccountEventPublisher accountEventPublisher;
    private final CustomerValidationClient customerValidationClient;

    public AccountService(
            AccountRepository accountRepository,
            AccountStatusHistoryService accountStatusHistoryService,
            AccountBalanceMovementService accountBalanceMovementService,
            AccountEventPublisher accountEventPublisher,
            CustomerValidationClient customerValidationClient
    ) {
        this.accountRepository = accountRepository;
        this.accountStatusHistoryService = accountStatusHistoryService;
        this.accountBalanceMovementService = accountBalanceMovementService;
        this.accountEventPublisher = accountEventPublisher;
        this.customerValidationClient = customerValidationClient;
    }

    @Transactional
    public void createDefaultAccount(CustomerCreatedEvent event) {
        if (accountRepository.existsByCustomerIdAndDeletedFalse(event.customerId())) {
            return;
        }

        Account savedAccount = persistNewAccount(event.customerId(), AccountType.CURRENT, Currency.AZN);
        publishAccountCreated(savedAccount);
    }

    @Transactional
    public AccountResponseDto create(CreateAccountRequestDto request) {
        CustomerValidationClient.CustomerValidationResult customer = customerValidationClient.validate(request.customerId());

        if (customer.deleted()) {
            throw new CustomerValidationException("Customer is deleted");
        }

        if (!"ACTIVE".equals(customer.status())) {
            throw new CustomerValidationException("Customer is not eligible for account creation");
        }

        Account savedAccount = persistNewAccount(
                request.customerId(),
                request.accountType() == null ? AccountType.CURRENT : request.accountType(),
                request.currency() == null ? Currency.AZN : request.currency()
        );

        publishAccountCreated(savedAccount);
        return mapToResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getById(UUID id) {
        return mapToResponse(getActiveAccountById(id));
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getByCustomerId(UUID customerId) {
        return accountRepository.findAllByCustomerIdAndDeletedFalse(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public AccountResponseDto block(UUID id, ChangeAccountStatusRequestDto request) {
        Account account = getActiveAccountById(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Closed account cannot be blocked");
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountStateException("Account is already blocked");
        }

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(AccountStatus.BLOCKED);
        Account savedAccount = accountRepository.save(account);
        accountStatusHistoryService.record(savedAccount, oldStatus, AccountStatus.BLOCKED, extractReason(request));
        publishAccountBlocked(savedAccount);
        return mapToResponse(savedAccount);
    }

    @Transactional
    public AccountResponseDto unblock(UUID id, ChangeAccountStatusRequestDto request) {
        Account account = getActiveAccountById(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Closed account cannot be unblocked");
        }

        if (account.getStatus() != AccountStatus.BLOCKED) {
            throw new InvalidAccountStateException("Only blocked account can be unblocked");
        }

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(AccountStatus.ACTIVE);
        Account savedAccount = accountRepository.save(account);
        accountStatusHistoryService.record(savedAccount, oldStatus, AccountStatus.ACTIVE, extractReason(request));
        return mapToResponse(savedAccount);
    }

    @Transactional
    public AccountResponseDto close(UUID id, ChangeAccountStatusRequestDto request) {
        Account account = getActiveAccountById(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Account is already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0 || account.getAvailableBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountOperationException("Account with non-zero balance cannot be closed");
        }

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(AccountStatus.CLOSED);
        Account savedAccount = accountRepository.save(account);
        accountStatusHistoryService.record(savedAccount, oldStatus, AccountStatus.CLOSED, extractReason(request));
        return mapToResponse(savedAccount);
    }

    @Transactional
    public AccountResponseDto debit(UUID id, AccountAmountRequestDto request) {
        Account account = getActiveAccountById(id);
        ensureAccountIsActive(account);

        if (account.getAvailableBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient available balance");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal availableBefore = account.getAvailableBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.amount());
        BigDecimal availableAfter = availableBefore.subtract(request.amount());

        account.setBalance(balanceAfter);
        account.setAvailableBalance(availableAfter);
        Account savedAccount = accountRepository.save(account);

        accountBalanceMovementService.record(
                savedAccount,
                request.transactionId(),
                BalanceMovementType.DEBIT,
                request.amount(),
                balanceBefore,
                balanceAfter
        );

        publishAccountDebited(savedAccount, request.transactionId(), request.amount());
        return mapToResponse(savedAccount);
    }

    @Transactional
    public AccountResponseDto credit(UUID id, AccountAmountRequestDto request) {
        Account account = getActiveAccountById(id);
        ensureAccountIsActive(account);

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal availableBefore = account.getAvailableBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.amount());
        BigDecimal availableAfter = availableBefore.add(request.amount());

        account.setBalance(balanceAfter);
        account.setAvailableBalance(availableAfter);
        Account savedAccount = accountRepository.save(account);

        accountBalanceMovementService.record(
                savedAccount,
                request.transactionId(),
                BalanceMovementType.CREDIT,
                request.amount(),
                balanceBefore,
                balanceAfter
        );

        publishAccountCredited(savedAccount, request.transactionId(), request.amount());
        return mapToResponse(savedAccount);
    }

    private Account persistNewAccount(UUID customerId, AccountType accountType, Currency currency) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountNumber(generateAccountNumber());
        account.setIban(generateIban());
        account.setAccountType(accountType);
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);
        accountStatusHistoryService.record(savedAccount, null, AccountStatus.ACTIVE, "Account created");
        return savedAccount;
    }

    private void ensureAccountIsActive(Account account) {
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountStateException("Blocked account cannot process balance operations");
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStateException("Closed account cannot process balance operations");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException("Only active account can process balance operations");
        }
    }

    private void publishAccountCreated(Account account) {
        accountEventPublisher.publishAccountCreated(
                new AccountCreatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        account.getId(),
                        account.getCustomerId(),
                        account.getAccountNumber(),
                        account.getIban(),
                        account.getAccountType().name(),
                        account.getCurrency().name(),
                        account.getStatus().name(),
                        account.getBalance(),
                        account.getAvailableBalance()
                )
        );
    }

    private void publishAccountBlocked(Account account) {
        accountEventPublisher.publishAccountBlocked(
                new AccountBlockedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        account.getId(),
                        account.getCustomerId(),
                        account.getAccountNumber(),
                        account.getIban(),
                        account.getStatus().name()
                )
        );
    }

    private void publishAccountDebited(Account account, UUID transactionId, BigDecimal amount) {
        accountEventPublisher.publishAccountDebited(
                new AccountDebitedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        account.getId(),
                        account.getCustomerId(),
                        transactionId,
                        amount,
                        account.getBalance(),
                        account.getAvailableBalance()
                )
        );
    }

    private void publishAccountCredited(Account account, UUID transactionId, BigDecimal amount) {
        accountEventPublisher.publishAccountCredited(
                new AccountCreditedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        account.getId(),
                        account.getCustomerId(),
                        transactionId,
                        amount,
                        account.getBalance(),
                        account.getAvailableBalance()
                )
        );
    }

    private Account getActiveAccountById(UUID id) {
        return accountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private String extractReason(ChangeAccountStatusRequestDto request) {
        return request == null ? null : request.reason();
    }

    private AccountResponseDto mapToResponse(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getCustomerId(),
                account.getAccountNumber(),
                account.getIban(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance(),
                account.getAvailableBalance(),
                account.getStatus()
        );
    }

    private String generateAccountNumber() {
        String candidate;
        do {
            candidate = "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (accountRepository.existsByAccountNumber(candidate));
        return candidate;
    }

    private String generateIban() {
        String candidate;
        do {
            candidate = "AZ00BANK" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        } while (accountRepository.existsByIban(candidate));
        return candidate;
    }
}
