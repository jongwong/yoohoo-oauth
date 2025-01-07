package cn.jongwong.server.config.security.jwt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {


    public JwtAuthenticationWebFilter(@Qualifier("customAuthenticationManager") ReactiveAuthenticationManager authenticationManager,
                                      ServerAuthenticationSuccessHandler authenticationSuccessHandler, ServerAuthenticationFailureHandler customAuthenticationFailureHandler) {
        super(authenticationManager);
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/admin/**", "/client/**"));
        setServerAuthenticationConverter(new SmsCodeAuthenticationConverter());
        setAuthenticationSuccessHandler(authenticationSuccessHandler); // 确保执行成功处理器
        setAuthenticationFailureHandler(customAuthenticationFailureHandler);
    }

    private static class SmsCodeAuthenticationConverter implements ServerAuthenticationConverter {

        @Override
        public Mono<Authentication> convert(ServerWebExchange exchange) {
            // 从请求头中获取 Authorization 字段
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                return Mono.empty();
            }


            // 你可以根据需要对 token 进行解析，或者直接将它传递给你的 Token 认证机制
            JwtCodeAuthenticationToken authenticationToken = new JwtCodeAuthenticationToken(token, null);

            authenticationToken.setToken(token);
            // 返回新的认证 token
            return Mono.just(authenticationToken);
        }
    }


}
