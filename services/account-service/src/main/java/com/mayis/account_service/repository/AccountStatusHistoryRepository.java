package com.mayis.account_service.repository;

import com.mayis.account_service.model.entity.AccountStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, UUID> {
}
