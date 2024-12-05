package cn.jongwong.server.config.oauth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        // 从会话中获取登录前的请求 URL
        String redirectUri = webFilterExchange.getExchange()
                .getRequest()
                .getHeaders()
                .getFirst("Referer");  // 获取请求头中的 Referer，表示登录前的页面
        System.out.printf("-------redirectUri-------%s%n", redirectUri);
        // 如果没有 Referer，则可以设置一个默认的跳转 URL
        if (redirectUri == null) {
            redirectUri = "/home";  // 你可以设定一个默认页面，或者重定向到首页
        }

        // 设置重定向响应
        ServerWebExchange exchange = webFilterExchange.getExchange();
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);  // 设置状态码为重定向
        exchange.getResponse().getHeaders().setLocation(URI.create(redirectUri));  // 设置跳转的 URL

        return exchange.getResponse().setComplete();
    }
}
