package com.mayis.auth_service.event;

import com.mayis.auth_service.config.properties.KafkaTopicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserEventPublisher implements UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public KafkaUserEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicProperties kafkaTopicProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicProperties = kafkaTopicProperties;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        publish(
                kafkaTopicProperties.getUserRegistered(),
                event.userId().toString(),
                event,
                "user.registered",
                event.userId()
        );
    }

    @Override
    public void publishUserRoleAssigned(UserRoleAssignedEvent event) {
        publish(
                kafkaTopicProperties.getUserRoleAssigned(),
                event.targetUserId().toString(),
                event,
                "user.role.assigned",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserRoleRemoved(UserRoleRemovedEvent event) {
        publish(
                kafkaTopicProperties.getUserRoleRemoved(),
                event.targetUserId().toString(),
                event,
                "user.role.removed",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserDeleted(UserDeletedEvent event) {
        publish(
                kafkaTopicProperties.getUserDeleted(),
                event.targetUserId().toString(),
                event,
                "user.deleted",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserSuspended(UserSuspendedEvent event) {
        publish(
                kafkaTopicProperties.getUserSuspended(),
                event.targetUserId().toString(),
                event,
                "user.suspended",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserRestored(UserRestoredEvent event) {
        publish(
                kafkaTopicProperties.getUserRestored(),
                event.targetUserId().toString(),
                event,
                "user.restored",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserActivated(UserActivatedEvent event) {
        publish(
                kafkaTopicProperties.getUserActivated(),
                event.targetUserId().toString(),
                event,
                "user.activated",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserDeactivated(UserDeactivatedEvent event) {
        publish(
                kafkaTopicProperties.getUserDeactivated(),
                event.targetUserId().toString(),
                event,
                "user.deactivated",
                event.targetUserId()
        );
    }

    @Override
    public void publishUserUnsuspended(UserUnsuspendedEvent event) {
        publish(
                kafkaTopicProperties.getUserUnsuspended(),
                event.targetUserId().toString(),
                event,
                "user.unsuspended",
                event.targetUserId()
        );
    }

    private void publish(
            String topic,
            String key,
            Object event,
            String eventName,
            Object targetUserId
    ) {
        try {
            kafkaTemplate.send(topic, key, event);
        } catch (Exception exception) {
            log.error("Failed to publish {} event for userId={}", eventName, targetUserId, exception);
        }
    }
}
