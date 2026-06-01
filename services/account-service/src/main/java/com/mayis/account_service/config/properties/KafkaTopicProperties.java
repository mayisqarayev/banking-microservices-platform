package com.mayis.account_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    private String customerCreated;
    private String accountCreated;
    private String accountBlocked;
    private String accountDebited;
    private String accountCredited;
}
