package com.mayis.account_service.event;

public interface AccountEventPublisher {

    void publishAccountCreated(AccountCreatedEvent event);
}
