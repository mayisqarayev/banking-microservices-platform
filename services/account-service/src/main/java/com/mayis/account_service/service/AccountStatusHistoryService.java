package com.mayis.account_service.service;

import com.mayis.account_service.model.entity.Account;
import com.mayis.account_service.model.entity.AccountStatusHistory;
import com.mayis.account_service.model.enums.AccountStatus;
import com.mayis.account_service.repository.AccountStatusHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountStatusHistoryService {

    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    public AccountStatusHistoryService(AccountStatusHistoryRepository accountStatusHistoryRepository) {
        this.accountStatusHistoryRepository = accountStatusHistoryRepository;
    }

    public void record(Account account, AccountStatus oldStatus, AccountStatus newStatus, String reason) {
        AccountStatusHistory history = new AccountStatusHistory();
        history.setAccount(account);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        accountStatusHistoryRepository.save(history);
    }
}
