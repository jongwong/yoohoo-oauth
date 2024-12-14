package cn.jongwong.server.config.security.handle;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        // 获取 ServerWebExchange 实例
        ServerWebExchange exchange = webFilterExchange.getExchange();

        // 将认证信息存储到 SecurityContext
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        // 设置当前的 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 返回空的 Mono，表示认证成功后继续往下执行过滤链中的其他逻辑
        return Mono.defer(() -> webFilterExchange.getChain().filter(exchange));
    }
}
