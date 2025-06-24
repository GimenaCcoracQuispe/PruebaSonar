package pe.edu.vallegrande.issue.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import java.time.Instant;
import java.util.Map;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigTest {
     private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void testJwtDecoderNotNull() throws Exception {
        // Usamos reflection para establecer el URI del JWKS
        Field field = SecurityConfig.class.getDeclaredField("jwkSetUri");
        field.setAccessible(true);
        field.set(securityConfig, "http://localhost:8080/oauth2/jwks");

        ReactiveJwtDecoder decoder = securityConfig.jwtDecoder();
        assertNotNull(decoder);
    }

    @Test
    void testConvertJwtWithRoleUser() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("role", "USER")
                .subject("test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Mono<CustomAuthenticationToken> result = securityConfig.convertJwt(jwt);

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
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Mono<CustomAuthenticationToken> result = securityConfig.convertJwt(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals("anonymous", token.getName());
                    assertTrue(token.getAuthorities().isEmpty());
                })
                .verifyComplete();
    }
}
