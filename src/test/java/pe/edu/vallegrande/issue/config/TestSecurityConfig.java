package pe.edu.vallegrande.issue.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;

@TestConfiguration
class TestSecurityConfig {
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return token -> Mono.just(
            Jwt.withTokenValue(token)
                .header("alg", "none")
                .subject("test-user")
                .claim("role", "USER")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claims(claims -> claims.putAll(new HashMap<>()))
                .build()
        );
    }
}
