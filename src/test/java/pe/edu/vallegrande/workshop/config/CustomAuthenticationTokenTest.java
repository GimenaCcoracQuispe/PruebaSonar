package pe.edu.vallegrande.workshop.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomAuthenticationTokenTest {

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("email", "user@example.com");

        jwt = new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),  // headers
                claims                 // body claims
        );
    }

    @Test
    void testCustomAuthenticationToken_properties() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        CustomAuthenticationToken token = new CustomAuthenticationToken(jwt, authorities);

        assertTrue(token.isAuthenticated());
        assertEquals("user123", token.getPrincipal());
        assertEquals("user123", token.getName());
        assertEquals(jwt, token.getCredentials());
        assertEquals(jwt, token.getJwt());
        assertEquals(1, token.getAuthorities().size());
        assertEquals("ROLE_USER", token.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testCustomAuthenticationToken_noAuthorities() {
        CustomAuthenticationToken token = new CustomAuthenticationToken(jwt, Collections.emptyList());

        assertEquals(0, token.getAuthorities().size());
        assertEquals("user123", token.getPrincipal());
    }
}
