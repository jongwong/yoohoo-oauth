package cn.jongwong.server.config.security.handle;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        System.out.println("-------WebFilterExchange-------");

        // 获取 ServerWebExchange 实例
        ServerWebExchange exchange = webFilterExchange.getExchange();

        // 将认证信息存储到 SecurityContext
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);


        // 获取会话并保存认证信息到 WebSession
        return exchange.getSession()
                .doOnNext(session -> System.out.println("Session initialized: " + session)) // 确保会话已初始化
                .flatMap(session -> {
                    // 获取会话中的属性，检查是否有重定向 URL
                    Map<String, Object> attributes = session.getAttributes();

                    String savedRequestUrl = (String) attributes.get("SPRING_SECURITY_SAVED_REQUEST");
                    if (savedRequestUrl == null) {
                        savedRequestUrl = "/";  // 默认重定向到首页
                    }


                    // 在会话保存后执行 WebSessionServerSecurityContextRepository 的 save 方法
                    return new WebSessionServerSecurityContextRepository()
                            .save(exchange, securityContext)
                            .doOnTerminate(() -> System.out.println("Security context saved successfully"))
                            .then(Mono.just(savedRequestUrl));
                })
                .doOnNext(savedRequestUrl -> {
                    // 设置 HTTP 302 状态码并进行重定向
                    System.out.println("Redirecting to: " + savedRequestUrl);

                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders().setLocation(URI.create(savedRequestUrl));

                }).then().doOnSuccess(aVoid -> {
                    System.out.println("Response successfully sent to redirect to: " + aVoid);
                })
                .doOnError(error -> {
                    // 捕获并打印可能发生的错误
                    System.out.println("Error during authentication success handling: " + error.getMessage());
                });
    }
}
