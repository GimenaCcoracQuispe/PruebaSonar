package pe.edu.vallegrande.attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.server.WebFilter;

public class AuthContextWebFilter {
    @Bean
    public WebFilter jwtTokenPropagationFilter() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return chain.filter(exchange).contextWrite(ctx -> ctx.put("Authorization", token));
            }
            return chain.filter(exchange);
        };
    }
}