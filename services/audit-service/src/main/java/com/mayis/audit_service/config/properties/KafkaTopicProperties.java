package com.mayis.audit_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    private String userRegistered;
    private String userRoleAssigned;
    private String userRoleRemoved;
    private String userDeleted;
    private String userSuspended;
    private String userRestored;
    private String userActivated;
    private String userDeactivated;
    private String userUnsuspended;
    private String customerCreated;
    private String customerUpdated;
    private String customerBlocked;
    private String accountCreated;
    private String accountBlocked;
    private String accountDebited;
    private String accountCredited;
}
