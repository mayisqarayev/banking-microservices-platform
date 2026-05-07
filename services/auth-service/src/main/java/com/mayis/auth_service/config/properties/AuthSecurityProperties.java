package com.mayis.auth_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.security")
public record AuthSecurityProperties(
        int maxFailedLoginAttempts
) {
}