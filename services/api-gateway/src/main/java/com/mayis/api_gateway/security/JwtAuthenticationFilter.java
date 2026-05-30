package com.mayis.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout"
    );
    private static final Set<String> ADMIN_ONLY_EXACT_PATHS = Set.of(
            "/api/v1/user",
            "/api/v1/roles"
    );
    private static final List<String> ADMIN_ONLY_PATH_PATTERNS = List.of(
            "/api/v1/user/*/reset-password",
            "/api/v1/user/*/restore",
            "/api/v1/user/*/roles",
            "/api/v1/user/*/roles/*",
            "/api/v1/user/*/activate",
            "/api/v1/user/*/deactivate",
            "/api/v1/user/*/suspend",
            "/api/v1/user/*/unsuspend"
    );

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing access token");
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = jwtService.extractClaims(token);
            List<String> roles = jwtService.extractRoles(claims);

            if (requiresAdmin(path) && (roles == null || !roles.contains("ADMIN"))) {
                return forbidden(exchange, "Forbidden");
            }

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", jwtService.extractUserId(claims).toString())
                    .header("X-Username", jwtService.extractUsername(claims))
                    .header("X-Roles", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtException | IllegalArgumentException exception) {
            return unauthorized(exchange, "Invalid access token");
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> pathMatcher.match(publicPath, path));
    }

    private boolean requiresAdmin(String path) {
        if (ADMIN_ONLY_EXACT_PATHS.contains(path)) {
            return true;
        }

        return ADMIN_ONLY_PATH_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return writeJsonResponse(exchange, message);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return writeJsonResponse(exchange, message);
    }

    private Mono<Void> writeJsonResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        byte[] body = ("{\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(body)));
    }
}
