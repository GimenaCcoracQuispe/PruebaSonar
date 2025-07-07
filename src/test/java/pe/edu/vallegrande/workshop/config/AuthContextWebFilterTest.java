package pe.edu.vallegrande.workshop.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.ContextView;

import static org.mockito.Mockito.*;

class AuthContextWebFilterTest {

    private AuthContextWebFilter authContextWebFilter;
    private WebFilterChain mockChain;

    @BeforeEach
    void setUp() {
        authContextWebFilter = new AuthContextWebFilter();
        mockChain = mock(WebFilterChain.class);
    }

    @Test
    void testFilter_withAuthorizationHeader() {
        String token = "Bearer abc.def.ghi";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();

        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        when(mockChain.filter(exchange)).thenAnswer(invocation -> {
            // Simula la ejecución de la cadena con contexto reactivo
            return Mono.deferContextual(ctx -> {
                ContextView contextView = ctx;
                String extractedToken = contextView.getOrDefault("Authorization", "N/A");
                // Aseguramos que el token se haya propagado correctamente
                assert extractedToken.equals("abc.def.ghi");
                return Mono.empty(); // debe retornar Mono<Void>
            });
        });

        StepVerifier.create(authContextWebFilter.jwtTokenPropagationFilter().filter(exchange, mockChain))
                .verifyComplete();
    }

    @Test
    void testFilter_withoutAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        when(mockChain.filter(exchange)).thenAnswer(invocation ->
                Mono.deferContextual(ctx -> {
                    String result = ctx.getOrDefault("Authorization", "N/A");
                    assert result.equals("N/A");
                    return Mono.empty(); // debe retornar Mono<Void>
                })
        );

        StepVerifier.create(authContextWebFilter.jwtTokenPropagationFilter().filter(exchange, mockChain))
                .verifyComplete();
    }
}