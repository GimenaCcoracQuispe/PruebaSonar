package pe.edu.vallegrande.issue.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void testConvertJwtWithRoleUser() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "USER")
                .subject("test-user")
                .issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(3600))
                .build();

        Mono<CustomAuthenticationToken> result = securityConfig.testConvertJwtPublic(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals("test-user", token.getPrincipal());
                    assertTrue(token.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
                })
                .verifyComplete();
    }

    @Test
    void testConvertJwtWithoutRole() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("anonymous")
                .issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(3600))
                .build();

        Mono<CustomAuthenticationToken> result = securityConfig.testConvertJwtPublic(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals("anonymous", token.getName());
                    assertTrue(token.getAuthorities().isEmpty());
                })
                .verifyComplete();
    }
}
