package com.mayis.customer_service.event;

import com.mayis.customer_service.config.properties.KafkaTopicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCustomerEventPublisher implements CustomerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaCustomerEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public KafkaCustomerEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicProperties kafkaTopicProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicProperties = kafkaTopicProperties;
    }

    @Override
    public void publishCustomerCreated(CustomerCreatedEvent event) {
        try {
            kafkaTemplate.send(
                    kafkaTopicProperties.getCustomerCreated(),
                    event.customerId().toString(),
                    event
            );
        } catch (Exception exception) {
            log.error("Failed to publish customer.created event for customerId={}", event.customerId(), exception);
        }
    }
}
