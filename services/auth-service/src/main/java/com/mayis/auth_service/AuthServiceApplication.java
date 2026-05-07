package com.mayis.auth_service;

import com.mayis.auth_service.config.properties.AuthSecurityProperties;
import com.mayis.auth_service.config.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		JwtProperties.class,
		AuthSecurityProperties.class
})public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
