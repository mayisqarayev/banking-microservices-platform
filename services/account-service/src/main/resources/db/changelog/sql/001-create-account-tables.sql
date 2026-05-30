CREATE TABLE accounts (
                          id UUID PRIMARY KEY,

                          customer_id UUID NOT NULL,
                          account_number VARCHAR(50) NOT NULL UNIQUE,
                          iban VARCHAR(34) NOT NULL UNIQUE,
                          account_type VARCHAR(50) NOT NULL,
                          currency VARCHAR(10) NOT NULL,
                          balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                          available_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                          status VARCHAR(50) NOT NULL,
                          version BIGINT NOT NULL DEFAULT 0,

                          deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          deleted_at TIMESTAMP,
                          deleted_by UUID,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE account_status_history (
                                        id UUID PRIMARY KEY,

                                        account_id UUID NOT NULL,
                                        old_status VARCHAR(50),
                                        new_status VARCHAR(50) NOT NULL,
                                        reason TEXT,
                                        changed_by UUID,
                                        changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                        CONSTRAINT fk_account_status_history_account
                                            FOREIGN KEY (account_id)
                                                REFERENCES accounts(id)
);

CREATE TABLE account_balance_movements (
                                           id UUID PRIMARY KEY,

                                           account_id UUID NOT NULL,
                                           transaction_id UUID,
                                           movement_type VARCHAR(50) NOT NULL,
                                           amount DECIMAL(19,2) NOT NULL,
                                           balance_before DECIMAL(19,2) NOT NULL,
                                           balance_after DECIMAL(19,2) NOT NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT fk_account_balance_movements_account
                                               FOREIGN KEY (account_id)
                                                   REFERENCES accounts(id)
);

CREATE INDEX idx_accounts_customer_id
    ON accounts(customer_id);

CREATE INDEX idx_accounts_account_number
    ON accounts(account_number);

CREATE INDEX idx_accounts_iban
    ON accounts(iban);

CREATE INDEX idx_accounts_status
    ON accounts(status);

CREATE INDEX idx_accounts_deleted
    ON accounts(deleted);

CREATE INDEX idx_account_status_history_account_id
    ON account_status_history(account_id);

CREATE INDEX idx_account_status_history_changed_at
    ON account_status_history(changed_at);

CREATE INDEX idx_account_balance_movements_account_id
    ON account_balance_movements(account_id);

CREATE INDEX idx_account_balance_movements_transaction_id
    ON account_balance_movements(transaction_id);
