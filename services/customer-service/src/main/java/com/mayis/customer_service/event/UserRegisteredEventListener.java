package com.mayis.customer_service.event;

import com.mayis.customer_service.service.CustomerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

    private final CustomerService customerService;

    public UserRegisteredEventListener(CustomerService customerService) {
        this.customerService = customerService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(UserRegisteredEvent event) {
        customerService.createFromUserRegistered(event);
    }
}
