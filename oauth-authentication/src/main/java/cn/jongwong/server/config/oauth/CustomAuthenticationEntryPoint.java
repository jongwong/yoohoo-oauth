package cn.jongwong.server.config.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        System.out.printf("-------CustomAuthenticationEntryPoint-------%s%n", e);
        // 返回 401 状态码
        // 设置 401 Unauthorized 状态码和错误消息
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("{\"error\": \"Unauthorized\"}".getBytes())));

        return exchange.getResponse().setComplete();
    }
}
