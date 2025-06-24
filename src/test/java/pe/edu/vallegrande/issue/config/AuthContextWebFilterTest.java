package pe.edu.vallegrande.issue.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class AuthContextWebFilterTest {
     private final AuthContextWebFilter filterConfig = new AuthContextWebFilter();

    @Test
    void testFilterAddsAuthorizationToContext() {
        String token = "Bearer abc.def.ghi";
        MockServerHttpRequest request = MockServerHttpRequest.get("/tema/all")
                .header("Authorization", token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenAnswer(invocation -> {
            ServerWebExchange ex = invocation.getArgument(0);
            return Mono.deferContextual(ctx -> {
                String contextToken = ctx.get("Authorization");
                return Mono.just(contextToken).then();
            });
        });

        StepVerifier.create(filterConfig.jwtTokenPropagationFilter().filter(exchange, chain))
                .verifyComplete();

        verify(chain, times(1)).filter(any());
    }

    @Test
    void testFilterWithoutAuthorization() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/tema/all").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filterConfig.jwtTokenPropagationFilter().filter(exchange, chain))
                .verifyComplete();
    }
}
