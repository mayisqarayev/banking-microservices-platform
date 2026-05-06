# 🗄 DATABASE_SCHEMA.md

# Banking Core Platform — Database Schema Design

## 1. Overview

This document defines the database structure for each microservice in the Banking Core Platform.

The platform follows the **database-per-service** pattern.

---

# 2. Core Architecture Rules

* Each service owns its own database
* Services must never access another service database directly
* All schema changes must be managed by Liquibase
* UUID is used as the primary key for all main entities
* Monetary values must use `DECIMAL(19,2)`
* All critical tables must contain audit fields
* All enums must be explicit and controlled
* Cross-service references are stored as plain UUID values
* Cross-database foreign keys are forbidden
* All main business entities must support soft delete
* Historical/audit tables must remain immutable

---

# 3. Global Technical Standards

## 3.1 Primary Keys

```text
id UUID PRIMARY KEY
```

---

## 3.2 Audit Fields

Recommended for all main business tables:

```text
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP
```

---

## 3.3 Soft Delete Fields

Recommended for all main business entities:

```text
deleted BOOLEAN NOT NULL DEFAULT false
deleted_at TIMESTAMP
deleted_by UUID
```

### Soft Delete Rules

```text
deleted = false -> active record
deleted = true  -> logically deleted record
```

### Soft Delete Applies To

```text
users
roles
customers
customer_documents
customer_addresses
accounts
payments
notifications
fraud_checks
```

### Immutable Tables (No Soft Delete)

```text
audit_logs
account_status_history
account_balance_movements
transaction_steps
payment_attempts
notification_attempts
```

These tables represent historical data and must remain immutable.

---

## 3.4 Money Fields

```text
DECIMAL(19,2)
```

---

## 3.5 Optimistic Locking

Used for balance-sensitive entities:

```text
version BIGINT
```

---

## 3.6 External References

In microservice architecture, references to another service entity are stored as plain UUID values.

Example:

```text
customer_id UUID
```

No cross-database foreign key should ever be created.

---

# 4. Auth Service Database

Database:

```text
auth_db
```

---

## 4.1 users

Stores application users used by Spring Security authentication.

| Field                   | Type         | Description                                 |
| ----------------------- | ------------ | ------------------------------------------- |
| id                      | UUID         | Primary key                                 |
| username                | VARCHAR(100) | Unique username used for login              |
| email                   | VARCHAR(150) | Unique email                                |
| password                | VARCHAR(255) | Hashed password                             |
| first_name              | VARCHAR(100) | User first name                             |
| last_name               | VARCHAR(100) | User last name                              |
| status                  | USER_STATUS  | Business lifecycle status                   |
| enabled                 | BOOLEAN      | Spring Security enabled flag                |
| account_non_expired     | BOOLEAN      | Spring Security account expiration flag     |
| account_non_locked      | BOOLEAN      | Spring Security account lock flag           |
| credentials_non_expired | BOOLEAN      | Spring Security credentials expiration flag |
| failed_login_attempts   | INT          | Failed login counter                        |
| last_login_at           | TIMESTAMP    | Last successful login                       |
| deleted                 | BOOLEAN      | Soft delete flag                            |
| deleted_at              | TIMESTAMP    | Soft delete timestamp                       |
| deleted_by              | UUID         | User/admin who deleted the record           |
| created_at              | TIMESTAMP    | Creation time                               |
| updated_at              | TIMESTAMP    | Last update time                            |

---

## 4.2 roles

Stores system roles.

| Field       | Type         | Description                       |
| ----------- | ------------ | --------------------------------- |
| id          | UUID         | Primary key                       |
| name        | ROLE_NAME    | Role name                         |
| description | VARCHAR(255) | Role description                  |
| deleted     | BOOLEAN      | Soft delete flag                  |
| deleted_at  | TIMESTAMP    | Soft delete timestamp             |
| deleted_by  | UUID         | User/admin who deleted the record |
| created_at  | TIMESTAMP    | Creation time                     |

---

## 4.3 user_roles

Many-to-many relation between users and roles.

| Field   | Type | Description |
| ------- | ---- | ----------- |
| user_id | UUID | FK to users |
| role_id | UUID | FK to roles |

---

## 4.4 refresh_tokens

Stores refresh tokens.

| Field      | Type         | Description          |
| ---------- | ------------ | -------------------- |
| id         | UUID         | Primary key          |
| user_id    | UUID         | FK to users          |
| token_hash | VARCHAR(255) | Hashed refresh token |
| expires_at | TIMESTAMP    | Expiration time      |
| revoked    | BOOLEAN      | Revocation flag      |
| created_at | TIMESTAMP    | Creation time        |

---

## 4.5 Auth Service Enums

```text
USER_STATUS:
- ACTIVE
- INACTIVE
- SUSPENDED
- DELETED
```

```text
ROLE_NAME:
- CUSTOMER
- ADMIN
- SUPPORT
- AUDITOR
```

---

## 4.6 Spring Security Mapping

The `users` table is designed for direct integration with Spring Security `UserDetails`.

```java
@Override
public boolean isEnabled() {
    return enabled && !deleted && status == UserStatus.ACTIVE;
}

@Override
public boolean isAccountNonExpired() {
    return accountNonExpired;
}

@Override
public boolean isAccountNonLocked() {
    return accountNonLocked;
}

@Override
public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
}

@Override
public String getPassword() {
    return password;
}

@Override
public String getUsername() {
    return username;
}
```

---

## 4.7 Business Status vs Security State

### Business State

```text
status = business lifecycle state
```

### Technical Security State

```text
enabled
account_non_expired
account_non_locked
credentials_non_expired
```

Example:

```text
status = ACTIVE
account_non_locked = false
```

Meaning:

```text
User is active from business perspective,
but temporarily locked due to failed login attempts.
```

---

## 4.8 Relations

```text
users 1---N refresh_tokens
users N---N roles
```

---

# 5. Customer Service Database

Database:

```text
customer_db
```

---

## 5.1 customers

Stores customer profile information.

| Field         | Type            | Description                       |
| ------------- | --------------- | --------------------------------- |
| id            | UUID            | Primary key                       |
| user_id       | UUID            | Auth Service user ID reference    |
| cif           | VARCHAR(50)     | Unique customer number            |
| first_name    | VARCHAR(100)    | First name                        |
| last_name     | VARCHAR(100)    | Last name                         |
| date_of_birth | DATE            | Date of birth                     |
| gender        | GENDER          | Gender                            |
| email         | VARCHAR(150)    | Email                             |
| phone_number  | VARCHAR(50)     | Phone number                      |
| status        | CUSTOMER_STATUS | Customer status                   |
| deleted       | BOOLEAN         | Soft delete flag                  |
| deleted_at    | TIMESTAMP       | Soft delete timestamp             |
| deleted_by    | UUID            | User/admin who deleted the record |
| created_at    | TIMESTAMP       | Creation time                     |
| updated_at    | TIMESTAMP       | Last update time                  |

---

## 5.2 customer_documents

Stores customer document information.

| Field           | Type          | Description                       |
| --------------- | ------------- | --------------------------------- |
| id              | UUID          | Primary key                       |
| customer_id     | UUID          | FK to customers                   |
| document_type   | DOCUMENT_TYPE | Document type                     |
| document_number | VARCHAR(100)  | Document number                   |
| issuing_country | VARCHAR(100)  | Issuing country                   |
| issue_date      | DATE          | Issue date                        |
| expiry_date     | DATE          | Expiry date                       |
| deleted         | BOOLEAN       | Soft delete flag                  |
| deleted_at      | TIMESTAMP     | Soft delete timestamp             |
| deleted_by      | UUID          | User/admin who deleted the record |
| created_at      | TIMESTAMP     | Creation time                     |

---

## 5.3 customer_addresses

Stores customer addresses.

| Field        | Type         | Description                       |
| ------------ | ------------ | --------------------------------- |
| id           | UUID         | Primary key                       |
| customer_id  | UUID         | FK to customers                   |
| address_type | ADDRESS_TYPE | Address type                      |
| country      | VARCHAR(100) | Country                           |
| city         | VARCHAR(100) | City                              |
| street       | VARCHAR(255) | Street                            |
| postal_code  | VARCHAR(50)  | Postal code                       |
| is_primary   | BOOLEAN      | Primary address flag              |
| deleted      | BOOLEAN      | Soft delete flag                  |
| deleted_at   | TIMESTAMP    | Soft delete timestamp             |
| deleted_by   | UUID         | User/admin who deleted the record |

---

## 5.4 Customer Service Enums

```text
CUSTOMER_STATUS:
- ACTIVE
- BLOCKED
- CLOSED
- PENDING_VERIFICATION
```

```text
GENDER:
- MALE
- FEMALE
- OTHER
```

```text
DOCUMENT_TYPE:
- NATIONAL_ID
- PASSPORT
- DRIVER_LICENSE
```

```text
ADDRESS_TYPE:
- RESIDENTIAL
- REGISTRATION
- WORK
```

---

## 5.5 Relations

```text
customers 1---N customer_documents
customers 1---N customer_addresses
```

---

# 6. Account Service Database

Database:

```text
account_db
```

---

## 6.1 accounts

Stores bank accounts.

| Field             | Type           | Description                       |
| ----------------- | -------------- | --------------------------------- |
| id                | UUID           | Primary key                       |
| customer_id       | UUID           | Customer ID reference             |
| account_number    | VARCHAR(50)    | Unique account number             |
| iban              | VARCHAR(34)    | Unique IBAN                       |
| account_type      | ACCOUNT_TYPE   | Account type                      |
| currency          | CURRENCY       | Account currency                  |
| balance           | DECIMAL(19,2)  | Current balance                   |
| available_balance | DECIMAL(19,2)  | Available balance                 |
| status            | ACCOUNT_STATUS | Account status                    |
| version           | BIGINT         | Optimistic locking version        |
| deleted           | BOOLEAN        | Soft delete flag                  |
| deleted_at        | TIMESTAMP      | Soft delete timestamp             |
| deleted_by        | UUID           | User/admin who deleted the record |
| created_at        | TIMESTAMP      | Creation time                     |
| updated_at        | TIMESTAMP      | Last update time                  |

---

## 6.2 account_status_history

Stores account status history.

| Field      | Type           | Description     |
| ---------- | -------------- | --------------- |
| id         | UUID           | Primary key     |
| account_id | UUID           | FK to accounts  |
| old_status | ACCOUNT_STATUS | Previous status |
| new_status | ACCOUNT_STATUS | New status      |
| reason     | TEXT           | Change reason   |
| changed_by | UUID           | User/admin ID   |
| changed_at | TIMESTAMP      | Change time     |

---

## 6.3 account_balance_movements

Stores balance movement history.

| Field          | Type                  | Description           |
| -------------- | --------------------- | --------------------- |
| id             | UUID                  | Primary key           |
| account_id     | UUID                  | FK to accounts        |
| transaction_id | UUID                  | Transaction reference |
| movement_type  | BALANCE_MOVEMENT_TYPE | Movement type         |
| amount         | DECIMAL(19,2)         | Amount                |
| balance_before | DECIMAL(19,2)         | Balance before        |
| balance_after  | DECIMAL(19,2)         | Balance after         |
| created_at     | TIMESTAMP             | Creation time         |

---

## 6.4 Account Service Enums

```text
ACCOUNT_TYPE:
- CURRENT
- SAVINGS
- CARD
```

```text
ACCOUNT_STATUS:
- ACTIVE
- BLOCKED
- CLOSED
- FROZEN
- PENDING
```

```text
CURRENCY:
- AZN
- USD
- EUR
```

```text
BALANCE_MOVEMENT_TYPE:
- DEBIT
- CREDIT
- REVERSAL
- HOLD
- RELEASE
```

---

## 6.5 Relations

```text
accounts 1---N account_status_history
accounts 1---N account_balance_movements
```

---

# 7. Transaction Service Database

Database:

```text
transaction_db
```

---

## 7.1 transactions

Stores transaction lifecycle.

| Field                  | Type               | Description                   |
| ---------------------- | ------------------ | ----------------------------- |
| id                     | UUID               | Primary key                   |
| transaction_reference  | VARCHAR(100)       | Unique transaction reference  |
| idempotency_key        | VARCHAR(255)       | Unique idempotency key        |
| source_account_id      | UUID               | Source account reference      |
| destination_account_id | UUID               | Destination account reference |
| amount                 | DECIMAL(19,2)      | Transaction amount            |
| currency               | CURRENCY           | Currency                      |
| transaction_type       | TRANSACTION_TYPE   | Transaction type              |
| status                 | TRANSACTION_STATUS | Transaction status            |
| description            | VARCHAR(255)       | Payment description           |
| failure_reason         | TEXT               | Failure reason                |
| created_at             | TIMESTAMP          | Creation time                 |
| updated_at             | TIMESTAMP          | Last update time              |

---

## 7.2 transaction_steps

Stores transaction step execution states.

| Field          | Type             | Description        |
| -------------- | ---------------- | ------------------ |
| id             | UUID             | Primary key        |
| transaction_id | UUID             | FK to transactions |
| step_name      | TRANSACTION_STEP | Step name          |
| status         | STEP_STATUS      | Step status        |
| error_message  | TEXT             | Error message      |
| created_at     | TIMESTAMP        | Creation time      |
| updated_at     | TIMESTAMP        | Last update time   |

---

## 7.3 idempotency_records

Stores idempotency results.

| Field            | Type         | Description            |
| ---------------- | ------------ | ---------------------- |
| id               | UUID         | Primary key            |
| idempotency_key  | VARCHAR(255) | Unique idempotency key |
| request_hash     | VARCHAR(255) | Request payload hash   |
| response_payload | JSONB        | Stored response        |
| status_code      | INT          | HTTP status code       |
| expires_at       | TIMESTAMP    | Expiration time        |
| created_at       | TIMESTAMP    | Creation time          |

---

## 7.4 Transaction Service Enums

```text
TRANSACTION_TYPE:
- INTERNAL_TRANSFER
- EXTERNAL_PAYMENT
- CARD_PAYMENT
```

```text
TRANSACTION_STATUS:
- PENDING
- PROCESSING
- COMPLETED
- FAILED
- CANCELLED
- REVERSED
```

```text
TRANSACTION_STEP:
- VALIDATE_SOURCE_ACCOUNT
- VALIDATE_DESTINATION_ACCOUNT
- DEBIT_SOURCE_ACCOUNT
- EXECUTE_PAYMENT
- CREDIT_DESTINATION_ACCOUNT
- COMPENSATE_DEBIT
- PUBLISH_EVENT
```

```text
STEP_STATUS:
- PENDING
- SUCCESS
- FAILED
- SKIPPED
```

---

# 8. Payment Service Database

Database:

```text
payment_db
```

---

## 8.1 payments

Stores payment execution information.

| Field             | Type           | Description                       |
| ----------------- | -------------- | --------------------------------- |
| id                | UUID           | Primary key                       |
| transaction_id    | UUID           | Transaction reference             |
| payment_reference | VARCHAR(100)   | Unique payment reference          |
| provider          | VARCHAR(100)   | Payment provider                  |
| amount            | DECIMAL(19,2)  | Payment amount                    |
| currency          | CURRENCY       | Payment currency                  |
| status            | PAYMENT_STATUS | Payment status                    |
| failure_reason    | TEXT           | Failure reason                    |
| deleted           | BOOLEAN        | Soft delete flag                  |
| deleted_at        | TIMESTAMP      | Soft delete timestamp             |
| deleted_by        | UUID           | User/admin who deleted the record |
| created_at        | TIMESTAMP      | Creation time                     |
| updated_at        | TIMESTAMP      | Last update time                  |

---

## 8.2 payment_attempts

Stores payment retry attempts.

| Field          | Type                   | Description    |
| -------------- | ---------------------- | -------------- |
| id             | UUID                   | Primary key    |
| payment_id     | UUID                   | FK to payments |
| attempt_number | INT                    | Attempt number |
| status         | PAYMENT_ATTEMPT_STATUS | Attempt status |
| error_message  | TEXT                   | Error message  |
| attempted_at   | TIMESTAMP              | Attempt time   |

---

## 8.3 Payment Service Enums

```text
PAYMENT_STATUS:
- PENDING
- PROCESSING
- COMPLETED
- FAILED
- CANCELLED
```

```text
PAYMENT_ATTEMPT_STATUS:
- SUCCESS
- FAILED
```

---

# 9. Notification Service Database

Database:

```text
notification_db
```

---

## 9.1 notifications

Stores notification records.

| Field       | Type                 | Description                       |
| ----------- | -------------------- | --------------------------------- |
| id          | UUID                 | Primary key                       |
| customer_id | UUID                 | Customer reference                |
| event_id    | UUID                 | Event reference                   |
| channel     | NOTIFICATION_CHANNEL | Notification channel              |
| recipient   | VARCHAR(255)         | Recipient                         |
| subject     | VARCHAR(255)         | Subject                           |
| message     | TEXT                 | Message body                      |
| status      | NOTIFICATION_STATUS  | Delivery status                   |
| deleted     | BOOLEAN              | Soft delete flag                  |
| deleted_at  | TIMESTAMP            | Soft delete timestamp             |
| deleted_by  | UUID                 | User/admin who deleted the record |
| created_at  | TIMESTAMP            | Creation time                     |
| sent_at     | TIMESTAMP            | Sent time                         |

---

## 9.2 notification_attempts

Stores notification delivery attempts.

| Field           | Type                        | Description         |
| --------------- | --------------------------- | ------------------- |
| id              | UUID                        | Primary key         |
| notification_id | UUID                        | FK to notifications |
| attempt_number  | INT                         | Attempt number      |
| status          | NOTIFICATION_ATTEMPT_STATUS | Attempt status      |
| error_message   | TEXT                        | Error message       |
| attempted_at    | TIMESTAMP                   | Attempt time        |

---

## 9.3 Notification Service Enums

```text
NOTIFICATION_CHANNEL:
- EMAIL
- SMS
- PUSH
```

```text
NOTIFICATION_STATUS:
- PENDING
- SENT
- FAILED
```

```text
NOTIFICATION_ATTEMPT_STATUS:
- SUCCESS
- FAILED
```

---

# 10. Audit Service Database

Database:

```text
audit_db
```

---

## 10.1 audit_logs

Stores immutable audit records.

| Field          | Type         | Description    |
| -------------- | ------------ | -------------- |
| id             | UUID         | Primary key    |
| event_id       | UUID         | Event ID       |
| event_type     | VARCHAR(100) | Event type     |
| service_name   | VARCHAR(100) | Source service |
| entity_type    | VARCHAR(100) | Entity type    |
| entity_id      | VARCHAR(100) | Entity ID      |
| user_id        | UUID         | User ID        |
| correlation_id | VARCHAR(100) | Correlation ID |
| request_ip     | VARCHAR(100) | Request IP     |
| payload        | JSONB        | Event payload  |
| created_at     | TIMESTAMP    | Creation time  |

---

## Audit Rules

* Audit records must never be updated
* Audit records must never be deleted
* Sensitive data must be masked
* Audit Service must remain append-only

---

# 11. Fraud Service Database (Planned)

Database:

```text
fraud_db
```

---

## 11.1 fraud_checks

Stores fraud analysis results.

| Field          | Type          | Description                       |
| -------------- | ------------- | --------------------------------- |
| id             | UUID          | Primary key                       |
| transaction_id | UUID          | Transaction reference             |
| customer_id    | UUID          | Customer reference                |
| amount         | DECIMAL(19,2) | Transaction amount                |
| currency       | CURRENCY      | Currency                          |
| risk_score     | INT           | Risk score                        |
| result         | FRAUD_RESULT  | Fraud result                      |
| reason         | TEXT          | Reason                            |
| deleted        | BOOLEAN       | Soft delete flag                  |
| deleted_at     | TIMESTAMP     | Soft delete timestamp             |
| deleted_by     | UUID          | User/admin who deleted the record |
| created_at     | TIMESTAMP     | Creation time                     |

---

## 11.2 Fraud Service Enums

```text
FRAUD_RESULT:
- APPROVED
- REVIEW_REQUIRED
- REJECTED
```

---

# 12. Index Strategy

## Auth Service

```text
users.email UNIQUE
users.username UNIQUE
users.status
users.enabled
users.account_non_locked
users.deleted
refresh_tokens.token_hash
```

## Customer Service

```text
customers.user_id UNIQUE
customers.cif UNIQUE
customers.email
customers.phone_number
customers.deleted
```

## Account Service

```text
accounts.customer_id
accounts.account_number UNIQUE
accounts.iban UNIQUE
accounts.deleted
account_balance_movements.transaction_id
```

## Transaction Service

```text
transactions.transaction_reference UNIQUE
transactions.idempotency_key UNIQUE
transactions.source_account_id
transactions.destination_account_id
transactions.status
idempotency_records.idempotency_key UNIQUE
```

## Payment Service

```text
payments.transaction_id
payments.payment_reference UNIQUE
payments.status
payments.deleted
```

## Notification Service

```text
notifications.customer_id
notifications.event_id
notifications.status
notifications.deleted
```

## Audit Service

```text
audit_logs.event_id
audit_logs.event_type
audit_logs.correlation_id
audit_logs.created_at
```

---

# 13. Final Design Principles

This database design supports:

* Service isolation
* Clear ownership
* Safe money movement
* Spring Security integration
* Soft delete support
* Idempotent payment processing
* Auditability
* Liquibase schema versioning
* Future scalability
* Event-driven architecture

Critical rule:

> A service owns its data. Other services may only access it through APIs or events.
