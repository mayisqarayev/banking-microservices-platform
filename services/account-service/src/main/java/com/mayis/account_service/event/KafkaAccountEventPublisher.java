package com.mayis.account_service.event;

import com.mayis.account_service.config.properties.KafkaTopicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAccountEventPublisher implements AccountEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaAccountEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public KafkaAccountEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicProperties kafkaTopicProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicProperties = kafkaTopicProperties;
    }

    @Override
    public void publishAccountCreated(AccountCreatedEvent event) {
        try {
            kafkaTemplate.send(
                    kafkaTopicProperties.getAccountCreated(),
                    event.accountId().toString(),
                    event
            );
        } catch (Exception exception) {
            log.error("Failed to publish account.created event for accountId={}", event.accountId(), exception);
        }
    }
}
