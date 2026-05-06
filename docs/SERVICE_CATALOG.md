📦 SERVICE_CATALOG.md
Banking Core Platform — Service Catalog
1. API Gateway
Responsibility
Acts as the single entry point for all external requests
Routes incoming requests to appropriate internal services
Validates JWT access tokens
Applies rate limiting
Injects correlation IDs into requests
Centralizes security enforcement
Does NOT Do
Execute business logic
Access or manage any database
Perform domain-level validation
Dependencies
Auth Service (for token validation)
Redis (for rate limiting)
Database
❌ No database
Kafka
❌ Does not publish or consume events
2. Auth Service
Responsibility
Handles user registration and authentication
Generates JWT access and refresh tokens
Manages password hashing and credential validation
Manages roles and permissions (RBAC)
Does NOT Do
Manage customer profiles
Handle account or transaction logic
Perform business domain validation
Database
auth_db
Kafka
Publish
user.registered
Consume
❌ None
Dependencies
❌ None
3. Customer Service
Responsibility
Manages customer profiles
Stores and updates customer information
Maintains customer status lifecycle
Does NOT Do
Handle authentication
Manage account balances
Process payments or transactions
Database
customer_db
Kafka
Publish
customer.created
customer.updated
customer.blocked
Consume
user.registered
Dependencies
Auth Service (for user existence validation)
4. Account Service
Responsibility
Manages bank accounts
Maintains account balances
Executes debit and credit operations
Handles account status management
Does NOT Do
Orchestrate transactions
Execute payments
Authenticate users
Database
account_db
Kafka
Publish
account.created
account.debited
account.credited
account.blocked
Consume
customer.created
Dependencies
Customer Service
5. Transaction Service
Responsibility
Orchestrates transaction workflows
Handles idempotency
Manages transaction lifecycle
Coordinates transfer operations between services
Does NOT Do
Directly update account balances (delegated to Account Service)
Execute payment operations (delegated to Payment Service)
Database
transaction_db
Kafka
Publish
transaction.created
transaction.completed
transaction.failed
Consume
account.debited
account.credited
payment.completed
payment.failed
Dependencies
Account Service
Payment Service
6. Payment Service
Responsibility
Executes payment operations
Simulates external payment provider logic
Handles retry and failure scenarios
Does NOT Do
Manage transaction lifecycle
Modify account balances
Database
payment_db
Kafka
Publish
payment.completed
payment.failed
Consume
transaction.created
Dependencies
❌ None (future: external providers)
7. Notification Service
Responsibility
Sends notifications (email, SMS, push)
Stores notification history
Tracks delivery status
Does NOT Do
Contain business logic
Participate in transaction processing
Database
notification_db
Kafka
Publish
❌ None
Consume
transaction.completed
transaction.failed
customer.created
Dependencies
❌ None
8. Audit Service
Responsibility
Stores audit logs for all system activities
Tracks user and system actions
Maintains immutable records for compliance
Does NOT Do
Execute business logic
Make decisions or trigger workflows
Database
audit_db
Kafka
Publish
❌ None
Consume
All system events (recommended)
Dependencies
❌ None
9. Fraud Service (Planned)
Responsibility
Analyzes transactions for suspicious behavior
Calculates risk scores
Flags or blocks potentially fraudulent transactions
Does NOT Do
Execute payments
Modify account balances
Database
fraud_db
Kafka
Publish
fraud.check.completed
Consume
transaction.created
Dependencies
❌ None
🔒 Service Boundary Rules
Each service owns its own database
Direct database access between services is strictly forbidden
All communication must occur via APIs or Kafka events
⚠️ Critical Design Rules
Account balances can only be modified by Account Service
Transaction Service is responsible only for orchestration
Payment Service is responsible only for execution
Auth Service handles authentication only
Audit Service must remain passive (no business decisions)
Notification Service must not trigger business logic