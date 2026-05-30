package com.mayis.account_service.event;

import com.mayis.account_service.service.AccountService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedEventListener {

    private final AccountService accountService;

    public CustomerCreatedEventListener(AccountService accountService) {
        this.accountService = accountService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.customer-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(CustomerCreatedEvent event) {
        accountService.createDefaultAccount(event);
    }
}
