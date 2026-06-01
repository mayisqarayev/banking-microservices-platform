package com.mayis.account_service.event;

public interface AccountEventPublisher {

    void publishAccountCreated(AccountCreatedEvent event);

    void publishAccountBlocked(AccountBlockedEvent event);

    void publishAccountDebited(AccountDebitedEvent event);

    void publishAccountCredited(AccountCreditedEvent event);
}
