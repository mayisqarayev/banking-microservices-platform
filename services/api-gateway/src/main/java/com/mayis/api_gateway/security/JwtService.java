package com.mayis.api_gateway.security;

import com.mayis.api_gateway.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public Claims extractClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Set<String> audience = claims.getAudience();
        if (audience == null || !audience.contains(jwtProperties.getAudience())) {
            throw new IllegalArgumentException("Invalid token audience");
        }

        if (!"access".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid token type");
        }

        return claims;
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.get("userId", String.class));
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        return (List<String>) claims.get("roles", List.class);
    }

    private SecretKey getSigningKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }
}
