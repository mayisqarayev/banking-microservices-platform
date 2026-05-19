package com.mayis.auth_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private String issuer;
    private String audience;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

}
