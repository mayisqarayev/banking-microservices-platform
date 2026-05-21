package com.mayis.auth_service.event;

import com.mayis.auth_service.config.properties.KafkaTopicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserEventPublisher implements UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserEventPublisher.class);

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public KafkaUserEventPublisher(
            KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate,
            KafkaTopicProperties kafkaTopicProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicProperties = kafkaTopicProperties;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send(
                    kafkaTopicProperties.getUserRegistered(),
                    event.userId().toString(),
                    event
            );
        } catch (Exception exception) {
            log.error("Failed to publish user.registered event for userId={}", event.userId(), exception);
        }
    }
}
