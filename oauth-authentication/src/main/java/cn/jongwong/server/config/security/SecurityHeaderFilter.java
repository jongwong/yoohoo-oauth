package cn.jongwong.server.config.security;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeaderFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().getHeaders().add("X-Frame-Options", "SAMEORIGIN");
        exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
        return chain.filter(exchange);
    }
}
